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

/**
 * Pantalla del Kiosco de Don Tito (Mercader).
 * Permite comprar suministros y equipamiento avanzado, y vender botín repetido o viejo por oro.
 */
public class PantallaKiosco extends JPanel {

    private ControladorJuego controlador;
    private final Image fondoKiosco;

    private final JLabel lblOro;
    private final JComboBox<String> comboCatalogoCompra;
    private final JComboBox<String> comboInventarioVenta;
    private final JLabel lblDetalleCompra;
    private final JLabel lblDetalleVenta;

    private final List<Item> catalogoItems = new ArrayList<>();
    private final List<Item> itemsInventarioVenta = new ArrayList<>();

    public PantallaKiosco() {
        this.fondoKiosco = GestorRecursos.obtenerFondo("otro");
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Inicializar catálogo fijo del kiosco
        catalogoItems.add(new PocionVida("Poción de Vida", "Restaura 50 HP", 50, 25, 12));
        catalogoItems.add(new PocionMana("Poción de Maná", "Restaura 40 MP", 40, 20, 10));
        catalogoItems.add(new PocionVida("Poción de Vida Mayor", "Restaura 80 HP", 80, 45, 22));
        catalogoItems.add(new PocionMana("Poción de Maná Mayor", "Restaura 60 MP", 60, 38, 18));
        catalogoItems.add(new Arma("Zarpado machete", "Hoja pesada de gran filo (+16 ATQ)", 16, 85, 40));
        catalogoItems.add(new Armadura("Chaleco kevlar", "Protección táctica antibalas (+14 DEF)", 14, 80, 38));

        // ── Barra Superior ────────────────────────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout(15, 0));
        pnlTop.setOpaque(false);

        JLabel lblTit = new JLabel("🏪 KIOSCO DE DON TITO (24 HORAS)");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTit.setForeground(EstilosUI.COLOR_ORO);
        pnlTop.add(lblTit, BorderLayout.WEST);

        lblOro = new JLabel("💰 Oro: $0");
        lblOro.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblOro.setForeground(EstilosUI.COLOR_ORO);
        pnlTop.add(lblOro, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // ── Contenedor Central: Comprar (Izquierda) y Vender (Derecha) ───────────
        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCentro.setOpaque(false);

        // --- Panel Comprar ---
        PanelVidrio pnlComprar = new PanelVidrio(18);
        pnlComprar.setLayout(new BoxLayout(pnlComprar, BoxLayout.Y_AXIS));
        pnlComprar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(39, 174, 96), 1, true),
                        "CATÁLOGO DE COMPRA", 0, 0, EstilosUI.FUENTE_BOLD, new Color(46, 204, 113)
                ),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        pnlComprar.add(new JLabel("Seleccionar artículo:"));
        comboCatalogoCompra = new JComboBox<>();
        comboCatalogoCompra.setFont(EstilosUI.FUENTE_BOLD);
        comboCatalogoCompra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        for (Item it : catalogoItems) {
            comboCatalogoCompra.addItem(it.getNombre() + " - $" + it.getPrecioCompra());
        }
        comboCatalogoCompra.addActionListener(e -> refrescarDetalleCompra());
        pnlComprar.add(comboCatalogoCompra);

        pnlComprar.add(Box.createVerticalStrut(15));

        lblDetalleCompra = new JLabel("<html>Cargando descripción...</html>");
        lblDetalleCompra.setFont(EstilosUI.FUENTE_NORMAL);
        lblDetalleCompra.setForeground(EstilosUI.TEXTO_CLARO);
        pnlComprar.add(lblDetalleCompra);

        pnlComprar.add(Box.createVerticalGlue());

        JButton btnComprar = EstilosUI.crearBoton("Comprar Artículo", new Color(39, 174, 96), new Color(46, 204, 113));
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComprar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnComprar.addActionListener(e -> {
            int idx = comboCatalogoCompra.getSelectedIndex();
            if (idx >= 0 && idx < catalogoItems.size() && controlador != null) {
                controlador.comprarItemKiosco(catalogoItems.get(idx));
            }
        });
        pnlComprar.add(btnComprar);
        pnlCentro.add(pnlComprar);

