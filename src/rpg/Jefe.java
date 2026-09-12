package rpg;

import java.util.List;

public class Jefe extends Enemigo {
    private int contadorTurno = 0;

    public Jefe(String nombre) {
        super(nombre, 350, 40, 18, 14, 250);
    }

    public Jefe(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    @Override
    public void tomarDecision(Party party, List<Enemigo> aliados) {
        contadorTurno++;

        // Ataque en área cada 3 turnos
        if (contadorTurno % 3 == 0) {
            System.out.println("¡" + getNombre() + " desata un ¡Aliento de Dragón Devastador! dañando a toda la party!");
            for (Personaje p : party.getMiembros()) {
                if (p.estaVivo()) {
                    p.recibirDanio(getAtaque() - 10);
                }
            }
        } else if (getVida() < (getVidaMaxima() * 0.3) && contadorTurno % 2 == 0) {
            // Se defiende si tiene poca vida
            defender();
        } else {
            // Ataque directo al héroe más veloz o al primero
            Personaje objetivo = party.obtenerPrimerMiembroVivo();
            if (objetivo != null) {
                System.out.println(getNombre() + " golpea con su cola gigantesca.");
                atacar(objetivo);
            }
        }
    }
}
