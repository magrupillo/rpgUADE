package rpg.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Esqueleto extends Enemigo {

    private int contadorTurno = 0;
    private final Random random = new Random();

    public Esqueleto(String nombre) {
        super(nombre, 140, 24, 8, 11, 60);
    }

    public Esqueleto(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    /** Cada tres turnos aturde a un personaje al azar; en otros turnos ataca al de mayor amenaza. */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        contadorTurno++;

        if (contadorTurno % 3 == 0) {
            List<Personaje> vivos = new ArrayList<>();
            for (Personaje p : party.getMiembros()) {
                if (p.estaVivo()) vivos.add(p);
            }
            if (!vivos.isEmpty()) {
                Personaje victima = vivos.get(random.nextInt(vivos.size()));
                victima.setAturdido(true);
                RegistroCombate.registrar("💀 " + getNombre()
                        + " hace crujir sus huesos malditos y ¡ATURDE a "
                        + victima.getNombre() + "! (Perderá su siguiente turno).");
                return;
            }
        }

        Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
        if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
        if (objetivo != null) {
            RegistroCombate.registrar("💀 " + getNombre()
                    + " ataca con su espada quebrada a " + objetivo.getNombre() + ".");
            atacar(objetivo);
        }
    }
}
