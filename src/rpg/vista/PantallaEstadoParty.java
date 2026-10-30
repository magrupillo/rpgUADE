package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.ItemDTO;
import rpg.dto.PartyGestionDTO;
import rpg.dto.PersonajeDTO;
import rpg.modelo.*;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pantalla de Gestión de Party, Atributos, Desbloqueo de Habilidades y Equipamiento.
 * Muestra barras visuales de XP (e.g. 60/100), interfaz para elegir entre habilidades al subir de nivel,
 * y asignación intuitiva de puntos y armas.
 */
public class PantallaEstadoParty extends JPanel {

    private ControladorJuego controlador;
    private Party partyModelo;
    private PartyGestionDTO datosActuales;
    private final Image fondoParty;

    private final JComboBox<String> comboHeroes;
    private final JLabel lblNombreHeroe;
    private final JLabel lblClaseHeroe;
    private final JLabel lblSpriteHeroe;
    private final JProgressBar barraXpHeroe;
    private final JLabel lblStatsTexto;
    private final JLabel lblSlotsActuales;

    // Asignación de puntos
    private final JLabel lblPuntosLibres;
    private final JButton btnMasHp;
    private final JButton btnMasMp;
    private final JButton btnMasAtq;
    private final JButton btnMasDef;
    private final JButton btnMasVel;

    // Desbloqueo de Habilidades
    private final JPanel panelHabilidades;

    // Equipamiento
    private final JComboBox<String> comboArmas;
    private final JComboBox<String> comboArmaduras;
    private final JButton btnEquiparArma;
    private final JButton btnDesequiparArma;
    private final JButton btnEquiparArmadura;
    private final JButton btnDesequiparArmadura;

    public PantallaEstadoParty() {
        this.fondoParty = GestorRecursos.obtenerFondo("otro");
        setLayout(new BorderLayout(15, 12));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // ── 1. Barra Superior ──────────────────────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout(15, 0));
        pnlTop.setOpaque(false);

        JLabel lblTit = new JLabel("GESTIÓN DE HÉROES, HABILIDADES Y EQUIPAMIENTO");
        lblTit.setFont(EstilosUI.FUENTE_TITULO);
        lblTit.setForeground(EstilosUI.COLOR_ORO);
        pnlTop.add(lblTit, BorderLayout.WEST);

        JButton btnVolver = EstilosUI.crearBoton("Volver al Mapa", new Color(39, 174, 96), new Color(46, 204, 113));
        btnVolver.addActionListener(e -> {
            if (controlador != null) controlador.volverAlMapaOATorre();
        });
        pnlTop.add(btnVolver, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // ── 2. Contenedor Principal (2 Columnas) ──────────────────────────────────
        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlCentro.setOpaque(false);

        // --- COLUMNA IZQUIERDA: Selector, Sprite, Barra XP y Stats ---
        JPanel pnlFicha = new JPanel();
        pnlFicha.setLayout(new BoxLayout(pnlFicha, BoxLayout.Y_AXIS));
        pnlFicha.setBackground(new Color(24, 28, 40)); // Sólido sin alpha para evitar ghosting
        pnlFicha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "FICHA DEL HÉROE", 0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.COLOR_ORO
                ),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        pnlFicha.add(new JLabel("Seleccionar Héroe de la Party:"));
        comboHeroes = new JComboBox<>();
        comboHeroes.setFont(EstilosUI.FUENTE_BOLD);
        comboHeroes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboHeroes.addActionListener(e -> refrescarFichaHeroe());
        pnlFicha.add(comboHeroes);

        pnlFicha.add(Box.createVerticalStrut(10));

        JPanel pnlHeroeAvatar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlHeroeAvatar.setOpaque(false);
        lblSpriteHeroe = new JLabel();
        lblSpriteHeroe.setPreferredSize(new Dimension(72, 72));
        pnlHeroeAvatar.add(lblSpriteHeroe);

        JPanel pnlNombreClase = new JPanel();
        pnlNombreClase.setLayout(new BoxLayout(pnlNombreClase, BoxLayout.Y_AXIS));
        pnlNombreClase.setOpaque(false);
        lblNombreHeroe = new JLabel("Héroe");
        lblNombreHeroe.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombreHeroe.setForeground(EstilosUI.TEXTO_CLARO);

        lblClaseHeroe = new JLabel("Clase");
        lblClaseHeroe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblClaseHeroe.setForeground(EstilosUI.COLOR_ORO);
        pnlNombreClase.add(lblNombreHeroe);
        pnlNombreClase.add(lblClaseHeroe);
        pnlHeroeAvatar.add(pnlNombreClase);
        pnlFicha.add(pnlHeroeAvatar);

        pnlFicha.add(Box.createVerticalStrut(10));

        JLabel lblXpTitulo = new JLabel("Progreso de Nivel (Experiencia):");
        lblXpTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblXpTitulo.setForeground(EstilosUI.TEXTO_MUTED);
        pnlFicha.add(lblXpTitulo);

        barraXpHeroe = EstilosUI.crearBarra(0, 100, new Color(155, 89, 182));
        barraXpHeroe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        pnlFicha.add(barraXpHeroe);

        pnlFicha.add(Box.createVerticalStrut(12));

        lblStatsTexto = new JLabel("<html>Cargando estadísticas...</html>");
        lblStatsTexto.setFont(EstilosUI.FUENTE_NORMAL);
        lblStatsTexto.setForeground(EstilosUI.TEXTO_CLARO);
        pnlFicha.add(lblStatsTexto);

        pnlFicha.add(Box.createVerticalStrut(10));

        lblSlotsActuales = new JLabel("<html><b>Equipamiento Actual:</b><br>Arma: Ninguna<br>Armadura: Ninguna</html>");
        lblSlotsActuales.setFont(EstilosUI.FUENTE_NORMAL);
        lblSlotsActuales.setForeground(new Color(241, 196, 15));
        pnlFicha.add(lblSlotsActuales);

        pnlCentro.add(pnlFicha);

        // --- COLUMNA DERECHA: Asignación de Puntos, Elección de Habilidades y Equipo ---
        JPanel pnlDerecho = new JPanel();
        pnlDerecho.setLayout(new BoxLayout(pnlDerecho, BoxLayout.Y_AXIS));
        pnlDerecho.setOpaque(false);

        // 1. Panel de Puntos de Atributo
        JPanel pnlPuntos = new JPanel();
        pnlPuntos.setLayout(new BoxLayout(pnlPuntos, BoxLayout.Y_AXIS));
        pnlPuntos.setBackground(new Color(24, 28, 40));
        pnlPuntos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "PUNTOS DE ATRIBUTO LIBRES", 0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.COLOR_ORO
                ),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        lblPuntosLibres = new JLabel("Puntos Disponibles: 0");
        lblPuntosLibres.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPuntosLibres.setForeground(new Color(46, 204, 113));
        pnlPuntos.add(lblPuntosLibres);

