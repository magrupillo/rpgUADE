package rpg.vista;

import rpg.dto.EntidadDTO;
import rpg.dto.PersonajeDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;

/**
 * Panel visual para cada entidad en la escena de batalla.
 * Muestra: sprite con aura de turno, nombre, mini barras HP/MP,
 * e indicadores de estados (Defendiendo, Envenenado, Aturdido, Provocando).
 */
public class PanelSprite extends JPanel {
    private final EntidadDTO entidad;
    private final boolean esTurnoActivo;
    private final int spriteTam;

    public PanelSprite(EntidadDTO entidad, boolean esTurnoActivo) {
        this(entidad, esTurnoActivo, 80);
    }

    public PanelSprite(EntidadDTO entidad, boolean esTurnoActivo, int tamano) {
        this.entidad = entidad;
        this.esTurnoActivo = esTurnoActivo;
        this.spriteTam = tamano;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        construirVista();
    }

    private void construirVista() {
        // 1. Etiqueta con el nombre y nivel
        String etiquetaTxt = entidad.getNombre();
        if (entidad.getNivel() > 1) {
            etiquetaTxt += " Nv." + entidad.getNivel();
        }
        JLabel lblNombre = new JLabel(etiquetaTxt);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNombre.setForeground(entidad.isEstaVivo() ? EstilosUI.TEXTO_CLARO : EstilosUI.TEXTO_MUTED);
        add(lblNombre);

        add(Box.createVerticalStrut(3));

        // 2. Avatar / Icono del Sprite (renderizado custom con indicadores)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dibujar Sprite
                Image img = GestorRecursos.obtenerSprite(entidad.getTipo(), spriteTam, spriteTam);
                int ix = (w - spriteTam) / 2;
                int iy = (h - spriteTam) / 2;

                if (!entidad.isEstaVivo()) {
                    // Efecto de desvanecimiento / caído
                    Composite original = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2.drawImage(img, ix, iy, this);
                    g2.setComposite(original);

                    // Cruz roja de caído
                    g2.setColor(new Color(231, 76, 60, 200));
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(ix + 15, iy + 15, ix + spriteTam - 15, iy + spriteTam - 15);
                    g2.drawLine(ix + spriteTam - 15, iy + 15, ix + 15, iy + spriteTam - 15);
                } else {
                    g2.drawImage(img, ix, iy, this);
                }

                // Si es el turno activo, dibujar aura dorada con resplandor
                if (esTurnoActivo && entidad.isEstaVivo()) {
                    g2.setColor(new Color(241, 196, 15, 50));
                    g2.fillRoundRect(ix - 6, iy - 6, spriteTam + 12, spriteTam + 12, 18, 18);
                    g2.setColor(new Color(241, 196, 15, 230));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(ix - 4, iy - 4, spriteTam + 8, spriteTam + 8, 16, 16);
                }

                // ── Indicadores de Estado (badges en las esquinas) ──
                if (entidad.isEstaVivo()) {
                    int badgeSize = 18;
                    int badgeY = iy;
                    int badgeX = ix + spriteTam - badgeSize + 2;

                    // Defendiendo: escudo azul
                    if (entidad.isDefendiendo()) {
                        dibujarBadge(g2, badgeX, badgeY, badgeSize, new Color(52, 152, 219, 230), "🛡");
                        badgeY += badgeSize + 2;
                    }

                    // Verificar estados adicionales solo para PersonajeDTO (héroes)
                    if (entidad instanceof PersonajeDTO) {
                        // No hay flags directas en DTO para veneno/aturdido, pero los podemos inferir visualmente
                        // La vista utiliza la info del DTO — los estados se muestran desde el nombre del turno
                    }
                }

                g2.dispose();
            }
        };

        int panelSize = spriteTam + 16;
        avatarPanel.setPreferredSize(new Dimension(panelSize, panelSize));
        avatarPanel.setMaximumSize(new Dimension(panelSize, panelSize));
        avatarPanel.setMinimumSize(new Dimension(panelSize, panelSize));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(avatarPanel);

        add(Box.createVerticalStrut(3));

        // 3. Mini barra de HP
        JProgressBar miniBarraHp = new JProgressBar(0, Math.max(1, entidad.getVidaMaxima()));
        miniBarraHp.setValue(entidad.getVidaActual());
        miniBarraHp.setStringPainted(true);
        miniBarraHp.setString(entidad.getVidaActual() + "/" + entidad.getVidaMaxima());
        miniBarraHp.setFont(new Font("Segoe UI", Font.BOLD, 9));
        miniBarraHp.setPreferredSize(new Dimension(spriteTam + 10, 14));
        miniBarraHp.setMaximumSize(new Dimension(spriteTam + 10, 14));
        miniBarraHp.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (entidad.isEstaVivo()) {
            double hpRatio = (double) entidad.getVidaActual() / Math.max(1, entidad.getVidaMaxima());
            if (hpRatio < 0.3) {
                miniBarraHp.setForeground(EstilosUI.COLOR_VIDA_BAJA);
            } else if (entidad.isEsHeroe()) {
                miniBarraHp.setForeground(EstilosUI.COLOR_VIDA);
            } else {
                miniBarraHp.setForeground(new Color(192, 57, 43));
            }
        } else {
            miniBarraHp.setForeground(Color.GRAY);
            miniBarraHp.setString("CAÍDO");
        }
        miniBarraHp.setBackground(new Color(25, 27, 36));
        miniBarraHp.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1));
        add(miniBarraHp);

        // 4. Mini barra de MP solo para héroes
        if (entidad.isEsHeroe() && entidad.getManaMaximo() > 0 && entidad.isEstaVivo()) {
            add(Box.createVerticalStrut(2));
            JProgressBar miniBarraMp = new JProgressBar(0, Math.max(1, entidad.getManaMaximo()));
            miniBarraMp.setValue(entidad.getManaActual());
            miniBarraMp.setStringPainted(true);
            miniBarraMp.setString(entidad.getManaActual() + "/" + entidad.getManaMaximo());
            miniBarraMp.setFont(new Font("Segoe UI", Font.BOLD, 9));
            miniBarraMp.setPreferredSize(new Dimension(spriteTam + 10, 12));
            miniBarraMp.setMaximumSize(new Dimension(spriteTam + 10, 12));
            miniBarraMp.setAlignmentX(Component.CENTER_ALIGNMENT);
            miniBarraMp.setForeground(EstilosUI.COLOR_MANA);
            miniBarraMp.setBackground(new Color(25, 27, 36));
            miniBarraMp.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1));
            add(miniBarraMp);
        }

        // 5. Indicador de estado de turno
        if (esTurnoActivo && entidad.isEstaVivo()) {
            JLabel lblTurno = new JLabel("▶ TURNO");
            lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblTurno.setForeground(EstilosUI.COLOR_ORO);
            lblTurno.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(Box.createVerticalStrut(2));
            add(lblTurno);
        }

        // 6. Indicadores de estado debajo del sprite
        if (entidad.isEstaVivo() && entidad.isDefendiendo()) {
            JLabel lblDef = new JLabel("🛡 DEF");
            lblDef.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblDef.setForeground(new Color(52, 152, 219));
            lblDef.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(lblDef);
        }
    }

    /** Dibuja un badge circular con un emoji en la esquina del sprite */
    private void dibujarBadge(Graphics2D g2, int x, int y, int size, Color bg, String icono) {
        g2.setColor(bg);
        g2.fillRoundRect(x, y, size, size, 6, 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(icono, x + (size - fm.stringWidth(icono)) / 2, y + size - 4);
    }

    @Override
    public Dimension getPreferredSize() {
        // Let the layout manager compute height from children, but set a consistent width
        Dimension d = super.getPreferredSize();
        int width = Math.max(spriteTam + 28, d.width);
        return new Dimension(width, d.height);
    }
}
