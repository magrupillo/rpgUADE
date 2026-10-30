package rpg.modelo;

/**
 * Nodo de fogata / descanso.
 * Al ser visitado, permite a la party recuperar vida, maná o ganar experiencia.
 */
public class NodoFogata extends Nodo {

    public NodoFogata(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "🔥"; }

    @Override
    public String getDescripcion() { return "Fogata / Descanso"; }

    @Override
    public void alVisitar(Juego juego) {
        juego.setBatallaActual(null);
        juego.setEventoActual(null);
        RegistroCombate.registrar("🔥 La party encuentra un remanso de paz en la Fogata del refugio.");
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarFogata(this);
    }
}
