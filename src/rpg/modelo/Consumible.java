package rpg.modelo;

public abstract class Consumible extends Item {

    private static final long serialVersionUID = 1L;

    public Consumible(String nombre, String descripcion, int precioCompra, int precioVenta) {
        super(nombre, descripcion, precioCompra, precioVenta);
    }

    public Consumible(String nombre, String descripcion) {
        super(nombre, descripcion, 25, 12);
    }

    public abstract void aplicar(Personaje objetivo);
}
