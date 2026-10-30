package rpg.controlador;

import rpg.dto.*;
import rpg.modelo.*;
import rpg.vista.PantallaBatalla;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Principal del sistema MVC para "El Último Ascenso".
 * Coordina las transiciones entre pantallas:
 * Inicio, Torre, Mapa, Kiosco, Combate, Resultados, Party, Evento, Descanso.
 * Maneja excepciones de persistencia (Clase 13) informando a la vista mediante JOptionPane.
 */
public class ControladorJuego implements NodoVisitor {

    private Juego juego;
    private final PantallaBatalla vista;
    private ControladorBatalla controladorBatalla;

    public ControladorJuego(Juego juego, PantallaBatalla vista) {
        this.juego = juego;
        this.vista = vista;
        this.vista.setControladorJuego(this);
    }

    public void iniciar() {
        mostrarPantallaInicio();
    }

    // ─── Pantalla Inicio ──────────────────────────────────────────────────────────

    public void mostrarPantallaInicio() {
        vista.actualizarEstadoGuardado();
        vista.mostrarPantalla("INICIO");
    }

    public void abrirCreacionParty() {
        iniciarNuevaPartida();
    }

    public void iniciarNuevaPartida() {
        juego = new Juego();
        juego.iniciarNuevaPartida();
        mostrarPantallaTorre();
    }

    public void iniciarPartidaConParty(Party party) {
        juego = new Juego();
        juego.iniciarNuevaPartida(party);
        mostrarPantallaTorre();
    }

