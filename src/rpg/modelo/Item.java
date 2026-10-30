package rpg.modelo;

import java.io.Serializable;

public abstract class Item implements Serializable {
    private String nombre;
    private String descripcion;

    public Item(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre + " (" + descripcion + ")";
    }
}
