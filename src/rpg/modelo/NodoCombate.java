package rpg.modelo;

public class NodoCombate extends Nodo {

    private static final long serialVersionUID = 1L;

    public NodoCombate(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "⚔️"; }

    @Override
    public String getDescripcion() { return "Combate Estándar"; }

    @Override
    public void alVisitar(Juego juego) {
        Batalla b = new Batalla(getNombre(), juego.getParty());
        b.agregarEnemigo(new Goblin("El Duende Verde"));
        b.agregarEnemigo(new Chaman("Quan Chi"));
        b.calcularOrdenTurnos();
        juego.setBatallaActual(b);
        juego.setEventoActual(null);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarCombate(this);
    }
}
