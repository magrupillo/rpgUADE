package rpg.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Esqueleto extends Enemigo {

    private static final long serialVersionUID = 1L;

    private int contadorTurno = 0;
    private final Random random = new Random();

    public Esqueleto(String nombre) {
        super(nombre, 190, 28, 13, 13, 60, 14);
    }

    public Esqueleto() {
        this("Skeletor");
    }

    public Esqueleto(String nombre, int vida, int ataque, int defensa, int velocidad, int xp, int oro) {
        super(nombre, vida, ataque, defensa, velocidad, xp, oro);
    }

    /**
     * IA de Skeletor:
     * Cada dos turnos usa el Bastón del Caos para ATURDIR a un héroe al azar (pierde su próximo turno).
     * En los turnos restantes, ataca al de mayor amenaza o al primer héroe vivo.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        contadorTurno++;

        if (contadorTurno % 2 == 0) {
            List<Personaje> vivos = new ArrayList<>();
            for (Personaje p : party.getMiembros()) {
                if (p.estaVivo() && !p.isAturdido()) vivos.add(p);
            }
            if (!vivos.isEmpty()) {
                Personaje victima = vivos.get(random.nextInt(vivos.size()));
                victima.setAturdido(true);
                RegistroCombate.registrar(getNombre() + " levanta el Bastón del Caos y ATURDE a "
                        + victima.getNombre() + "! (Perderá su siguiente turno).");
                return;
            }
        }

        Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
        if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
        if (objetivo != null) {
            RegistroCombate.registrar(getNombre() + " ataca con su espada mística a " + objetivo.getNombre() + ".");
            atacar(objetivo);
        }
    }
}
