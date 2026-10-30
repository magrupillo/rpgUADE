package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;
import rpg.util.PanelVidrio;

import javax.swing.*;
import java.awt.*;

public class PantallaDescanso extends JPanel {
    private ControladorJuego controlador;
    private final JLabel lblResultado;
    private final JButton btnDescansar;
    private final JButton btnEntrenar;
    private final JButton btnContinuar;

    public PantallaDescanso() {
        setLayout(new GridBagLayout());
        setBackground(EstilosUI.FONDO_OSCURO);

        PanelVidrio panelCentral = new PanelVidrio(22);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 2, true),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));
        panelCentral.setPreferredSize(new Dimension(650, 420));

        JLabel lblTitulo = new JLabel("FOGON CRIOLLO", SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(new Color(243, 156, 18));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblTitulo);

        panelCentral.add(Box.createVerticalStrut(10));

        JLabel lblDesc = new JLabel("<html><center>Un punto seguro en la torre. El fuego crepitante reconforta al grupo.<br>¿Que deciden hacer antes de continuar?</center></html>", SwingConstants.CENTER);
        lblDesc.setFont(EstilosUI.FUENTE_NORMAL);
        lblDesc.setForeground(EstilosUI.TEXTO_MUTED);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblDesc);

        panelCentral.add(Box.createVerticalStrut(25));

        btnDescansar = EstilosUI.crearBoton("Tomar unos mates y descansar (Recupera 40% HP y MP)", new Color(39, 174, 96), new Color(46, 204, 113));
        btnDescansar.setFont(EstilosUI.FUENTE_BOLD);
        btnDescansar.setMaximumSize(new Dimension(520, 42));
        btnDescansar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDescansar.addActionListener(e -> accionar(1));
        panelCentral.add(btnDescansar);

        panelCentral.add(Box.createVerticalStrut(12));

        btnEntrenar = EstilosUI.crearBoton("Entrenar tecnicas de combate (+120 XP grupal)", new Color(41, 128, 185), new Color(52, 152, 219));
        btnEntrenar.setFont(EstilosUI.FUENTE_BOLD);
        btnEntrenar.setMaximumSize(new Dimension(520, 42));
        btnEntrenar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrenar.addActionListener(e -> accionar(2));
        panelCentral.add(btnEntrenar);

        panelCentral.add(Box.createVerticalStrut(15));

        lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setFont(EstilosUI.FUENTE_BOLD);
        lblResultado.setForeground(EstilosUI.COLOR_ORO);
        lblResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblResultado.setVisible(false);
        panelCentral.add(lblResultado);

        panelCentral.add(Box.createVerticalStrut(10));

        btnContinuar = EstilosUI.crearBoton("Continuar la Ascension", new Color(39, 174, 96), new Color(46, 204, 113));
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
            g2.setColor(new Color(15, 18, 25, 200));
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

    public void reiniciarVista() {
        btnDescansar.setVisible(true);
        btnEntrenar.setVisible(true);
        lblResultado.setVisible(false);
        btnContinuar.setVisible(false);
    }

    private void accionar(int opcion) {
        if (controlador != null) {
            String res = controlador.descansarEnFogata(opcion);
            btnDescansar.setVisible(false);
            btnEntrenar.setVisible(false);
            lblResultado.setText("<html><center>" + res + "</center></html>");
            lblResultado.setVisible(true);
            btnContinuar.setVisible(true);
        }
    }
}
