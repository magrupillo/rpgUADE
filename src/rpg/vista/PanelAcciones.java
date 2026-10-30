package rpg.vista;

import rpg.controlador.ControladorBatalla;
import rpg.dto.*;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel de comandos de combate táctico.
 * Contiene botones dedicados directos para las 3 habilidades por personaje (con bloqueo visual si no están aprendidas),
 * selección de objetivo sincronizada por clic sobre los sprites, y uso rápido de consumibles.
 */
public class PanelAcciones extends JPanel {

    private ControladorBatalla controlador;

    private final JComboBox<String> comboObjetivos;
    private final JComboBox<String> comboItems;

    private final JButton btnAtacar;
    private final JButton btnHab1;
    private final JButton btnHab2;
    private final JButton btnHab3;
    private final JButton btnUsarItem;
    private final JButton btnDefender;
    private final JButton btnContinuar;

    private final CardLayout cardLayoutCentral;
    private final JPanel panelCentral;
    private final JLabel lblResultadoFin;

    private JLabel lblNombreHeroe;
    private JLabel lblTipoHeroe;
    private JLabel lblStatsHeroe;
    private JLabel lblTurnoMensaje;
    private JLabel lblSpriteHeroe;

    private final List<Integer> indicesEnemigosVivos = new ArrayList<>();
    private final List<Integer> indicesItemsDisponibles = new ArrayList<>();
    private EstadoBatallaDTO estadoActual;

    private int enemigoSeleccionadoIdx = -1; // -1 significa por defecto / área
    private Consumer<Integer> listenerObjetivoCambiado;

    public PanelAcciones() {
        setLayout(new BorderLayout(6, 4));
        setBackground(EstilosUI.FONDO_PANEL);
        setPreferredSize(new Dimension(0, 115));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, EstilosUI.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // ── Panel izquierdo: Info del Héroe en Turno con Sprite ───────────────────
        JPanel panelInfoHeroe = crearPanelInfoHeroe();
        add(panelInfoHeroe, BorderLayout.WEST);

        // ── Panel central con CardLayout: Cambia entre COMBATE y FIN DE COMBATE ───
        cardLayoutCentral = new CardLayout();
        panelCentral = new JPanel(cardLayoutCentral);
        panelCentral.setOpaque(false);

        // --- Card COMBATE ---
        JPanel cardCombate = new JPanel(new BorderLayout(4, 4));
        cardCombate.setOpaque(false);

        // Fila Superior: Selectores de Objetivo y Consumible
        JPanel panelSelectores = new JPanel(new GridLayout(1, 2, 10, 0));
        panelSelectores.setOpaque(false);
        panelSelectores.add(crearSelector("Objetivo (Clic en enemigo o elegir):", comboObjetivos = new JComboBox<>(), new Color(192, 57, 43)));
        panelSelectores.add(crearSelector("Consumible del Inventario:", comboItems = new JComboBox<>(), new Color(39, 174, 96)));
        cardCombate.add(panelSelectores, BorderLayout.NORTH);

        // Fila Inferior: Botones de Acción Táctica
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        panelBotones.setOpaque(false);

        btnAtacar = crearBotonAccion("ATACAR",
                "<html><b>Ataque Físico Básico</b><br>Objetivo: 1 enemigo seleccionado.<br>Daño directo según el ATQ del héroe. Coste: 0 MP.</html>",
                new Color(180, 50, 35), new Color(220, 70, 50), 85, 38);

        btnHab1 = crearBotonAccion("Hab 1", "Habilidad táctica 1", new Color(110, 45, 155), new Color(135, 65, 180), 140, 38);
        btnHab2 = crearBotonAccion("Hab 2", "Habilidad táctica 2", new Color(110, 45, 155), new Color(135, 65, 180), 140, 38);
        btnHab3 = crearBotonAccion("Hab 3", "Habilidad táctica 3", new Color(110, 45, 155), new Color(135, 65, 180), 140, 38);

        btnUsarItem = crearBotonAccion("POCIÓN",
                "<html><b>Poción / Consumible</b><br>Objetivo: Héroe activo.<br>Consume la poción elegida del inventario.</html>",
                new Color(30, 155, 80), new Color(40, 195, 105), 95, 38);

        btnDefender = crearBotonAccion("DEFENDER",
                "<html><b>Postura Defensiva</b><br>Objetivo: Propio.<br>Duplica DEF hasta el siguiente turno y regenera 10% MP/Ki.</html>",
                new Color(30, 100, 165), new Color(45, 135, 210), 95, 38);

        panelBotones.add(btnAtacar);
        panelBotones.add(btnHab1);
        panelBotones.add(btnHab2);
        panelBotones.add(btnHab3);
        panelBotones.add(btnUsarItem);
        panelBotones.add(btnDefender);
        cardCombate.add(panelBotones, BorderLayout.CENTER);

        // --- Card FIN DE COMBATE (Grande, despejado y sin tapar) ---
        JPanel cardFin = new JPanel(new GridBagLayout());
        cardFin.setOpaque(false);
        GridBagConstraints gbcFin = new GridBagConstraints();
        gbcFin.gridx = 0;
        gbcFin.gridy = 0;
        gbcFin.insets = new Insets(0, 0, 8, 0);

        lblResultadoFin = new JLabel("¡COMBATE FINALIZADO!", SwingConstants.CENTER);
        lblResultadoFin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblResultadoFin.setForeground(EstilosUI.COLOR_ORO);
        cardFin.add(lblResultadoFin, gbcFin);

        gbcFin.gridy = 1;
        btnContinuar = EstilosUI.crearBoton("CONTINUAR", new Color(39, 174, 96), new Color(46, 204, 113));
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnContinuar.setPreferredSize(new Dimension(320, 44));
        cardFin.add(btnContinuar, gbcFin);

        panelCentral.add(cardCombate, "COMBATE");
        panelCentral.add(cardFin, "FIN");
        add(panelCentral, BorderLayout.CENTER);

        configurarEventos();
    }

