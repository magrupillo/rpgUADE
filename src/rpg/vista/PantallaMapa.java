package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.MapaDTO;
import rpg.dto.NodoDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de Mapa de la Torre (El Último Ascenso).
 * Presenta el grafo procedimental de nodos con Niebla de Exploración (Fog of War)
 * y barra de estado con oro y notificaciones de party.
 */
public class PantallaMapa extends JPanel {

    private ControladorJuego controlador;
    private final JLabel lblPiso;
    private final JLabel lblOro;
    private final JButton btnParty;
    private final JPanel panelGrafoNodos;
    private final Image fondoMapa;

    public PantallaMapa() {
        this.fondoMapa = GestorRecursos.obtenerFondo("mapa");
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ── 1. Barra Superior con Estado, Oro y Navegación ──────────────────────────
        JPanel barraSuperior = new JPanel(new BorderLayout(15, 0));
        barraSuperior.setBackground(new Color(24, 28, 40, 230));
        barraSuperior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlInfo.setOpaque(false);
        lblPiso = new JLabel("Piso 1 de la Torre (1/3)");
        lblPiso.setFont(EstilosUI.FUENTE_TITULO);
        lblPiso.setForeground(EstilosUI.COLOR_ORO);

        lblOro = new JLabel("Oro: $50");
        lblOro.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblOro.setForeground(EstilosUI.COLOR_ORO);

        pnlInfo.add(lblPiso);
        pnlInfo.add(lblOro);
        barraSuperior.add(pnlInfo, BorderLayout.WEST);

        // Botones de acción rápida
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);

        btnParty = EstilosUI.crearBoton("Héroes y Equipo", new Color(41, 128, 185), new Color(52, 152, 219));
        btnParty.addActionListener(e -> {
            if (controlador != null) controlador.abrirGestionParty();
        });

        JButton btnGuardar = EstilosUI.crearBoton("Guardar Partida", new Color(39, 174, 96), new Color(46, 204, 113));
        btnGuardar.addActionListener(e -> {
            if (controlador != null) controlador.guardarPartida();
        });

        pnlBotones.add(btnParty);
        pnlBotones.add(btnGuardar);
        barraSuperior.add(pnlBotones, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        // ── 2. Contenedor del Grafo de Nodos con Scrollbar suave (Centro) ─────────
        panelGrafoNodos = new JPanel();
        panelGrafoNodos.setOpaque(false);

        JScrollPane scrollGrafo = new JScrollPane(panelGrafoNodos);
        scrollGrafo.setBorder(BorderFactory.createEmptyBorder());
        scrollGrafo.setOpaque(false);
        scrollGrafo.getViewport().setOpaque(false);
        scrollGrafo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollGrafo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGrafo.getHorizontalScrollBar().setUnitIncrement(24);
        scrollGrafo.getVerticalScrollBar().setUnitIncrement(24);
        add(scrollGrafo, BorderLayout.CENTER);

        // ── 3. Barra Inferior con Instrucciones ────────────────────────────────────
        JPanel barraInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        barraInferior.setOpaque(false);
        JLabel lblGuia = new JLabel("Elige una sala conectada para avanzar. Las zonas lejanas permanecen cubiertas por la niebla hasta que te aproximes.");
        lblGuia.setFont(EstilosUI.FUENTE_NORMAL);
        lblGuia.setForeground(EstilosUI.TEXTO_MUTED);
        barraInferior.add(lblGuia);
        add(barraInferior, BorderLayout.SOUTH);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(MapaDTO mapaDTO, int metaPuntos) {
        actualizar(mapaDTO, metaPuntos, false);
    }

    public void actualizar(MapaDTO mapaDTO, int oroActual, boolean tienePuntosPendientes) {
        lblPiso.setText("Piso " + mapaDTO.getPiso() + " de la Torre (" + mapaDTO.getPiso() + "/3)");
        lblOro.setText("Oro: $" + oroActual);

        if (tienePuntosPendientes) {
            btnParty.setText("Héroes y Equipo (Puntos pendientes!)");
            btnParty.setBackground(new Color(230, 126, 34));
        } else {
            btnParty.setText("Héroes y Equipo");
            btnParty.setBackground(new Color(41, 128, 185));
        }

        panelGrafoNodos.removeAll();

        // Determinar columna del nodo actual para aplicar la niebla
        int columnaActual = 0;
        for (NodoDTO n : mapaDTO.getNodos()) {
            if (n.isEsActual()) {
                columnaActual = n.getColumna();
                break;
            }
        }

        int maxCol = 0;
        for (NodoDTO n : mapaDTO.getNodos()) {
            if (n.getColumna() > maxCol) maxCol = n.getColumna();
        }
        int totalCols = maxCol + 1;

        Map<Integer, List<NodoDTO>> columnas = new HashMap<>();
        for (int i = 0; i < totalCols; i++) {
            columnas.put(i, new ArrayList<>());
        }

        for (NodoDTO n : mapaDTO.getNodos()) {
            columnas.get(n.getColumna()).add(n);
        }

        panelGrafoNodos.setLayout(new GridLayout(1, totalCols, 14, 0));

        for (int col = 0; col < totalCols; col++) {
            String tituloCol;
            if (col == 0) tituloCol = "Entrada";
            else if (col == totalCols - 1) tituloCol = "Jefe";
            else if (col == totalCols - 2) tituloCol = "Pre-Jefe";
            else if (col == 1) tituloCol = "Caminos";
            else if (col == 2) tituloCol = "Intermedio";
            else if (col == 3) tituloCol = "Encrucijada";
            else tituloCol = "Peligro";

            JPanel pnlCol = new JPanel();
            pnlCol.setLayout(new BoxLayout(pnlCol, BoxLayout.Y_AXIS));
            pnlCol.setOpaque(false);
            pnlCol.setPreferredSize(new Dimension(185, 0));
            pnlCol.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                    "Nivel " + (col + 1) + " (" + tituloCol + ")",
                    0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.TEXTO_MUTED
            ));

            List<NodoDTO> nodosCol = columnas.get(col);
            pnlCol.add(Box.createVerticalGlue());
            for (NodoDTO n : nodosCol) {
                // Niebla: Se revela si fue completado, si es actual, o si está en la columna siguiente inmediata
                boolean revelado = n.isCompletado() || n.isEsActual() || n.isDisponible() || (col <= columnaActual + 1);
                JPanel cardNodo = crearTarjetaNodo(n, revelado);
                pnlCol.add(cardNodo);
                pnlCol.add(Box.createVerticalStrut(10));
            }
            pnlCol.add(Box.createVerticalGlue());

            panelGrafoNodos.add(pnlCol);
        }

