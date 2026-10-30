package rpg.dto;

import java.io.Serializable;

/**
 * DTO para transferir información descriptiva de habilidades de combate
 * a la vista (nombre, coste de maná, descripción táctica y tipo de objetivo).
 */
public class HabilidadDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int costeMana;
    private final String descripcion;
    private final String tipoObjetivoSimple;

    public HabilidadDTO(String nombre, int costeMana, String descripcion, String tipoObjetivoSimple) {
        this.nombre = nombre;
        this.costeMana = costeMana;
        this.descripcion = descripcion;
        this.tipoObjetivoSimple = tipoObjetivoSimple;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCosteMana() {
        return costeMana;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipoObjetivoSimple() {
        return tipoObjetivoSimple;
    }

    public String getTooltipFormateado(boolean tieneMana) {
        String avisoMana = tieneMana ? "" : "<div style='color: #e74c3c; font-weight: bold; margin-bottom: 3px;'>[Sin maná suficiente para ejecutar]</div>";
        return "<html><body style='width: 240px; padding: 3px; font-family: Segoe UI; font-size: 11px;'>"
                + "<div style='font-size: 12px; font-weight: bold; color: #f1c40f; margin-bottom: 2px;'>"
                + nombre + " <span style='color: #85c1e9;'>(" + costeMana + " MP)</span></div>"
                + avisoMana
                + "<div style='color: #e67e22; margin-bottom: 3px;'><b>Objetivo:</b> " + tipoObjetivoSimple + "</div>"
                + "<div style='color: #ecf0f1; line-height: 1.3;'>" + descripcion + "</div>"
                + "</body></html>";
    }

    public String getTooltipFormateado() {
        return getTooltipFormateado(true);
    }

    @Override
    public String toString() {
        return nombre + " (" + costeMana + " MP)";
    }
}
