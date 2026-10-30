package rpg.dto;

/**
 * DTO plano e inmutable que transporta los datos visuales de cualquier entidad (héroe o enemigo)
 * hacia la Vista sin exponer el Modelo.
 */
public class EntidadDTO {
    private final String nombre;
    private final String tipo;
    private final int vidaActual;
    private final int vidaMaxima;
    private final int manaActual;
    private final int manaMaximo;
    private final int nivel;
    private final int ataque;
    private final int defensa;
    private final int velocidad;
    private final boolean estaVivo;
    private final boolean defendiendo;
    private final String rutaSprite;
    private final boolean esHeroe;

    public EntidadDTO(String nombre, String tipo, int vidaActual, int vidaMaxima,
                      int manaActual, int manaMaximo, int nivel, int ataque,
                      int defensa, int velocidad, boolean estaVivo, boolean defendiendo,
                      String rutaSprite, boolean esHeroe) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.manaActual = manaActual;
        this.manaMaximo = manaMaximo;
        this.nivel = nivel;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.estaVivo = estaVivo;
        this.defendiendo = defendiendo;
        this.rutaSprite = rutaSprite;
        this.esHeroe = esHeroe;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getManaActual() {
        return manaActual;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    public int getNivel() {
        return nivel;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public boolean isEstaVivo() {
        return estaVivo;
    }

    public boolean isDefendiendo() {
        return defendiendo;
    }

    public String getRutaSprite() {
        return rutaSprite;
    }

    public boolean isEsHeroe() {
        return esHeroe;
    }

    @Override
    public String toString() {
        return nombre + " (HP: " + vidaActual + "/" + vidaMaxima + (manaMaximo > 0 ? " | MP: " + manaActual + "/" + manaMaximo : "") + ")";
    }
}
