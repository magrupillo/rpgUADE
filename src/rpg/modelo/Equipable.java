package rpg.modelo;

public abstract class Equipable extends Item {

    private static final long serialVersionUID = 1L;

    public Equipable(String nombre, String descripcion, int precioCompra, int precioVenta) {
        super(nombre, descripcion, precioCompra, precioVenta);
    }

    public Equipable(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    public abstract TipoEquipo getTipoEquipo();
}
