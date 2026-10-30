package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.modelo.*;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;
import rpg.util.PanelVidrio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PantallaCreacionParty extends JPanel {
    private ControladorJuego controlador;

    public PantallaCreacionParty() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Título
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);
        JLabel lblTitulo = new JLabel("🛡️ CONFIGURACIÓN DE LA PARTY Y ATRIBUTOS BASE", SwingConstants.CENTER);
        lblTitulo.setFont(EstilosUI.FUENTE_TITULO);
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);
        JLabel lblDesc = new JLabel("Forma tu equipo de héroes antes de ingresar a la torre. Cada héroe cuenta con 10 puntos de atributos libres.", SwingConstants.CENTER);
        lblDesc.setFont(EstilosUI.FUENTE_NORMAL);
        lblDesc.setForeground(EstilosUI.TEXTO_MUTED);
        headerPanel.add(lblTitulo, BorderLayout.NORTH);
        headerPanel.add(lblDesc, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Tarjetas de los 4 Héroes
        JPanel panelCards = new JPanel(new GridLayout(1, 4, 12, 0));
        panelCards.setOpaque(false);

        panelCards.add(crearTarjetaArquetipo("Arthur", "Guerrero", "Físico / Tanque", "Nivel de Amenaza (Provocar): Obliga a que los enemigos lo ataquen protegiendo a aliados."));
        panelCards.add(crearTarjetaArquetipo("Legolas", "Arquero", "Precisión / Crítico", "Ataque Crítico: Probabilidad de impacto que duplica el daño base. Lluvia de flechas en área."));
        panelCards.add(crearTarjetaArquetipo("Gandalf", "Mago", "Hechizos Ofensivos", "Daño Explosivo: Bolas de fuego masivas y tormenta de rayos contra todos los enemigos."));
        panelCards.add(crearTarjetaArquetipo("Elena", "Curador", "Soporte Vital", "Curación: Restaura vida y purifica estados alterados (veneno y aturdimiento) de aliados."));

        add(panelCards, BorderLayout.CENTER);

        // Botón de Inicio de Partida
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelInferior.setOpaque(false);

        JButton btnVolver = EstilosUI.crearBoton("⬅ Volver al Menú", new Color(70, 80, 95), new Color(90, 100, 120));
        btnVolver.addActionListener(e -> {
            if (controlador != null) controlador.mostrarPantallaInicio();
        });

        JButton btnComenzar = EstilosUI.crearBoton("⚔️ INICIAR ASCENSIÓN A LA TORRE ⚔️", new Color(39, 174, 96), new Color(46, 204, 113));
        btnComenzar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnComenzar.setPreferredSize(new Dimension(320, 45));
        btnComenzar.addActionListener(e -> {
            if (controlador != null) {
                Party party = crearPartyInicial();
                controlador.iniciarPartidaConParty(party);
            }
        });

        panelInferior.add(btnVolver);
        panelInferior.add(btnComenzar);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    private JPanel crearTarjetaArquetipo(String nombreDefault, String clase, String rol, String mecanica) {
        PanelVidrio card = new PanelVidrio(18);
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Header
        JPanel pnlHead = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlHead.setOpaque(false);
        JLabel lblNombre = new JLabel(nombreDefault, SwingConstants.CENTER);
        lblNombre.setFont(EstilosUI.FUENTE_TITULO);
        lblNombre.setForeground(EstilosUI.TEXTO_CLARO);

        JLabel lblClase = new JLabel(clase + " - " + rol, SwingConstants.CENTER);
        lblClase.setFont(EstilosUI.FUENTE_BOLD);
        lblClase.setForeground(EstilosUI.COLOR_ORO);
        pnlHead.add(lblNombre);
        pnlHead.add(lblClase);
        card.add(pnlHead, BorderLayout.NORTH);

        // Sprite y Mecánica
        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        JLabel lblIcono = new JLabel(new ImageIcon(GestorRecursos.obtenerSprite(clase, 80, 80)));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlCentro.add(lblIcono);

        pnlCentro.add(Box.createVerticalStrut(10));

        JTextArea txtMecanica = new JTextArea(mecanica);
        txtMecanica.setEditable(false);
        txtMecanica.setWrapStyleWord(true);
        txtMecanica.setLineWrap(true);
        txtMecanica.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtMecanica.setForeground(EstilosUI.TEXTO_MUTED);
        txtMecanica.setOpaque(false);
        pnlCentro.add(txtMecanica);

        card.add(pnlCentro, BorderLayout.CENTER);

        // Indicador de 10 puntos asignables
        JLabel lblPuntos = new JLabel("+10 Pts de Atributos disponibles", SwingConstants.CENTER);
        lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPuntos.setForeground(new Color(46, 204, 113));
        card.add(lblPuntos, BorderLayout.SOUTH);

        return card;
    }

    private Party crearPartyInicial() {
        Party party = new Party();

        // 4 Arquetipos de la Party con 10 puntos libres disponibles para distribuir (Sección 3)
        Personaje arthur = new Guerrero("Arthur (Guerrero)");
        arthur.agregarPuntosAtributo(10);

        Personaje legolas = new Arquero("Legolas (Arquero)");
        legolas.agregarPuntosAtributo(10);

        Personaje gandalf = new Mago("Gandalf (Mago)");
        gandalf.agregarPuntosAtributo(10);

        Personaje elena = new Curador("Elena (Curadora)");
        elena.agregarPuntosAtributo(10);

        party.agregarMiembro(arthur);
        party.agregarMiembro(legolas);
        party.agregarMiembro(gandalf);
        party.agregarMiembro(elena);

        // Equipamiento y consumibles iniciales de la Party
        Inventario inv = party.getInventario();
        Arma espadaAcero = new Arma("Espada de Acero", "Aumenta el daño físico", 15);
        Armadura cotaMallas = new Armadura("Cota de Mallas", "Protección de acero forjado", 10);
        Arma varaMagica = new Arma("Vara de Roble Encantada", "Canalizador de magia arcana", 12);
        Armadura mantoSeda = new Armadura("Manto de Seda Rúnica", "Resistencia mágica", 8);

        inv.agregarItem(espadaAcero);
        inv.agregarItem(cotaMallas);
        inv.agregarItem(varaMagica);
        inv.agregarItem(mantoSeda);
        inv.agregarItem(new PocionVida("Poción de Salud Mayor", "Restaura 70 puntos de vida", 70));
        inv.agregarItem(new PocionVida("Poción de Salud Media", "Restaura 40 puntos de vida", 40));
        inv.agregarItem(new PocionMana("Poción de Maná Mayor", "Restaura 50 puntos de maná", 50));

        // Equipar automáticamente arma y armadura al guerrero de partida
        inv.equiparArma(espadaAcero, arthur);
        inv.equiparArmadura(cotaMallas, arthur);

        return party;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Image fondo = GestorRecursos.obtenerFondo("otro");
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 205));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        EstilosUI.pintarVineta(g2, getWidth(), getHeight(), 190);
        g2.dispose();
    }
}
