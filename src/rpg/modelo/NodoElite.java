package rpg.modelo;

public class NodoElite extends Nodo {

    private static final long serialVersionUID = 1L;

    public NodoElite(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "💀"; }

    @Override
    public String getDescripcion() { return "Combate Élite (Alto Riesgo / Botín Superior)"; }

    @Override
    public void alVisitar(Juego juego) {
        Batalla b = new Batalla(getNombre(), juego.getParty());
        b.setEsCombateElite(true);
        b.agregarEnemigo(new Orco("Cocodrilo Asesino"));
        b.agregarEnemigo(new Esqueleto("Skeletor"));
        b.calcularOrdenTurnos();
        juego.setBatallaActual(b);
        juego.setEventoActual(null);
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarCombate(this);
    }
}
