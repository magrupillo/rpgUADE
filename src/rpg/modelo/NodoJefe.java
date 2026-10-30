package rpg.modelo;

/**
 * Nodo de jefe de piso. Último nodo de cada piso.
 * Al ser visitado, genera una batalla contra el Jefe.
 */
public class NodoJefe extends Nodo {

    public NodoJefe(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "🐉"; }

    @Override
    public String getDescripcion() { return "Jefe de Piso"; }

    @Override
    public void alVisitar(Juego juego) {
        Batalla b = new Batalla(getNombre(), juego.getParty());
        String nombreJefe = juego.getPiso() > 1 ? "Rey Demonio" : "Dragón Carmesí";
        b.agregarEnemigo(new Jefe(nombreJefe));
        b.calcularOrdenTurnos();
        juego.setBatallaActual(b);
        juego.setEventoActual(null);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarCombate(this);
    }
}
