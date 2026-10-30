package rpg.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Sistema visual centralizado de "El Último Ascenso".
 * Paleta oscura de fantasía (azul noche + oro), botones con degradé y brillo,
 * barras de estado redondeadas y tema oscuro global para Nimbus.
 */
public class EstilosUI {
    // ── Paleta base ──────────────────────────────────────────────────────────────
    public static final Color FONDO_OSCURO = new Color(13, 15, 22);
    public static final Color FONDO_PANEL = new Color(24, 28, 40);
    public static final Color FONDO_TARJETA = new Color(33, 38, 54);
    public static final Color BORDE_SUAVE = new Color(58, 66, 92);
    public static final Color BORDE_ACTIVO = new Color(241, 196, 15);
    public static final Color BORDE_ENEMIGO = new Color(231, 76, 60);

    public static final Color COLOR_VIDA = new Color(46, 204, 113);
    public static final Color COLOR_VIDA_MEDIA = new Color(241, 196, 15);
    public static final Color COLOR_VIDA_BAJA = new Color(231, 76, 60);
    public static final Color COLOR_ROJO = new Color(231, 76, 60);
    public static final Color COLOR_MANA = new Color(52, 152, 219);
    public static final Color COLOR_ORO = new Color(241, 196, 15);
    public static final Color COLOR_ORO_OSCURO = new Color(176, 132, 18);
    public static final Color COLOR_PURPURA = new Color(155, 89, 182);

    public static final Color TEXTO_CLARO = new Color(240, 242, 248);
    public static final Color TEXTO_MUTED = new Color(160, 168, 190);
    public static final Color FONDO_LOG = new Color(10, 12, 18);

    // ── Tipografía ───────────────────────────────────────────────────────────────
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FUENTE_LOG = new Font("Consolas", Font.PLAIN, 12);

    private static final int RADIO_BOTON = 10;

    /**
     * Aplica la paleta oscura a Nimbus. Debe llamarse ANTES de setLookAndFeel.
     * Afecta diálogos (JOptionPane), combos, tooltips, scrollbars y textos por defecto.
     */
    public static void aplicarTemaOscuro() {
        UIManager.put("control", FONDO_PANEL);
        UIManager.put("info", new Color(20, 24, 36));               // fondo de tooltips
        UIManager.put("nimbusBase", new Color(28, 36, 58));
        UIManager.put("nimbusBlueGrey", new Color(52, 60, 84));
        UIManager.put("nimbusLightBackground", FONDO_TARJETA);
        UIManager.put("nimbusFocus", COLOR_ORO_OSCURO);
        UIManager.put("nimbusSelectionBackground", new Color(120, 90, 20));
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("nimbusDisabledText", new Color(110, 116, 134));
        UIManager.put("nimbusInfoBlue", COLOR_MANA);
        UIManager.put("nimbusAlertYellow", COLOR_ORO);
        UIManager.put("nimbusOrange", COLOR_ORO_OSCURO);
        UIManager.put("text", TEXTO_CLARO);
        UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(COLOR_ORO_OSCURO, 1));

