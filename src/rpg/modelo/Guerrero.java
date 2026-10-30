package rpg.modelo;

import java.util.ArrayList;
import java.util.List;

public class Guerrero extends Personaje {

    private int fuerza;

    public Guerrero(String nombre) {
        super(nombre, 220, 50, 1, 25, 15, 12);
        this.fuerza = 20;
    }

    public Guerrero(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int fuerza) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.fuerza = fuerza;
    }

    public int getFuerza() { return fuerza; }

    /** El Guerrero tiene probabilidad de crítico ligeramente mayor. */
    @Override
    protected double getCriticoBonus() { return 0.05; }

    @Override
    public List<Habilidad> getHabilidades() {
        int fuerzaCaptura = this.fuerza;
        List<Habilidad> habs = new ArrayList<>();

        // Habilidad 0: Golpe Devastador (mono-objetivo)
        habs.add(new Habilidad("💥 Golpe Devastador", 20) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                if (objetivos == null || objetivos.isEmpty()) {
                    RegistroCombate.registrar(lanzador.getNombre() + " necesita un objetivo.");
                    return false;
                }
                Combatiente objetivo = objetivos.get(0);
                int danio = (lanzador.getAtaque() + fuerzaCaptura) * lanzador.getNivel();
                RegistroCombate.registrar("💥 ¡" + lanzador.getNombre()
                        + " ejecuta Golpe Devastador contra " + objetivo.getNombre() + "!");
                objetivo.recibirDanio(danio);
                return true;
            }
        });

        // Habilidad 1: Provocar / Guardia (sin objetivo)
        habs.add(new Habilidad("🛡️ Provocar / Guardia", 15) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                lanzador.setProvocando(true);
                lanzador.defender();
                RegistroCombate.registrar("🛡️ ¡" + lanzador.getNombre()
                        + " activa PROVOCAR! Ruge desafiante y atrae la atención de todos los enemigos.");
                return true;
            }
        });

        return habs;
    }
}
