package rpg.app;

import rpg.controlador.ControladorJuego;
import rpg.modelo.Juego;
import rpg.util.EstilosUI;
import rpg.vista.PantallaBatalla;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación GUI (Clase 8 - MVC).
 * - Configura el entorno gráfico.
 * - Instancia el Modelo central (Juego).
 * - Instancia la Vista principal (PantallaBatalla con navegación por
 * CardLayout).
 * - Instancia el Controlador (ControladorJuego) como intermediario.
 * - Inicia la aplicación desplegando la Pantalla de Inicio en el hilo de
 * eventos de Swing.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Paleta oscura de fantasía aplicada a Nimbus (diálogos, combos, tooltips)
                EstilosUI.aplicarTemaOscuro();
                // Look and Feel moderno del sistema o Nimbus
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("[Main] No se pudo inicializar LookAndFeel Nimbus, usando predeterminado: " + e.getMessage());
            }

            // 1. Instanciación del Modelo central
            Juego juego = new Juego();

            // 2. Instanciación de la Vista principal
            PantallaBatalla pantallaPrincipal = new PantallaBatalla();

            // 3. Instanciación del Controlador Principal
            ControladorJuego controlador = new ControladorJuego(juego, pantallaPrincipal);

            // 4. Iniciar el flujo de juego mostrando la Pantalla de Inicio
            controlador.iniciar();
            pantallaPrincipal.setVisible(true);
        });
    }
}