        // Tooltips ágiles: aparecen rápido y quedan visibles para leer la descripción
        ToolTipManager.sharedInstance().setInitialDelay(250);
        ToolTipManager.sharedInstance().setDismissDelay(20000);
    }

    // ── Utilidades de color ─────────────────────────────────────────────────────
    public static Color aclarar(Color c, float factor) {
        int r = c.getRed() + (int) ((255 - c.getRed()) * factor);
        int g = c.getGreen() + (int) ((255 - c.getGreen()) * factor);
        int b = c.getBlue() + (int) ((255 - c.getBlue()) * factor);
        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b), c.getAlpha());
    }

    public static Color oscurecer(Color c, float factor) {
        return new Color(
                Math.max(0, (int) (c.getRed() * (1 - factor))),
                Math.max(0, (int) (c.getGreen() * (1 - factor))),
                Math.max(0, (int) (c.getBlue() * (1 - factor))),
                c.getAlpha());
    }

    public static Color mezclar(Color a, Color b, float t) {
        return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    public static Color conAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    /** Color de vida según porcentaje restante: verde → amarillo → rojo. */
    public static Color colorSegunVida(int actual, int maximo) {
        double ratio = maximo > 0 ? (double) actual / maximo : 0;
        if (ratio > 0.5) return COLOR_VIDA;
        if (ratio > 0.25) return COLOR_VIDA_MEDIA;
        return COLOR_VIDA_BAJA;
    }

    // ── Botones ─────────────────────────────────────────────────────────────────

    /**
     * Botón redondeado con degradé, brillo superior y borde luminoso.
     * Respeta setBackground() posteriores (estados de piso, habilidades, etc.)
     * y se atenúa visualmente cuando está deshabilitado.
     */
    public static JButton crearBoton(String texto, Color colorBase, Color colorHover) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                Color base = getBackground() != null ? getBackground() : colorBase;
                boolean colorPersonalizado = !base.equals(colorBase);
                Color hover = colorPersonalizado ? aclarar(base, 0.15f) : colorHover;

                Color relleno;
                if (!isEnabled()) {
                    relleno = mezclar(base, new Color(38, 42, 54), 0.65f);
                } else if (getModel().isPressed()) {
                    relleno = oscurecer(base, 0.2f);
                } else if (getModel().isRollover()) {
                    relleno = hover;
                } else {
                    relleno = base;
                }

                // Degradé vertical
                g2.setPaint(new GradientPaint(0, 0, aclarar(relleno, 0.12f), 0, h, oscurecer(relleno, 0.18f)));
                g2.fillRoundRect(0, 0, w, h, RADIO_BOTON, RADIO_BOTON);

                // Brillo superior (efecto "gema")
                g2.setColor(new Color(255, 255, 255, isEnabled() ? 30 : 8));
                g2.fillRoundRect(1, 1, w - 2, h / 2, RADIO_BOTON, RADIO_BOTON);

                // Borde: dorado al pasar el mouse, sutil en reposo
                if (isEnabled() && getModel().isRollover()) {
                    g2.setColor(conAlpha(COLOR_ORO, 210));
                    g2.setStroke(new BasicStroke(1.6f));
                } else {
                    g2.setColor(isEnabled() ? conAlpha(aclarar(relleno, 0.35f), 150) : new Color(70, 76, 92, 140));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(0, 0, w - 1, h - 1, RADIO_BOTON, RADIO_BOTON);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setBackground(colorBase);
        btn.setFont(FUENTE_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setRolloverEnabled(true);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Barras de estado ────────────────────────────────────────────────────────

    /** Barra redondeada con degradé y texto con sombra. El color se toma de getForeground(). */
    public static JProgressBar crearBarra(int min, int max, Color colorBarra) {
        JProgressBar barra = new JProgressBar(min, max) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arco = Math.min(h, 10);

                // Pista de fondo
                g2.setColor(new Color(12, 14, 22));
                g2.fillRoundRect(0, 0, w, h, arco, arco);

                // Relleno proporcional
                int rango = Math.max(1, getMaximum() - getMinimum());
                double ratio = Math.max(0, Math.min(1, (double) (getValue() - getMinimum()) / rango));
                int anchoRelleno = (int) Math.round((w - 2) * ratio);
                if (anchoRelleno > 0) {
                    Color c = getForeground();
                    g2.setPaint(new GradientPaint(0, 0, aclarar(c, 0.25f), 0, h, oscurecer(c, 0.25f)));
                    g2.fillRoundRect(1, 1, anchoRelleno, h - 2, arco, arco);
                    g2.setColor(new Color(255, 255, 255, 45));
                    g2.fillRoundRect(1, 1, anchoRelleno, Math.max(1, (h - 2) / 2), arco, arco);
                }

                // Borde
                g2.setColor(BORDE_SUAVE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);

                // Texto centrado con sombra
                if (isStringPainted() && getString() != null) {
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    String s = getString();
                    int tx = (w - fm.stringWidth(s)) / 2;
                    int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                    g2.setColor(new Color(0, 0, 0, 170));
                    g2.drawString(s, tx + 1, ty + 1);
                    g2.setColor(Color.WHITE);
                    g2.drawString(s, tx, ty);
                }
                g2.dispose();
            }
        };
        barra.setStringPainted(true);
        barra.setForeground(colorBarra);
        barra.setOpaque(false);
        barra.setBorderPainted(false);
        barra.setBorder(BorderFactory.createEmptyBorder());
        barra.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return barra;
    }

    /** Barra de vida cuyo color cambia según el porcentaje restante. */
    public static JProgressBar crearBarraVida(int actual, int maximo) {
        int max = Math.max(1, maximo);
        int act = Math.max(0, Math.min(actual, max));
        JProgressBar barra = crearBarra(0, max, colorSegunVida(act, max));
        barra.setValue(act);
        barra.setString(act + " / " + max);
        return barra;
    }

    // ── Bordes y paneles ────────────────────────────────────────────────────────

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

    /**
     * Pinta una viñeta (bordes oscurecidos) sobre un área. Da profundidad
     * cinematográfica a los fondos de pantalla.
     */
    public static void pintarVineta(Graphics2D g2, int w, int h, int intensidad) {
        float radio = Math.max(w, h) * 0.75f;
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(w / 2, h / 2), radio,
                new float[]{0.45f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, Math.min(255, intensidad))});
        g2.setPaint(rgp);
        g2.fillRect(0, 0, w, h);
    }
}
