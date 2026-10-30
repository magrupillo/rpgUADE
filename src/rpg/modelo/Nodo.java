package rpg.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta que representa un nodo del mapa de la torre.
 *
 * Cada tipo de nodo se implementa como una subclase específica que define
 * su propio comportamiento al ser visitado (polimorfismo, sin TipoNodo).
 *
 * Subclases disponibles:
 * {@link NodoCombate}, {@link NodoElite}, {@link NodoJefe},
 * {@link NodoEvento}, {@link NodoFogata}.
 */
public abstract class Nodo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final int columna;   // nivel de profundidad en el mapa (0 = inicio)
    private final String nombre;
    private final List<Integer> conexiones;
    private boolean completado;
    private boolean disponible;
    private boolean revelado;

    public Nodo(int id, int columna, String nombre) {
        this.id = id;
        this.columna = columna;
        this.nombre = nombre;
        this.conexiones = new ArrayList<>();
        this.completado = false;
        this.disponible = (columna == 0);
        this.revelado = (columna <= 1);
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public int getId() { return id; }

    public int getColumna() { return columna; }

    public String getNombre() { return nombre; }

    public List<Integer> getConexiones() { return conexiones; }

    public boolean isCompletado() { return completado; }

    public void setCompletado(boolean completado) { this.completado = completado; }

    public boolean isDisponible() { return disponible; }

    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public boolean isRevelado() { return revelado; }

    public void setRevelado(boolean revelado) { this.revelado = revelado; }

    // ─── Conexiones ──────────────────────────────────────────────────────────────

    public void conectarCon(int idDestino) {
        if (!conexiones.contains(idDestino)) {
            conexiones.add(idDestino);
        }
    }

    // ─── Datos de presentación (polimórficos) ────────────────────────────────────

    /** Indica si el nodo corresponde a un jefe de piso. */
    public boolean esJefe() { return false; }

    /** Icono/emoji representativo del tipo de nodo para la UI. */
    public abstract String getIcono();

    /** Descripción corta del tipo de nodo para la UI. */
    public abstract String getDescripcion();

    // ─── Comportamiento polimórfico al ser visitado ──────────────────────────────
    /**
     * Define el comportamiento específico del nodo al ser visitado en el modelo.
     * Cada subclase lo implementa polimórficamente sin condicionales de tipo.
     */
    public abstract void alVisitar(Juego juego);

    /**
     * Permite a un {@link NodoVisitor} ejecutar la lógica o transición correspondiente.
     */
    public abstract void aceptar(NodoVisitor visitor);

    // ─── toString ────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return getIcono() + " " + nombre + " (" + getDescripcion() + ")";
    }
}
