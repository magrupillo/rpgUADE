package rpg.vista;

import rpg.controlador.ControladorBatalla;
import rpg.dto.EntidadDTO;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.ItemDTO;
import rpg.dto.PersonajeDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de comandos de combate rediseñado.
 * Muestra el héroe activo en turno, sus estadísticas clave, y los botones de acción
 * con descripción clara. No incluye "Pasar Turno" (los turnos no son voluntariamente omitibles).
 */
public class PanelAcciones extends JPanel {
    private ControladorBatalla controlador;

    // Selectores
    private final JComboBox<String> comboHabilidades;
    private final JComboBox<String> comboObjetivos;
    private final JComboBox<String> comboItems;

    // Botones de acción
    private final JButton btnAtacar;
    private final JButton btnHabilidad;
    private final JButton btnUsarItem;
    private final JButton btnDefender;
    private final JButton btnContinuar;

    // Panel info del héroe activo con sprite
    private JLabel lblNombreHeroe;
    private JLabel lblTipoHeroe;
    private JLabel lblStatsHeroe;
    private JLabel lblTurnoMensaje;
    private JLabel lblSpriteHeroe;

    // Índices internos
    private final List<Integer> indicesEnemigosVivos = new ArrayList<>();
    private final List<Integer> indicesItemsDisponibles = new ArrayList<>();
    private EstadoBatallaDTO estadoActual;