        pnlPuntos.add(Box.createVerticalStrut(6));

        JPanel pnlBotonesAtributos = new JPanel(new GridLayout(1, 5, 6, 0));
        pnlBotonesAtributos.setOpaque(false);
        pnlBotonesAtributos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        btnMasHp = EstilosUI.crearBoton("+15 HP", new Color(39, 174, 96), new Color(46, 204, 113));
        btnMasMp = EstilosUI.crearBoton("+12 MP", new Color(41, 128, 185), new Color(52, 152, 219));
        btnMasAtq = EstilosUI.crearBoton("+3 ATQ", new Color(192, 57, 43), new Color(231, 76, 60));
        btnMasDef = EstilosUI.crearBoton("+2 DEF", new Color(211, 84, 0), new Color(230, 126, 34));
        btnMasVel = EstilosUI.crearBoton("+2 VEL", new Color(142, 68, 173), new Color(155, 89, 182));

        btnMasHp.addActionListener(e -> asignarStat("HP"));
        btnMasMp.addActionListener(e -> asignarStat("MP"));
        btnMasAtq.addActionListener(e -> asignarStat("ATQ"));
        btnMasDef.addActionListener(e -> asignarStat("DEF"));
        btnMasVel.addActionListener(e -> asignarStat("VEL"));

        pnlBotonesAtributos.add(btnMasHp);
        pnlBotonesAtributos.add(btnMasMp);
        pnlBotonesAtributos.add(btnMasAtq);
        pnlBotonesAtributos.add(btnMasDef);
        pnlBotonesAtributos.add(btnMasVel);
        pnlPuntos.add(pnlBotonesAtributos);

        pnlDerecho.add(pnlPuntos);
        pnlDerecho.add(Box.createVerticalStrut(8));

