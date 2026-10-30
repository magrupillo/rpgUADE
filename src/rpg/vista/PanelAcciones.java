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
 * Panel de comandos de combate.
 * Muestra el héroe activo en turno con su sprite y estadísticas, selectores claros y botones
 * con descripciones tácticas explícitas sin emojis rotos.
 */
public class PanelAcciones extends JPanel {

    private ControladorBatalla controlador;

    private final JComboBox<String> comboHabilidades;
    private final JComboBox<String> comboObjetivos;
    private final JComboBox<String> comboItems;

    private final JButton btnAtacar;
    private final JButton btnHabilidad;
    private final JButton btnUsarItem;
    private final JButton btnDefender;
    private final JButton btnContinuar;

    private JLabel lblNombreHeroe;
    private JLabel lblTipoHeroe;
    private JLabel lblStatsHeroe;
    private JLabel lblTurnoMensaje;
    private JLabel lblSpriteHeroe;

    private final List<Integer> indicesEnemigosVivos = new ArrayList<>();
    private final List<Integer> indicesItemsDisponibles = new ArrayList<>();
    private EstadoBatallaDTO estadoActual;

    public PanelAcciones() {
        setLayout(new BorderLayout(6, 4));
        setBackground(EstilosUI.FONDO_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, EstilosUI.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // ── Panel izquierdo: Info del Héroe en Turno con Sprite ───────────────────
        JPanel panelInfoHeroe = crearPanelInfoHeroe();
        add(panelInfoHeroe, BorderLayout.WEST);

        // ── Panel central: Selectores + Botones ──────────────────────────────────
        JPanel panelCentral = new JPanel(new BorderLayout(4, 4));
        panelCentral.setOpaque(false);

        // Fila de selectores
        JPanel panelSelectores = new JPanel(new GridLayout(1, 3, 10, 0));
        panelSelectores.setOpaque(false);
        panelSelectores.add(crearSelector("Objetivo:", comboObjetivos = new JComboBox<>(), new Color(192, 57, 43)));
        panelSelectores.add(crearSelector("Habilidad:", comboHabilidades = new JComboBox<>(), new Color(155, 89, 182)));
        panelSelectores.add(crearSelector("Consumible:", comboItems = new JComboBox<>(), new Color(39, 174, 96)));
        panelCentral.add(panelSelectores, BorderLayout.NORTH);

        // Fila de botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        panelBotones.setOpaque(false);

        btnAtacar = crearBotonAccion("ATACAR",
                "Ataque físico básico contra el objetivo seleccionado",
                new Color(180, 50, 35), new Color(220, 70, 50));
        btnHabilidad = crearBotonAccion("HABILIDAD",
                "Usar la técnica seleccionada (consume Maná/Ki)",
                new Color(120, 55, 165), new Color(145, 80, 190));
        btnUsarItem = crearBotonAccion("USAR ÍTEM",
                "Consumir la poción seleccionada del inventario compartido",
                new Color(30, 155, 80), new Color(40, 195, 105));
        btnDefender = crearBotonAccion("DEFENDER",
                "Postura defensiva: +100% DEF y regenera 10% MP/Ki hasta tu próximo turno",
                new Color(30, 100, 165), new Color(45, 135, 210));

        btnContinuar = EstilosUI.crearBoton("CONTINUAR", new Color(39, 174, 96), new Color(46, 204, 113));
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnContinuar.setVisible(false);

        panelBotones.add(btnAtacar);
        panelBotones.add(btnHabilidad);
        panelBotones.add(btnUsarItem);
        panelBotones.add(btnDefender);
        panelBotones.add(btnContinuar);
        panelCentral.add(panelBotones, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        configurarEventos();
    }

    private JPanel crearPanelInfoHeroe() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(300, 95));

        lblSpriteHeroe = new JLabel();
        lblSpriteHeroe.setPreferredSize(new Dimension(54, 54));
        p.add(lblSpriteHeroe);

        JPanel infoTexto = new JPanel();
        infoTexto.setLayout(new BoxLayout(infoTexto, BoxLayout.Y_AXIS));
        infoTexto.setOpaque(false);

        lblTurnoMensaje = new JLabel("Esperando...");
        lblTurnoMensaje.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTurnoMensaje.setForeground(new Color(241, 196, 15));

        lblNombreHeroe = new JLabel("Héroe Activo");
        lblNombreHeroe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreHeroe.setForeground(EstilosUI.TEXTO_CLARO);

        lblTipoHeroe = new JLabel("Clase");
        lblTipoHeroe.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTipoHeroe.setForeground(EstilosUI.TEXTO_MUTED);

        lblStatsHeroe = new JLabel("HP: --/-- | MP: --/--");
        lblStatsHeroe.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatsHeroe.setForeground(new Color(46, 204, 113));

        infoTexto.add(lblTurnoMensaje);
        infoTexto.add(lblNombreHeroe);
        infoTexto.add(lblTipoHeroe);
        infoTexto.add(lblStatsHeroe);
        p.add(infoTexto);

        return p;
    }

