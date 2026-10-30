package rpg.modelo;

public class PocionVida extends Consumible {

    private static final long serialVersionUID = 1L;

    private int puntosCuracion;

    public PocionVida(String nombre, String descripcion, int puntosCuracion, int precioCompra, int precioVenta) {
        super(nombre, descripcion, precioCompra, precioVenta);
        this.puntosCuracion = puntosCuracion;
    }

    public PocionVida(String nombre, String descripcion, int puntosCuracion) {
        super(nombre, descripcion, 25, 12);
        this.puntosCuracion = puntosCuracion;
    }

    public int getPuntosCuracion() {
        return puntosCuracion;
    }

    @Override
    public void aplicar(Personaje objetivo) {
        objetivo.curarVida(this.puntosCuracion);
        RegistroCombate.registrar(objetivo.getNombre() + " tomó " + getNombre() + " y recuperó " + this.puntosCuracion + " puntos de vida.");
    }
}
