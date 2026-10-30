package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.modelo.Juego;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de la Torre (El Último Ascenso).
 * Permite visualizar la progresión vertical entre los 3 pisos de la torre
 * y seleccionar el piso activo desbloqueado.
 */
public class PantallaTorre extends JPanel {

    private ControladorJuego controlador;
    private final JLabel lblOro;
    private final JButton btnGestion;
    private final JButton btnPiso3;
    private final JButton btnPiso2;
    private final JButton btnPiso1;

    private int pisoMaximoDesbloqueado = 1;
    private final Image fondoTorre;

    public PantallaTorre() {
        this.fondoTorre = GestorRecursos.obtenerFondo("torre");
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Barra Superior (Oro y Héroes) ──────────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout(15, 0));
        pnlTop.setOpaque(false);

        lblOro = new JLabel("💰 Oro: $50");
        lblOro.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblOro.setForeground(EstilosUI.COLOR_ORO);
        pnlTop.add(lblOro, BorderLayout.WEST);

        JPanel pnlTopDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlTopDerecha.setOpaque(false);

        btnGestion = EstilosUI.crearBoton("Héroes y Equipo", new Color(41, 128, 185), new Color(52, 152, 219));
        btnGestion.addActionListener(e -> {
            if (controlador != null) controlador.abrirGestionParty();
        });

        JButton btnMenu = EstilosUI.crearBoton("Menú Principal", new Color(70, 80, 95), new Color(90, 100, 120));
        btnMenu.addActionListener(e -> {
            if (controlador != null) controlador.mostrarPantallaInicio();
        });

        pnlTopDerecha.add(btnGestion);
        pnlTopDerecha.add(btnMenu);
        pnlTop.add(pnlTopDerecha, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // ── Panel Central: Estructura Vertical de la Torre ────────────────────────
        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        JLabel lblTit = new JLabel("TORRE DE LA ASCENSIÓN", SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTit.setForeground(EstilosUI.COLOR_ORO);
        lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlCentro.add(lblTit);

        pnlCentro.add(Box.createVerticalStrut(4));

        JLabel lblSub = new JLabel("Supera los 3 pisos de la torre para derrotar a Bowser y coronar la victoria", SwingConstants.CENTER);
        lblSub.setFont(EstilosUI.FUENTE_NORMAL);
        lblSub.setForeground(EstilosUI.TEXTO_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlCentro.add(lblSub);

        pnlCentro.add(Box.createVerticalStrut(30));

        // Piso 3 (Cúspide - Bowser)
        btnPiso3 = crearTarjetaPiso("PISO 3: CÚSPIDE DE LA TORRE (Jefe Final: Bowser)", 3);
        pnlCentro.add(btnPiso3);

        pnlCentro.add(Box.createVerticalStrut(18));

        // Piso 2 (Profundidades)
        btnPiso2 = crearTarjetaPiso("PISO 2: PROFUNDIDADES PELIGROSAS", 2);
        pnlCentro.add(btnPiso2);

        pnlCentro.add(Box.createVerticalStrut(18));

        // Piso 1 (Base)
        btnPiso1 = crearTarjetaPiso("PISO 1: BASE DE LA TORRE (Entrada)", 1);
        pnlCentro.add(btnPiso1);

        add(pnlCentro, BorderLayout.CENTER);
    }

    private JButton crearTarjetaPiso(String texto, int numeroPiso) {
        JButton btn = EstilosUI.crearBoton(texto, new Color(45, 52, 72), new Color(60, 70, 95));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setMaximumSize(new Dimension(560, 65));
        btn.setPreferredSize(new Dimension(560, 65));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            if (controlador != null && numeroPiso <= pisoMaximoDesbloqueado) {
                controlador.entrarPiso(numeroPiso);
            }
        });
        return btn;
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(Juego juego) {
        if (juego == null) return;
        this.pisoMaximoDesbloqueado = juego.getPisoMaximoDesbloqueado();
        lblOro.setText("💰 Oro: $" + juego.getParty().getOro());

        // Actualizar notificación de puntos pendientes
        if (juego.getParty().tienePuntosAtributosPendientes()) {
            btnGestion.setText("Héroes y Equipo (Puntos pendientes!)");
            btnGestion.setBackground(new Color(230, 126, 34));
        } else {
            btnGestion.setText("Héroes y Equipo");
            btnGestion.setBackground(new Color(41, 128, 185));
        }

        configurarBotonPiso(btnPiso1, 1, "PISO 1: BASE DE LA TORRE (Entrada)", juego.getPiso());
        configurarBotonPiso(btnPiso2, 2, "PISO 2: PROFUNDIDADES PELIGROSAS", juego.getPiso());
        configurarBotonPiso(btnPiso3, 3, "PISO 3: CÚSPIDE DE LA TORRE (Jefe Final: Bowser)", juego.getPiso());
    }

    private void configurarBotonPiso(JButton btn, int piso, String titulo, int pisoActualJuego) {
        if (piso < pisoMaximoDesbloqueado) {
            btn.setText("✔ " + titulo + " [SUPERADO]");
            btn.setEnabled(true);
            btn.setBackground(new Color(39, 174, 96));
        } else if (piso == pisoMaximoDesbloqueado) {
            btn.setText("▶ " + titulo + " [DISPONIBLE]");
            btn.setEnabled(true);
            btn.setBackground(new Color(211, 84, 0));
        } else {
            btn.setText("🔒 " + titulo + " [BLOQUEADO]");
            btn.setEnabled(false);
            btn.setBackground(new Color(45, 50, 65));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoTorre != null) {
            g2.drawImage(fondoTorre, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 205)); // Overlay oscuro elegante
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
