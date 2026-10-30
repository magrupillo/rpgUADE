package rpg.modelo;

import java.io.*;

/**
 * Gestor de persistencia mediante serialización de objetos en disco (Clase 13).
 * Utiliza try-with-resources y propaga fallos encapsulados en {@link PersistenciaException}.
 */
public class GestorPersistencia {

    private static final String ARCHIVO_GUARDADO = "partida_run.dat";

    /**
     * Guarda el estado completo de la partida en el archivo binario.
     *
     * @param juego estado actual del juego
     * @throws PersistenciaException si ocurre algún fallo de E/S al escribir en el disco
     */
    public static void guardarPartida(Juego juego) throws PersistenciaException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_GUARDADO))) {
            oos.writeObject(juego);
        } catch (IOException e) {
            throw new PersistenciaException("Fallo al guardar la partida en disco: " + e.getMessage(), e);
        }
    }

    /**
     * Carga el estado del juego desde el archivo de persistencia.
     *
     * @return instancia de {@link Juego} deserializada, o {@code null} si el archivo no existe
     * @throws PersistenciaException si el archivo existe pero está corrupto o es incompatible
     */
    public static Juego cargarPartida() throws PersistenciaException {
        File file = new File(ARCHIVO_GUARDADO);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Juego) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("El archivo de guardado está dañado o es incompatible: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si existe un archivo de partida guardada válido en disco (regla de validación normal).
     *
     * @return {@code true} si existe el archivo y no está vacío
     */
    public static boolean existePartidaGuardada() {
        File file = new File(ARCHIVO_GUARDADO);
        return file.exists() && file.length() > 0;
    }

    /**
     * Elimina el archivo de guardado ante muerte definitiva (Permadeath).
     *
     * @return {@code true} si el archivo fue eliminado con éxito o no existía
     */
    public static boolean borrarPartidaPorPermadeath() {
        File file = new File(ARCHIVO_GUARDADO);
        if (file.exists()) {
            boolean borrado = file.delete();
            if (!borrado) {
                file.deleteOnExit();
                return false;
            }
        }
        return true;
    }
}
