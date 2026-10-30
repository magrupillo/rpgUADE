package rpg.modelo;

import java.io.Serializable;

/**
 * Coordina el flujo global del juego y delega el guardado/carga al {@link GestorPersistencia}.
 *
 * No existe metaprogresión: cada run es independiente.
 * Si todos los personajes mueren se produce permadeath, perdiendo todo el progreso.
 */
public class Juego implements Serializable {

    private Party party;
    private Mapa mapa;
    private Batalla batallaActual;
    private EventoAleatorio eventoActual;
    private int piso;

    public Juego() {
        this.piso = 1;
    }

    public void iniciarNuevaPartida(Party party) {
        this.party = party;
        this.piso = 1;
        this.mapa = new Mapa(piso);
        this.batallaActual = null;
        this.eventoActual = null;
        RegistroCombate.limpiar();
        RegistroCombate.registrar("🏰 ¡Comienza una nueva expedición en la Torre Roguelike!");
        RegistroCombate.registrar("Piso actual: " + piso + " | Equipo listo para la ascensión.");
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public Party getParty()                  { return party; }
    public Mapa getMapa()                    { return mapa; }
    public Batalla getBatallaActual()        { return batallaActual; }
    public EventoAleatorio getEventoActual() { return eventoActual; }
    public int getPiso()                     { return piso; }

    public void setBatallaActual(Batalla batallaActual) { this.batallaActual = batallaActual; }
    public void setEventoActual(EventoAleatorio eventoActual) { this.eventoActual = eventoActual; }

    // ─── Selección de nodo (polimorfismo sin TipoNodo ni condicionales) ───────────

    /**
     * Selecciona el nodo destino en el mapa y delega la ejecución de su comportamiento
     * de manera estrictamente polimórfica llamando a {@link Nodo#alVisitar(Juego)}.
     *
     * @return {@code true} si el avance fue exitoso
     */
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

    // ─── Completar nodo ───────────────────────────────────────────────────────────

    public void completarNodoActual() {
        if (mapa == null) return;
        mapa.completarNodoActual();
        if (mapa.esJefePisoCompletado()) {
            piso++;
            mapa.siguientePiso();
        }
    }

    // ─── Acciones de nodos especiales ────────────────────────────────────────────

    public String descansarEnFogata(int opcion) {
        if (opcion == 1) {
            party.recuperarPorcentaje(0.40);
            String msg = "🔥 La party descansa junto al fuego y recupera el 40% de su Vida y Maná.";
            RegistroCombate.registrar(msg);
            completarNodoActual();
            return msg;
        } else {
            party.repartirExperiencia(120);
            String msg = "⚔️ Los héroes entrenan sus tácticas de combate y adquieren valiosa experiencia.";
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
