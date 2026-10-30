package rpg.modelo;

/**
 * Excepción personalizada para el subsistema de persistencia (Clase 13).
 * Se lanza ante errores de E/S o problemas durante la serialización/deserialización
 * de la partida, permitiendo que el controlador capture y gestione el error
 * sin mezclar lógica de interfaz en el modelo.
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
