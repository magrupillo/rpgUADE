package rpg.modelo;

import java.io.Serializable;

/**
 * Coordina el flujo global del juego y delega el guardado/carga al {@link GestorPersistencia}.
 * La ascensión consta de 3 pisos para alcanzar la victoria final sobre Bowser.
 */
public class Juego implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TOTAL_PISOS = 3;

    private Party party;
    private Mapa mapa;
    private Batalla batallaActual;
    private EventoAleatorio eventoActual;
    private int piso;
    private int pisoMaximoDesbloqueado;

    // Estadísticas para el resumen de la run
    private int combatesGanados;
    private int totalOroAcumulado;
    private boolean juegoCompletadoVictoria;

    public Juego() {
        this.piso = 1;
        this.pisoMaximoDesbloqueado = 1;
        this.combatesGanados = 0;
        this.totalOroAcumulado = 0;
        this.juegoCompletadoVictoria = false;
    }

    public void iniciarNuevaPartida() {
        iniciarNuevaPartida(null);
    }

    public void iniciarNuevaPartida(Party party) {
        if (party == null) {
            party = crearPartyPredeterminada();
        }
        this.party = party;
        this.piso = 1;
        this.pisoMaximoDesbloqueado = 1;
        this.mapa = new Mapa(piso);
        this.batallaActual = null;
        this.eventoActual = null;
        this.combatesGanados = 0;
        this.totalOroAcumulado = party.getOro();
        this.juegoCompletadoVictoria = false;

        RegistroCombate.limpiar();
        RegistroCombate.registrar("¡Comienza la ascensión en 'El Último Ascenso'!");
        RegistroCombate.registrar("Piso 1: Base de la Torre | Equipo listo.");
    }

    public static Party crearPartyPredeterminada() {
        Party p = new Party();
        p.agregarMiembro(new Guerrero("Kratos"));
        p.agregarMiembro(new Arquero("Tommy Shelby"));
        p.agregarMiembro(new Curador("El Guasón"));
        p.agregarMiembro(new Mago("Goku"));

        // Pociones iniciales en el inventario
        p.getInventario().agregarItem(new PocionVida("Poción de Vida", "Restaura 50 HP", 50, 25, 12));
        p.getInventario().agregarItem(new PocionMana("Poción de Maná", "Restaura 40 MP", 40, 20, 10));

        return p;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public Party getParty()                  { return party; }
    public Mapa getMapa()                    { return mapa; }
    public Batalla getBatallaActual()        { return batallaActual; }
    public EventoAleatorio getEventoActual() { return eventoActual; }
    public int getPiso()                     { return piso; }
    public int getPisoActual()               { return piso; }
    public int getPisoMaximoDesbloqueado()   { return pisoMaximoDesbloqueado; }
    public int getCombatesGanados()          { return combatesGanados; }
    public int getTotalOroAcumulado()        { return totalOroAcumulado; }
    public boolean isJuegoCompletadoVictoria(){ return juegoCompletadoVictoria; }

    public void avanzarPiso(int piso)        { entrarPiso(piso); }

    public void setBatallaActual(Batalla batallaActual) { this.batallaActual = batallaActual; }
    public void setEventoActual(EventoAleatorio eventoActual) { this.eventoActual = eventoActual; }

    public void registrarCombateGanado(int oroGanado) {
        this.combatesGanados++;
        this.totalOroAcumulado += oroGanado;
    }

    // ─── Selección de nodo ───────────────────────────────────────────────────────

    public boolean seleccionarNodo(int idNodo) {
        if (mapa == null) return false;
        boolean exito = mapa.avanzarANodo(idNodo);
        if (!exito) return false;

        Nodo nodo = mapa.getNodoActual();
        if (nodo != null) {
            nodo.alVisitar(this);
        }
        return true;
    }

    public void completarNodoActual() {
        if (mapa == null) return;
        mapa.completarNodoActual();

        if (mapa.esJefePisoCompletado()) {
            if (piso >= TOTAL_PISOS) {
                this.juegoCompletadoVictoria = true;
                RegistroCombate.registrar("\n==========================================");
                RegistroCombate.registrar("¡¡HAS DERROTADO A BOWSER Y CONQUISTADO LA TORRE!!");
                RegistroCombate.registrar("==========================================\n");
            } else {
                pisoMaximoDesbloqueado = Math.max(pisoMaximoDesbloqueado, piso + 1);
            }
        }
    }

    public void entrarPiso(int nuevoPiso) {
        if (nuevoPiso <= pisoMaximoDesbloqueado && nuevoPiso <= TOTAL_PISOS) {
            this.piso = nuevoPiso;
            this.mapa = new Mapa(piso);
            RegistroCombate.registrar("\nEntrando al Piso " + piso + " de la Torre...");
        }
    }

    // ─── Kiosco: Compra y Venta ──────────────────────────────────────────────────

    public boolean comprarItem(Item item) {
        if (party == null || item == null) return false;
        if (party.getOro() >= item.getPrecioCompra()) {
            boolean agregado = party.getInventario().agregarItem(item);
            if (agregado) {
                party.gastarOro(item.getPrecioCompra());
                RegistroCombate.registrar("Compraste " + item.getNombre() + " por $" + item.getPrecioCompra());
                return true;
            }
        }
        return false;
    }

    public boolean venderItem(Item item) {
        if (party == null || item == null) return false;
        boolean removido = party.getInventario().removerItem(item);
        if (removido) {
            party.ganarOro(item.getPrecioVenta());
            RegistroCombate.registrar("Vendiste " + item.getNombre() + " por $" + item.getPrecioVenta());
            return true;
        }
        return false;
    }

    // ─── Acciones de Nodos Especiales ────────────────────────────────────────────

    public String descansarEnFogata(int opcion) {
        if (opcion == 1) {
            party.recuperarPorcentaje(0.45);
            String msg = "La party descansa junto al Fogón y recupera el 45% de su Vida y Maná.";
            RegistroCombate.registrar(msg);
            completarNodoActual();
            return msg;
        } else {
            party.repartirExperiencia(140);
            String msg = "Los héroes entrenan tácticas de combate y adquieren valiosa experiencia (+140 XP).";
            RegistroCombate.registrar(msg);
            completarNodoActual();
            return msg;
        }
    }

    public String resolverEventoActual(int opcion) {
        if (eventoActual != null) {
            String resultado = eventoActual.resolverOpcion(opcion, party);
            completarNodoActual();
            return resultado;
        }
        return "";
    }
}
