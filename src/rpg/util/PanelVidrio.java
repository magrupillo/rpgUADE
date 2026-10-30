package rpg.util;

import javax.swing.*;
import java.awt.*;

/**
 * Panel "de vidrio" translúcido y redondeado.
 * A diferencia de un JPanel opaco con color semitransparente (que en Swing genera
 * artefactos de repintado/"fantasmas"), este panel se declara no opaco y pinta
 * su propio fondo, por lo que la transparencia sobre las imágenes de fondo es correcta.
 * Respeta setBackground() para el color (con alpha).
 */
public class PanelVidrio extends JPanel {

    private final int radio;

    public PanelVidrio() {
        this(new FlowLayout(), 14);
    }

    public PanelVidrio(int radio) {
        this(new FlowLayout(), radio);
    }

    public PanelVidrio(LayoutManager layout) {
        this(layout, 14);
    }

    public PanelVidrio(LayoutManager layout, int radio) {
        super(layout);
        this.radio = radio;
        setOpaque(false);
        setBackground(new Color(24, 28, 40, 215));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        Color bg = getBackground();
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, radio, radio);

        // Brillo sutil en la parte superior para dar volumen
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 18), 0, Math.max(1, h / 3), new Color(255, 255, 255, 0)));
        g2.fillRoundRect(0, 0, w, Math.max(1, h / 3), radio, radio);

        g2.dispose();
        super.paintComponent(g);
    }
}
