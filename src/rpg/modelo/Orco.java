package rpg.modelo;

import java.util.List;

public class Orco extends Enemigo {

    public Orco(String nombre) {
        super(nombre, 180, 32, 12, 10, 75);
    }

    public Orco(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    /** Ataca al héroe con mayor nivel de amenaza (Provocar); si no, al primero disponible. */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
        if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
        if (objetivo != null) {
            RegistroCombate.registrar("🪓 " + getNombre()
                    + " ruge con furia salvaje y ataca a " + objetivo.getNombre() + "!");
            atacar(objetivo);
        }
    }
}
