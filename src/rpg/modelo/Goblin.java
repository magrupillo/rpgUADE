package rpg.modelo;

import java.util.List;

public class Goblin extends Enemigo {

    private static final long serialVersionUID = 1L;

    public Goblin(String nombre) {
        super(nombre, 150, 24, 10, 18, 45, 10);
    }

    public Goblin() {
        this("El Duende Verde");
    }

    public Goblin(String nombre, int vida, int ataque, int defensa, int velocidad, int xp, int oro) {
        super(nombre, vida, ataque, defensa, velocidad, xp, oro);
    }

    /**
     * IA del Duende Verde: Busca y ataca con saña al héroe más herido de la party.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        Personaje objetivo = party.obtenerMiembroMasHerido();
        if (objetivo == null) {
            objetivo = party.obtenerPrimerMiembroVivo();
        }

        if (objetivo != null) {
            RegistroCombate.registrar(getNombre() + " se abalanza con risa macabra sobre el héroe más herido ("
                    + objetivo.getNombre() + ")!");
            atacar(objetivo);
        }
    }
}