        panelGrafoNodos.revalidate();
        panelGrafoNodos.repaint();
    }

    private JPanel crearTarjetaNodo(NodoDTO n, boolean revelado) {
        JPanel card = new JPanel(new BorderLayout(6, 6));

        if (!revelado) {
            // Tarjeta cubierta por la Niebla de Guerra
            card.setBackground(new Color(20, 22, 30, 180));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(45, 50, 65), 1, true),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            card.setMaximumSize(new Dimension(220, 105));
            card.setPreferredSize(new Dimension(175, 100));

            JLabel lblOculto = new JLabel("???", SwingConstants.CENTER);
            lblOculto.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblOculto.setForeground(new Color(110, 115, 135));

            JLabel lblDescOculta = new JLabel("Zona No Explorada", SwingConstants.CENTER);
            lblDescOculta.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblDescOculta.setForeground(new Color(90, 95, 115));

            card.add(lblOculto, BorderLayout.CENTER);
            card.add(lblDescOculta, BorderLayout.SOUTH);
            return card;
        }

        card.setBackground(n.isEsActual() ? new Color(48, 42, 28) : new Color(26, 30, 42, 220));

        Color colorBorde = EstilosUI.BORDE_SUAVE;
        if (n.isEsActual()) colorBorde = EstilosUI.COLOR_ORO;
        else if (n.isDisponible() && !n.isCompletado()) colorBorde = EstilosUI.BORDE_ACTIVO;
        else if (n.isCompletado()) colorBorde = new Color(46, 204, 113);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, n.isEsActual() || n.isDisponible() ? 2 : 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(220, 105));
        card.setPreferredSize(new Dimension(175, 100));
        card.setToolTipText(n.getTipo() + " - Conecta con " + n.getConexiones().size() + " ruta(s)");

        // Cabecera
        JPanel pnlHead = new JPanel(new BorderLayout(4, 1));
        pnlHead.setOpaque(false);
        JLabel lblIcono = new JLabel(n.getIcono() + " " + n.getNombre());
        lblIcono.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblIcono.setForeground(n.isCompletado() ? new Color(46, 204, 113) : EstilosUI.TEXTO_CLARO);

        JLabel lblTipo = new JLabel(n.getTipo());
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTipo.setForeground(EstilosUI.TEXTO_MUTED);

        pnlHead.add(lblIcono, BorderLayout.NORTH);
        pnlHead.add(lblTipo, BorderLayout.SOUTH);
        card.add(pnlHead, BorderLayout.NORTH);

        // Estado / Botón
        if (n.isCompletado()) {
            JLabel lblComp = new JLabel("[Completada]", SwingConstants.CENTER);
            lblComp.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblComp.setForeground(new Color(46, 204, 113));
            card.add(lblComp, BorderLayout.SOUTH);
        } else if (n.isDisponible()) {
            JButton btnAvanzar = EstilosUI.crearBoton("Entrar", new Color(192, 57, 43), new Color(231, 76, 60));
            btnAvanzar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAvanzar.setPreferredSize(new Dimension(0, 30));
            btnAvanzar.addActionListener(e -> {
                if (controlador != null) {
                    controlador.seleccionarNodo(n.getId());
                }
            });
            card.add(btnAvanzar, BorderLayout.SOUTH);
        } else {
            JLabel lblBloq = new JLabel("[En espera]", SwingConstants.CENTER);
            lblBloq.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblBloq.setForeground(EstilosUI.TEXTO_MUTED);
            card.add(lblBloq, BorderLayout.SOUTH);
        }

        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoMapa != null) {
            g2.drawImage(fondoMapa, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 205));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
