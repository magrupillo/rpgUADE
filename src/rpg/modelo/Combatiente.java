package rpg.modelo;

import java.io.Serializable;

/**
 * Interfaz que establece el contrato común para todos los participantes del combate.
 * Implementada tanto por {@link Personaje} como por {@link Enemigo}.
 * Permite el polimorfismo en la cola de iniciativa y el cálculo de daño.
 */
public interface Combatiente extends Serializable {

    String getNombre();

    int getVida();

    int getVidaMaxima();

    int getAtaque();

    int getDefensa();

    int getVelocidad();

    boolean estaVivo();

    boolean isDefendiendo();

    void defender();

    void resetearDefensaTemporal();

    /**
     * Recibe daño aplicando la fórmula unificada interna.
     * @param danio daño bruto antes de restar defensa
     */
    void recibirDanio(int danio);
}