    private JPanel crearSelector(String etiqueta, JComboBox<String> combo, Color colorBorde) {
        JPanel p = new JPanel(new BorderLayout(3, 1));
        p.setOpaque(false);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(EstilosUI.TEXTO_CLARO);
        p.add(lbl, BorderLayout.NORTH);

        combo.setBackground(EstilosUI.FONDO_TARJETA);
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        combo.setBorder(BorderFactory.createLineBorder(colorBorde, 1));
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    private JButton crearBotonAccion(String texto, String tooltip, Color base, Color hover) {
        JButton btn = EstilosUI.crearBoton(texto, base, hover);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(135, 34));
        return btn;
    }

    private void configurarEventos() {
        btnAtacar.addActionListener(e -> {
            if (controlador == null || estadoActual == null) return;
            int idxObj = obtenerIndiceObjetivoSeleccionado();
            if (idxObj < 0) return;
            controlador.atacar(estadoActual.getIndiceHeroeActivo(), idxObj);
        });

        btnHabilidad.addActionListener(e -> {
            if (controlador == null || estadoActual == null) return;
            int idxHab = comboHabilidades.getSelectedIndex();
            if (idxHab < 0) return;
            int idxObj = obtenerIndiceObjetivoSeleccionado();
            controlador.usarHabilidad(estadoActual.getIndiceHeroeActivo(), idxHab, Math.max(0, idxObj));
        });

        btnUsarItem.addActionListener(e -> {
            if (controlador == null || estadoActual == null) return;
            int sel = comboItems.getSelectedIndex();
            if (sel < 0 || sel >= indicesItemsDisponibles.size()) return;
            int idxItemInventario = indicesItemsDisponibles.get(sel);
            controlador.usarItem(estadoActual.getIndiceHeroeActivo(), idxItemInventario, estadoActual.getIndiceHeroeActivo());
        });

        btnDefender.addActionListener(e -> {
            if (controlador == null || estadoActual == null) return;
            controlador.defender(estadoActual.getIndiceHeroeActivo());
        });

        btnContinuar.addActionListener(e -> {
            if (controlador != null && estadoActual != null) {
                controlador.procesarFinDeCombate(estadoActual.isVictoria());
            }
        });
    }

    private int obtenerIndiceObjetivoSeleccionado() {
        int sel = comboObjetivos.getSelectedIndex();
        if (sel >= 0 && sel < indicesEnemigosVivos.size()) {
            return indicesEnemigosVivos.get(sel);
        }
        return -1;
    }

    public void setControlador(ControladorBatalla controlador) {
        this.controlador = controlador;
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        this.estadoActual = estadoDTO;

        if (estadoDTO.isBatallaTerminada()) {
            setBotonesAccionHabilitados(false);
            btnContinuar.setVisible(true);
            btnContinuar.setText(estadoDTO.isVictoria() ? "VER RECOMPENSAS" : "VER RESULTADOS");
            lblTurnoMensaje.setText(estadoDTO.isVictoria() ? "Victoria!" : "Derrota!");
            lblStatsHeroe.setText(estadoDTO.getMensajeFin());
            return;
        }

        btnContinuar.setVisible(false);
        setBotonesAccionHabilitados(true);

        int idxHeroe = estadoDTO.getIndiceHeroeActivo();
        List<PersonajeDTO> heroes = estadoDTO.getHeroes();
        if (idxHeroe >= 0 && idxHeroe < heroes.size()) {
            PersonajeDTO h = heroes.get(idxHeroe);
            lblTurnoMensaje.setText("TU TURNO");
            lblNombreHeroe.setText(h.getNombre());
            lblTipoHeroe.setText(h.getTipo() + " (Nv." + h.getNivel() + ")");
            lblStatsHeroe.setText("HP: " + h.getVida() + "/" + h.getVidaMaxima()
                    + " | MP: " + h.getMana() + "/" + h.getManaMaximo()
                    + " | ATQ: " + h.getAtaque() + " | DEF: " + h.getDefensa());

            Image img = GestorRecursos.obtenerSprite(h.getTipo(), 54, 54);
            if (img != null) lblSpriteHeroe.setIcon(new ImageIcon(img));

            // Cargar combo de habilidades
            comboHabilidades.removeAllItems();
            for (String hab : h.getHabilidades()) {
                comboHabilidades.addItem(hab);
            }
        }

        // Cargar objetivos (enemigos vivos)
        comboObjetivos.removeAllItems();
        indicesEnemigosVivos.clear();
        List<EntidadDTO> enemigos = estadoDTO.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            EntidadDTO e = enemigos.get(i);
            if (e.isEstaVivo()) {
                indicesEnemigosVivos.add(i);
                comboObjetivos.addItem(e.getNombre() + " (" + e.getVida() + "/" + e.getVidaMaxima() + " HP)");
            }
        }

        // Cargar ítems consumibles disponibles
        comboItems.removeAllItems();
        indicesItemsDisponibles.clear();
        for (ItemDTO it : estadoDTO.getInventario()) {
            if (it.isConsumible()) {
                indicesItemsDisponibles.add(it.getIndiceInventario());
                comboItems.addItem(it.getNombre());
            }
        }
        btnUsarItem.setEnabled(!indicesItemsDisponibles.isEmpty());
    }

    private void setBotonesAccionHabilitados(boolean hab) {
        btnAtacar.setEnabled(hab);
        btnHabilidad.setEnabled(hab);
        btnUsarItem.setEnabled(hab);
        btnDefender.setEnabled(hab);
        comboObjetivos.setEnabled(hab);
        comboHabilidades.setEnabled(hab);
        comboItems.setEnabled(hab);
    }
}
