package rpg.modelo;

import java.util.ArrayList;
import java.util.List;

public class Curador extends Personaje {

    private int poderCurativo;

    public Curador(String nombre) {
        super(nombre, 140, 100, 1, 10, 8, 14);
        this.poderCurativo = 35;
    }

    public Curador(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int poderCurativo) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.poderCurativo = poderCurativo;
    }

    public int getPoderCurativo() { return poderCurativo; }

    @Override
    public List<Habilidad> getHabilidades() {
        int poderCaptura = this.poderCurativo;
        List<Habilidad> habs = new ArrayList<>();

        // Habilidad 0: Rezo de Sanación (mono-objetivo: un aliado)
        habs.add(new Habilidad("✨ Rezo de Sanación", 20) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                Combatiente objetivo = null;
                // objetivos[0] es el aliado seleccionado (Personaje)
                if (objetivos != null && !objetivos.isEmpty()) {
                    objetivo = objetivos.get(0);
                }
                if (objetivo == null) {
                    // fallback: el más herido
                    objetivo = batalla.getParty().obtenerMiembroMasHerido();
                }
                if (objetivo == null) objetivo = lanzador;

                int curacion = (poderCaptura + lanzador.getAtaque()) * lanzador.getNivel();
                RegistroCombate.registrar("✨ ¡" + lanzador.getNombre()
                        + " canaliza Rezo de Sanación sobre " + objetivo.getNombre()
                        + " restaurando " + curacion + " HP!");
                if (objetivo instanceof Personaje p) {
                    p.curarVida(curacion);
                    p.setAturdido(false);
                    p.setEnvenenado(false);
                }
                return true;
            }
        });

        // Habilidad 1: Bendición Sagrada (área: todos los aliados vivos)
        habs.add(new Habilidad("🌟 Bendición Sagrada [Área + Purificar]", 35) {
            @Override
            public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
                if (!lanzador.gastarMana(getCosteMana())) return false;
                int curacion = (poderCaptura + (lanzador.getAtaque() / 2)) * lanzador.getNivel();
                RegistroCombate.registrar("🌟 ¡" + lanzador.getNombre()
                        + " invoca una Bendición Sagrada purificando a toda la Party!");
                // objetivos contiene todos los miembros vivos de la party
                for (Combatiente aliado : objetivos) {
                    if (aliado.estaVivo() && aliado instanceof Personaje p) {
                        p.curarVida(curacion);
                        p.setAturdido(false);
                        p.setEnvenenado(false);
                        RegistroCombate.registrar("  💚 " + p.getNombre()
                                + " recuperó " + curacion + " HP y sus estados fueron purgados.");
                    }
                }
                return true;
            }
        });

        return habs;
    }
}
