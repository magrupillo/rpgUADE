package rpg.modelo;

import java.util.List;
import java.util.Random;

/**
 * Clase abstracta que representa a un enemigo en la batalla.
 * Implementa {@link Combatiente} para participar en el ciclo de turnos de {@link Batalla}.
 *
 * Su inteligencia artificial se ejecuta invocando {@link #ejecutarTurno(Party, List)},
 * donde cada subclase evalúa el estado de la batalla y toma decisiones automáticamente.
 */
public abstract class Enemigo implements Combatiente {

    private String nombre;
    private int vida;
    private int vidaMaxima;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int experienciaOtorgada;
    private boolean defendiendo;

    private static final double PROB_CRITICO_BASE = 0.05;
    private static final Random RND = new Random();

    public Enemigo(String nombre, int vida, int ataque, int defensa,
                   int velocidad, int experienciaOtorgada) {
        this.nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.experienciaOtorgada = experienciaOtorgada;
        this.defendiendo = false;
    }

    // ─── Combatiente ─────────────────────────────────────────────────────────────

    @Override public String getNombre()    { return nombre; }
    @Override public int getVida()         { return vida; }
    @Override public int getVidaMaxima()   { return vidaMaxima; }
    @Override public int getAtaque()       { return ataque; }
    @Override public int getVelocidad()    { return velocidad; }
    @Override public boolean isDefendiendo(){ return defendiendo; }

    @Override
    public int getDefensa() {
        return defendiendo ? (defensa * 2) : defensa;
    }

    @Override
    public boolean estaVivo() { return vida > 0; }

    @Override
    public void defender() {
        this.defendiendo = true;
        RegistroCombate.registrar("🛡️ " + nombre + " adopta una postura defensiva.");
    }

    @Override
    public void resetearDefensaTemporal() { this.defendiendo = false; }

    /**
     * Fórmula unificada de daño recibido: max(1, danio_bruto - defensa_efectiva).
     */
    @Override
    public void recibirDanio(int danio) {
        int danioFinal = Math.max(1, danio - getDefensa());
        this.vida -= danioFinal;
        if (this.vida < 0) this.vida = 0;
        RegistroCombate.registrar("💥 " + nombre + " recibe " + danioFinal
                + " de daño (DEF: " + getDefensa() + "). Vida: " + vida + "/" + vidaMaxima);
        if (!estaVivo()) {
            RegistroCombate.registrar("☠ ¡" + nombre + " ha sido derrotado!");
        }
    }

    // ─── Extras ──────────────────────────────────────────────────────────────────

    public int getExperienciaOtorgada() { return experienciaOtorgada; }

    public String getTipo() { return getClass().getSimpleName(); }

    public String getRutaSprite() { return "sprites/" + getTipo().toLowerCase() + ".png"; }

    public void curarVida(int cantidad) {
        this.vida = Math.min(vida + cantidad, vidaMaxima);
        RegistroCombate.registrar("💚 " + nombre + " recuperó " + cantidad
                + " de vida (" + vida + "/" + vidaMaxima + ").");
    }

    /**
     * Ataque básico del enemigo con fórmula unificada (incluye probabilidad de crítico).
     */
    public void atacar(Combatiente objetivo) {
        boolean critico = RND.nextDouble() < PROB_CRITICO_BASE;
        int danio = critico ? ataque * 2 : ataque;
        if (critico) {
            RegistroCombate.registrar("⚡ ¡CRÍTICO! " + nombre + " golpea ferozmente a "
                    + objetivo.getNombre() + " por " + danio + " de daño.");
        } else {
            RegistroCombate.registrar("⚔️ " + nombre + " ataca a "
                    + objetivo.getNombre() + " por " + danio + " de daño.");
        }
        objetivo.recibirDanio(danio);
    }

    /**
     * Inteligencia artificial del enemigo.
     * Evalúa el estado de la batalla y decide qué acción realizar sin input del jugador.
     *
     * @param party   la party del jugador
     * @param aliados lista de todos los enemigos (incluyendo a sí mismo)
     */
    public abstract void ejecutarTurno(Party party, List<Enemigo> aliados);

    @Override
    public String toString() {
        return nombre + " [HP:" + vida + "/" + vidaMaxima
                + " | ATQ:" + ataque + " | DEF:" + defensa + " | Vel:" + velocidad + "]";
    }
}
