package rpg.modelo;

import rpg.dto.EntidadDTO;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.ItemDTO;
import rpg.dto.PersonajeDTO;
import rpg.dto.RecompensaDTO;

import java.io.Serializable;
import java.util.*;

/**
 * Modelo de Batalla por turnos (MVC).
 * El ciclo de batalla se ejecuta en un bucle que verifica la continuidad del combate
 * llamando a {@link #verificarFin()} al final de cada turno.
 */
public class Batalla implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String lugar;
    private final Party party;
    private final List<Enemigo> enemigos;
    private int ronda;
    private boolean esCombateElite;

    private List<Integer> colaIniciativa;
    private int indiceTurnoActual;

    private boolean batallaTerminada;
    private boolean victoria;

    private static final int OFFSET_ENEMIGO = 1000;

    public Batalla(String lugar, Party party) {
        this.lugar = lugar;
        this.party = party;
        this.enemigos = new ArrayList<>();
        this.ronda = 1;
        this.colaIniciativa = new ArrayList<>();
        this.indiceTurnoActual = 0;
        this.batallaTerminada = false;
        this.victoria = false;
        this.esCombateElite = false;
    }

    public void setEsCombateElite(boolean elite) {
        this.esCombateElite = elite;
    }

    public boolean isEsCombateElite() {
        return esCombateElite;
    }

    public void iniciar() {
        calcularOrdenTurnos();
    }

    public boolean huboVictoria() {
        return isVictoria();
    }

    public String getLugar()             { return lugar; }
    public Party getParty()              { return party; }
    public List<Enemigo> getEnemigos()   { return enemigos; }
    public int getRonda()                { return ronda; }
    public boolean isVictoria() {
        if (!batallaTerminada) verificarFin();
        return victoria;
    }

    public void agregarEnemigo(Enemigo e) { enemigos.add(e); }

    public boolean verificarFin() {
        if (!batallaTerminada) {
            if (enemigosDerrotados()) {
                finalizarBatalla(true);
            } else if (party.estaDerrotada()) {
                finalizarBatalla(false);
            }
        }
        return batallaTerminada;
    }

    public boolean esFinDeBatalla() { return verificarFin(); }

    public boolean enemigosDerrotados() {
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) return false;
        }
        return true;
    }

    public Enemigo obtenerPrimerEnemigoVivo() {
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) return e;
        }
        return null;
    }

    public void calcularOrdenTurnos() {
        List<int[]> participantes = new ArrayList<>();

        List<Personaje> miembros = party.getMiembros();
        for (int i = 0; i < miembros.size(); i++) {
            if (miembros.get(i).estaVivo()) {
                participantes.add(new int[]{miembros.get(i).getVelocidad(), i});
            }
        }
        for (int i = 0; i < enemigos.size(); i++) {
            if (enemigos.get(i).estaVivo()) {
                participantes.add(new int[]{enemigos.get(i).getVelocidad(), i + OFFSET_ENEMIGO});
            }
        }

        participantes.sort((a, b) -> {
            if (b[0] != a[0]) return Integer.compare(b[0], a[0]);
            return Math.random() < 0.5 ? -1 : 1;
        });

        colaIniciativa = new ArrayList<>();
        for (int[] p : participantes) colaIniciativa.add(p[1]);
        indiceTurnoActual = 0;

        RegistroCombate.registrar("\n==========================================");
        RegistroCombate.registrar("               RONDA " + ronda);
        StringBuilder orden = new StringBuilder("Orden de iniciativa: ");
        for (int id : colaIniciativa) {
            if (id < OFFSET_ENEMIGO) orden.append(party.getMiembros().get(id).getNombre()).append(" | ");
            else orden.append(enemigos.get(id - OFFSET_ENEMIGO).getNombre()).append(" | ");
        }
        RegistroCombate.registrar(orden.toString());
        RegistroCombate.registrar("==========================================");
    }

    public void procesarTurno(Combatiente actor) {
        if (actor instanceof Enemigo enemigo) {
            if (enemigo.estaVivo() && !party.estaDerrotada()) {
                enemigo.resetearDefensaTemporal();
                RegistroCombate.registrar("\n[Turno de " + enemigo.getNombre() + "]");
                enemigo.ejecutarTurno(party, enemigos);
                if (party.estaDerrotada()) finalizarBatalla(false);
                else if (enemigosDerrotados()) finalizarBatalla(true);
            }
        }
    }

    public int getIndiceHeroeTurno() {
        avanzarHastaTurnoValido();
        if (colaIniciativa.isEmpty()) return 0;
        int ficha = colaIniciativa.get(indiceTurnoActual);
        return ficha < OFFSET_ENEMIGO ? ficha : -1;
    }

    public Personaje obtenerHeroeTurnoActual() {
        int idx = getIndiceHeroeTurno();
        if (idx >= 0 && idx < party.getMiembros().size()) {
            return party.getMiembros().get(idx);
        }
        return null;
    }

    public List<String> getColaIniciativaNombres() {
        List<String> nombres = new ArrayList<>();
        for (int id : colaIniciativa) {
            if (id < OFFSET_ENEMIGO) {
                Personaje p = party.getMiembros().get(id);
                nombres.add(p.getNombre() + "|heroe|" + p.getVelocidad());
            } else {
                Enemigo e = enemigos.get(id - OFFSET_ENEMIGO);
                nombres.add(e.getNombre() + "|enemigo|" + e.getVelocidad());
            }
        }
        return nombres;
    }

    public int getPosicionActualEnCola() { return indiceTurnoActual; }

    private void avanzarHastaTurnoValido() {
        while (!batallaTerminada && !colaIniciativa.isEmpty()) {
            if (indiceTurnoActual >= colaIniciativa.size()) {
                ronda++;
                for (Personaje p : party.getMiembros()) p.resetearDefensaTemporal();
                for (Enemigo e : enemigos) e.resetearDefensaTemporal();
                calcularOrdenTurnos();
                continue;
            }

            int ficha = colaIniciativa.get(indiceTurnoActual);

            if (ficha < OFFSET_ENEMIGO) {
                Personaje heroe = party.getMiembros().get(ficha);
                if (!heroe.estaVivo()) { indiceTurnoActual++; continue; }
                boolean turnoConsumido = aplicarEfectosEstadoAlInicio(heroe);
                if (turnoConsumido) { indiceTurnoActual++; continue; }
                return;
            } else {
                int idxEnemigo = ficha - OFFSET_ENEMIGO;
                Enemigo enemigo = enemigos.get(idxEnemigo);
                procesarTurno(enemigo);
                indiceTurnoActual++;
            }
        }
    }

    private boolean aplicarEfectosEstadoAlInicio(Personaje h) {
        if (!h.estaVivo()) return true;

        if (h.isEnvenenado()) {
            int danioVeneno = Math.max(6, (int) (h.getVidaMaxima() * 0.08));
            h.curarVida(-danioVeneno);
            RegistroCombate.registrar("[VENENO] El veneno corroe a " + h.getNombre()
                    + " restando " + danioVeneno + " HP. (Vida: " + h.getVida() + "/" + h.getVidaMaxima() + ")");
            if (!h.estaVivo()) {
                RegistroCombate.registrar("¡" + h.getNombre() + " sucumbió ante el veneno mortal!");
                if (enemigosDerrotados()) finalizarBatalla(true);
                else if (party.estaDerrotada()) finalizarBatalla(false);
                return true;
            }
        }

        if (h.isAturdido()) {
            RegistroCombate.registrar("[ATURDIDO] ¡" + h.getNombre() + " está aturdido y pierde su turno!");
            h.setAturdido(false);
            return true;
        }

        return false;
    }

    public void ejecutarAtaqueHeroe(int indiceHeroe, int indiceObjetivo) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        Enemigo objetivo = obtenerEnemigoValido(indiceObjetivo);
        if (objetivo == null) return;

        heroe.resetearDefensaTemporal();
        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");
        heroe.atacar(objetivo);
        postAccionHeroe();
    }

    public void ejecutarHabilidadHeroe(int indiceHeroe, int indiceHabilidad, int indiceObjetivo) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        heroe.resetearDefensaTemporal();
        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");

        List<Combatiente> objetivos = construirObjetivos(heroe, indiceHabilidad, indiceObjetivo);
        boolean exito = heroe.ejecutarHabilidad(indiceHabilidad, objetivos, this);
        if (!exito) return;

        postAccionHeroe();
    }

    /**
     * Construcción de objetivos polimórfica basada estrictamente en {@link TipoObjetivo}.
     * Elimina comprobaciones por nombre de habilidad o tipos de clase (instanceof).
     */
    private List<Combatiente> construirObjetivos(Personaje heroe, int indiceHabilidad, int indiceObjetivo) {
        List<Habilidad> habs = heroe.getHabilidades();
        if (habs == null || indiceHabilidad < 0 || indiceHabilidad >= habs.size()) {
            return Collections.emptyList();
        }

        Habilidad hab = habs.get(indiceHabilidad);
        TipoObjetivo tipo = hab.getTipoObjetivo();

        List<Combatiente> resultado = new ArrayList<>();

        switch (tipo) {
            case ENEMIGOS_TODOS -> {
                for (Enemigo e : enemigos) {
                    if (e.estaVivo()) resultado.add(e);
                }
            }
            case ALIADOS_TODOS -> {
                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) resultado.add(p);
                }
            }
            case ALIADO_MAS_HERIDO -> {
                Personaje masHerido = party.obtenerMiembroMasHerido();
                resultado.add(masHerido != null ? masHerido : heroe);
            }
            case PROPIO -> {
                resultado.add(heroe);
            }
            case ALIADO_INDIVIDUAL -> {
                Personaje al = obtenerAliadoValido(indiceObjetivo);
                if (al != null) resultado.add(al);
                else resultado.add(heroe);
            }
            case ENEMIGO_INDIVIDUAL -> {
                Enemigo en = obtenerEnemigoValido(indiceObjetivo);
                if (en != null) resultado.add(en);
            }
        }

        return resultado;
    }

    public void defenderHeroe(int indiceHeroe) {
        ejecutarDefensaHeroe(indiceHeroe);
    }

    public void usarItemHeroe(int indiceHeroe, int indiceItem) {
        ejecutarUsoItemHeroe(indiceHeroe, indiceItem, indiceHeroe);
    }

    public void ejecutarDefensaHeroe(int indiceHeroe) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");
        heroe.defender();
        postAccionHeroe();
    }

    public void ejecutarUsoItemHeroe(int indiceHeroe, int indiceItem, int indiceObjetivo) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        List<Item> items = party.getInventario().getItems();
        if (indiceItem < 0 || indiceItem >= items.size()) return;

        Item item = items.get(indiceItem);
        if (item instanceof Consumible consumible) {
            Personaje objetivo = obtenerAliadoValido(indiceObjetivo);
            if (objetivo == null) objetivo = heroe;

            heroe.resetearDefensaTemporal();
            RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");
            party.getInventario().consumirItem(consumible, objetivo);
            postAccionHeroe();
        }
    }

    private void postAccionHeroe() {
        if (enemigosDerrotados()) {
            finalizarBatalla(true);
            return;
        }
        if (party.estaDerrotada()) {
            finalizarBatalla(false);
            return;
        }

        indiceTurnoActual++;
        avanzarHastaTurnoValido();
    }

    private void finalizarBatalla(boolean ganoElJugador) {
        this.batallaTerminada = true;
        this.victoria = ganoElJugador;

        RegistroCombate.registrar("\n==========================================");
        if (victoria) {
            RegistroCombate.registrar("¡Victoria gloriosa! Los enemigos han sido derrotados en " + lugar + ".");
        } else {
            RegistroCombate.registrar("¡La Party ha sido aniquilada en " + lugar + "! Fin de la partida (Permadeath).");
        }
        RegistroCombate.registrar("==========================================");
    }

    /**
     * Calcula la recompensa al ganar:
     * - Reparte XP entre los supervivientes.
     * - Acredita oro a la party.
     * - Otorga botín de armas, armaduras o pociones con probabilidad balanceada.
     */
    public RecompensaDTO calcularRecompensa() {
        int xpTotal = 0;
        int oroTotal = 0;
        List<String> itemsObtenidos = new ArrayList<>();
        List<String> supervivientesNombres = new ArrayList<>();

        for (Enemigo e : enemigos) {
            xpTotal += e.getExperienciaOtorgada();
            oroTotal += e.getOroOtorgado();
        }

        if (esCombateElite) {
            xpTotal = (int) (xpTotal * 1.4);
            oroTotal = (int) (oroTotal * 1.5);
        }

        // Repartir XP
        int supervivientes = 0;
        for (Personaje p : party.getMiembros()) {
            if (p.estaVivo()) { supervivientes++; supervivientesNombres.add(p.getNombre()); }
        }

        int xpPorHeroe = supervivientes > 0 ? xpTotal / supervivientes : 0;

        RegistroCombate.registrar("\n--- RECOMPENSA DE BATALLA ---");
        RegistroCombate.registrar("Se reparten " + xpTotal + " XP en total ("
                + xpPorHeroe + " XP por héroe superviviente).");

        for (Personaje p : party.getMiembros()) {
            if (p.estaVivo()) p.ganarExperiencia(xpPorHeroe);
        }

        // Acreditar Oro a la Party
        party.ganarOro(oroTotal);

        // Generar Botín de armas / armaduras / pociones
        Random rnd = new Random();

        if (esCombateElite) {
            // Combate Élite: Drop garantizado de alta calidad
            if (rnd.nextBoolean()) {
                Arma machete = new Arma("Zarpado machete", "Hoja afilada de gran filo (+16 ATQ)", 16, 90, 45);
                party.getInventario().agregarItem(machete);
                itemsObtenidos.add(machete.getNombre() + " (+16 ATQ)");
            } else {
                Armadura kevlar = new Armadura("Chaleco kevlar", "Protección táctica reforzada (+14 DEF)", 14, 85, 40);
                party.getInventario().agregarItem(kevlar);
                itemsObtenidos.add(kevlar.getNombre() + " (+14 DEF)");
            }
            // Y una poción
            PocionVida pocion = new PocionVida("Poción de Vida Mayor", "Restaura 80 HP", 80, 40, 20);
            party.getInventario().agregarItem(pocion);
            itemsObtenidos.add(pocion.getNombre());
        } else {
            // Combate normal: 60% chance de drop
            if (rnd.nextInt(100) < 60) {
                int roll = rnd.nextInt(100);
                if (roll < 20) {
                    Arma machete = new Arma("Zarpado machete", "Hoja afilada de gran filo (+16 ATQ)", 16, 90, 45);
                    party.getInventario().agregarItem(machete);
                    itemsObtenidos.add(machete.getNombre() + " (+16 ATQ)");
                } else if (roll < 40) {
                    Armadura kevlar = new Armadura("Chaleco kevlar", "Protección táctica reforzada (+14 DEF)", 14, 85, 40);
                    party.getInventario().agregarItem(kevlar);
                    itemsObtenidos.add(kevlar.getNombre() + " (+14 DEF)");
                } else if (roll < 70) {
                    PocionVida pv = new PocionVida("Poción de Vida", "Restaura 50 HP", 50, 25, 12);
                    party.getInventario().agregarItem(pv);
                    itemsObtenidos.add(pv.getNombre());
                } else {
                    PocionMana pm = new PocionMana("Poción de Maná", "Restaura 40 MP", 40, 20, 10);
                    party.getInventario().agregarItem(pm);
                    itemsObtenidos.add(pm.getNombre());
                }
            }
        }

        return new RecompensaDTO(xpTotal, xpPorHeroe, oroTotal, itemsObtenidos, supervivientesNombres);
    }

    private Personaje obtenerHeroeValido(int indice) {
        List<Personaje> miembros = party.getMiembros();
        int turnoIdx = getIndiceHeroeTurno();
        if (indice >= 0 && indice < miembros.size()) {
            Personaje p = miembros.get(indice);
            if (p.estaVivo() && indice == turnoIdx) return p;
        }
        Personaje heroeTurno = (turnoIdx >= 0 && turnoIdx < miembros.size())
                ? miembros.get(turnoIdx) : null;
        if (heroeTurno != null && heroeTurno.estaVivo()) {
            return heroeTurno;
        }
        return null;
    }

    private Enemigo obtenerEnemigoValido(int indice) {
        if (indice >= 0 && indice < enemigos.size()) {
            Enemigo e = enemigos.get(indice);
            if (e.estaVivo()) return e;
        }
        return obtenerPrimerEnemigoVivo();
    }

    private Personaje obtenerAliadoValido(int indice) {
        List<Personaje> miembros = party.getMiembros();
        if (indice >= 0 && indice < miembros.size()) return miembros.get(indice);
        return null;
    }

    public EstadoBatallaDTO crearEstadoDTO() {
        if (!batallaTerminada) avanzarHastaTurnoValido();

        List<PersonajeDTO> heroesDTO = new ArrayList<>();
        for (Personaje p : party.getMiembros()) {
            String armaNom = (p.getArma() != null) ? p.getArma().getNombre() : "Ninguna";
            String armaduraNom = (p.getArmadura() != null) ? p.getArmadura().getNombre() : "Ninguna";
            heroesDTO.add(new PersonajeDTO(
                    p.getNombre(), p.getTipo(), p.getVida(), p.getVidaMaxima(),
                    p.getMana(), p.getManaMaximo(), p.getNivel(), p.getAtaque(),
                    p.getDefensa(), p.getVelocidad(), p.estaVivo(), p.isDefendiendo(),
                    p.getRutaSprite(), p.getExperiencia(), p.getExperienciaSiguienteNivel(),
                    armaNom, armaduraNom, p.getPuntosAtributoDisponibles(), p.getNombresHabilidades()
            ));
        }

        List<EntidadDTO> enemigosDTO = new ArrayList<>();
        for (Enemigo e : enemigos) {
            enemigosDTO.add(new EntidadDTO(
                    e.getNombre(), e.getTipo(), e.getVida(), e.getVidaMaxima(),
                    0, 0, 1, e.getAtaque(), e.getDefensa(), e.getVelocidad(),
                    e.estaVivo(), e.isDefendiendo(), e.getRutaSprite(), false
            ));
        }

        List<ItemDTO> inventarioDTO = new ArrayList<>();
        List<Item> items = party.getInventario().getItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            inventarioDTO.add(new ItemDTO(i, item.getNombre(), item.getDescripcion(), item instanceof Consumible));
        }

        String turnoStr;
        int indiceHeroeActivo;
        if (batallaTerminada) {
            turnoStr = victoria ? "¡VICTORIA!" : "¡DERROTA!";
            indiceHeroeActivo = 0;
        } else {
            int idx = getIndiceHeroeTurno();
            if (idx >= 0) {
                Personaje activo = party.getMiembros().get(idx);
                turnoStr = "Turno de: " + activo.getNombre() + " (VEL " + activo.getVelocidad() + ")";
                indiceHeroeActivo = idx;
            } else {
                turnoStr = "Procesando turno de enemigos...";
                indiceHeroeActivo = 0;
            }
        }

        String mensajeFin = "";
        if (batallaTerminada) {
            mensajeFin = victoria
                    ? "¡VICTORIA! Todos los enemigos han sido derrotados."
                    : "¡DERROTA! Toda la party ha sido aniquilada (Permadeath).";
        }

        return new EstadoBatallaDTO(
                heroesDTO, enemigosDTO, turnoStr, indiceHeroeActivo,
                inventarioDTO, RegistroCombate.getHistorial(),
                batallaTerminada, victoria, mensajeFin, lugar, ronda,
                getColaIniciativaNombres(), indiceTurnoActual
        );
    }
}
