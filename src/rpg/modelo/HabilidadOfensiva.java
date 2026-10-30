package rpg.modelo;

import java.util.List;

/**
 * Habilidad que inflige daño a uno o varios enemigos.
 */
public class HabilidadOfensiva extends Habilidad {

    private static final long serialVersionUID = 1L;

    private final double multiplicadorDanio;
    private final boolean criticoGarantizado;

    public HabilidadOfensiva(String nombre, String descripcion, int costeMana, TipoObjetivo tipoObjetivo,
                             double multiplicadorDanio, boolean criticoGarantizado) {
        super(nombre, descripcion, costeMana, tipoObjetivo);
        this.multiplicadorDanio = multiplicadorDanio;
        this.criticoGarantizado = criticoGarantizado;
    }

    @Override
    public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
        if (!lanzador.gastarMana(getCosteMana())) return false;
        if (objetivos == null || objetivos.isEmpty()) {
            RegistroCombate.registrar(lanzador.getNombre() + " no tiene objetivos para " + getNombre() + ".");
            return false;
        }

        RegistroCombate.registrar(lanzador.getNombre() + " desata " + getNombre() + "!");

        for (Combatiente objetivo : objetivos) {
            if (objetivo != null && objetivo.estaVivo()) {
                boolean esCrit = criticoGarantizado || (Math.random() < (0.10 + lanzador.getCriticoBonus()));
                double multFinal = multiplicadorDanio * (esCrit ? 1.5 : 1.0);
                int danio = (int) Math.round(lanzador.getAtaque() * multFinal);
                if (esCrit) {
                    RegistroCombate.registrar("  [CRÍTICO] Impacto letal contra " + objetivo.getNombre() + " por " + danio + " de daño!");
                } else {
                    RegistroCombate.registrar("  Impacto contra " + objetivo.getNombre() + " por " + danio + " de daño.");
                }
                objetivo.recibirDanio(danio);
            }
        }
        return true;
    }
}
