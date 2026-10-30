package rpg.modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Clase abstracta que representa una habilidad especial de un {@link Personaje}.
 * Cada subclase de Personaje posee una instancia de Habilidad.
 *
 * El método {@link #ejecutar(List, Batalla)} recibe una lista de objetivos en lugar de
 * un único combatiente, lo que permite resolver ataques y curaciones en área.
 */
public abstract class Habilidad implements Serializable {

    private final String nombre;
    private final int costeMana;

    public Habilidad(String nombre, int costeMana) {
        this.nombre = nombre;
        this.costeMana = costeMana;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCosteMana() {
        return costeMana;
    }

    /**
     * Ejecuta la habilidad sobre la lista de objetivos.
     * La lista puede contener uno o varios {@link Combatiente}s (área).
     *
     * @param lanzador  el {@link Personaje} que usa la habilidad
     * @param objetivos lista de combatientes afectados (aliados o enemigos)
     * @param batalla   contexto de la batalla actual
     * @return {@code true} si la habilidad se ejecutó con éxito
     */
    public abstract boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla);

    @Override
    public String toString() {
        return nombre + " (" + costeMana + " MP)";
    }
}
