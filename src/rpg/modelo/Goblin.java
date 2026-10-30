package rpg.modelo;

import java.util.List;

public class Goblin extends Enemigo {

    public Goblin(String nombre) {
        super(nombre, 90, 20, 5, 18, 40);
    }

    public Goblin(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    /** Ignora la amenaza; siempre ataca al personaje con menor vida actual. */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        Personaje objetivoDebil = null;
        for (Personaje p : party.getMiembros()) {
            if (p.estaVivo()) {
                if (objetivoDebil == null || p.getVida() < objetivoDebil.getVida()) {
                    objetivoDebil = p;
                }
            }
        }
        if (objetivoDebil != null) {
            RegistroCombate.registrar("🗡️ " + getNombre()
                    + " se escurre en las sombras e ignora la amenaza para apuñalar al más herido: "
                    + objetivoDebil.getNombre() + "!");
            atacar(objetivoDebil);
        }
    }
}
