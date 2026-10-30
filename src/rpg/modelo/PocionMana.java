package rpg.modelo;

public class PocionMana extends Consumible {

    private static final long serialVersionUID = 1L;

    private int puntosMana;

    public PocionMana(String nombre, String descripcion, int puntosMana, int precioCompra, int precioVenta) {
        super(nombre, descripcion, precioCompra, precioVenta);
        this.puntosMana = puntosMana;
    }

    public PocionMana(String nombre, String descripcion, int puntosMana) {
        super(nombre, descripcion, 20, 10);
        this.puntosMana = puntosMana;
    }

    public int getPuntosMana() {
        return puntosMana;
    }

    @Override
    public void aplicar(Personaje objetivo) {
        objetivo.recuperarMana(this.puntosMana);
        RegistroCombate.registrar(objetivo.getNombre() + " tomó " + getNombre() + " y recargó su energía (+ " + this.puntosMana + " maná/ki).");
    }
}
