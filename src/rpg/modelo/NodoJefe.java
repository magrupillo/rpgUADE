package rpg.modelo;

public class NodoJefe extends Nodo {

    private static final long serialVersionUID = 1L;

    public NodoJefe(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public boolean esJefe() { return true; }

    @Override
    public String getIcono() { return "👑"; }

    @Override
    public String getDescripcion() { return "Jefe de Piso"; }

    @Override
    public void alVisitar(Juego juego) {
        Batalla b = new Batalla(getNombre(), juego.getParty());
        b.setEsCombateElite(true);
        b.agregarEnemigo(Jefe.crearJefeParaPiso(juego.getPiso()));
        b.calcularOrdenTurnos();
        juego.setBatallaActual(b);
        juego.setEventoActual(null);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarCombate(this);
    }
}
