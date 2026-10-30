package rpg.dto;

public class EventoDTO {
    private final String titulo;
    private final String descripcion;
    private final String opcionA;
    private final String opcionB;

    public EventoDTO(String titulo, String descripcion, String opcionA, String opcionB) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.opcionA = opcionA;
        this.opcionB = opcionB;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getOpcionA() {
        return opcionA;
    }

    public String getOpcionB() {
        return opcionB;
    }
}
