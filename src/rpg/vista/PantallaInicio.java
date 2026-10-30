package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.modelo.GestorPersistencia;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;
import rpg.util.PanelVidrio;

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

        JPanel panelCentral = new PanelVidrio(null, 22);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(new Color(16, 19, 30, 205));
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.conAlpha(EstilosUI.COLOR_ORO, 170), 2, true),
                BorderFactory.createEmptyBorder(36, 56, 36, 56)
        ));

        // Título del juego con resplandor dorado
        JComponent lblTitulo = new TituloResplandor("EL ÚLTIMO ASCENSO", new Font("Segoe UI", Font.BOLD, 40));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);

        panelCentral.add(Box.createVerticalStrut(6));

        JLabel lblSub = new JLabel("Tres pisos. Un equipo. Ninguna segunda oportunidad.", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(new Color(200, 190, 160));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblSub);

        panelCentral.add(Box.createVerticalStrut(18));
        JComponent separador = new SeparadorOrnamental();
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(separador);
        panelCentral.add(Box.createVerticalStrut(24));

        // Botón Nueva Partida
        JButton btnNueva = EstilosUI.crearBoton("NUEVA PARTIDA", new Color(39, 150, 86), new Color(46, 190, 105));
        btnNueva.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNueva.setMaximumSize(new Dimension(300, 48));
        btnNueva.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNueva.addActionListener(e -> {
            if (controlador != null) controlador.iniciarNuevaPartida();
        });
        panelCentral.add(btnNueva);

        panelCentral.add(Box.createVerticalStrut(14));

        // Botón Cargar Partida
        btnCargar = EstilosUI.crearBoton("CARGAR PARTIDA", new Color(36, 110, 165), new Color(52, 145, 210));
        btnCargar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCargar.setMaximumSize(new Dimension(300, 48));
        btnCargar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCargar.addActionListener(e -> {
            if (controlador != null) controlador.cargarPartida();
        });
        panelCentral.add(btnCargar);

        panelCentral.add(Box.createVerticalStrut(14));

        // Botón Salir
        JButton btnSalir = EstilosUI.crearBoton("SALIR", new Color(120, 36, 36), new Color(170, 50, 42));
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalir.setMaximumSize(new Dimension(300, 48));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> System.exit(0));
        panelCentral.add(btnSalir);

        panelCentral.add(Box.createVerticalStrut(22));
        JLabel lblPie = new JLabel("Programación III  ·  UADE", SwingConstants.CENTER);
        lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPie.setForeground(EstilosUI.TEXTO_MUTED);
        lblPie.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblPie);

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
            g2.setColor(new Color(10, 12, 18, 120));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        EstilosUI.pintarVineta(g2, getWidth(), getHeight(), 210);
        g2.dispose();
    }

    /** Título con sombra profunda, resplandor dorado y degradé metálico. */
    private static class TituloResplandor extends JComponent {
        private final String texto;
        private final Font fuente;

        TituloResplandor(String texto, Font fuente) {
            this.texto = texto;
            this.fuente = fuente;
            FontMetrics fm = new Canvas().getFontMetrics(fuente);
            Dimension d = new Dimension(fm.stringWidth(texto) + 40, fm.getHeight() + 16);
            setPreferredSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(fuente);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(texto)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            // Resplandor (varias pasadas desplazadas con baja opacidad)
            g2.setColor(new Color(241, 196, 15, 22));
            for (int r = 6; r >= 1; r -= 1) {
                g2.drawString(texto, x - r, y);
                g2.drawString(texto, x + r, y);
                g2.drawString(texto, x, y - r);
                g2.drawString(texto, x, y + r);
            }
            // Sombra
            g2.setColor(new Color(0, 0, 0, 200));
            g2.drawString(texto, x + 3, y + 3);
            // Degradé metálico dorado
            g2.setPaint(new GradientPaint(0, y - fm.getAscent(), new Color(255, 236, 150),
                    0, y, new Color(214, 150, 20)));
            g2.drawString(texto, x, y);
            g2.dispose();
        }
    }

    /** Línea decorativa dorada con rombo central. */
    private static class SeparadorOrnamental extends JComponent {
        SeparadorOrnamental() {
            Dimension d = new Dimension(320, 14);
            setPreferredSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int cy = getHeight() / 2;
            int cx = w / 2;
            g2.setPaint(new GradientPaint(0, cy, new Color(241, 196, 15, 0), cx - 12, cy, new Color(241, 196, 15, 200)));
            g2.fillRect(0, cy - 1, cx - 12, 2);
            g2.setPaint(new GradientPaint(cx + 12, cy, new Color(241, 196, 15, 200), w, cy, new Color(241, 196, 15, 0)));
            g2.fillRect(cx + 12, cy - 1, w - cx - 12, 2);
            Polygon rombo = new Polygon(new int[]{cx, cx + 7, cx, cx - 7}, new int[]{cy - 6, cy, cy + 6, cy}, 4);
            g2.setColor(EstilosUI.COLOR_ORO);
            g2.fill(rombo);
            g2.dispose();
        }
    }
}