    public void cargarPartida() {
        if (!GestorPersistencia.existePartidaGuardada()) {
            JOptionPane.showMessageDialog(vista,
                    "No hay ninguna partida guardada disponible.",
                    "Cargar Partida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Juego juegoCargado = GestorPersistencia.cargarPartida();
            this.juego = juegoCargado;
            JOptionPane.showMessageDialog(vista,
                    "¡Partida cargada exitosamente!\nPiso actual: " + juego.getPisoActual(),
                    "Cargar Partida", JOptionPane.INFORMATION_MESSAGE);

            if (juego.getMapa().isJefePisoCompletado()) {
                mostrarPantallaTorre();
            } else {
                volverAlMapa();
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo cargar la partida guardada.\nDetalle: " + e.getMessage(),
                    "Archivo Dañado o Ilegible", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardarPartida() {
        try {
            GestorPersistencia.guardarPartida(juego);
            vista.actualizarEstadoGuardado();
            JOptionPane.showMessageDialog(vista,
                    "Partida guardada correctamente en el disco.",
                    "Guardar Partida", JOptionPane.INFORMATION_MESSAGE);
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(vista,
                    "Fallo al guardar la partida:\n" + e.getMessage(),
                    "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── Pantalla Torre ───────────────────────────────────────────────────────────

    public void mostrarPantallaTorre() {
        vista.actualizarTorre(juego);
        vista.mostrarPantalla("TORRE");
    }

    public void entrarPiso(int piso) {
        juego.avanzarPiso(piso);
        volverAlMapa();
    }

    // ─── Pantalla Mapa ────────────────────────────────────────────────────────────

    public void volverAlMapa() {
        Mapa mapa = juego.getMapa();
        List<NodoDTO> nodosDTO = new ArrayList<>();
        for (Nodo n : mapa.getTodosLosNodos()) {
            boolean esActual = (n.getId() == mapa.getIdNodoActual());
            nodosDTO.add(new NodoDTO(
                    n.getId(),
                    n.getColumna(),
                    n.getNombre(),
                    n.getDescripcion(),
                    n.getIcono(),
                    n.isCompletado(),
                    n.isDisponible(),
                    esActual,
                    n.getConexiones(),
                    n.isRevelado()
            ));
        }

        boolean puntosPendientes = false;
        for (Personaje p : juego.getParty().getMiembros()) {
            if (p.getPuntosAtributoDisponibles() > 0 || p.puedeAprenderHabilidad()) {
                puntosPendientes = true;
                break;
            }
        }

        MapaDTO mapaDTO = new MapaDTO(mapa.getPiso(), mapa.getIdNodoActual(), nodosDTO);
        vista.actualizarMapa(mapaDTO, juego.getParty().getOro(), puntosPendientes);
        vista.mostrarPantalla("MAPA");
    }

    public void seleccionarNodo(int idNodo) {
        boolean exito = juego.seleccionarNodo(idNodo);
        if (!exito) return;

        Nodo nodo = juego.getMapa().getNodoActual();
        if (nodo != null) {
            nodo.aceptar(this);
        }
    }

    @Override
    public void visitarCombate(Nodo nodo) {
        Batalla batalla = juego.getBatallaActual();
        controladorBatalla = new ControladorBatalla(batalla, vista);
        controladorBatalla.setControladorJuego(this);
        controladorBatalla.iniciar();
        vista.mostrarPantalla("COMBATE");
    }

    @Override
    public void visitarEvento(NodoEvento nodo) {
        EventoAleatorio ev = juego.getEventoActual();
        EventoDTO dto = new EventoDTO(ev.getTitulo(), ev.getDescripcion(), ev.getOpcionA(), ev.getOpcionB());
        vista.actualizarEvento(dto);
        vista.mostrarPantalla("EVENTO");
    }

    @Override
    public void visitarFogata(NodoFogata nodo) {
        vista.reiniciarVistaFogata();
        vista.mostrarPantalla("DESCANSO");
    }

    @Override
    public void visitarKiosco(NodoKiosco nodo) {
        vista.actualizarKiosco(juego.getParty());
        vista.mostrarPantalla("KIOSCO");
    }

    // ─── Kiosco ───────────────────────────────────────────────────────────────────

    public void comprarItemKiosco(Item item) {
        boolean exito = juego.comprarItem(item);
        if (!exito) {
            JOptionPane.showMessageDialog(vista,
                    "No tenés suficiente oro o el inventario está lleno.",
                    "Kiosco", JOptionPane.WARNING_MESSAGE);
        }
        vista.actualizarKiosco(juego.getParty());
    }

    public void venderItemKiosco(Item item) {
        boolean exito = juego.venderItem(item);
        if (!exito) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo vender el objeto.",
                    "Kiosco", JOptionPane.WARNING_MESSAGE);
        }
        vista.actualizarKiosco(juego.getParty());
    }

    public void completarKioscoYVolver() {
        juego.completarNodoActual();
        volverAlMapa();
    }

    // ─── Fin de Combate ───────────────────────────────────────────────────────────

    public void procesarFinDeCombate(boolean victoria) {
        Batalla batalla = juego.getBatallaActual();

        if (victoria) {
            juego.completarNodoActual();
            RecompensaDTO recompensa = batalla.calcularRecompensa();
            juego.registrarCombateGanado(recompensa.getOro());
            EstadoBatallaDTO estadoDTO = batalla.crearEstadoDTO();
            vista.actualizarResultados(estadoDTO, recompensa, juego);
        } else {
            GestorPersistencia.borrarPartidaPorPermadeath();
            EstadoBatallaDTO estadoDTO = batalla.crearEstadoDTO();
            vista.actualizarResultados(estadoDTO, null, juego);
        }
        vista.mostrarPantalla("RESULTADOS");
    }

    public void volverAlMapaOATorre() {
        continuarDesdeResultados();
    }

    public void continuarDesdeResultados() {
        if (!juego.getParty().estaDerrotada() && juego.isJuegoCompletadoVictoria()) {
            mostrarPantallaInicio();
            return;
        }

        if (juego.getParty().estaDerrotada()) {
            mostrarPantallaInicio();
            return;
        }

        if (juego.getMapa().isJefePisoCompletado()) {
            mostrarPantallaTorre();
        } else {
            volverAlMapa();
        }
    }

    // ─── Pantalla Party / Gestión y Progreso ──────────────────────────────────────

    public void abrirGestionParty() {
        Party party = juego.getParty();
        List<PersonajeDTO> heroesDTO = construirHeroesDTO(party);

        List<ItemDTO> armasDTO     = construirArmasDTO(party);
        List<ItemDTO> armadurasDTO = construirArmadurasDTO(party);
        List<ItemDTO> consumiblesDTO = construirConsumiblesDTO(party);

        PartyGestionDTO dto = new PartyGestionDTO(heroesDTO, armasDTO, armadurasDTO, consumiblesDTO, party.getOro());
        vista.actualizarGestionParty(dto, party);
        vista.mostrarPantalla("PARTY");
    }

    public void desbloquearHabilidadHeroe(int indiceHeroe, Habilidad h) {
        Party party = juego.getParty();
        if (indiceHeroe >= 0 && indiceHeroe < party.getMiembros().size()) {
            Personaje p = party.getMiembros().get(indiceHeroe);
            p.aprenderHabilidad(h);
            abrirGestionParty();
        }
    }

    private List<PersonajeDTO> construirHeroesDTO(Party party) {
        List<PersonajeDTO> lista = new ArrayList<>();
        for (Personaje p : party.getMiembros()) {
            String armaNom     = (p.getArma()    != null) ? p.getArma().getNombre()    : "Ninguna";
            String armaduraNom = (p.getArmadura() != null) ? p.getArmadura().getNombre() : "Ninguna";
            lista.add(new PersonajeDTO(
                    p.getNombre(), p.getTipo(), p.getVida(), p.getVidaMaxima(),
                    p.getMana(), p.getManaMaximo(), p.getNivel(), p.getAtaque(),
                    p.getDefensa(), p.getVelocidad(), p.estaVivo(), p.isDefendiendo(),
                    p.getRutaSprite(), p.getExperiencia(), p.getExperienciaSiguienteNivel(),
                    armaNom, armaduraNom, p.getPuntosAtributoDisponibles(), p.getNombresHabilidades()
            ));
        }
        return lista;
    }

    private List<ItemDTO> construirArmasDTO(Party party) {
        List<ItemDTO> lista = new ArrayList<>();
        for (Arma a : party.getInventario().getArmas()) {
            int idx = party.getInventario().getItems().indexOf(a);
            lista.add(new ItemDTO(idx, a.getNombre(), "+" + a.getBonusAtaque() + " ATQ | Valor: $" + a.getPrecioVenta(), false));
        }
        return lista;
    }

    private List<ItemDTO> construirArmadurasDTO(Party party) {
        List<ItemDTO> lista = new ArrayList<>();
        for (Armadura arm : party.getInventario().getArmaduras()) {
            int idx = party.getInventario().getItems().indexOf(arm);
            lista.add(new ItemDTO(idx, arm.getNombre(), "+" + arm.getBonusDefensa() + " DEF | Valor: $" + arm.getPrecioVenta(), false));
        }
        return lista;
    }

    private List<ItemDTO> construirConsumiblesDTO(Party party) {
        List<ItemDTO> lista = new ArrayList<>();
        for (Consumible c : party.getInventario().getConsumibles()) {
            int idx = party.getInventario().getItems().indexOf(c);
            lista.add(new ItemDTO(idx, c.getNombre(), c.getDescripcion() + " | Valor: $" + c.getPrecioVenta(), true));
        }
        return lista;
    }

    // ─── Asignación de Atributos ──────────────────────────────────────────────────

    public void asignarPuntoAtributo(int indiceHeroe, String stat) {
        Party party = juego.getParty();
        if (indiceHeroe < 0 || indiceHeroe >= party.getMiembros().size()) return;
        Personaje p = party.getMiembros().get(indiceHeroe);
        switch (stat) {
            case "HP"  -> p.asignarPuntoVida();
            case "MP"  -> p.asignarPuntoMana();
            case "ATQ" -> p.asignarPuntoAtaque();
            case "DEF" -> p.asignarPuntoDefensa();
            case "VEL" -> p.asignarPuntoVelocidad();
        }
        abrirGestionParty();
    }

    // ─── Gestión de Equipo ────────────────────────────────────────────────────────

    public void equiparArma(int indiceHeroe, int indiceItemInventario) {
        Party party = juego.getParty();
        if (indiceHeroe < 0 || indiceHeroe >= party.getMiembros().size()) return;
        Personaje p = party.getMiembros().get(indiceHeroe);
        List<Item> items = party.getInventario().getItems();
        if (indiceItemInventario >= 0 && indiceItemInventario < items.size()) {
            Item it = items.get(indiceItemInventario);
            if (it instanceof Arma) {
                party.getInventario().equiparArma((Arma) it, p);
                abrirGestionParty();
            }
        }
    }

    public void desequiparArma(int indiceHeroe) {
        Party party = juego.getParty();
        if (indiceHeroe < 0 || indiceHeroe >= party.getMiembros().size()) return;
        party.getInventario().desequiparArma(party.getMiembros().get(indiceHeroe));
        abrirGestionParty();
    }

    public void equiparArmadura(int indiceHeroe, int indiceItemInventario) {
        Party party = juego.getParty();
        if (indiceHeroe < 0 || indiceHeroe >= party.getMiembros().size()) return;
        Personaje p = party.getMiembros().get(indiceHeroe);
        List<Item> items = party.getInventario().getItems();
        if (indiceItemInventario >= 0 && indiceItemInventario < items.size()) {
            Item it = items.get(indiceItemInventario);
            if (it instanceof Armadura) {
                party.getInventario().equiparArmadura((Armadura) it, p);
                abrirGestionParty();
            }
        }
    }

    public void desequiparArmadura(int indiceHeroe) {
        Party party = juego.getParty();
        if (indiceHeroe < 0 || indiceHeroe >= party.getMiembros().size()) return;
        party.getInventario().desequiparArmadura(party.getMiembros().get(indiceHeroe));
        abrirGestionParty();
    }

    // ─── Eventos y Fogón ──────────────────────────────────────────────────────────

    public String resolverEvento(int opcion) {
        return juego.resolverEventoActual(opcion);
    }

    public String descansarEnFogata(int opcion) {
        return juego.descansarEnFogata(opcion);
    }

    public Juego getJuego() {
        return juego;
    }
}
