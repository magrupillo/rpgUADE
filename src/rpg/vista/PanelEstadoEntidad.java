package rpg.vista;

import rpg.dto.EntidadDTO;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.PersonajeDTO;
import rpg.util.EstilosUI;
import rpg.util.GestorRecursos;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de estado que muestra las barras de HP/MP de cada participante con mini sprites.
 * Destaca visualmente la entidad cuyo turno está activo en la cola de iniciativa.
 */
public class PanelEstadoEntidad extends JPanel {
    private final JPanel contenedorHeroes;
    private final JPanel contenedorEnemigos;

    public PanelEstadoEntidad() {
        setLayout(new GridLayout(1, 2, 8, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(0, 120));

        contenedorHeroes = crearContenedor("⚔️  PARTY", EstilosUI.COLOR_MANA);
        contenedorEnemigos = crearContenedor("💀  ENEMIGOS", EstilosUI.COLOR_ROJO);
        add(contenedorHeroes);
        add(contenedorEnemigos);
    }

    private JPanel crearContenedor(String titulo, Color colorTitulo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(EstilosUI.FONDO_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1, true),
                        titulo, 0, 0, new Font("Segoe UI", Font.BOLD, 11), colorTitulo),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        return p;
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        int idxHeroeActivo = estadoDTO.getIndiceHeroeActivo();
        boolean batallaTerminada = estadoDTO.isBatallaTerminada();

        // Héroes
        contenedorHeroes.removeAll();
        List<PersonajeDTO> heroes = estadoDTO.getHeroes();
        for (int i = 0; i < heroes.size(); i++) {
            boolean esTurno = !batallaTerminada && i == idxHeroeActivo;
            contenedorHeroes.add(crearFilaHeroe(heroes.get(i), esTurno));
            if (i < heroes.size() - 1) contenedorHeroes.add(Box.createVerticalStrut(2));
        }

        // Enemigos
        contenedorEnemigos.removeAll();
        List<EntidadDTO> enemigos = estadoDTO.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            contenedorEnemigos.add(crearFilaEnemigo(enemigos.get(i)));
            if (i < enemigos.size() - 1) contenedorEnemigos.add(Box.createVerticalStrut(2));
        }

        contenedorHeroes.revalidate(); contenedorHeroes.repaint();
        contenedorEnemigos.revalidate(); contenedorEnemigos.repaint();
    }

    private JPanel crearFilaHeroe(PersonajeDTO h, boolean esTurnoActivo) {
        JPanel fila = new JPanel(new BorderLayout(6, 1));
        fila.setOpaque(esTurnoActivo);
        if (esTurnoActivo) {
            fila.setBackground(new Color(30, 50, 80, 160));
        }
        fila.setBorder(esTurnoActivo
                ? BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(EstilosUI.COLOR_ORO, 1, true),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4))
                : BorderFactory.createEmptyBorder(2, 4, 2, 4));

        // Mini sprite icono
        JLabel lblIcono = new JLabel(new ImageIcon(GestorRecursos.obtenerSprite(h.getTipo(), 24, 24)));
        fila.add(lblIcono, BorderLayout.WEST);

        // Panel central: nombre + barras
        JPanel centro = new JPanel(new BorderLayout(4, 0));
        centro.setOpaque(false);

        // Encabezado
        String tituloTxt = (esTurnoActivo ? "▶ " : "") + h.getNombre();
        if (h.isDefendiendo()) tituloTxt += " 🛡";
        if (!h.isEstaVivo()) tituloTxt = "☠ " + h.getNombre();

        JLabel lblNom = new JLabel(tituloTxt);
        lblNom.setFont(new Font("Segoe UI", esTurnoActivo ? Font.BOLD : Font.PLAIN, 11));
        lblNom.setForeground(h.isEstaVivo()
                ? (esTurnoActivo ? EstilosUI.COLOR_ORO : EstilosUI.TEXTO_CLARO)
                : EstilosUI.TEXTO_MUTED);
        centro.add(lblNom, BorderLayout.NORTH);

        // Barras HP + MP
        JPanel barras = new JPanel(new GridLayout(2, 1, 0, 1));
        barras.setOpaque(false);

        JProgressBar barraHp = EstilosUI.crearBarraVida(h.getVidaActual(), Math.max(1, h.getVidaMaxima()));
        barraHp.setPreferredSize(new Dimension(0, 12));
        if (!h.isEstaVivo()) {
            barraHp.setValue(0);
            barraHp.setString("DERROTADO");
        }
        barras.add(barraHp);

        JProgressBar barraMp = EstilosUI.crearBarra(0, Math.max(1, h.getManaMaximo()), EstilosUI.COLOR_MANA);
        barraMp.setValue(h.getManaActual());
        barraMp.setString("MP " + h.getManaActual() + "/" + h.getManaMaximo());
        barraMp.setPreferredSize(new Dimension(0, 12));
        barras.add(barraMp);
        centro.add(barras, BorderLayout.CENTER);

        fila.add(centro, BorderLayout.CENTER);

        // Velocidad
        JLabel lblVel = new JLabel("V" + h.getVelocidad());
        lblVel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblVel.setForeground(esTurnoActivo ? EstilosUI.COLOR_ORO : new Color(100, 120, 160));
        fila.add(lblVel, BorderLayout.EAST);

        return fila;
    }

    private JPanel crearFilaEnemigo(EntidadDTO e) {
        JPanel fila = new JPanel(new BorderLayout(6, 1));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        // Mini sprite icono
        JLabel lblIcono = new JLabel(new ImageIcon(GestorRecursos.obtenerSprite(e.getTipo(), 24, 24)));
        fila.add(lblIcono, BorderLayout.WEST);

        // Panel central
        JPanel centro = new JPanel(new BorderLayout(4, 0));
        centro.setOpaque(false);

        String tituloTxt = e.isEstaVivo() ? e.getNombre() : "☠ " + e.getNombre();
        JLabel lblNom = new JLabel(tituloTxt);
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNom.setForeground(e.isEstaVivo() ? EstilosUI.TEXTO_CLARO : EstilosUI.TEXTO_MUTED);
        centro.add(lblNom, BorderLayout.NORTH);

        JProgressBar barraHp = EstilosUI.crearBarraVida(e.getVidaActual(), Math.max(1, e.getVidaMaxima()));
        barraHp.setPreferredSize(new Dimension(0, 12));
        if (!e.isEstaVivo()) {
            barraHp.setValue(0);
            barraHp.setString("DERROTADO");
        }
        centro.add(barraHp, BorderLayout.CENTER);

        fila.add(centro, BorderLayout.CENTER);

        // Stats
        JLabel lblStats = new JLabel("A" + e.getAtaque() + " D" + e.getDefensa() + " V" + e.getVelocidad());
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblStats.setForeground(new Color(180, 80, 80));
        fila.add(lblStats, BorderLayout.EAST);

        return fila;
    }
}
