package rpg.modelo;

/**
 * Nodo de evento aleatorio / narrativo.
 * Al ser visitado, presenta al jugador una elección con consecuencias.
 */
public class NodoEvento extends Nodo {

    private static final long serialVersionUID = 1L;

    private final EventoAleatorio evento;

    public NodoEvento(int id, int columna, String nombre, EventoAleatorio evento) {
        super(id, columna, nombre);
        this.evento = (evento != null) ? evento : EventoAleatorio.crearAleatorio();
    }

    public NodoEvento(int id, int columna, String nombre) {
        this(id, columna, nombre, EventoAleatorio.crearAleatorio());
    }

    public EventoAleatorio getEvento() { return evento; }

    @Override
    public String getIcono() { return "📜"; }

    @Override
    public String getDescripcion() { return "Evento Misterioso"; }

    @Override
    public void alVisitar(Juego juego) {
        juego.setBatallaActual(null);
        juego.setEventoActual(this.evento);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarEvento(this);
    }
}