    public PanelAcciones() {
        setLayout(new BorderLayout(6, 4));
        setBackground(EstilosUI.FONDO_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, EstilosUI.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // ── Panel izquierdo: Info del Héroe en Turno (con sprite) ────────────
        JPanel panelInfoHeroe = crearPanelInfoHeroe();
        add(panelInfoHeroe, BorderLayout.WEST);

        // ── Panel central: Selectores + Botones ────────────────────────────────
        JPanel panelCentral = new JPanel(new BorderLayout(4, 4));
        panelCentral.setOpaque(false);

        // Fila de selectores
        JPanel panelSelectores = new JPanel(new GridLayout(1, 3, 8, 0));
        panelSelectores.setOpaque(false);
        panelSelectores.add(crearSelector("🎯 Objetivo:", comboObjetivos = new JComboBox<>(), new Color(192, 57, 43)));
        panelSelectores.add(crearSelector("✨ Habilidad:", comboHabilidades = new JComboBox<>(), new Color(155, 89, 182)));
        panelSelectores.add(crearSelector("🧪 Consumible:", comboItems = new JComboBox<>(), new Color(39, 174, 96)));
        comboHabilidades.addActionListener(e -> {/* solo display */});
        panelCentral.add(panelSelectores, BorderLayout.NORTH);

        // Fila de botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        panelBotones.setOpaque(false);

        btnAtacar = crearBotonAccion("⚔️ ATACAR",
                "Ataque físico básico al objetivo seleccionado",
                new Color(180, 50, 35), new Color(220, 70, 50));
        btnHabilidad = crearBotonAccion("✨ HABILIDAD",
                "Usar la habilidad seleccionada (consume Maná)",
                new Color(120, 55, 165), new Color(145, 80, 190));
        btnUsarItem = crearBotonAccion("🧪 ÍTEM",
                "Consumir el ítem seleccionado del inventario",
                new Color(30, 155, 80), new Color(40, 195, 105));
        btnDefender = crearBotonAccion("🛡️ DEFENDER",
                "Postura defensiva: reduce el daño recibido esta ronda",
                new Color(30, 100, 165), new Color(45, 135, 210));

        btnContinuar = EstilosUI.crearBoton("▶ CONTINUAR", new Color(39, 174, 96), new Color(46, 204, 113));
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnContinuar.setVisible(false);

        panelBotones.add(btnAtacar);
        panelBotones.add(btnHabilidad);
        panelBotones.add(btnUsarItem);
        panelBotones.add(btnDefender);
        panelBotones.add(btnContinuar);
        panelCentral.add(panelBotones, BorderLayout.CENTER);

        // Mensaje de turno inferior
        lblTurnoMensaje = new JLabel("Esperando inicio de combate...", SwingConstants.CENTER);
        lblTurnoMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblTurnoMensaje.setForeground(EstilosUI.TEXTO_MUTED);
        panelCentral.add(lblTurnoMensaje, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        configurarEventos();
    }

    private JPanel crearPanelInfoHeroe() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 45, 70, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(41, 98, 155, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(6, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panel.setPreferredSize(new Dimension(210, 0));

        // Sprite del héroe activo
        lblSpriteHeroe = new JLabel();
        lblSpriteHeroe.setHorizontalAlignment(SwingConstants.CENTER);
        lblSpriteHeroe.setPreferredSize(new Dimension(48, 48));
        panel.add(lblSpriteHeroe, BorderLayout.WEST);

        // Info texto
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);

        JLabel lblHead = new JLabel("▶ TURNO ACTUAL");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblHead.setForeground(EstilosUI.TEXTO_MUTED);
        lblHead.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblNombreHeroe = new JLabel("—");
        lblNombreHeroe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreHeroe.setForeground(EstilosUI.COLOR_ORO);
        lblNombreHeroe.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTipoHeroe = new JLabel("—");
        lblTipoHeroe.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTipoHeroe.setForeground(new Color(155, 89, 182));
        lblTipoHeroe.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStatsHeroe = new JLabel("HP — | MP — | VEL —");
        lblStatsHeroe.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblStatsHeroe.setForeground(EstilosUI.TEXTO_MUTED);
        lblStatsHeroe.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTexto.add(lblHead);
        panelTexto.add(Box.createVerticalStrut(2));
        panelTexto.add(lblNombreHeroe);
        panelTexto.add(lblTipoHeroe);
        panelTexto.add(Box.createVerticalStrut(2));
        panelTexto.add(lblStatsHeroe);
        panel.add(panelTexto, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSelector(String titulo, JComboBox<String> combo, Color colorTitulo) {
        JPanel p = new JPanel(new BorderLayout(2, 1));
        p.setOpaque(false);
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(colorTitulo);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        combo.setBackground(EstilosUI.FONDO_TARJETA);
        combo.setForeground(EstilosUI.TEXTO_CLARO);
        p.add(lbl, BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private JButton crearBotonAccion(String texto, String tooltip, Color base, Color hover) {
        JButton btn = EstilosUI.crearBoton(texto, base, hover);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(130, 34));
        return btn;
    }

    public void setControlador(ControladorBatalla controlador) {
        this.controlador = controlador;
    }

    private void configurarEventos() {
        btnAtacar.addActionListener(e -> {
            if (controlador == null) return;
            controlador.ejecutarAtaqueBasico(obtenerIndiceHeroeActivo(), obtenerIndiceObjetivoSeleccionado());
        });

        btnHabilidad.addActionListener(e -> {
            if (controlador == null) return;
            int habIdx = Math.max(0, comboHabilidades.getSelectedIndex());
            controlador.ejecutarHabilidad(obtenerIndiceHeroeActivo(), habIdx, obtenerIndiceObjetivoSeleccionado());
        });

        btnUsarItem.addActionListener(e -> {
            if (controlador == null) return;
            controlador.usarItem(obtenerIndiceHeroeActivo(), obtenerIndiceItemSeleccionado());
        });

        btnDefender.addActionListener(e -> {
            if (controlador == null) return;
            controlador.defender(obtenerIndiceHeroeActivo());
        });

        btnContinuar.addActionListener(e -> {
            if (controlador != null) controlador.continuarFinDeCombate();
        });
    }

    /** El héroe que actúa viene dictado por la cola de iniciativa, no por selección del usuario. */
    private int obtenerIndiceHeroeActivo() {
        return (estadoActual != null) ? estadoActual.getIndiceHeroeActivo() : 0;
    }

    private int obtenerIndiceObjetivoSeleccionado() {
        int sel = comboObjetivos.getSelectedIndex();
        if (sel >= 0 && sel < indicesEnemigosVivos.size()) return indicesEnemigosVivos.get(sel);
        return 0;
    }

    private int obtenerIndiceItemSeleccionado() {
        int sel = comboItems.getSelectedIndex();
        if (sel >= 0 && sel < indicesItemsDisponibles.size()) return indicesItemsDisponibles.get(sel);
        return 0;
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        this.estadoActual = estadoDTO;

        boolean activa = !estadoDTO.isBatallaTerminada();
        btnAtacar.setEnabled(activa);
        btnHabilidad.setEnabled(activa);
        btnUsarItem.setEnabled(activa);
        btnDefender.setEnabled(activa);
        btnContinuar.setVisible(!activa);

        if (!activa) {
            btnContinuar.setText(estadoDTO.isVictoria() ? "🏆  CONTINUAR" : "💀  RESULTADOS");
            lblTurnoMensaje.setText(estadoDTO.getMensajeFinBatalla());
            lblTurnoMensaje.setForeground(estadoDTO.isVictoria() ? EstilosUI.COLOR_ORO : EstilosUI.COLOR_ROJO);
            lblNombreHeroe.setText(estadoDTO.isVictoria() ? "¡VICTORIA!" : "DERROTA");
            lblNombreHeroe.setForeground(estadoDTO.isVictoria() ? EstilosUI.COLOR_ORO : EstilosUI.COLOR_ROJO);
            lblTipoHeroe.setText("");
            lblStatsHeroe.setText("");
            lblSpriteHeroe.setIcon(null);
        } else {
            lblTurnoMensaje.setForeground(EstilosUI.TEXTO_MUTED);
            // Actualizar info del héroe activo
            int idxActivo = estadoDTO.getIndiceHeroeActivo();
            List<PersonajeDTO> heroes = estadoDTO.getHeroes();
            if (idxActivo >= 0 && idxActivo < heroes.size()) {
                PersonajeDTO h = heroes.get(idxActivo);
                lblNombreHeroe.setText(h.getNombre());
                lblNombreHeroe.setForeground(EstilosUI.COLOR_ORO);
                lblTipoHeroe.setText(h.getTipo());
                lblStatsHeroe.setText(
                        "HP " + h.getVidaActual() + "/" + h.getVidaMaxima()
                        + "  MP " + h.getManaActual() + "/" + h.getManaMaximo()
                        + "  VEL " + h.getVelocidad());
                lblTurnoMensaje.setText("Seleccioná una acción para " + h.getNombre());
                // Actualizar sprite del héroe activo
                lblSpriteHeroe.setIcon(new ImageIcon(GestorRecursos.obtenerSprite(h.getTipo(), 44, 44)));
            } else {
                lblNombreHeroe.setText("Enemigos actuando...");
                lblNombreHeroe.setForeground(EstilosUI.COLOR_ROJO);
                lblTipoHeroe.setText("");
                lblStatsHeroe.setText("");
                lblTurnoMensaje.setText("");
                lblSpriteHeroe.setIcon(null);
            }

            // Habilidades del héroe activo
            comboHabilidades.removeAllItems();
            if (idxActivo >= 0 && idxActivo < heroes.size()) {
                for (String hab : heroes.get(idxActivo).getHabilidades()) {
                    comboHabilidades.addItem(hab);
                }
            }
            if (comboHabilidades.getItemCount() == 0) comboHabilidades.addItem("Habilidad Básica");
        }

        // Combo de objetivos (enemigos vivos)
        comboObjetivos.removeAllItems();
        indicesEnemigosVivos.clear();
        List<EntidadDTO> enemigos = estadoDTO.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            EntidadDTO e = enemigos.get(i);
            if (e.isEstaVivo()) {
                indicesEnemigosVivos.add(i);
                comboObjetivos.addItem(e.getNombre() + " (HP " + e.getVidaActual() + "/" + e.getVidaMaxima() + ")");
            }
        }

        // Combo de ítems
        comboItems.removeAllItems();
        indicesItemsDisponibles.clear();
        for (ItemDTO it : estadoDTO.getInventario()) {
            if (it.isEsConsumible()) {
                indicesItemsDisponibles.add(it.getIndice());
                comboItems.addItem(it.getNombre());
            }
        }
        if (comboItems.getItemCount() == 0) {
            comboItems.addItem("(Sin consumibles)");
            btnUsarItem.setEnabled(false);
        }
    }
}
