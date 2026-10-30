package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.EventoDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;

public class PantallaEvento extends JPanel {
    private ControladorJuego controlador;
    private final JLabel lblTitulo;
    private final JTextArea txtDescripcion;
    private final JButton btnOpcionA;
    private final JButton btnOpcionB;
    private final JButton btnContinuar;
    private final JLabel lblResultado;

    public PantallaEvento() {
        setLayout(new GridBagLayout());
        setBackground(EstilosUI.FONDO_OSCURO);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(EstilosUI.FONDO_PANEL);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_ORO, 2, true),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));
        panelCentral.setPreferredSize(new Dimension(650, 480));

        lblTitulo = new JLabel("EVENTO EN EL CAMINO", SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);

        panelCentral.add(Box.createVerticalStrut(15));

        txtDescripcion = new JTextArea();
        txtDescripcion.setEditable(false);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescripcion.setForeground(EstilosUI.TEXTO_CLARO);
        txtDescripcion.setBackground(EstilosUI.FONDO_LOG);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        txtDescripcion.setMaximumSize(new Dimension(600, 180));
        panelCentral.add(txtDescripcion);

        panelCentral.add(Box.createVerticalStrut(20));

        btnOpcionA = EstilosUI.crearBoton("Opcion 1", new Color(142, 68, 173), new Color(155, 89, 182));
        btnOpcionA.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOpcionA.setMaximumSize(new Dimension(580, 42));
        btnOpcionA.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOpcionA.addActionListener(e -> resolver(1));
        panelCentral.add(btnOpcionA);

        panelCentral.add(Box.createVerticalStrut(10));

        btnOpcionB = EstilosUI.crearBoton("Opcion 2", new Color(52, 73, 94), new Color(70, 95, 120));
        btnOpcionB.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOpcionB.setMaximumSize(new Dimension(580, 42));
        btnOpcionB.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOpcionB.addActionListener(e -> resolver(2));
        panelCentral.add(btnOpcionB);

        panelCentral.add(Box.createVerticalStrut(15));

        lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setFont(EstilosUI.FUENTE_BOLD);
        lblResultado.setForeground(new Color(46, 204, 113));
        lblResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblResultado.setVisible(false);
        panelCentral.add(lblResultado);

        panelCentral.add(Box.createVerticalStrut(10));

        btnContinuar = EstilosUI.crearBoton("Continuar al Mapa", new Color(39, 174, 96), new Color(46, 204, 113));
        btnContinuar.setMaximumSize(new Dimension(280, 40));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.setVisible(false);
        btnContinuar.addActionListener(e -> {
            if (controlador != null) controlador.volverAlMapa();
        });
        panelCentral.add(btnContinuar);

        add(panelCentral);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image fondo = GestorRecursos.obtenerFondo("otro");
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(15, 18, 25, 205));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(EventoDTO dto) {
        lblTitulo.setText(dto.getTitulo());
        txtDescripcion.setText(dto.getDescripcion());
        btnOpcionA.setText("1. " + dto.getOpcionA());
        btnOpcionB.setText("2. " + dto.getOpcionB());

        btnOpcionA.setVisible(true);
        btnOpcionB.setVisible(true);
        lblResultado.setVisible(false);
        btnContinuar.setVisible(false);
    }

    private void resolver(int opcion) {
        if (controlador != null) {
            String res = controlador.resolverEvento(opcion);
            btnOpcionA.setVisible(false);
            btnOpcionB.setVisible(false);
            lblResultado.setText("<html><center>" + res.replace("\n", "<br>") + "</center></html>");
            lblResultado.setVisible(true);
            btnContinuar.setVisible(true);
        }
    }
}
