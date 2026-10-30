package rpg.vista;

import rpg.dto.EstadoBatallaDTO;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de Swing con un JTextArea dentro de un JScrollPane para mostrar
 * el registro/historial de combate en tiempo real.
 */
public class PanelRegistro extends JPanel {
    private final JTextArea areaLog;
    private final JScrollPane scrollPane;
    private int ultimoTamanioLog = 0;

    public PanelRegistro() {
        setLayout(new BorderLayout(0, 6));
        setBackground(EstilosUI.FONDO_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1),
                        "📜 REGISTRO DE COMBATE",
                        0, 0, EstilosUI.FUENTE_BOLD, EstilosUI.COLOR_ORO
                ),
                BorderFactory.createEmptyBorder(6, 8, 8, 8)
        ));

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(EstilosUI.FUENTE_LOG);
        areaLog.setBackground(EstilosUI.FONDO_LOG);
        areaLog.setForeground(new Color(225, 230, 240));
        areaLog.setCaretColor(EstilosUI.COLOR_ORO);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setMargin(new Insets(6, 8, 6, 8));

        scrollPane = new JScrollPane(areaLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE_SUAVE, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(300, 160));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void actualizar(EstadoBatallaDTO estadoDTO) {
        List<String> logs = estadoDTO.getRegistroDeCombate();
        if (logs.size() != ultimoTamanioLog) {
            StringBuilder sb = new StringBuilder();
            for (String linea : logs) {
                sb.append(linea).append("\n");
            }
            areaLog.setText(sb.toString());
            ultimoTamanioLog = logs.size();

            // Auto-scroll al final del registro
            SwingUtilities.invokeLater(() -> {
                areaLog.setCaretPosition(areaLog.getDocument().getLength());
            });
        }
    }
}
