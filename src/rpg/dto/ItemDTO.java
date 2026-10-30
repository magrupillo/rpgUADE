package rpg.dto;

/**
 * DTO para representar ítems del inventario de forma desacoplada para la Vista.
 */
public class ItemDTO {
    private final int indice;
    private final String nombre;
    private final String descripcion;
    private final boolean esConsumible;

    public ItemDTO(int indice, String nombre, String descripcion, boolean esConsumible) {
        this.indice = indice;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esConsumible = esConsumible;
    }

    public int getIndice() {
        return indice;
    }

    public int getIndiceInventario() {
        return indice;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isEsConsumible() {
        return esConsumible;
    }

    public boolean isConsumible() {
        return esConsumible;
    }

    @Override
    public String toString() {
        return nombre + " - " + descripcion;
    }
}
