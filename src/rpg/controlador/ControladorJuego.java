package rpg.controlador;

import rpg.dto.*;
import rpg.modelo.*;
import rpg.vista.PantallaBatalla;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Principal del sistema MVC.
 * Coordina las transiciones entre pantallas (Inicio, Mapa, Combate, Resultados, Party, Evento, Descanso).
 * No accede directamente al modelo desde la vista; todo pasa por este controlador.
 */
public class ControladorJuego implements NodoVisitor {

    private final Juego juego;
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
        vista.mostrarPantalla("CREACION");
    }

    public void iniciarPartidaConParty(Party party) {
        juego.iniciarNuevaPartida(party);
        volverAlMapa();
    }

    public void cargarPartida() {
        Juego juegoGuardado = GestorPersistencia.cargarPartida();
        if (juegoGuardado != null) {
            // Redirigir al juego cargado
            ControladorJuego ctrlCargado = new ControladorJuego(juegoGuardado, vista);
            ctrlCargado.volverAlMapa();
        } else {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "No se encontró ninguna partida guardada o el archivo está corrupto.",
                    "Cargar partida", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
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
                    n.getConexiones()
            ));
        }

        // Sin metaprogresión: pasamos 0 como meta puntos (no existe entre runs)
        MapaDTO mapaDTO = new MapaDTO(mapa.getPiso(), mapa.getIdNodoActual(), nodosDTO);
        vista.actualizarMapa(mapaDTO, 0);
        vista.mostrarPantalla("MAPA");
    }

    /**
     * El jugador seleccionó un nodo del mapa.
     * Se bloquean las rutas alternativas y se transiciona polimórficamente a la vista correspondiente
     * mediante el patrón Visitor / Double Dispatch (sin condicionales ni comprobación de tipo).
     */
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

    // ─── Fin de combate ───────────────────────────────────────────────────────────

    /**
     * Procesa el fin del combate:
     * - Victoria: calcula recompensa con {@code calcularRecompensa()} → {@link RecompensaDTO}
     * - Derrota: permadeath (borra guardado)
     */
    public void procesarFinDeCombate(boolean victoria) {
        Batalla batalla = juego.getBatallaActual();

        if (victoria) {
            juego.completarNodoActual();
            RecompensaDTO recompensa = batalla.calcularRecompensa();
            EstadoBatallaDTO estadoDTO = batalla.crearEstadoDTO();
            vista.actualizarResultados(estadoDTO, recompensa);
        } else {
            // Permadeath: borrar el guardado
            GestorPersistencia.borrarPartidaPorPermadeath();
            EstadoBatallaDTO estadoDTO = batalla.crearEstadoDTO();
            vista.actualizarResultados(estadoDTO, null);
        }
        vista.mostrarPantalla("RESULTADOS");
    }

    // ─── Pantalla Party / Inventario ──────────────────────────────────────────────

    public void abrirGestionParty() {
        Party party = juego.getParty();
        List<PersonajeDTO> heroesDTO = construirHeroesDTO(party);

        List<ItemDTO> armasDTO    = construirArmasDTO(party);
        List<ItemDTO> armadurasDTO = construirArmadurasDTO(party);
        List<ItemDTO> consumiblesDTO = construirConsumiblesDTO(party);

        PartyGestionDTO dto = new PartyGestionDTO(heroesDTO, armasDTO, armadurasDTO, consumiblesDTO, 0);
        vista.actualizarGestionParty(dto);
        vista.mostrarPantalla("PARTY");
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
            lista.add(new ItemDTO(idx, a.getNombre(), "+" + a.getBonusAtaque() + " ATQ", false));
        }
        return lista;
    }

    private List<ItemDTO> construirArmadurasDTO(Party party) {
        List<ItemDTO> lista = new ArrayList<>();
        for (Armadura arm : party.getInventario().getArmaduras()) {
            int idx = party.getInventario().getItems().indexOf(arm);
            lista.add(new ItemDTO(idx, arm.getNombre(), "+" + arm.getBonusDefensa() + " DEF", false));
        }
        return lista;
    }

    private List<ItemDTO> construirConsumiblesDTO(Party party) {
        List<ItemDTO> lista = new ArrayList<>();
        for (Consumible c : party.getInventario().getConsumibles()) {
            int idx = party.getInventario().getItems().indexOf(c);
            lista.add(new ItemDTO(idx, c.getNombre(), c.getDescripcion(), true));
        }
        return lista;
    }

    // ─── Atributos ────────────────────────────────────────────────────────────────

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

    // ─── Equipo ───────────────────────────────────────────────────────────────────

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

    // ─── Eventos y Fogata ─────────────────────────────────────────────────────────

    public String resolverEvento(int opcion) {
        return juego.resolverEventoActual(opcion);
    }

    public String descansarEnFogata(int opcion) {
        return juego.descansarEnFogata(opcion);
    }

    // ─── Guardado ─────────────────────────────────────────────────────────────────

    public void guardarPartida() {
        boolean exito = GestorPersistencia.guardarPartida(juego);
        String msg = exito
                ? "✅ Partida guardada correctamente."
                : "❌ No se pudo guardar la partida.";
        javax.swing.JOptionPane.showMessageDialog(null, msg, "Guardar", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
