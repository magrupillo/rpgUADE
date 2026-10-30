package rpg.modelo;

import java.util.ArrayList;
import java.util.List;

public class Mago extends Personaje {

    private int inteligencia;

    public Mago(String nombre) {
        super(nombre, 120, 120, 1, 15, 6, 16);
        this.inteligencia = 30;
    }

    public Mago(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int inteligencia) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.inteligencia = inteligencia;
    }

    public int getInteligencia() { return inteligencia; }

    @Override
    public List<Habilidad> getHabilidades() {
        int intelCaptura = this.inteligencia;
        List<Habilidad> habs = new ArrayList<>();

        // Habilidad 0: Bola de Fuego (mono-objetivo)
        habs.add(new Habilidad("🔥 Bola de Fuego", 25) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                if (objetivos == null || objetivos.isEmpty()) {
                    RegistroCombate.registrar(lanzador.getNombre() + " necesita un objetivo.");
                    return false;
                }
                Combatiente objetivo = objetivos.get(0);
                int danio = (lanzador.getAtaque() + intelCaptura) * lanzador.getNivel();
                RegistroCombate.registrar("🔥 ¡" + lanzador.getNombre()
                        + " lanza una abrasadora Bola de Fuego contra " + objetivo.getNombre() + "!");
                objetivo.recibirDanio(danio);
                return true;
            }
        });

        // Habilidad 1: Tormenta Arcana (área: todos los enemigos vivos)
        habs.add(new Habilidad("⚡ Tormenta Arcana [Área]", 35) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                int danio = (lanzador.getAtaque() + (intelCaptura / 2)) * lanzador.getNivel();
                RegistroCombate.registrar("⚡ ¡" + lanzador.getNombre()
                        + " desata una Tormenta Arcana sobre TODOS los enemigos!");
                for (Combatiente objetivo : objetivos) {
                    if (objetivo.estaVivo()) objetivo.recibirDanio(danio);
                }
                return true;
            }
        });

        return habs;
    }
}
