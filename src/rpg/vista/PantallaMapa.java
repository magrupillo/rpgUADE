package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.MapaDTO;
import rpg.dto.NodoDTO;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de Mapa (Sección 2 y 9 del documento):
 * Interfaz para elegir el próximo destino en la torre a través de un grafo
 * de nodos interconectados (Combate Estándar, Élite, Evento, Descanso, Jefe).
 */
public class PantallaMapa extends JPanel {
    private ControladorJuego controlador;
    private final JLabel lblPiso;
    private final JPanel panelGrafoNodos;

    public PantallaMapa() {
        setLayout(new BorderLayout(15, 15));
        setBackground(EstilosUI.FONDO_OSCURO);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 1. Barra Superior con Estado y Navegación
        JPanel barraSuperior = new JPanel(new BorderLayout(15, 0));
        barraSuperior.setBackground(EstilosUI.FONDO_PANEL);
        barraSuperior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlInfo.setOpaque(false);
        lblPiso = new JLabel("🏰 Piso 1 de la Torre");
        lblPiso.setFont(EstilosUI.FUENTE_TITULO);
        lblPiso.setForeground(EstilosUI.COLOR_ORO);

        pnlInfo.add(lblPiso);
        barraSuperior.add(pnlInfo, BorderLayout.WEST);

        // Botones de acción rápida
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);

        JButton btnParty = EstilosUI.crearBoton("🛡️ Héroes y Equipo", new Color(41, 128, 185), new Color(52, 152, 219));
        btnParty.addActionListener(e -> {
            if (controlador != null) controlador.abrirGestionParty();
        });

        JButton btnGuardar = EstilosUI.crearBoton("💾 Guardar Partida", new Color(39, 174, 96), new Color(46, 204, 113));
        btnGuardar.addActionListener(e -> {
            if (controlador != null) controlador.guardarPartida();
        });

        JButton btnMenu = EstilosUI.crearBoton("🏠 Menú", new Color(70, 80, 95), new Color(90, 100, 120));
        btnMenu.addActionListener(e -> {
            if (controlador != null) controlador.mostrarPantallaInicio();
        });

        pnlBotones.add(btnParty);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnMenu);
        barraSuperior.add(pnlBotones, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        // 2. Contenedor del Grafo de Nodos con Scrollbar suave (Centro)
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

        // 3. Barra Inferior con Instrucciones
        JPanel barraInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        barraInferior.setOpaque(false);
        JLabel lblGuia = new JLabel("💡 Selecciona una sala habilitada para avanzar. Las rutas conectadas convergen y bifurcan permitiendo alternar tu camino.");
        lblGuia.setFont(EstilosUI.FUENTE_NORMAL);
        lblGuia.setForeground(EstilosUI.TEXTO_MUTED);
        barraInferior.add(lblGuia);
        add(barraInferior, BorderLayout.SOUTH);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(MapaDTO mapaDTO, int metaPuntos) {
        lblPiso.setText("🏰 Piso " + mapaDTO.getPiso() + " de la Torre");

        panelGrafoNodos.removeAll();

        // Determinar dinámicamente el número total de capas/columnas
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

        panelGrafoNodos.setLayout(new GridLayout(1, totalCols, 12, 0));

        for (int col = 0; col < totalCols; col++) {
            String tituloCol;
            if (col == 0) tituloCol = "Entrada";
            else if (col == totalCols - 1) tituloCol = "Jefe";
            else if (col == totalCols - 2) tituloCol = "Pre-Jefe";
            else if (col == 1) tituloCol = "Caminos";
            else if (col == 2) tituloCol = "Profundidades";
            else if (col == 3) tituloCol = "Encrucijada";
            else tituloCol = "Peligro";

            JPanel pnlCol = new JPanel();
            pnlCol.setLayout(new BoxLayout(pnlCol, BoxLayout.Y_AXIS));
            pnlCol.setOpaque(false);
            pnlCol.setPreferredSize(new Dimension(175, 0));
            pnlCol.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                    "Nivel " + (col + 1) + " (" + tituloCol + ")",
                    0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.TEXTO_MUTED
            ));

            List<NodoDTO> nodosCol = columnas.get(col);
            pnlCol.add(Box.createVerticalGlue());
            for (NodoDTO n : nodosCol) {
                JPanel cardNodo = crearTarjetaNodo(n);
                pnlCol.add(cardNodo);
                pnlCol.add(Box.createVerticalStrut(8));
            }
            pnlCol.add(Box.createVerticalGlue());

            panelGrafoNodos.add(pnlCol);
        }

        panelGrafoNodos.revalidate();
        panelGrafoNodos.repaint();
    }

    private JPanel crearTarjetaNodo(NodoDTO n) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(n.isEsActual() ? new Color(45, 40, 25) : EstilosUI.FONDO_PANEL);

        Color colorBorde = EstilosUI.BORDE_SUAVE;
        if (n.isEsActual()) colorBorde = EstilosUI.COLOR_ORO;
        else if (n.isDisponible() && !n.isCompletado()) colorBorde = EstilosUI.BORDE_ACTIVO;
        else if (n.isCompletado()) colorBorde = new Color(46, 204, 113);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, n.isEsActual() || n.isDisponible() ? 2 : 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(220, 105));
        card.setPreferredSize(new Dimension(165, 100));
        card.setToolTipText("Sala #" + n.getId() + " - Conecta con " + n.getConexiones().size() + " camino(s) hacia adelante");

        // Título del Nodo
        JPanel pnlHead = new JPanel(new BorderLayout(4, 1));
        pnlHead.setOpaque(false);
        JLabel lblIcono = new JLabel(n.getIcono() + " " + n.getNombre());
        lblIcono.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblIcono.setForeground(n.isCompletado() ? new Color(46, 204, 113) : EstilosUI.TEXTO_CLARO);

        JLabel lblTipo = new JLabel(n.getTipo() + "  •  " + n.getConexiones().size() + " ruta(s)");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTipo.setForeground(EstilosUI.TEXTO_MUTED);

        pnlHead.add(lblIcono, BorderLayout.NORTH);
        pnlHead.add(lblTipo, BorderLayout.SOUTH);
        card.add(pnlHead, BorderLayout.NORTH);

        // Botón o Etiqueta de Estado
        if (n.isCompletado()) {
            JLabel lblComp = new JLabel("✔️ Completada", SwingConstants.CENTER);
            lblComp.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblComp.setForeground(new Color(46, 204, 113));
            card.add(lblComp, BorderLayout.SOUTH);
        } else if (n.isDisponible()) {
            JButton btnAvanzar = EstilosUI.crearBoton("▶ Entrar", new Color(192, 57, 43), new Color(231, 76, 60));
            btnAvanzar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAvanzar.setPreferredSize(new Dimension(0, 28));
            btnAvanzar.addActionListener(e -> {
                if (controlador != null) {
                    controlador.seleccionarNodo(n.getId());
                }
            });
            card.add(btnAvanzar, BorderLayout.SOUTH);
        } else {
            JLabel lblBloq = new JLabel("🔒 Bloqueada", SwingConstants.CENTER);
            lblBloq.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblBloq.setForeground(EstilosUI.TEXTO_MUTED);
            card.add(lblBloq, BorderLayout.SOUTH);
        }

        return card;
    }
}