    private JPanel crearPanelInfoHeroe() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(280, 95));

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

    private JButton crearBotonAccion(String texto, String tooltip, Color base, Color hover, int ancho, int alto) {
        JButton btn = EstilosUI.crearBoton(texto, base, hover);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(ancho, alto));
        return btn;
    }

    private void configurarEventos() {
        btnAtacar.addActionListener(e -> {
            if (controlador == null || estadoActual == null) return;
            int idxObj = obtenerIndiceObjetivoSeleccionado();
            controlador.atacar(estadoActual.getIndiceHeroeActivo(), Math.max(0, idxObj));
        });

        btnHab1.addActionListener(e -> ejecutarHabilidadDirecta(0));
        btnHab2.addActionListener(e -> ejecutarHabilidadDirecta(1));
        btnHab3.addActionListener(e -> ejecutarHabilidadDirecta(2));

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

        comboObjetivos.addActionListener(e -> {
            int sel = comboObjetivos.getSelectedIndex();
            if (sel <= 0) {
                enemigoSeleccionadoIdx = -1;
            } else if (sel - 1 < indicesEnemigosVivos.size()) {
                enemigoSeleccionadoIdx = indicesEnemigosVivos.get(sel - 1);
            }
            if (listenerObjetivoCambiado != null) {
                listenerObjetivoCambiado.accept(enemigoSeleccionadoIdx);
            }
        });
    }

    private void ejecutarHabilidadDirecta(int indiceHabilidad) {
        if (controlador == null || estadoActual == null) return;
        int idxObj = obtenerIndiceObjetivoSeleccionado();
        controlador.usarHabilidad(estadoActual.getIndiceHeroeActivo(), indiceHabilidad, Math.max(0, idxObj));
    }

    public void setListenerObjetivoCambiado(Consumer<Integer> listener) {
        this.listenerObjetivoCambiado = listener;
    }

    public void toggleSeleccionObjetivo(int idxEnemigo) {
        if (enemigoSeleccionadoIdx == idxEnemigo) {
            // Deseleccionar: volver al default (todos / auto)
            enemigoSeleccionadoIdx = -1;
            comboObjetivos.setSelectedIndex(0);
        } else {
            enemigoSeleccionadoIdx = idxEnemigo;
            int pos = indicesEnemigosVivos.indexOf(idxEnemigo);
            if (pos >= 0) {
                comboObjetivos.setSelectedIndex(pos + 1);
            }
        }
        if (listenerObjetivoCambiado != null) {
            listenerObjetivoCambiado.accept(enemigoSeleccionadoIdx);
        }
    }

    private int obtenerIndiceObjetivoSeleccionado() {
        if (enemigoSeleccionadoIdx >= 0) {
            return enemigoSeleccionadoIdx;
        }
        if (!indicesEnemigosVivos.isEmpty()) {
            return indicesEnemigosVivos.get(0);
        }
        return 0;
    }

    public void setControlador(ControladorBatalla controlador) {
        this.controlador = controlador;
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        this.estadoActual = estadoDTO;

        if (estadoDTO.isBatallaTerminada()) {
            setBotonesAccionHabilitados(false);
            cardLayoutCentral.show(panelCentral, "FIN");
            btnContinuar.setText(estadoDTO.isVictoria() ? "VER RECOMPENSAS" : "VER RESULTADOS");
            lblResultadoFin.setText(estadoDTO.isVictoria() ? "¡VICTORIA EN EL COMBATE!" : "¡PARTY DERROTADA!");
            lblResultadoFin.setForeground(estadoDTO.isVictoria() ? new Color(46, 204, 113) : new Color(231, 76, 60));
            lblTurnoMensaje.setText(estadoDTO.isVictoria() ? "Victoria" : "Derrota");
            lblStatsHeroe.setText(estadoDTO.getMensajeFin());
            return;
        }

        cardLayoutCentral.show(panelCentral, "COMBATE");
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

            // Configurar botones directos para las 3 habilidades con coste de maná y tooltips
            configurarBotonHabilidad(btnHab1, h, 0);
            configurarBotonHabilidad(btnHab2, h, 1);
            configurarBotonHabilidad(btnHab3, h, 2);
        }

        // Cargar objetivos (enemigos vivos)
        comboObjetivos.removeAllItems();
        comboObjetivos.addItem("★ Todos / Objetivo Automático");
        indicesEnemigosVivos.clear();
        List<EntidadDTO> enemigos = estadoDTO.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            EntidadDTO e = enemigos.get(i);
            if (e.isEstaVivo()) {
                indicesEnemigosVivos.add(i);
                comboObjetivos.addItem((i + 1) + ". " + e.getNombre() + " (" + e.getVida() + "/" + e.getVidaMaxima() + " HP)");
            }
        }

        // Mantener selección si el enemigo sigue vivo
        if (enemigoSeleccionadoIdx >= 0 && indicesEnemigosVivos.contains(enemigoSeleccionadoIdx)) {
            int pos = indicesEnemigosVivos.indexOf(enemigoSeleccionadoIdx);
            comboObjetivos.setSelectedIndex(pos + 1);
        } else {
            enemigoSeleccionadoIdx = -1;
            comboObjetivos.setSelectedIndex(0);
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

    private void configurarBotonHabilidad(JButton btn, PersonajeDTO h, int slot) {
        List<HabilidadDTO> habsDTO = h.getHabilidadesDTO();
        if (habsDTO != null && slot < habsDTO.size()) {
            HabilidadDTO hab = habsDTO.get(slot);
            boolean tieneMana = h.getMana() >= hab.getCosteMana();

            String colorMana = tieneMana ? "#85c1e9" : "#e74c3c";
            String textoMana = hab.getCosteMana() > 0 ? hab.getCosteMana() + " MP" : "0 MP (Gratis)";
            if (!tieneMana) {
                textoMana += " (Sin MP)";
            }

            btn.setText("<html><center><span style='font-size:11px;'><b>" + hab.getNombre() + "</b></span><br><span style='font-size:10px; color:" + colorMana + ";'><b>" + textoMana + "</b></span></center></html>");
            btn.setToolTipText(hab.getTooltipFormateado());
            btn.setEnabled(tieneMana);
            btn.setBackground(tieneMana ? new Color(110, 45, 155) : new Color(50, 40, 60));
        } else if (slot < h.getHabilidades().size()) {
            String textoCompleto = h.getHabilidades().get(slot);
            String nombreCorto = extraerNombreHabilidad(textoCompleto);
            btn.setText("<html><center><b>" + nombreCorto + "</b></center></html>");
            btn.setToolTipText(textoCompleto);
            btn.setEnabled(true);
            btn.setBackground(new Color(110, 45, 155));
        } else {
            btn.setText("<html><center><font color='#888888'>[Bloqueada]</font><br><font size='2' color='#666666'>-- MP</font></center></html>");
            btn.setToolTipText("<html><body style='width: 220px; font-family: Segoe UI; font-size: 11px;'><b>Habilidad Bloqueada</b><br><span style='color: #bdc3c7;'>Aún no aprendida. Se desbloquea al subir de nivel en el menú 'Héroes y Equipo'.</span></body></html>");
            btn.setEnabled(false);
            btn.setBackground(new Color(35, 40, 50));
        }
    }

    private String extraerNombreHabilidad(String desc) {
        if (desc == null) return "Habilidad";
        int idxParentesis = desc.indexOf("(");
        if (idxParentesis > 0) {
            return desc.substring(0, idxParentesis).trim();
        }
        return desc;
    }

    private void setBotonesAccionHabilitados(boolean hab) {
        btnAtacar.setEnabled(hab);
        btnHab1.setEnabled(hab);
        btnHab2.setEnabled(hab);
        btnHab3.setEnabled(hab);
        btnUsarItem.setEnabled(hab && !indicesItemsDisponibles.isEmpty());
        btnDefender.setEnabled(hab);
        comboObjetivos.setEnabled(hab);
        comboItems.setEnabled(hab);
    }
}
