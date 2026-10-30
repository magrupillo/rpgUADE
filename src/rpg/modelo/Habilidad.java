package rpg.modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Clase base abstracta para habilidades de combate (Clase 8 - Polimorfismo).
 * Define el tipo de objetivo para que la batalla resuelva objetivos sin condicionales de tipo.
 */
public abstract class Habilidad implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String descripcion;
    private final int costeMana;
    private final TipoObjetivo tipoObjetivo;

    public Habilidad(String nombre, String descripcion, int costeMana, TipoObjetivo tipoObjetivo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costeMana = costeMana;
        this.tipoObjetivo = tipoObjetivo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCosteMana() {
        return costeMana;
    }

    public TipoObjetivo getTipoObjetivo() {
        return tipoObjetivo;
    }

    /**
     * Ejecuta la habilidad sobre la lista de objetivos.
     *
     * @param lanzador  héroe que usa la habilidad
     * @param objetivos lista de combatientes afectados
     * @param batalla   contexto de la batalla
     * @return true si se ejecutó con éxito
     */
    public abstract boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla);

    @Override
    public String toString() {
        return nombre + " (" + costeMana + " MP)";
    }
}
