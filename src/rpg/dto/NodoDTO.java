package rpg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodoDTO {
    private final int id;
    private final int columna;
    private final String nombre;
    private final String tipo;
    private final String icono;
    private final boolean completado;
    private final boolean disponible;
    private final boolean esActual;
    private final boolean revelado;
    private final List<Integer> conexiones;

    public NodoDTO(int id, int columna, String nombre, String tipo, String icono,
                   boolean completado, boolean disponible, boolean esActual, boolean revelado, List<Integer> conexiones) {
        this.id = id;
        this.columna = columna;
        this.nombre = nombre;
        this.tipo = tipo;
        this.icono = icono;
        this.completado = completado;
        this.disponible = disponible;
        this.esActual = esActual;
        this.revelado = revelado;
        this.conexiones = conexiones != null ? Collections.unmodifiableList(new ArrayList<>(conexiones)) : Collections.emptyList();
    }

    public NodoDTO(int id, int columna, String nombre, String tipo, String icono,
                   boolean completado, boolean disponible, boolean esActual, List<Integer> conexiones, boolean revelado) {
        this(id, columna, nombre, tipo, icono, completado, disponible, esActual, revelado, conexiones);
    }

    public NodoDTO(int id, int columna, String nombre, String tipo, String icono,
                   boolean completado, boolean disponible, boolean esActual, List<Integer> conexiones) {
        this(id, columna, nombre, tipo, icono, completado, disponible, esActual, true, conexiones);
    }

    public int getId() {
        return id;
    }

    public int getColumna() {
        return columna;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIcono() {
        return icono;
    }

    public boolean isCompletado() {
        return completado;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public boolean isEsActual() {
        return esActual;
    }

    public boolean isRevelado() {
        return revelado;
    }

    public List<Integer> getConexiones() {
        return conexiones;
    }
}
