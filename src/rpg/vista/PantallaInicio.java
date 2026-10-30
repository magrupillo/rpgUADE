package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla Inicial (Sección 9 del documento de requerimientos):
 * Menú principal con opciones de "Nueva Partida", "Cargar Partida" y "Salir".
 */
public class PantallaInicio extends JPanel {
    private ControladorJuego controlador;
    private final JButton btnCargar;

    public PantallaInicio() {
        setLayout(new GridBagLayout());
        setBackground(EstilosUI.FONDO_OSCURO);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(EstilosUI.FONDO_PANEL);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_ACTIVO, 2, true),
                BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));

        // Título del juego
        JLabel lblTitulo = new JLabel("⚔️ TOWER CRAWLER RPG ⚔️");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);

        panelCentral.add(Box.createVerticalStrut(8));

        JLabel lblSubtitulo = new JLabel("Videojuego Táctico por Turnos | Arquitectura MVC");
        lblSubtitulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(EstilosUI.TEXTO_MUTED);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblSubtitulo);

        panelCentral.add(Box.createVerticalStrut(28));

        // Botón Nueva Partida
        JButton btnNueva = EstilosUI.crearBoton("🏰  NUEVA PARTIDA", new Color(39, 174, 96), new Color(46, 204, 113));
        btnNueva.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnNueva.setMaximumSize(new Dimension(320, 45));
        btnNueva.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNueva.addActionListener(e -> {
            if (controlador != null) controlador.abrirCreacionParty();
        });
        panelCentral.add(btnNueva);

        panelCentral.add(Box.createVerticalStrut(14));

        // Botón Cargar Partida
        btnCargar = EstilosUI.crearBoton("💾  CARGAR PARTIDA", new Color(41, 128, 185), new Color(52, 152, 219));
        btnCargar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCargar.setMaximumSize(new Dimension(320, 45));
        btnCargar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCargar.addActionListener(e -> {
            if (controlador != null) controlador.cargarPartida();
        });
        panelCentral.add(btnCargar);

        panelCentral.add(Box.createVerticalStrut(14));

        // Botón Salir
        JButton btnSalir = EstilosUI.crearBoton("❌  SALIR", new Color(140, 40, 40), new Color(192, 57, 43));
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSalir.setMaximumSize(new Dimension(320, 45));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> System.exit(0));
        panelCentral.add(btnSalir);

        add(panelCentral);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizarEstadoGuardado() {
        // Guardado no implementado en esta versión del proyecto
        btnCargar.setEnabled(false);
        btnCargar.setToolTipText("El guardado de partidas no está disponible en esta versión.");
    }
}
