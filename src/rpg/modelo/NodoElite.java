package rpg.modelo;

/**
 * Nodo de combate élite contra enemigos más fuertes.
 * Al ser visitado, genera una batalla contra Orco y Esqueleto.
 */
public class NodoElite extends Nodo {

    public NodoElite(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "💀"; }

    @Override
    public String getDescripcion() { return "Combate Élite"; }

    @Override
    public void alVisitar(Juego juego) {
        Batalla b = new Batalla(getNombre(), juego.getParty());
        b.agregarEnemigo(new Orco("Orco Berserker"));
        b.agregarEnemigo(new Esqueleto("Esqueleto Guardián"));
        b.calcularOrdenTurnos();
        juego.setBatallaActual(b);
        juego.setEventoActual(null);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarCombate(this);
    }
}
