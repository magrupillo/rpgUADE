package rpg.modelo;

public class NodoKiosco extends Nodo {

    private static final long serialVersionUID = 1L;

    public NodoKiosco(int id, int columna, String nombre) {
        super(id, columna, nombre);
    }

    @Override
    public String getIcono() { return "🏪"; }

    @Override
    public String getDescripcion() { return "Kiosco de Don Tito (Comprar y Vender)"; }

    @Override
    public void alVisitar(Juego juego) {
        juego.setBatallaActual(null);
        juego.setEventoActual(null);
        RegistroCombate.registrar("La party llega al Kiosco de Don Tito: abran paso que hay provisiones, armas y trueques!");
    }

    @Override
    public void aceptar(NodoVisitor visitor) {
        visitor.visitarKiosco(this);
    }
}
