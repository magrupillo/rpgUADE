package rpg.modelo;

import java.io.*;

public class GestorPersistencia {
    private static final String ARCHIVO_GUARDADO = "partida_run.dat";

    public static boolean guardarPartida(Juego juego) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_GUARDADO))) {
            oos.writeObject(juego);
            RegistroCombate.registrar("💾 Partida guardada exitosamente en " + ARCHIVO_GUARDADO);
            return true;
        } catch (IOException e) {
            RegistroCombate.registrar("❌ Error al guardar la partida: " + e.getMessage());
            return false;
        }
    }

    public static Juego cargarPartida() {
        File file = new File(ARCHIVO_GUARDADO);
        if (!file.exists()) {
            RegistroCombate.registrar("⚠️ No se encontró ningún archivo de guardado previo.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Juego juego = (Juego) ois.readObject();
            RegistroCombate.registrar("📂 Partida cargada con éxito desde " + ARCHIVO_GUARDADO);
            return juego;
        } catch (Exception e) {
            RegistroCombate.registrar("❌ Error al cargar la partida: " + e.getMessage());
            return null;
        }
    }

    public static boolean existePartidaGuardada() {
        File file = new File(ARCHIVO_GUARDADO);
        return file.exists() && file.length() > 0;
    }

    public static void borrarPartidaPorPermadeath() {
        File file = new File(ARCHIVO_GUARDADO);
        if (file.exists()) {
            if (file.delete()) {
                RegistroCombate.registrar("☠️ [PERMADEATH] La partida guardada ha sido eliminada permanentemente.");
            }
        }
    }
}
