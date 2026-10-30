package rpg.modelo;

import java.util.List;

/**
 * Habilidad que restaura vida y/o purifica estados alterados de aliados.
 */
public class HabilidadCurativa extends Habilidad {

    private static final long serialVersionUID = 1L;

    private final int poderBase;
    private final boolean purificaEstados;

    public HabilidadCurativa(String nombre, String descripcion, int costeMana, TipoObjetivo tipoObjetivo,
                             int poderBase, boolean purificaEstados) {
        super(nombre, descripcion, costeMana, tipoObjetivo);
        this.poderBase = poderBase;
        this.purificaEstados = purificaEstados;
    }

    @Override
    public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
        if (!lanzador.gastarMana(getCosteMana())) return false;
        if (objetivos == null || objetivos.isEmpty()) {
            RegistroCombate.registrar(lanzador.getNombre() + " no tiene aliados para curar.");
            return false;
        }

        RegistroCombate.registrar(lanzador.getNombre() + " activa " + getNombre() + "!");

        for (Combatiente objetivo : objetivos) {
            if (objetivo != null && objetivo.estaVivo()) {
                int curacion = poderBase + (int) (lanzador.getAtaque() * 0.5);
                if (objetivo instanceof Personaje p) {
                    p.curarVida(curacion);
                    if (purificaEstados) {
                        p.setAturdido(false);
                        p.setEnvenenado(false);
                    }
                    RegistroCombate.registrar("  " + p.getNombre() + " recuperó " + curacion
                            + " HP. (Vida: " + p.getVida() + "/" + p.getVidaMaxima() + ")");
                }
            }
        }
        return true;
    }
}
