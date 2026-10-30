package rpg.modelo;

import java.io.Serializable;

public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String descripcion;
    private int precioCompra;
    private int precioVenta;

    public Item(String nombre, String descripcion, int precioCompra, int precioVenta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
    }

    public Item(String nombre, String descripcion) {
        this(nombre, descripcion, 30, 15);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecioCompra() {
        return precioCompra;
    }

    public int getPrecioVenta() {
        return precioVenta;
    }

    @Override
    public String toString() {
        return nombre + " (" + descripcion + ")";
    }
}