        // 2. Panel de Desbloqueo y Selección de Habilidades
        panelHabilidades = new JPanel();
        panelHabilidades.setLayout(new BoxLayout(panelHabilidades, BoxLayout.Y_AXIS));
        panelHabilidades.setBackground(new Color(24, 28, 40));
        panelHabilidades.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "HABILIDADES Y DESBLOQUEO", 0, 0, EstilosUI.FUENTE_BOLD, new Color(155, 89, 182)
                ),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        pnlDerecho.add(panelHabilidades);
        pnlDerecho.add(Box.createVerticalStrut(8));

        // 3. Panel de Equipamiento (Compacto y equilibrado)
        JPanel pnlEquipo = new JPanel();
        pnlEquipo.setLayout(new BoxLayout(pnlEquipo, BoxLayout.Y_AXIS));
        pnlEquipo.setBackground(new Color(24, 28, 40));
        pnlEquipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "EQUIPAMIENTO (INVENTARIO COMPARTIDO)", 0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.COLOR_ORO
                ),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // Armas
        JPanel filaArmas = new JPanel(new BorderLayout(8, 0));
        filaArmas.setOpaque(false);
        JLabel lblArmasTit = new JLabel("Armas:");
        lblArmasTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblArmasTit.setPreferredSize(new Dimension(75, 26));
        filaArmas.add(lblArmasTit, BorderLayout.WEST);

        comboArmas = new JComboBox<>();
        comboArmas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filaArmas.add(comboArmas, BorderLayout.CENTER);

        btnEquiparArma = EstilosUI.crearBoton("Equipar", new Color(39, 174, 96), new Color(46, 204, 113));
        btnEquiparArma.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEquiparArma.setPreferredSize(new Dimension(80, 26));

        btnDesequiparArma = EstilosUI.crearBoton("Desequipar", new Color(140, 40, 40), new Color(192, 57, 43));
        btnDesequiparArma.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDesequiparArma.setPreferredSize(new Dimension(95, 26));

        JPanel btnArmas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnArmas.setOpaque(false);
        btnArmas.add(btnEquiparArma);
        btnArmas.add(btnDesequiparArma);
        filaArmas.add(btnArmas, BorderLayout.EAST);
        pnlEquipo.add(filaArmas);

        pnlEquipo.add(Box.createVerticalStrut(6));

        // Armaduras
        JPanel filaArmaduras = new JPanel(new BorderLayout(8, 0));
        filaArmaduras.setOpaque(false);
        JLabel lblArmadTit = new JLabel("Armaduras:");
        lblArmadTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblArmadTit.setPreferredSize(new Dimension(75, 26));
        filaArmaduras.add(lblArmadTit, BorderLayout.WEST);

        comboArmaduras = new JComboBox<>();
        comboArmaduras.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filaArmaduras.add(comboArmaduras, BorderLayout.CENTER);

        btnEquiparArmadura = EstilosUI.crearBoton("Equipar", new Color(39, 174, 96), new Color(46, 204, 113));
        btnEquiparArmadura.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEquiparArmadura.setPreferredSize(new Dimension(80, 26));

        btnDesequiparArmadura = EstilosUI.crearBoton("Desequipar", new Color(140, 40, 40), new Color(192, 57, 43));
        btnDesequiparArmadura.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDesequiparArmadura.setPreferredSize(new Dimension(95, 26));

        JPanel btnArmaduras = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnArmaduras.setOpaque(false);
        btnArmaduras.add(btnEquiparArmadura);
        btnArmaduras.add(btnDesequiparArmadura);
        filaArmaduras.add(btnArmaduras, BorderLayout.EAST);
        pnlEquipo.add(filaArmaduras);

        pnlDerecho.add(pnlEquipo);

        pnlCentro.add(pnlDerecho);
        add(pnlCentro, BorderLayout.CENTER);

        configurarEventosEquipo();
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void setPartyModelo(Party party) {
        this.partyModelo = party;
    }

    private void configurarEventosEquipo() {
        btnEquiparArma.addActionListener(e -> {
            int selHeroe = comboHeroes.getSelectedIndex();
            int selArma = comboArmas.getSelectedIndex();
            if (controlador != null && selHeroe >= 0 && selArma >= 0 && datosActuales != null) {
                if (selArma < datosActuales.getArmasDisponibles().size()) {
                    int idxInventario = datosActuales.getArmasDisponibles().get(selArma).getIndiceInventario();
                    controlador.equiparArma(selHeroe, idxInventario);
                }
            }
        });

        btnDesequiparArma.addActionListener(e -> {
            int selHeroe = comboHeroes.getSelectedIndex();
            if (controlador != null && selHeroe >= 0) {
                controlador.desequiparArma(selHeroe);
            }
        });

        btnEquiparArmadura.addActionListener(e -> {
            int selHeroe = comboHeroes.getSelectedIndex();
            int selArmadura = comboArmaduras.getSelectedIndex();
            if (controlador != null && selHeroe >= 0 && selArmadura >= 0 && datosActuales != null) {
                if (selArmadura < datosActuales.getArmadurasDisponibles().size()) {
                    int idxInventario = datosActuales.getArmadurasDisponibles().get(selArmadura).getIndiceInventario();
                    controlador.equiparArmadura(selHeroe, idxInventario);
                }
            }
        });

        btnDesequiparArmadura.addActionListener(e -> {
            int selHeroe = comboHeroes.getSelectedIndex();
            if (controlador != null && selHeroe >= 0) {
                controlador.desequiparArmadura(selHeroe);
            }
        });
    }

    private void asignarStat(String stat) {
        int idx = comboHeroes.getSelectedIndex();
        if (controlador != null && idx >= 0) {
            controlador.asignarPuntoAtributo(idx, stat);
        }
    }

    public void actualizar(PartyGestionDTO dto) {
        actualizar(dto, null);
    }

    public void actualizar(PartyGestionDTO dto, Party partyModelo) {
        this.datosActuales = dto;
        this.partyModelo = partyModelo;

        int selActual = comboHeroes.getSelectedIndex();
        comboHeroes.removeAllItems();

        for (PersonajeDTO h : dto.getHeroes()) {
            String badge = h.getPuntosAtributoDisponibles() > 0 ? " [+" + h.getPuntosAtributoDisponibles() + " pts]" : "";
            comboHeroes.addItem(h.getNombre() + " (" + h.getTipo() + " Nv." + h.getNivel() + ")" + badge);
        }

        if (selActual >= 0 && selActual < dto.getHeroes().size()) {
            comboHeroes.setSelectedIndex(selActual);
        } else if (comboHeroes.getItemCount() > 0) {
            comboHeroes.setSelectedIndex(0);
        }

        // Armas
        comboArmas.removeAllItems();
        for (ItemDTO a : dto.getArmasDisponibles()) {
            comboArmas.addItem(a.getNombre() + " (" + a.getDescripcion() + ")");
        }

        // Armaduras
        comboArmaduras.removeAllItems();
        for (ItemDTO arm : dto.getArmadurasDisponibles()) {
            comboArmaduras.addItem(arm.getNombre() + " (" + arm.getDescripcion() + ")");
        }

        refrescarFichaHeroe();
    }

    private void refrescarFichaHeroe() {
        int idx = comboHeroes.getSelectedIndex();
        if (datosActuales == null || idx < 0 || idx >= datosActuales.getHeroes().size()) return;

        PersonajeDTO h = datosActuales.getHeroes().get(idx);
        Personaje modeloHeroe = (partyModelo != null && idx < partyModelo.getMiembros().size())
                ? partyModelo.getMiembros().get(idx) : null;

        lblNombreHeroe.setText(h.getNombre());
        lblClaseHeroe.setText(h.getTipo() + " - Nivel " + h.getNivel());

        Image img = GestorRecursos.obtenerSprite(h.getTipo(), 72, 72);
        if (img != null) lblSpriteHeroe.setIcon(new ImageIcon(img));

        // Barra de Experiencia (e.g. 60 / 100 XP)
        int xp = Math.max(0, h.getExperiencia());
        int xpSig = Math.max(1, h.getExperienciaSiguienteNivel());
        barraXpHeroe.setMaximum(xpSig);
        barraXpHeroe.setValue(xp);
        barraXpHeroe.setString(xp + " / " + xpSig + " XP para Nivel " + (h.getNivel() + 1));

        // Stats detalladas
        String statUnica = "";
        if (modeloHeroe instanceof Guerrero g) statUnica = "<br><b>Fuerza:</b> " + g.getFuerza();
        else if (modeloHeroe instanceof Arquero a) statUnica = "<br><b>Puntería:</b> " + a.getPunteria();
        else if (modeloHeroe instanceof Curador c) statUnica = "<br><b>Poder Curativo:</b> " + c.getPoderCurativo();
        else if (modeloHeroe instanceof Mago m) statUnica = "<br><b>Inteligencia:</b> " + m.getInteligencia();

        lblStatsTexto.setText("<html>"
                + "<b>Vida (HP):</b> " + h.getVida() + " / " + h.getVidaMaxima() + "<br>"
                + "<b>Maná/Ki (MP):</b> " + h.getMana() + " / " + h.getManaMaximo() + "<br>"
                + "<b>Ataque Total:</b> " + h.getAtaque() + "<br>"
                + "<b>Defensa Total:</b> " + h.getDefensa() + "<br>"
                + "<b>Velocidad:</b> " + h.getVelocidad()
                + statUnica + "</html>");

        lblSlotsActuales.setText("<html><b>Equipamiento Actual:</b><br>"
                + "• <b>Arma:</b> " + h.getArmaEquipada() + "<br>"
                + "• <b>Armadura:</b> " + h.getArmaduraEquipada() + "</html>");

        // Puntos de atributo disponibles
        int pts = h.getPuntosAtributoDisponibles();
        lblPuntosLibres.setText("Puntos de Atributo Disponibles: " + pts);
        boolean hayPuntos = (pts > 0);
        btnMasHp.setEnabled(hayPuntos);
        btnMasMp.setEnabled(hayPuntos);
        btnMasAtq.setEnabled(hayPuntos);
        btnMasDef.setEnabled(hayPuntos);
        btnMasVel.setEnabled(hayPuntos);

        // Desbloqueo y Selección de Habilidades
        panelHabilidades.removeAll();

        if (modeloHeroe != null) {
            JLabel lblHabsActivas = new JLabel("Habilidades Aprendidas (" + modeloHeroe.getHabilidades().size() + "/3):");
            lblHabsActivas.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblHabsActivas.setForeground(EstilosUI.TEXTO_CLARO);
            panelHabilidades.add(lblHabsActivas);

            for (Habilidad hab : modeloHeroe.getHabilidades()) {
                JLabel l = new JLabel("✔ " + hab.getNombre() + " (" + hab.getCosteMana() + " MP) - " + hab.getDescripcion());
                l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                l.setForeground(new Color(46, 204, 113));
                panelHabilidades.add(l);
            }

            // Habilidades bloqueadas para elegir
            List<Habilidad> bloqueadas = modeloHeroe.getHabilidadesBloqueadas();
            if (!bloqueadas.isEmpty()) {
                panelHabilidades.add(Box.createVerticalStrut(8));
                if (modeloHeroe.tieneEleccionHabilidadPendiente()) {
                    JLabel lblEleccion = new JLabel("¡SUBISTE DE NIVEL! Elige una nueva habilidad a desbloquear:");
                    lblEleccion.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lblEleccion.setForeground(new Color(241, 196, 15));
                    panelHabilidades.add(lblEleccion);

                    for (Habilidad hab : bloqueadas) {
                        JPanel filaHab = new JPanel(new BorderLayout(8, 0));
                        filaHab.setOpaque(false);
                        JLabel l = new JLabel("• " + hab.getNombre() + ": " + hab.getDescripcion());
                        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        l.setForeground(EstilosUI.TEXTO_CLARO);
                        filaHab.add(l, BorderLayout.CENTER);

                        JButton btnAprender = EstilosUI.crearBoton("Aprender", new Color(155, 89, 182), new Color(175, 110, 200));
                        btnAprender.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        btnAprender.addActionListener(ev -> {
                            if (controlador != null) {
                                controlador.desbloquearHabilidadHeroe(idx, hab);
                            }
                        });
                        filaHab.add(btnAprender, BorderLayout.EAST);
                        panelHabilidades.add(filaHab);
                        panelHabilidades.add(Box.createVerticalStrut(4));
                    }
                } else {
                    JLabel lblBloq = new JLabel("Habilidades restantes (se desbloquean al subir de nivel):");
                    lblBloq.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                    lblBloq.setForeground(EstilosUI.TEXTO_MUTED);
                    panelHabilidades.add(lblBloq);

                    for (Habilidad hab : bloqueadas) {
                        JLabel l = new JLabel("🔒 " + hab.getNombre() + ": " + hab.getDescripcion());
                        l.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                        l.setForeground(EstilosUI.TEXTO_MUTED);
                        panelHabilidades.add(l);
                    }
                }
            } else {
                panelHabilidades.add(Box.createVerticalStrut(4));
                JLabel lCompleto = new JLabel("¡Maestría alcanzada! Todas las habilidades desbloqueadas.");
                lCompleto.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lCompleto.setForeground(EstilosUI.COLOR_ORO);
                panelHabilidades.add(lCompleto);
            }
        }

        panelHabilidades.revalidate();
        panelHabilidades.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoParty != null) {
            g2.drawImage(fondoParty, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 215));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
