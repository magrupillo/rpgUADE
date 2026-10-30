package rpg.modelo;

import java.io.Serializable;

/**
 * Representa una opción seleccionable dentro de un evento narrativo.
 */
public class OpcionEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String texto;
    private final String consecuencia;

    public OpcionEvento(String texto, String consecuencia) {
        this.texto = texto;
        this.consecuencia = consecuencia;
    }

    public String getTexto() {
        return texto;
    }

    public String getConsecuencia() {
        return consecuencia;
    }

    @Override
    public String toString() {
        return texto;
    }
}
