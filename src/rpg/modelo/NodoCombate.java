package rpg.modelo;

/**
 * Nodo de combate estándar contra enemigos comunes.
 * Al ser visitado, genera una batalla con Goblin y Chamán.
 */
public class NodoCombate extends Nodo {

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
        b.agregarEnemigo(new Goblin("Goblin Pícaro"));
        b.agregarEnemigo(new Chaman("Chamán del Pantano"));
        b.calcularOrdenTurnos();
        juego.setBatallaActual(b);
        juego.setEventoActual(null);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarCombate(this);
    }
}
