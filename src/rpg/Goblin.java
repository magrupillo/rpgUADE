package rpg;

import java.util.List;

public class Goblin extends Enemigo {

    public Goblin(String nombre) {
        super(nombre, 90, 20, 5, 18, 40);
    }

    public Goblin(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    @Override
    public void tomarDecision(Party party, List<Enemigo> aliados) {
        Personaje objetivo = party.obtenerPrimerMiembroVivo();
        if (objetivo != null) {
            System.out.println(getNombre() + " realiza una emboscada rápida.");
            atacar(objetivo);
        }
    }
}
