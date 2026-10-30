package rpg.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestor interno del modelo para almacenar el historial de eventos y logs de combate
 * sin acoplarse a ninguna tecnología de interfaz gráfica (Swing / AWT).
 */
public class RegistroCombate {
    private static final List<String> historial = new ArrayList<>();

    public static synchronized void registrar(String mensaje) {
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            historial.add(mensaje);
            // También se emite por consola para depuración
            System.out.println(mensaje);
        }
    }

    public static synchronized List<String> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historial));
    }

    public static synchronized void limpiar() {
        historial.clear();
    }
}
