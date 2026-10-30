package rpg.vista;

import rpg.dto.EntidadDTO;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.PersonajeDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra la escena visual del combate:
 *  - Fondo ambientado con 'batalla.png'
 *  - Barra de iniciativa con el orden de turnos por velocidad
 *  - Sprites de la party (izquierda) y enemigos (derecha)
 *  - Indicador del lugar y ronda actual
 */
public class PanelEscenaBatalla extends JPanel {

    private final JPanel panelHeroes;
    private final JPanel panelEnemigos;
    private final JLabel lblLugar;
    private final JLabel lblRonda;
    private final PanelColaIniciativa panelCola;
    private final Image fondoBatalla;

    public PanelEscenaBatalla() {
        this.fondoBatalla = GestorRecursos.obtenerFondo("batalla");
        setLayout(new BorderLayout(6, 4));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // ── Cabecera: lugar + ronda ─────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setOpaque(false);

        lblLugar = new JLabel("Campo de Batalla", SwingConstants.LEFT);
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
        panelHeroes.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelHeroes.setOpaque(false);
        panelHeroes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185, 140), 1, true),
                "PARTY",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(52, 152, 219)
        ));
        gbc.gridx = 0;
        gbc.weightx = 0.46;
        panelCombate.add(panelHeroes, gbc);

        JPanel panelVS = new JPanel(new GridBagLayout());
        panelVS.setOpaque(false);
        JLabel lblVS = new JLabel("VS");
        lblVS.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVS.setForeground(new Color(230, 126, 34));
        panelVS.add(lblVS);
        gbc.gridx = 1;
        gbc.weightx = 0.08;
        panelCombate.add(panelVS, gbc);

        panelEnemigos = new JPanel();
        panelEnemigos.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelEnemigos.setOpaque(false);
        panelEnemigos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(192, 57, 43, 140), 1, true),
                "ENEMIGOS",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(231, 76, 60)
        ));
        gbc.gridx = 2;
        gbc.weightx = 0.46;
        panelCombate.add(panelEnemigos, gbc);

        add(panelCombate, BorderLayout.CENTER);
    }

    private int objetivoSeleccionadoIdx = -1;
    private java.util.function.Consumer<Integer> onObjetivoClick;
    private EstadoBatallaDTO ultimoEstado;

    public void setOnObjetivoClick(java.util.function.Consumer<Integer> listener) {
        this.onObjetivoClick = listener;
    }

    public void setObjetivoSeleccionado(int idx) {
        this.objetivoSeleccionadoIdx = idx;
        if (ultimoEstado != null) {
            actualizar(ultimoEstado);
        }
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        this.ultimoEstado = estadoDTO;
        lblLugar.setText(estadoDTO.getLugar());
        lblRonda.setText("Ronda " + estadoDTO.getRondaActual() + "   •   " + estadoDTO.getTurnoActual());

        // Cola de iniciativa
        panelCola.actualizar(estadoDTO.getColaIniciativa(), estadoDTO.getPosicionActualEnCola());

        // Héroes
        panelHeroes.removeAll();
        List<PersonajeDTO> heroes = estadoDTO.getHeroes();
        for (int i = 0; i < heroes.size(); i++) {
            PersonajeDTO h = heroes.get(i);
            boolean esTurno = !estadoDTO.isBatallaTerminada() && i == estadoDTO.getIndiceHeroeActivo();
            panelHeroes.add(new PanelSprite(h, esTurno));
        }

        // Enemigos interactivos: clic sobre el sprite selecciona/deselecciona objetivo
        panelEnemigos.removeAll();
        List<EntidadDTO> enemigos = estadoDTO.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            final int enemyIdx = i;
            EntidadDTO e = enemigos.get(i);
            boolean esObj = (enemyIdx == objetivoSeleccionadoIdx);
            panelEnemigos.add(new PanelSprite(e, false, esObj, () -> {
                if (onObjetivoClick != null) {
                    onObjetivoClick.accept(enemyIdx);
                }
            }));
        }

        panelHeroes.revalidate();
        panelHeroes.repaint();
        panelEnemigos.revalidate();
        panelEnemigos.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoBatalla != null) {
            g2.drawImage(fondoBatalla, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(14, 16, 22, 180));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_PANEL);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        EstilosUI.pintarVineta(g2, getWidth(), getHeight(), 190);
        g2.dispose();
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Barra horizontal de la cola de iniciativa
    // ──────────────────────────────────────────────────────────────────────────────
    private static class PanelColaIniciativa extends JPanel {
        private List<String> cola = List.of();
        private int posActual = 0;

        PanelColaIniciativa() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setPreferredSize(new Dimension(0, 36));
        }

        void actualizar(List<String> colaNombres, int posicionActual) {
            this.cola = colaNombres != null ? colaNombres : List.of();
            this.posActual = posicionActual;
            removeAll();

            JLabel lblIni = new JLabel("Iniciativa: ");
            lblIni.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblIni.setForeground(EstilosUI.COLOR_ORO);
            add(lblIni);

            for (int i = 0; i < cola.size(); i++) {
                String entry = cola.get(i);
                String[] parts = entry.split("\\|");
                String nombre = parts[0];
                boolean esHeroe = parts.length > 1 && parts[1].equals("heroe");
                String vel = parts.length > 2 ? parts[2] : "";

                boolean esTurnoActivo = (i == posActual);

                Color base = esHeroe ? new Color(36, 110, 165) : new Color(175, 45, 35);
                Color colorFondo = esTurnoActivo ? new Color(241, 196, 15) : base;

                JPanel ficha = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(colorFondo);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        if (esTurnoActivo) {
                            g2.setColor(Color.WHITE);
                            g2.setStroke(new BasicStroke(1.5f));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                        } else {
                            g2.setColor(new Color(255, 255, 255, 40));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                ficha.setOpaque(false);
                ficha.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 8));

                JLabel lblNombre = new JLabel((i + 1) + ". " + nombre + " (" + vel + ")");
                lblNombre.setFont(new Font("Segoe UI", esTurnoActivo ? Font.BOLD : Font.PLAIN, 10));
                lblNombre.setForeground(esTurnoActivo ? Color.BLACK : Color.WHITE);
                ficha.add(lblNombre);

                add(ficha);
            }

            revalidate();
            repaint();
        }
    }
}
