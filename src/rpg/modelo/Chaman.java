package rpg.modelo;

import java.util.List;

public class Chaman extends Enemigo {

    public Chaman(String nombre) {
        super(nombre, 130, 22, 6, 15, 80);
    }

    public Chaman(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    /** Cura al aliado más herido si está por debajo del 60% de vida; si no, ataca. */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        Enemigo aliadoHerido = null;
        for (Enemigo e : aliados) {
            if (e.estaVivo() && e.getVida() < (e.getVidaMaxima() * 0.6)) {
                aliadoHerido = e;
                break;
            }
        }
        if (aliadoHerido != null) {
            RegistroCombate.registrar("🔮 " + getNombre()
                    + " invoca un maleficio curativo sobre su aliado " + aliadoHerido.getNombre() + ".");
            aliadoHerido.curarVida(35);
        } else {
            Personaje objetivo = party.obtenerPrimerMiembroVivo();
            if (objetivo != null) {
                RegistroCombate.registrar("⚡ " + getNombre()
                        + " lanza un rayo de energía oscura contra " + objetivo.getNombre() + "!");
                atacar(objetivo);
            }
        }
    }
}
