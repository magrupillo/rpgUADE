package rpg.modelo;

import java.util.List;

public class Chaman extends Enemigo {

    private static final long serialVersionUID = 1L;

    public Chaman(String nombre) {
        super(nombre, 170, 26, 12, 15, 65, 35);
    }

    public Chaman() {
        this("Quan Chi");
    }

    public Chaman(String nombre, int vida, int ataque, int defensa, int velocidad, int xp, int oro) {
        super(nombre, vida, ataque, defensa, velocidad, xp, oro);
    }

    /**
     * IA de Quan Chi:
     * Cura a su ALIADO más herido (no a sí mismo) si tiene menos del 60% de HP.
     * Si ningún aliado necesita curación (o está solo), ataca con hechicería oscura.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        Enemigo aliadoMasHerido = null;
        double menorPorcentajeVida = 1.0;

        for (Enemigo e : aliados) {
            if (e != this && e.estaVivo()) {
                double pct = (double) e.getVida() / e.getVidaMaxima();
                if (pct < 0.60 && pct < menorPorcentajeVida) {
                    menorPorcentajeVida = pct;
                    aliadoMasHerido = e;
                }
            }
        }

        if (aliadoMasHerido != null) {
            int curacion = 45;
            RegistroCombate.registrar(getNombre() + " invoca magia nigromántica y cura a su aliado "
                    + aliadoMasHerido.getNombre() + " (+ " + curacion + " HP)!");
            aliadoMasHerido.curarVida(curacion);
        } else {
            Personaje objetivo = party.obtenerPrimerMiembroVivo();
            if (objetivo != null) {
                RegistroCombate.registrar(getNombre() + " conjura una calavera de fuego espectral contra "
                        + objetivo.getNombre() + "!");
                atacar(objetivo);
            }
        }
    }
}
