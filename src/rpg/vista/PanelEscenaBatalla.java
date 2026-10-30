package rpg.vista;

import rpg.dto.EntidadDTO;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.PersonajeDTO;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra la escena visual del combate:
 *  - Barra de iniciativa con el orden de turnos (heroes y enemigos intercalados por velocidad)
 *  - Sprites de la party (izquierda) y enemigos (derecha)
 *  - Indicador del lugar y ronda actual
 */
public class PanelEscenaBatalla extends JPanel {
    private final JPanel panelHeroes;
    private final JPanel panelEnemigos;
    private final JLabel lblLugar;
    private final JLabel lblRonda;
    private final PanelColaIniciativa panelCola;

    public PanelEscenaBatalla() {
        setLayout(new BorderLayout(6, 4));
        setBackground(EstilosUI.FONDO_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // ── Cabecera: lugar + ronda ─────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setOpaque(false);

        lblLugar = new JLabel("📍 Campo de Batalla", SwingConstants.LEFT);
        lblLugar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLugar.setForeground(EstilosUI.COLOR_ORO);

        lblRonda = new JLabel("Ronda 1", SwingConstants.RIGHT);
        lblRonda.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblRonda.setForeground(EstilosUI.TEXTO_MUTED);

        headerPanel.add(lblLugar, BorderLayout.WEST);
        headerPanel.add(lblRonda, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ── Cola de iniciativa visual ───────────────────────────────────────────
        panelCola = new PanelColaIniciativa();
        add(panelCola, BorderLayout.SOUTH);

        // ── Escena de combate central ───────────────────────────────────────────
        JPanel panelCombate = new JPanel(new GridBagLayout());
        panelCombate.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        panelHeroes = new JPanel();
        panelHeroes.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        panelHeroes.setOpaque(false);
        panelHeroes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185, 120), 1, true),
                "🛡️  PARTY",
                0, 0, new Font("Segoe UI", Font.BOLD, 11), new Color(52, 152, 219)
        ));
        gbc.gridx = 0;
        gbc.weightx = 0.46;
        panelCombate.add(panelHeroes, gbc);

        JPanel panelVS = new JPanel(new GridBagLayout());
        panelVS.setOpaque(false);
        JLabel lblVS = new JLabel("⚔️");
        lblVS.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        panelVS.add(lblVS);
        gbc.gridx = 1;
        gbc.weightx = 0.08;
        panelCombate.add(panelVS, gbc);

        panelEnemigos = new JPanel();
        panelEnemigos.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        panelEnemigos.setOpaque(false);
        panelEnemigos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(192, 57, 43, 120), 1, true),
                "👹  ENEMIGOS",
                0, 0, new Font("Segoe UI", Font.BOLD, 11), new Color(231, 76, 60)
        ));
        gbc.gridx = 2;
        gbc.weightx = 0.46;
        panelCombate.add(panelEnemigos, gbc);

        add(panelCombate, BorderLayout.CENTER);
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        lblLugar.setText("📍 " + estadoDTO.getLugar());
        lblRonda.setText("Ronda " + estadoDTO.getRondaActual() + "   •   " + estadoDTO.getTurnoActual());

        // Cola de iniciativa
        panelCola.actualizar(estadoDTO.getColaIniciativa(), estadoDTO.getPosicionActualEnCola());

        // Determinar sprite size based on entity count
        int totalEntities = estadoDTO.getHeroes().size() + estadoDTO.getEnemigos().size();
        int spriteSize = totalEntities > 6 ? 64 : 80;

        // Héroes
        panelHeroes.removeAll();
        List<PersonajeDTO> heroes = estadoDTO.getHeroes();
        for (int i = 0; i < heroes.size(); i++) {
            PersonajeDTO h = heroes.get(i);
            boolean esTurno = !estadoDTO.isBatallaTerminada() && i == estadoDTO.getIndiceHeroeActivo();
            panelHeroes.add(new PanelSprite(h, esTurno, spriteSize));
        }

        // Enemigos
        panelEnemigos.removeAll();
        for (EntidadDTO e : estadoDTO.getEnemigos()) {
            panelEnemigos.add(new PanelSprite(e, false, spriteSize));
        }

        panelHeroes.revalidate();
        panelHeroes.repaint();
        panelEnemigos.revalidate();
        panelEnemigos.repaint();
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Panel interno: barra horizontal de la cola de iniciativa
    // ──────────────────────────────────────────────────────────────────────────────
    private static class PanelColaIniciativa extends JPanel {
        private List<String> cola = List.of();
        private int posActual = 0;

        PanelColaIniciativa() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 2));
            setPreferredSize(new Dimension(0, 34));
        }

        void actualizar(List<String> cola, int posActual) {
            this.cola = cola;
            this.posActual = posActual;
            rebuild();
        }

        private void rebuild() {
            removeAll();

            if (cola.isEmpty()) { revalidate(); repaint(); return; }

            JLabel lblTitulo = new JLabel("ORDEN: ");
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblTitulo.setForeground(EstilosUI.TEXTO_MUTED);
            add(lblTitulo);

            for (int i = 0; i < cola.size(); i++) {
                String[] partes = cola.get(i).split("\\|");
                String nombre = partes[0];
                boolean esHeroe = partes.length > 1 && partes[1].equals("heroe");
                String vel = partes.length > 2 ? partes[2] : "?";
                boolean esActual = (i == posActual);

                JPanel chip = crearChip(nombre, vel, esHeroe, esActual);
                add(chip);

                // Separador flecha (excepto el último)
                if (i < cola.size() - 1) {
                    JLabel flecha = new JLabel("›");
                    flecha.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    flecha.setForeground(new Color(80, 90, 110));
                    add(flecha);
                }
            }
            revalidate();
            repaint();
        }

        private JPanel crearChip(String nombre, String vel, boolean esHeroe, boolean esActual) {
            JPanel chip = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg;
                    if (esActual) {
                        bg = esHeroe ? new Color(41, 98, 155, 230) : new Color(140, 40, 40, 230);
                    } else {
                        bg = esHeroe ? new Color(30, 45, 70, 180) : new Color(60, 25, 25, 180);
                    }
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    if (esActual) {
                        g2.setColor(esHeroe ? EstilosUI.COLOR_ORO : EstilosUI.COLOR_ROJO);
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            chip.setOpaque(false);
            chip.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 1));
            chip.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

            // Truncate long names
            String displayName = nombre.length() > 10 ? nombre.substring(0, 9) + "…" : nombre;
            String icono = esHeroe ? "⚔" : "💀";
            JLabel lblNom = new JLabel(icono + " " + displayName);
            lblNom.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblNom.setForeground(esActual ? Color.WHITE : EstilosUI.TEXTO_MUTED);

            JLabel lblVel = new JLabel("V" + vel);
            lblVel.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            lblVel.setForeground(esActual
                    ? (esHeroe ? EstilosUI.COLOR_ORO : new Color(255, 120, 120))
                    : new Color(100, 110, 130));

            chip.add(lblNom);
            chip.add(lblVel);
            return chip;
        }
    }
}
