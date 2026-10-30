package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.ItemDTO;
import rpg.dto.PartyGestionDTO;
import rpg.dto.PersonajeDTO;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PantallaEstadoParty extends JPanel {
    private ControladorJuego controlador;
    private PartyGestionDTO datosActuales;

    private final JComboBox<String> comboHeroes;
    private final JLabel lblNombreHeroe;
    private final JLabel lblStats;
    private final JLabel lblPuntosLibres;
    private final JLabel lblSlotsActuales;

    // Botones de asignación de atributos
    private final JButton btnMasHp;
    private final JButton btnMasMp;
    private final JButton btnMasAtq;
    private final JButton btnMasDef;
    private final JButton btnMasVel;

    // Controles de Equipamiento
    private final JComboBox<String> comboArmas;
    private final JComboBox<String> comboArmaduras;
    private final JButton btnEquiparArma;
    private final JButton btnDesequiparArma;
    private final JButton btnEquiparArmadura;
    private final JButton btnDesequiparArmadura;

    public PantallaEstadoParty() {
        setLayout(new BorderLayout(15, 15));
        setBackground(EstilosUI.FONDO_OSCURO);
        setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // 1. Barra Superior
        JPanel pnlTop = new JPanel(new BorderLayout(15, 0));
        pnlTop.setOpaque(false);

        JLabel lblTit = new JLabel("🛡️ GESTIÓN DE PARTY, EQUIPAMIENTO Y ATRIBUTOS");
        lblTit.setFont(EstilosUI.FUENTE_TITULO);
        lblTit.setForeground(EstilosUI.COLOR_ORO);
        pnlTop.add(lblTit, BorderLayout.WEST);

        JButton btnVolver = EstilosUI.crearBoton("🗺️ Volver al Mapa", new Color(39, 174, 96), new Color(46, 204, 113));
        btnVolver.addActionListener(e -> {
            if (controlador != null) controlador.volverAlMapa();
        });
        pnlTop.add(btnVolver, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // 2. Contenedor Principal (Panel Izquierdo: Selección y Stats | Panel Derecho: Asignación y Slots)
        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlCentro.setOpaque(false);

        // --- COLUMNA IZQUIERDA: Selector y Ficha del Héroe ---
        JPanel pnlFicha = new JPanel();
        pnlFicha.setLayout(new BoxLayout(pnlFicha, BoxLayout.Y_AXIS));
        pnlFicha.setBackground(EstilosUI.FONDO_PANEL);
        pnlFicha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "FICHA DEL HÉROE",
                        0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.COLOR_ORO
                ),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        pnlFicha.add(new JLabel("Seleccionar Héroe:"));
        comboHeroes = new JComboBox<>();
        comboHeroes.setFont(EstilosUI.FUENTE_BOLD);
        comboHeroes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboHeroes.addActionListener(e -> refrescarFichaHeroe());
        pnlFicha.add(comboHeroes);

        pnlFicha.add(Box.createVerticalStrut(15));

        lblNombreHeroe = new JLabel("---");
        lblNombreHeroe.setFont(EstilosUI.FUENTE_TITULO);
        lblNombreHeroe.setForeground(EstilosUI.TEXTO_CLARO);
        pnlFicha.add(lblNombreHeroe);

        pnlFicha.add(Box.createVerticalStrut(10));

        lblStats = new JLabel("<html>Cargando estadísticas...</html>");
        lblStats.setFont(EstilosUI.FUENTE_NORMAL);
        lblStats.setForeground(EstilosUI.TEXTO_CLARO);
        pnlFicha.add(lblStats);

        pnlFicha.add(Box.createVerticalStrut(15));

        lblSlotsActuales = new JLabel("<html><b>Slots Equipados:</b><br>Arma: Ninguna<br>Armadura: Ninguna</html>");
        lblSlotsActuales.setFont(EstilosUI.FUENTE_NORMAL);
        lblSlotsActuales.setForeground(new Color(52, 152, 219));
        pnlFicha.add(lblSlotsActuales);

        pnlCentro.add(pnlFicha);

        // --- COLUMNA DERECHA: Puntos de Atributos & Slots de Equipo ---
        JPanel pnlGestion = new JPanel();
        pnlGestion.setLayout(new BoxLayout(pnlGestion, BoxLayout.Y_AXIS));
        pnlGestion.setBackground(EstilosUI.FONDO_PANEL);
        pnlGestion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "ASIGNACIÓN Y EQUIPAMIENTO (SLOTS)",
                        0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.COLOR_ORO
                ),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Subsección Atributos Libres
        lblPuntosLibres = new JLabel("Puntos de Atributo Libres: 0");
        lblPuntosLibres.setFont(EstilosUI.FUENTE_BOLD);
        lblPuntosLibres.setForeground(new Color(46, 204, 113));
        pnlGestion.add(lblPuntosLibres);

        pnlGestion.add(Box.createVerticalStrut(8));

        JPanel pnlBotonesStats = new JPanel(new GridLayout(1, 5, 6, 0));
        pnlBotonesStats.setOpaque(false);
        pnlBotonesStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnMasHp = EstilosUI.crearBoton("+15 HP", new Color(52, 73, 94), new Color(70, 95, 120));
        btnMasMp = EstilosUI.crearBoton("+10 MP", new Color(52, 73, 94), new Color(70, 95, 120));
        btnMasAtq = EstilosUI.crearBoton("+2 ATQ", new Color(52, 73, 94), new Color(70, 95, 120));
        btnMasDef = EstilosUI.crearBoton("+2 DEF", new Color(52, 73, 94), new Color(70, 95, 120));
        btnMasVel = EstilosUI.crearBoton("+2 VEL", new Color(52, 73, 94), new Color(70, 95, 120));

        pnlBotonesStats.add(btnMasHp);
        pnlBotonesStats.add(btnMasMp);
        pnlBotonesStats.add(btnMasAtq);
        pnlBotonesStats.add(btnMasDef);
        pnlBotonesStats.add(btnMasVel);
        pnlGestion.add(pnlBotonesStats);

        pnlGestion.add(Box.createVerticalStrut(20));

        // Subsección Slot Arma
        pnlGestion.add(new JLabel("Slot de Arma (Inventario):"));
        comboArmas = new JComboBox<>();
        comboArmas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pnlGestion.add(comboArmas);

        JPanel pnlAccArma = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlAccArma.setOpaque(false);
        btnEquiparArma = EstilosUI.crearBoton("⚔️ Equipar Arma", new Color(41, 128, 185), new Color(52, 152, 219));
        btnDesequiparArma = EstilosUI.crearBoton("Desequipar", new Color(70, 80, 95), new Color(90, 100, 120));
        pnlAccArma.add(btnEquiparArma);
        pnlAccArma.add(btnDesequiparArma);
        pnlGestion.add(pnlAccArma);

        pnlGestion.add(Box.createVerticalStrut(15));

        // Subsección Slot Armadura
        pnlGestion.add(new JLabel("Slot de Armadura (Inventario):"));
        comboArmaduras = new JComboBox<>();
        comboArmaduras.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pnlGestion.add(comboArmaduras);

        JPanel pnlAccArmad = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlAccArmad.setOpaque(false);
        btnEquiparArmadura = EstilosUI.crearBoton("🛡️ Equipar Armadura", new Color(41, 128, 185), new Color(52, 152, 219));
        btnDesequiparArmadura = EstilosUI.crearBoton("Desequipar", new Color(70, 80, 95), new Color(90, 100, 120));
        pnlAccArmad.add(btnEquiparArmadura);
        pnlAccArmad.add(btnDesequiparArmadura);
        pnlGestion.add(pnlAccArmad);

        pnlCentro.add(pnlGestion);
        add(pnlCentro, BorderLayout.CENTER);

        configurarEventos();
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    private void configurarEventos() {
        btnMasHp.addActionListener(e -> {
            if (controlador != null) controlador.asignarPuntoAtributo(comboHeroes.getSelectedIndex(), "HP");
        });
        btnMasMp.addActionListener(e -> {
            if (controlador != null) controlador.asignarPuntoAtributo(comboHeroes.getSelectedIndex(), "MP");
        });
        btnMasAtq.addActionListener(e -> {
            if (controlador != null) controlador.asignarPuntoAtributo(comboHeroes.getSelectedIndex(), "ATQ");
        });
        btnMasDef.addActionListener(e -> {
            if (controlador != null) controlador.asignarPuntoAtributo(comboHeroes.getSelectedIndex(), "DEF");
        });
        btnMasVel.addActionListener(e -> {
            if (controlador != null) controlador.asignarPuntoAtributo(comboHeroes.getSelectedIndex(), "VEL");
        });

        btnEquiparArma.addActionListener(e -> {
            int heroIdx = comboHeroes.getSelectedIndex();
            int armaIdx = comboArmas.getSelectedIndex();
            if (controlador != null && datosActuales != null && armaIdx >= 0 && armaIdx < datosActuales.getInventarioArmas().size()) {
                ItemDTO item = datosActuales.getInventarioArmas().get(armaIdx);
                controlador.equiparArma(heroIdx, item.getIndice());
            }
        });

        btnDesequiparArma.addActionListener(e -> {
            int heroIdx = comboHeroes.getSelectedIndex();
            if (controlador != null) controlador.desequiparArma(heroIdx);
        });

        btnEquiparArmadura.addActionListener(e -> {
            int heroIdx = comboHeroes.getSelectedIndex();
            int armaduraIdx = comboArmaduras.getSelectedIndex();
            if (controlador != null && datosActuales != null && armaduraIdx >= 0 && armaduraIdx < datosActuales.getInventarioArmaduras().size()) {
                ItemDTO item = datosActuales.getInventarioArmaduras().get(armaduraIdx);
                controlador.equiparArmadura(heroIdx, item.getIndice());
            }
        });

        btnDesequiparArmadura.addActionListener(e -> {
            int heroIdx = comboHeroes.getSelectedIndex();
            if (controlador != null) controlador.desequiparArmadura(heroIdx);
        });
    }

    public void actualizar(PartyGestionDTO dto) {
        this.datosActuales = dto;

        int prevHeroIndex = Math.max(0, comboHeroes.getSelectedIndex());
        comboHeroes.removeAllItems();
        for (PersonajeDTO h : dto.getHeroes()) {
            comboHeroes.addItem(h.getNombre() + " (Nv. " + h.getNivel() + ")");
        }
        if (comboHeroes.getItemCount() > 0) {
            comboHeroes.setSelectedIndex(Math.min(prevHeroIndex, comboHeroes.getItemCount() - 1));
        }

        // Cargar Armas en Inventario
        comboArmas.removeAllItems();
        for (ItemDTO it : dto.getInventarioArmas()) {
            comboArmas.addItem(it.getNombre() + " (" + it.getDescripcion() + ")");
        }
        btnEquiparArma.setEnabled(comboArmas.getItemCount() > 0);

        // Cargar Armaduras en Inventario
        comboArmaduras.removeAllItems();
        for (ItemDTO it : dto.getInventarioArmaduras()) {
            comboArmaduras.addItem(it.getNombre() + " (" + it.getDescripcion() + ")");
        }
        btnEquiparArmadura.setEnabled(comboArmaduras.getItemCount() > 0);

        refrescarFichaHeroe();
    }

    private void refrescarFichaHeroe() {
        if (datosActuales == null || comboHeroes.getSelectedIndex() < 0) return;
        int idx = comboHeroes.getSelectedIndex();
        if (idx >= datosActuales.getHeroes().size()) return;

        PersonajeDTO h = datosActuales.getHeroes().get(idx);
        lblNombreHeroe.setText(h.getNombre() + " - " + h.getTipo());

        String statsHtml = "<html>" +
                "<b>Nivel:</b> " + h.getNivel() + "<br>" +
                "<b>HP:</b> " + h.getVidaActual() + " / " + h.getVidaMaxima() + "<br>" +
                "<b>MP:</b> " + h.getManaActual() + " / " + h.getManaMaximo() + "<br>" +
                "<b>Ataque Total:</b> " + h.getAtaque() + "<br>" +
                "<b>Defensa Total:</b> " + h.getDefensa() + "<br>" +
                "<b>Velocidad:</b> " + h.getVelocidad() + "<br>" +
                "<b>Experiencia:</b> " + h.getExperiencia() + " / " + h.getExperienciaSiguienteNivel() +
                "</html>";
        lblStats.setText(statsHtml);

        lblSlotsActuales.setText("<html><b>Slots Equipados:</b><br>" +
                "⚔️ <b>Arma:</b> " + h.getArmaEquipada() + "<br>" +
                "🛡️ <b>Armadura:</b> " + h.getArmaduraEquipada() + "</html>");

        lblPuntosLibres.setText("Puntos de Atributo Libres: " + h.getPuntosAtributoDisponibles());
        boolean tienePuntos = (h.getPuntosAtributoDisponibles() > 0);
        btnMasHp.setEnabled(tienePuntos);
        btnMasMp.setEnabled(tienePuntos);
        btnMasAtq.setEnabled(tienePuntos);
        btnMasDef.setEnabled(tienePuntos);
        btnMasVel.setEnabled(tienePuntos);
    }
}
