package rpg.modelo;

import java.util.ArrayList;
import java.util.List;

public class Arquero extends Personaje {

    private int destreza;

    public Arquero(String nombre) {
        super(nombre, 160, 60, 1, 20, 10, 22);
        this.destreza = 25;
    }

    public Arquero(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int destreza) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.destreza = destreza;
    }

    public int getDestreza() { return destreza; }

    /** El Arquero tiene mayor probabilidad de crítico. */
    @Override
    protected double getCriticoBonus() { return 0.10; }

    @Override
    public List<Habilidad> getHabilidades() {
        int destrezaCaptura = this.destreza;
        List<Habilidad> habs = new ArrayList<>();

        // Habilidad 0: Disparo Certero (mono-objetivo, crítico garantizado x2)
        habs.add(new Habilidad("🎯 Disparo Certero [Crítico x2]", 15) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                if (objetivos == null || objetivos.isEmpty()) {
                    RegistroCombate.registrar(lanzador.getNombre() + " necesita un objetivo.");
                    return false;
                }
                Combatiente objetivo = objetivos.get(0);
                int danioCritico = ((lanzador.getAtaque() + destrezaCaptura) * 2) * lanzador.getNivel();
                RegistroCombate.registrar("🎯 ¡GOLPE CRÍTICO! " + lanzador.getNombre()
                        + " ejecuta un Disparo Certero letal contra " + objetivo.getNombre() + "!");
                objetivo.recibirDanio(danioCritico);
                return true;
            }
        });

        // Habilidad 1: Lluvia de Flechas (área: todos los enemigos vivos)
        habs.add(new Habilidad("🏹 Lluvia de Flechas [Área]", 25) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                int danio = (lanzador.getAtaque() + (destrezaCaptura / 2)) * lanzador.getNivel();
                RegistroCombate.registrar("🏹 ¡" + lanzador.getNombre()
                        + " dispara una Lluvia de Flechas sobre el campo enemigo!");
                // objetivos contiene todos los enemigos vivos
                for (Combatiente objetivo : objetivos) {
                    if (objetivo.estaVivo()) objetivo.recibirDanio(danio);
                }
                return true;
            }
        });

        return habs;
    }
}
