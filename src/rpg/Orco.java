package rpg;

import java.util.List;

public class Orco extends Enemigo {

    public Orco(String nombre) {
        super(nombre, 180, 32, 12, 10, 75);
    }

    public Orco(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    @Override
    public void tomarDecision(Party party, List<Enemigo> aliados) {
        // Busca al héroe con menor vida actual
        Personaje objetivoDebil = null;
        for (Personaje p : party.getMiembros()) {
            if (p.estaVivo()) {
                if (objetivoDebil == null || p.getVida() < objetivoDebil.getVida()) {
                    objetivoDebil = p;
                }
            }
        }

        if (objetivoDebil != null) {
            System.out.println(getNombre() + " ruge furioso y embiste al héroe más débil (" + objetivoDebil.getNombre() + ")!");
            atacar(objetivoDebil);
        }
    }
}
