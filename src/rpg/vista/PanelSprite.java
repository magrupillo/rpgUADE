package rpg.vista;

import rpg.dto.EntidadDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel visual para cada entidad en la escena de batalla.
 * Muestra el sprite (con Dr. Eggman, Darth Vader y Bowser más grandes),
 * retícula de objetivo seleccionable por clic, barras de HP/MP, y badges de estado legibles.
 */
public class PanelSprite extends JPanel {

    private final EntidadDTO entidad;
    private final boolean esTurnoActivo;
    private final boolean esObjetivo;
    private final Runnable onClick;
    private final int spriteTam;

    public PanelSprite(EntidadDTO entidad, boolean esTurnoActivo, boolean esObjetivo, Runnable onClick) {
        this.entidad = entidad;
        this.esTurnoActivo = esTurnoActivo;
        this.esObjetivo = esObjetivo;
        this.onClick = onClick;

        // Jefes por piso (Dr. Eggman, Darth Vader, Bowser) se renderizan más grandes
        boolean esJefe = entidad.getTipo().toLowerCase().contains("jefe")
                || entidad.getNombre().toLowerCase().contains("bowser")
                || entidad.getNombre().toLowerCase().contains("eggman")
                || entidad.getNombre().toLowerCase().contains("vader");
        this.spriteTam = esJefe ? 115 : 80;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        construirVista();
    }

    public PanelSprite(EntidadDTO entidad, boolean esTurnoActivo) {
        this(entidad, esTurnoActivo, false, null);
    }

    private void construirVista() {
        // 1. Nombre y Nivel
        String etiquetaTxt = entidad.getNombre();
        if (entidad.getNivel() > 1) {
            etiquetaTxt += " Nv." + entidad.getNivel();
        }
        JLabel lblNombre = new JLabel(etiquetaTxt);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNombre.setForeground(entidad.isEstaVivo() ? EstilosUI.TEXTO_CLARO : EstilosUI.TEXTO_MUTED);
        add(lblNombre);

        add(Box.createVerticalStrut(4));

        // 2. Avatar / Icono del Sprite
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                Image img = GestorRecursos.obtenerSprite(entidad.getTipo(), spriteTam, spriteTam);
                int ix = (w - spriteTam) / 2;
                int iy = (h - spriteTam) / 2;

                if (!entidad.isEstaVivo()) {
                    Composite original = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                    g2.drawImage(img, ix, iy, this);
                    g2.setComposite(original);

                    // Cruz roja de caído
                    g2.setColor(new Color(231, 76, 60, 220));
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(ix + 12, iy + 12, ix + spriteTam - 12, iy + spriteTam - 12);
                    g2.drawLine(ix + spriteTam - 12, iy + 12, ix + 12, iy + spriteTam - 12);
                } else {
                    g2.drawImage(img, ix, iy, this);
                }

                // Resplandor dorado si es el turno activo
                if (esTurnoActivo && entidad.isEstaVivo()) {
                    g2.setColor(new Color(241, 196, 15, 60));
                    g2.fillRoundRect(ix - 6, iy - 6, spriteTam + 12, spriteTam + 12, 16, 16);
                    g2.setColor(new Color(241, 196, 15, 240));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(ix - 4, iy - 4, spriteTam + 8, spriteTam + 8, 14, 14);
                }

                // Retícula roja / destacada si está seleccionado como objetivo
                if (esObjetivo && entidad.isEstaVivo()) {
                    g2.setColor(new Color(231, 76, 60, 80));
                    g2.fillRoundRect(ix - 8, iy - 8, spriteTam + 16, spriteTam + 16, 16, 16);
                    g2.setColor(new Color(231, 76, 60, 245));
                    g2.setStroke(new BasicStroke(3.5f));
                    g2.drawRoundRect(ix - 6, iy - 6, spriteTam + 12, spriteTam + 12, 14, 14);
                    dibujarBadgeTexto(g2, ix + (spriteTam - 74) / 2, iy - 16, 74, 15, new Color(231, 76, 60, 235), "OBJETIVO");
                }

                // Badges de estado legibles con texto limpio (sin emojis rotos)
                if (entidad.isEstaVivo()) {
                    int badgeW = 34;
                    int badgeH = 16;
                    int badgeY = iy;
                    int badgeX = ix + spriteTam - badgeW + 4;

                    if (entidad.isDefendiendo()) {
                        dibujarBadgeTexto(g2, badgeX, badgeY, badgeW, badgeH, new Color(52, 152, 219, 230), "DEF");
                        badgeY += badgeH + 2;
                    }
                    if (entidad.isEnvenenado()) {
                        dibujarBadgeTexto(g2, badgeX, badgeY, badgeW, badgeH, new Color(142, 68, 173, 230), "VEN");
                        badgeY += badgeH + 2;
                    }
                    if (entidad.isAturdido()) {
                        dibujarBadgeTexto(g2, badgeX, badgeY, badgeW, badgeH, new Color(243, 156, 18, 230), "ATUR");
                        badgeY += badgeH + 2;
                    }
                    if (entidad.isProvocando()) {
                        dibujarBadgeTexto(g2, badgeX, badgeY, badgeW, badgeH, new Color(230, 126, 34, 230), "PROV");
                    }
                }

                g2.dispose();
            }

            private void dibujarBadgeTexto(Graphics2D g2, int x, int y, int w, int h, Color colorFondo, String texto) {
                g2.setColor(colorFondo);
                g2.fillRoundRect(x, y, w, h, 6, 6);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, y, w, h, 6, 6);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (w - fm.stringWidth(texto)) / 2;
                int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(texto, tx, ty);
            }
        };

        avatarPanel.setPreferredSize(new Dimension(spriteTam + 24, spriteTam + 24));
        avatarPanel.setMaximumSize(new Dimension(spriteTam + 24, spriteTam + 24));
        avatarPanel.setOpaque(false);

        if (onClick != null) {
            avatarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            avatarPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (entidad.isEstaVivo()) {
                        onClick.run();
                    }
                }
            });
        }

        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(avatarPanel);

        add(Box.createVerticalStrut(4));

        // 3. Barra de Vida
        int vidaActual = Math.max(0, entidad.getVida());
        int vidaMaxima = Math.max(1, entidad.getVidaMaxima());
        JProgressBar barraHp = EstilosUI.crearBarra(0, vidaMaxima, new Color(231, 76, 60));
        barraHp.setValue(vidaActual);
        barraHp.setString(vidaActual + "/" + vidaMaxima);
        barraHp.setPreferredSize(new Dimension(spriteTam + 10, 14));
        barraHp.setMaximumSize(new Dimension(spriteTam + 10, 14));
        barraHp.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(barraHp);

        // 4. Barra de Maná (si la entidad usa Maná)
        if (entidad.getManaMaximo() > 0) {
            add(Box.createVerticalStrut(2));
            int manaActual = Math.max(0, entidad.getMana());
            int manaMaximo = Math.max(1, entidad.getManaMaximo());
            JProgressBar barraMp = EstilosUI.crearBarra(0, manaMaximo, new Color(52, 152, 219));
            barraMp.setValue(manaActual);
            barraMp.setString(manaActual + "/" + manaMaximo);
            barraMp.setPreferredSize(new Dimension(spriteTam + 10, 12));
            barraMp.setMaximumSize(new Dimension(spriteTam + 10, 12));
            barraMp.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(barraMp);
        }
    }
}
