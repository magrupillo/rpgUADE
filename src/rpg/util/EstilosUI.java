package rpg.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EstilosUI {
    public static final Color FONDO_OSCURO = new Color(16, 18, 24);
    public static final Color FONDO_PANEL = new Color(26, 30, 42);
    public static final Color FONDO_TARJETA = new Color(34, 39, 54);
    public static final Color BORDE_SUAVE = new Color(55, 62, 85);
    public static final Color BORDE_ACTIVO = new Color(241, 196, 15);
    public static final Color BORDE_ENEMIGO = new Color(231, 76, 60);

    public static final Color COLOR_VIDA = new Color(46, 204, 113);
    public static final Color COLOR_VIDA_BAJA = new Color(231, 76, 60);
    public static final Color COLOR_ROJO = new Color(231, 76, 60);
    public static final Color COLOR_MANA = new Color(52, 152, 219);
    public static final Color COLOR_ORO = new Color(241, 196, 15);
    public static final Color COLOR_PURPURA = new Color(155, 89, 182);

    public static final Color TEXTO_CLARO = new Color(245, 246, 250);
    public static final Color TEXTO_MUTED = new Color(160, 168, 190);
    public static final Color FONDO_LOG = new Color(12, 14, 20);

    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FUENTE_LOG = new Font("Consolas", Font.PLAIN, 12);

    public static JButton crearBoton(String texto, Color colorBase, Color colorHover) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(colorBase.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(colorHover);
                } else {
                    g2.setColor(colorBase);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FUENTE_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JProgressBar crearBarra(int min, int max, Color colorBarra) {
        JProgressBar barra = new JProgressBar(min, max);
        barra.setStringPainted(true);
        barra.setForeground(colorBarra);
        barra.setBackground(new Color(20, 22, 32));
        barra.setBorder(BorderFactory.createLineBorder(BORDE_SUAVE, 1));
        barra.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return barra;
    }

    public static Border crearBordeTarjeta() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_SUAVE, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
    }

    public static JPanel crearPanelConFondo(Image fondo, int opacidadOscura) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (fondo != null) {
                    g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                    if (opacidadOscura > 0) {
                        g2.setColor(new Color(12, 14, 20, Math.min(255, opacidadOscura)));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                    }
                } else {
                    g2.setColor(FONDO_OSCURO);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };
    }
}
