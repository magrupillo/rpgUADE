package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.EventoDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;
import rpg.util.PanelVidrio;

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

        PanelVidrio panelCentral = new PanelVidrio(22);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_ORO, 2, true),
                BorderFactory.createEmptyBorder(22, 30, 22, 30)
        ));
        panelCentral.setPreferredSize(new Dimension(750, 560));

        lblTitulo = new JLabel("EVENTO EN EL CAMINO", SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);

        panelCentral.add(Box.createVerticalStrut(14));

        txtDescripcion = new JTextArea();
        txtDescripcion.setEditable(false);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescripcion.setForeground(EstilosUI.TEXTO_CLARO);
        txtDescripcion.setBackground(EstilosUI.FONDO_LOG);
        txtDescripcion.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1));
        scrollDesc.setPreferredSize(new Dimension(680, 200));
        scrollDesc.setMaximumSize(new Dimension(680, 200));
        scrollDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(scrollDesc);

        panelCentral.add(Box.createVerticalStrut(18));

        btnOpcionA = EstilosUI.crearBoton("Opción 1", new Color(142, 68, 173), new Color(155, 89, 182));
        btnOpcionA.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpcionA.setPreferredSize(new Dimension(680, 52));
        btnOpcionA.setMaximumSize(new Dimension(680, 52));
        btnOpcionA.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOpcionA.addActionListener(e -> resolver(1));
        panelCentral.add(btnOpcionA);

        panelCentral.add(Box.createVerticalStrut(10));

        btnOpcionB = EstilosUI.crearBoton("Opción 2", new Color(52, 73, 94), new Color(70, 95, 120));
        btnOpcionB.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpcionB.setPreferredSize(new Dimension(680, 52));
        btnOpcionB.setMaximumSize(new Dimension(680, 52));
        btnOpcionB.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOpcionB.addActionListener(e -> resolver(2));
        panelCentral.add(btnOpcionB);

        panelCentral.add(Box.createVerticalStrut(14));

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
        Graphics2D g2 = (Graphics2D) g.create();
        Image fondo = GestorRecursos.obtenerFondo("otro");
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 205));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        EstilosUI.pintarVineta(g2, getWidth(), getHeight(), 190);
        g2.dispose();
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(EventoDTO dto) {
        lblTitulo.setText(dto.getTitulo());
        txtDescripcion.setText(dto.getDescripcion());
        txtDescripcion.setCaretPosition(0);

        btnOpcionA.setText("<html><center>1. " + dto.getOpcionA() + "</center></html>");
        btnOpcionB.setText("<html><center>2. " + dto.getOpcionB() + "</center></html>");

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