        // --- Panel Vender ---
        PanelVidrio pnlVender = new PanelVidrio(18);
        pnlVender.setLayout(new BoxLayout(pnlVender, BoxLayout.Y_AXIS));
        pnlVender.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(230, 126, 34), 1, true),
                        "VENDER ARTÍCULOS DE TU INVENTARIO", 0, 0, EstilosUI.FUENTE_BOLD, new Color(243, 156, 18)
                ),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        pnlVender.add(new JLabel("Seleccionar artículo a vender:"));
        comboInventarioVenta = new JComboBox<>();
        comboInventarioVenta.setFont(EstilosUI.FUENTE_BOLD);
        comboInventarioVenta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboInventarioVenta.addActionListener(e -> refrescarDetalleVenta());
        pnlVender.add(comboInventarioVenta);

        pnlVender.add(Box.createVerticalStrut(15));

        lblDetalleVenta = new JLabel("<html>No hay ítems para vender o inventario vacío.</html>");
        lblDetalleVenta.setFont(EstilosUI.FUENTE_NORMAL);
        lblDetalleVenta.setForeground(EstilosUI.TEXTO_CLARO);
        pnlVender.add(lblDetalleVenta);

        pnlVender.add(Box.createVerticalGlue());

        JButton btnVender = EstilosUI.crearBoton("Vender Artículo", new Color(211, 84, 0), new Color(230, 126, 34));
        btnVender.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVender.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnVender.addActionListener(e -> {
            int idx = comboInventarioVenta.getSelectedIndex();
            if (idx >= 0 && idx < itemsInventarioVenta.size() && controlador != null) {
                controlador.venderItemKiosco(itemsInventarioVenta.get(idx));
            }
        });
        pnlVender.add(btnVender);
        pnlCentro.add(pnlVender);

        add(pnlCentro, BorderLayout.CENTER);

        // ── Barra Inferior ────────────────────────────────────────────────────────
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlBotones.setOpaque(false);

        JButton btnVolver = EstilosUI.crearBoton("Volver al Mapa", new Color(41, 128, 185), new Color(52, 152, 219));
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(220, 42));
        btnVolver.addActionListener(e -> {
            if (controlador != null) {
                controlador.completarKioscoYVolver();
            }
        });
        pnlBotones.add(btnVolver);
        add(pnlBotones, BorderLayout.SOUTH);

        refrescarDetalleCompra();
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void actualizar(Party party) {
        if (party == null) return;
        lblOro.setText("💰 Oro: $" + party.getOro());

        // Actualizar combo de venta
        itemsInventarioVenta.clear();
        comboInventarioVenta.removeAllItems();

        List<Item> items = party.getInventario().getItems();
        for (Item it : items) {
            itemsInventarioVenta.add(it);
            comboInventarioVenta.addItem(it.getNombre() + " (Venta: $" + it.getPrecioVenta() + ")");
        }

        refrescarDetalleVenta();
    }

    private void refrescarDetalleCompra() {
        int idx = comboCatalogoCompra.getSelectedIndex();
        if (idx >= 0 && idx < catalogoItems.size()) {
            Item it = catalogoItems.get(idx);
            lblDetalleCompra.setText("<html><b>" + it.getNombre() + "</b><br>"
                    + it.getDescripcion() + "<br><br>"
                    + "<font color='#f1c40f'>Precio de Compra: $" + it.getPrecioCompra() + "</font></html>");
        }
    }

    private void refrescarDetalleVenta() {
        int idx = comboInventarioVenta.getSelectedIndex();
        if (idx >= 0 && idx < itemsInventarioVenta.size()) {
            Item it = itemsInventarioVenta.get(idx);
            lblDetalleVenta.setText("<html><b>" + it.getNombre() + "</b><br>"
                    + it.getDescripcion() + "<br><br>"
                    + "<font color='#2ecc71'>Precio de Venta: $" + it.getPrecioVenta() + "</font></html>");
        } else {
            lblDetalleVenta.setText("<html>El inventario no tiene ítems disponibles para venta.</html>");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (fondoKiosco != null) {
            g2.drawImage(fondoKiosco, 0, 0, getWidth(), getHeight(), this);
            g2.setColor(new Color(15, 18, 25, 210));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(EstilosUI.FONDO_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        EstilosUI.pintarVineta(g2, getWidth(), getHeight(), 190);
        g2.dispose();
    }
}
