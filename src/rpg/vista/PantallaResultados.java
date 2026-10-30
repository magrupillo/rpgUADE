package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.PersonajeDTO;
import rpg.dto.RecompensaDTO;
import rpg.modelo.Juego;
import rpg.modelo.Personaje;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pantalla de Resultados Post-Combate y Resumen Completo de Run.
 * Muestra oro obtenido, armas y armaduras dropeadas, barras de progreso de XP (e.g. 60/100),
 * alertas de puntos y habilidades a desbloquear, y resumen final al ganar o perder.
 */
public class PantallaResultados extends JPanel {

    private ControladorJuego controlador;
    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;
    private final JPanel panelRecompensas;
    private final JPanel panelHeroesCards;
    private final JButton btnGestionEquipo;
    private final JButton btnContinuar;
    private final Image fondoResultados;

    private boolean esVictoria = true;
    private boolean esFinDeRun = false;

    public PantallaResultados() {
        this.fondoResultados = GestorRecursos.obtenerFondo("otro");
        setLayout(new BorderLayout(15, 12));
        setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));

        // ── Cabecera ──────────────────────────────────────────────────────────────
        JPanel pnlHead = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlHead.setOpaque(false);
        lblTitulo = new JLabel("VICTORIA EN COMBATE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);

        lblSubtitulo = new JLabel("Los enemigos han sido vencidos y la party recolecta su botín.", SwingConstants.CENTER);
        lblSubtitulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(EstilosUI.TEXTO_MUTED);

        pnlHead.add(lblTitulo);
        pnlHead.add(lblSubtitulo);
        add(pnlHead, BorderLayout.NORTH);

        // ── Centro: Recompensas de Botín + Tarjetas con Barras de XP ──────────────
        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        panelRecompensas = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 6));
        panelRecompensas.setBackground(new Color(24, 28, 40, 220));
        panelRecompensas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        panelRecompensas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlCentro.add(panelRecompensas);

        pnlCentro.add(Box.createVerticalStrut(12));

        panelHeroesCards = new JPanel(new GridLayout(1, 4, 14, 0));
        panelHeroesCards.setOpaque(false);
        pnlCentro.add(panelHeroesCards);

        add(pnlCentro, BorderLayout.CENTER);

        // ── Pie: Botones de Acción ────────────────────────────────────────────────
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pnlBotones.setOpaque(false);

        btnGestionEquipo = EstilosUI.crearBoton("Asignar Puntos y Equipar Héroes", new Color(230, 126, 34), new Color(243, 156, 18));
        btnGestionEquipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGestionEquipo.setPreferredSize(new Dimension(300, 42));
        btnGestionEquipo.addActionListener(e -> {
            if (controlador != null) controlador.abrirGestionParty();
        });

        btnContinuar = EstilosUI.crearBoton("Continuar", new Color(39, 174, 96), new Color(46, 204, 113));
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnContinuar.setPreferredSize(new Dimension(280, 42));
        btnContinuar.addActionListener(e -> {
            if (controlador != null) {
                if (!esVictoria || esFinDeRun) {
                    controlador.mostrarPantallaInicio();
                } else {
                    controlador.volverAlMapaOATorre();
                }
            }
        });

        pnlBotones.add(btnGestionEquipo);
        pnlBotones.add(btnContinuar);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(EstadoBatallaDTO estadoDTO, RecompensaDTO recompensa) {
        actualizar(estadoDTO, recompensa, null);
    }

    public void actualizar(EstadoBatallaDTO estadoDTO, RecompensaDTO recompensa, Juego juego) {
        this.esVictoria = estadoDTO.isVictoria();
        this.esFinDeRun = (juego != null && (juego.isJuegoCompletadoVictoria() || !esVictoria));

        panelRecompensas.removeAll();
        panelHeroesCards.removeAll();

        if (juego != null && juego.isJuegoCompletadoVictoria()) {
            // VICTORIA TOTAL DE LA RUN
            lblTitulo.setText("VICTORIA TOTAL - ¡CONQUISTASTE LA TORRE!");
            lblTitulo.setForeground(EstilosUI.COLOR_ORO);
            lblSubtitulo.setText("Derrotaste a Bowser en el Piso 3. ¡Sos el amo supremo de 'El Último Ascenso'!");
            btnContinuar.setText("Volver al Menú Principal");
            btnGestionEquipo.setVisible(false);
            mostrarResumenFinalRun(juego);
            return;
        }

        if (!esVictoria) {
            // DERROTA / PERMADEATH
            lblTitulo.setText("DERROTA - PERMADEATH");
            lblTitulo.setForeground(EstilosUI.COLOR_ROJO);
            lblSubtitulo.setText("Toda la party cayó en combate. La partida guardada ha sido eliminada.");
            btnContinuar.setText("Volver al Menú Principal");
            btnGestionEquipo.setVisible(false);
            if (juego != null) {
                mostrarResumenFinalRun(juego);
            }
            return;
        }

        // VICTORIA REGULAR DE COMBATE
        lblTitulo.setText("VICTORIA EN " + estadoDTO.getLugar().toUpperCase());
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);
        btnContinuar.setText("Continuar");

        // Detalle de recompensas (Oro, XP e Ítems)
        if (recompensa != null) {
            JLabel lblOro = new JLabel("💰 Oro: +$" + recompensa.getOro());
            lblOro.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblOro.setForeground(EstilosUI.COLOR_ORO);
            panelRecompensas.add(lblOro);

            JLabel lblXp = new JLabel("XP Total: +" + recompensa.getExperienciaTotalOtorgada()
                    + " (+" + recompensa.getExperienciaPorHeroe() + " c/u)");
            lblXp.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblXp.setForeground(new Color(52, 152, 219));
            panelRecompensas.add(lblXp);

            if (!recompensa.getItemsObtenidos().isEmpty()) {
                JLabel lblItems = new JLabel("Botín Obtenido: " + String.join(", ", recompensa.getItemsObtenidos()));
                lblItems.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblItems.setForeground(new Color(46, 204, 113));
                panelRecompensas.add(lblItems);
            } else {
                JLabel lblSinItems = new JLabel("Botín: Sin objetos adicionales.");
                lblSinItems.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                lblSinItems.setForeground(EstilosUI.TEXTO_MUTED);
                panelRecompensas.add(lblSinItems);
            }
        }

        // Tarjetas de héroes con barras de XP
        boolean algunPuntoPendiente = false;
        List<PersonajeDTO> heroes = estadoDTO.getHeroes();
        for (PersonajeDTO h : heroes) {
            if (h.getPuntosAtributoDisponibles() > 0) algunPuntoPendiente = true;
            panelHeroesCards.add(crearTarjetaHeroeProgreso(h));
        }

        btnGestionEquipo.setVisible(algunPuntoPendiente || (recompensa != null && !recompensa.getItemsObtenidos().isEmpty()));
        if (algunPuntoPendiente) {
            btnGestionEquipo.setText("Puntos Pendientes: Asignar y Equipar");
            btnGestionEquipo.setBackground(new Color(230, 126, 34));
        } else {
            btnGestionEquipo.setText("Ver Héroes y Equipar Botín");
            btnGestionEquipo.setBackground(new Color(41, 128, 185));
        }

        panelRecompensas.revalidate();
        panelRecompensas.repaint();
        panelHeroesCards.revalidate();
        panelHeroesCards.repaint();
    }

    private JPanel crearTarjetaHeroeProgreso(PersonajeDTO h) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(26, 30, 42, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(h.getPuntosAtributoDisponibles() > 0 ? EstilosUI.BORDE_ACTIVO : EstilosUI.BORDE_SUAVE,
                        h.getPuntosAtributoDisponibles() > 0 ? 2 : 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // Nombre y Clase
        JLabel lblNom = new JLabel(h.getNombre(), SwingConstants.CENTER);
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNom.setForeground(EstilosUI.TEXTO_CLARO);
        lblNom.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblNom);

        JLabel lblClase = new JLabel(h.getTipo() + " - Nivel " + h.getNivel(), SwingConstants.CENTER);
        lblClase.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblClase.setForeground(EstilosUI.COLOR_ORO);
        lblClase.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblClase);

        card.add(Box.createVerticalStrut(8));

        // Sprite
        Image img = GestorRecursos.obtenerSprite(h.getTipo(), 68, 68);
        JLabel lblSprite = new JLabel(new ImageIcon(img));
        lblSprite.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSprite);

        card.add(Box.createVerticalStrut(10));

        // Barra de Experiencia visual (e.g. 60 / 100 XP)
        int xpActual = Math.max(0, h.getExperiencia());
        int xpMax = Math.max(1, h.getExperienciaSiguienteNivel());
        JProgressBar barraXp = EstilosUI.crearBarra(0, xpMax, new Color(155, 89, 182));
        barraXp.setValue(xpActual);
        barraXp.setString(xpActual + " / " + xpMax + " XP");
        barraXp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        barraXp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        card.add(barraXp);

        card.add(Box.createVerticalStrut(10));

        // Aviso de Puntos o Subida de Nivel
        if (h.getPuntosAtributoDisponibles() > 0) {
            JLabel lblNivelUp = new JLabel("¡SUBIÓ DE NIVEL!", SwingConstants.CENTER);
            lblNivelUp.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblNivelUp.setForeground(new Color(46, 204, 113));
            lblNivelUp.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblNivelUp);

            JLabel lblPts = new JLabel("+" + h.getPuntosAtributoDisponibles() + " Pts de Atributo a elegir", SwingConstants.CENTER);
            lblPts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblPts.setForeground(new Color(241, 196, 15));
            lblPts.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblPts);
        } else {
            JLabel lblStats = new JLabel("HP: " + h.getVida() + "/" + h.getVidaMaxima() + " | ATQ: " + h.getAtaque(), SwingConstants.CENTER);
            lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblStats.setForeground(EstilosUI.TEXTO_MUTED);
            lblStats.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblStats);
        }

        return card;
    }

    private void mostrarResumenFinalRun(Juego juego) {
        JPanel pnlResumen = new JPanel();
        pnlResumen.setLayout(new BoxLayout(pnlResumen, BoxLayout.Y_AXIS));
        pnlResumen.setBackground(new Color(24, 28, 40, 230));
        pnlResumen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.COLOR_ORO, 2, true),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));
        pnlResumen.setMaximumSize(new Dimension(650, 260));

        JLabel lblTitResumen = new JLabel("RESUMEN COMPLETO DE LA RUN", SwingConstants.CENTER);
        lblTitResumen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitResumen.setForeground(EstilosUI.COLOR_ORO);
        lblTitResumen.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlResumen.add(lblTitResumen);

        pnlResumen.add(Box.createVerticalStrut(14));

        String estadoRun = juego.isJuegoCompletadoVictoria() ? "Victoria Total (Piso 3 Superado)" : "Derrota en Piso " + juego.getPiso();
        agregarFilaResumen(pnlResumen, "Resultado de la Run:", estadoRun, juego.isJuegoCompletadoVictoria() ? new Color(46, 204, 113) : EstilosUI.COLOR_ROJO);
        agregarFilaResumen(pnlResumen, "Pisos de la Torre alcanzados:", juego.getPiso() + " de " + Juego.TOTAL_PISOS, EstilosUI.TEXTO_CLARO);
        agregarFilaResumen(pnlResumen, "Combates Ganados:", String.valueOf(juego.getCombatesGanados()), EstilosUI.TEXTO_CLARO);
        agregarFilaResumen(pnlResumen, "Oro Total Acumulado en la Run:", "$" + juego.getTotalOroAcumulado(), EstilosUI.COLOR_ORO);

        pnlResumen.add(Box.createVerticalStrut(10));
        JLabel lblHeroesTit = new JLabel("Estado final de la Party:", SwingConstants.CENTER);
        lblHeroesTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHeroesTit.setForeground(EstilosUI.TEXTO_MUTED);
        lblHeroesTit.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlResumen.add(lblHeroesTit);

        StringBuilder sb = new StringBuilder();
        for (Personaje p : juego.getParty().getMiembros()) {
            sb.append(p.getNombre()).append(" (Nv.").append(p.getNivel()).append(" ").append(p.getTipo()).append(")  •  ");
        }
        JLabel lblPartyFinal = new JLabel(sb.toString(), SwingConstants.CENTER);
        lblPartyFinal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPartyFinal.setForeground(EstilosUI.TEXTO_CLARO);
        lblPartyFinal.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlResumen.add(lblPartyFinal);

        panelHeroesCards.setLayout(new GridBagLayout());
        panelHeroesCards.add(pnlResumen);
    }

    private void agregarFilaResumen(JPanel pnl, String etiqueta, String valor, Color colorValor) {
        JPanel f = new JPanel(new BorderLayout(10, 0));
        f.setOpaque(false);
        f.setMaximumSize(new Dimension(580, 24));

        JLabel l1 = new JLabel(etiqueta);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l1.setForeground(EstilosUI.TEXTO_CLARO);

        JLabel l2 = new JLabel(valor, SwingConstants.RIGHT);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l2.setForeground(colorValor);

        f.add(l1, BorderLayout.WEST);
        f.add(l2, BorderLayout.EAST);
        pnl.add(f);
        pnl.add(Box.createVerticalStrut(4));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoResultados != null) {
            g2.drawImage(fondoResultados, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 210));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
