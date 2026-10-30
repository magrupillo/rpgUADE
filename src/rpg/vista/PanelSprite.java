package rpg.vista;

import rpg.dto.EntidadDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;

/**
 * Panel visual para cada entidad en la escena de batalla.
 * Muestra el sprite (con Bowser más grande), barras de HP/MP, y badges de estado legibles sin emojis rotos.
 */
public class PanelSprite extends JPanel {

    private final EntidadDTO entidad;
    private final boolean esTurnoActivo;
    private final int spriteTam;

    public PanelSprite(EntidadDTO entidad, boolean esTurnoActivo) {
        this.entidad = entidad;
        this.esTurnoActivo = esTurnoActivo;
        // Si es el Jefe (Bowser), renderizar sprite significativamente más grande
        boolean esJefe = entidad.getTipo().equalsIgnoreCase("jefe") || entidad.getNombre().toLowerCase().contains("bowser");
        this.spriteTam = esJefe ? 115 : 80;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        construirVista();
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

            private void dibujarBadgeTexto(Graphics2D g2, int x, int y, int w, int h, Color colorFondo, String txt) {
                g2.setColor(colorFondo);
                g2.fillRoundRect(x, y, w, h, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (w - fm.stringWidth(txt)) / 2;
                int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(txt, tx, ty);
            }
        };

        avatarPanel.setPreferredSize(new Dimension(spriteTam + 24, spriteTam + 16));
        avatarPanel.setMaximumSize(new Dimension(spriteTam + 30, spriteTam + 20));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarPanel.setOpaque(false);
        add(avatarPanel);

        add(Box.createVerticalStrut(4));

        // 3. Barra de HP
        int vidaActual = Math.max(0, entidad.getVida());
        int vidaMax = Math.max(1, entidad.getVidaMaxima());
        JProgressBar barraHp = EstilosUI.crearBarra(0, vidaMax,
                (vidaActual < vidaMax * 0.3) ? EstilosUI.COLOR_VIDA_BAJA : EstilosUI.COLOR_VIDA);
        barraHp.setValue(vidaActual);
        barraHp.setString("HP: " + vidaActual + "/" + vidaMax);
        barraHp.setFont(new Font("Segoe UI", Font.BOLD, 10));
        barraHp.setPreferredSize(new Dimension(spriteTam + 20, 15));
        barraHp.setMaximumSize(new Dimension(spriteTam + 24, 15));
        barraHp.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(barraHp);

        // 4. Barra de MP (para héroes)
        if (entidad.getManaMaximo() > 0) {
            add(Box.createVerticalStrut(2));
            int manaActual = Math.max(0, entidad.getMana());
            int manaMax = Math.max(1, entidad.getManaMaximo());
            JProgressBar barraMp = EstilosUI.crearBarra(0, manaMax, EstilosUI.COLOR_MANA);
            barraMp.setValue(manaActual);
            barraMp.setString("MP: " + manaActual + "/" + manaMax);
            barraMp.setFont(new Font("Segoe UI", Font.BOLD, 10));
            barraMp.setPreferredSize(new Dimension(spriteTam + 20, 13));
            barraMp.setMaximumSize(new Dimension(spriteTam + 24, 13));
            barraMp.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(barraMp);
        }
    }
}
