package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.modelo.GestorPersistencia;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;

/**
 * Menú Principal del juego.
 * Título limpio ("El Último Ascenso"), fondo estilizado con 'titulo.png' y botones esenciales.
 */
public class PantallaInicio extends JPanel {

    private ControladorJuego controlador;
    private final JButton btnCargar;
    private final Image fondoTitulo;

    public PantallaInicio() {
        this.fondoTitulo = GestorRecursos.obtenerFondo("titulo");
        setLayout(new GridBagLayout());

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(new Color(22, 26, 38, 220));
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_ACTIVO, 2, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        // Título del juego
        JLabel lblTitulo = new JLabel("EL ÚLTIMO ASCENSO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);

        panelCentral.add(Box.createVerticalStrut(35));

        // Botón Nueva Partida
        JButton btnNueva = EstilosUI.crearBoton("NUEVA PARTIDA", new Color(39, 174, 96), new Color(46, 204, 113));
        btnNueva.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNueva.setMaximumSize(new Dimension(300, 48));
        btnNueva.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNueva.addActionListener(e -> {
            if (controlador != null) controlador.iniciarNuevaPartida();
        });
        panelCentral.add(btnNueva);

        panelCentral.add(Box.createVerticalStrut(15));

        // Botón Cargar Partida
        btnCargar = EstilosUI.crearBoton("CARGAR PARTIDA", new Color(41, 128, 185), new Color(52, 152, 219));
        btnCargar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCargar.setMaximumSize(new Dimension(300, 48));
        btnCargar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCargar.addActionListener(e -> {
            if (controlador != null) controlador.cargarPartida();
        });
        panelCentral.add(btnCargar);

        panelCentral.add(Box.createVerticalStrut(15));

        // Botón Salir
        JButton btnSalir = EstilosUI.crearBoton("SALIR", new Color(140, 40, 40), new Color(192, 57, 43));
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalir.setMaximumSize(new Dimension(300, 48));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> System.exit(0));
        panelCentral.add(btnSalir);

        add(panelCentral);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizarEstadoGuardado() {
        boolean existe = GestorPersistencia.existePartidaGuardada();
        btnCargar.setEnabled(existe);
        if (existe) {
            btnCargar.setToolTipText("Cargar la partida guardada previamente.");
        } else {
            btnCargar.setToolTipText("No hay ningún archivo de guardado previo disponible.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoTitulo != null) {
            g2.drawImage(fondoTitulo, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(10, 12, 18, 160));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
