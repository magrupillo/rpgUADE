package rpg.modelo;

public class NodoFogata extends Nodo {

    private static final long serialVersionUID = 1L;

    public NodoFogata(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "🔥"; }

    @Override
    public String getDescripcion() { return "Fogón / Descanso Criollo"; }

    @Override
    public void alVisitar(Juego juego) {
        juego.setBatallaActual(null);
        juego.setEventoActual(null);
        RegistroCombate.registrar("La party se sienta junto al Fogón a descansar y calentar agua para los mates.");
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarFogata(this);
    }
}
