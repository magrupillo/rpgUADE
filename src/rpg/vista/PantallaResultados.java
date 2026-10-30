package rpg.vista;

import rpg.controlador.ControladorJuego;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.PersonajeDTO;
import rpg.dto.RecompensaDTO;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PantallaResultados extends JPanel {
    private ControladorJuego controlador;
    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;
    private final JPanel panelDetalles;
    private final JButton btnContinuar;
    private boolean ultimaVictoria = true;

    public PantallaResultados() {
        setLayout(new BorderLayout(15, 15));
        setBackground(EstilosUI.FONDO_OSCURO);
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Header
        JPanel pnlHead = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHead.setOpaque(false);
        lblTitulo = new JLabel("🏆 ¡VICTORIA GLORIOSA!", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(EstilosUI.COLOR_ORO);

        lblSubtitulo = new JLabel("Los enemigos han sido derrotados y la party obtiene sus recompensas.", SwingConstants.CENTER);
        lblSubtitulo.setFont(EstilosUI.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(EstilosUI.TEXTO_MUTED);

        pnlHead.add(lblTitulo);
        pnlHead.add(lblSubtitulo);
        add(pnlHead, BorderLayout.NORTH);

        // Centro: Tarjetas de Estado de los Héroes
        panelDetalles = new JPanel(new GridLayout(1, 4, 12, 0));
        panelDetalles.setOpaque(false);
        add(panelDetalles, BorderLayout.CENTER);

        // Botón Continuar
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBotones.setOpaque(false);
        btnContinuar = EstilosUI.crearBoton("🗺️ CONTINUAR EN EL MAPA DE LA TORRE", new Color(39, 174, 96), new Color(46, 204, 113));
        btnContinuar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnContinuar.setPreferredSize(new Dimension(360, 45));
        btnContinuar.addActionListener(e -> {
            if (controlador != null) {
                if (ultimaVictoria) {
                    controlador.volverAlMapa();
                } else {
                    controlador.mostrarPantallaInicio();
                }
            }
        });
        pnlBotones.add(btnContinuar);
        add(pnlBotones, BorderLayout.SOUTH);
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    /**
     * Actualiza la pantalla de resultados con el estado del combate y la recompensa obtenida.
     * El botón bloqueante devuelve al jugador al mapa (victoria) o al inicio (derrota / permadeath).
     *
     * @param estadoDTO  estado final de la batalla
     * @param recompensa datos del RecompensaDTO (puede ser null en caso de derrota)
     */
    public void actualizar(EstadoBatallaDTO estadoDTO, RecompensaDTO recompensa) {
        this.ultimaVictoria = estadoDTO.isVictoria();
        if (estadoDTO.isVictoria()) {
            lblTitulo.setText("🏆 ¡VICTORIA EN " + estadoDTO.getLugar().toUpperCase() + "!");
            lblTitulo.setForeground(EstilosUI.COLOR_ORO);
            String subMsg = "Experiencia repartida entre los supervivientes.";
            if (recompensa != null) {
                subMsg = "XP Total: " + recompensa.getExperienciaTotalOtorgada()
                        + "  |  XP por héroe: " + recompensa.getExperienciaPorHeroe()
                        + "  |  Oro: " + recompensa.getOro();
                if (!recompensa.getItemsObtenidos().isEmpty()) {
                    subMsg += "  |  Ítems: " + String.join(", ", recompensa.getItemsObtenidos());
                }
            }
            lblSubtitulo.setText(subMsg);
            btnContinuar.setText("🗺️ CONTINUAR EN EL MAPA DE LA TORRE");
        } else {
            lblTitulo.setText("☠️ ¡DERROTA - PERMADEATH!");
            lblTitulo.setForeground(EstilosUI.COLOR_ROJO);
            lblSubtitulo.setText("Toda la party ha caído en combate. La run actual ha concluido.");
            btnContinuar.setText("🏠 VOLVER AL MENÚ PRINCIPAL");
        }

        panelDetalles.removeAll();
        List<PersonajeDTO> heroes = estadoDTO.getHeroes();
        for (PersonajeDTO h : heroes) {
            JPanel card = new JPanel(new BorderLayout(8, 8));
            card.setBackground(EstilosUI.FONDO_PANEL);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(h.isEstaVivo() ? EstilosUI.BORDE_SUAVE : EstilosUI.COLOR_ROJO, 1, true),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            JLabel lblNom = new JLabel(h.getNombre(), SwingConstants.CENTER);
            lblNom.setFont(EstilosUI.FUENTE_BOLD);
            lblNom.setForeground(h.isEstaVivo() ? EstilosUI.TEXTO_CLARO : EstilosUI.TEXTO_MUTED);
            card.add(lblNom, BorderLayout.NORTH);

            JPanel pnlC = new JPanel(new GridLayout(4, 1, 0, 4));
            pnlC.setOpaque(false);
            pnlC.add(new JLabel("Nivel: " + h.getNivel(), SwingConstants.CENTER));
            pnlC.add(new JLabel("HP: " + h.getVidaActual() + " / " + h.getVidaMaxima(), SwingConstants.CENTER));
            pnlC.add(new JLabel("XP: " + h.getExperiencia() + " / " + h.getExperienciaSiguienteNivel(), SwingConstants.CENTER));

            if (h.getPuntosAtributoDisponibles() > 0) {
                JLabel lblPuntos = new JLabel("⭐ +" + h.getPuntosAtributoDisponibles() + " Pts para asignar!", SwingConstants.CENTER);
                lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblPuntos.setForeground(EstilosUI.COLOR_ORO);
                pnlC.add(lblPuntos);
            } else {
                pnlC.add(new JLabel(h.isEstaVivo() ? "Estado: Listo" : "Estado: Caído", SwingConstants.CENTER));
            }

            card.add(pnlC, BorderLayout.CENTER);
            panelDetalles.add(card);
        }

        panelDetalles.revalidate();
        panelDetalles.repaint();
    }
}
