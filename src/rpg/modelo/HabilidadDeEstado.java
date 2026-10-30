package rpg.modelo;

import java.util.List;

/**
 * Habilidad que modifica el estado táctico (taunt/provocar, recarga de ki/maná, estados alterados).
 */
public class HabilidadDeEstado extends Habilidad {

    private static final long serialVersionUID = 1L;

    public enum EfectoEstado {
        PROVOCAR_Y_GUARDIA,
        CARGAR_KI,
        GAS_VENENO_AREA
    }

    private final EfectoEstado efecto;

    public HabilidadDeEstado(String nombre, String descripcion, int costeMana, TipoObjetivo tipoObjetivo,
                             EfectoEstado efecto) {
        super(nombre, descripcion, costeMana, tipoObjetivo);
        this.efecto = efecto;
    }

    @Override
    public boolean ejecutar(Personaje lanzador, List<Combatiente> objetivos, Batalla batalla) {
        if (!lanzador.gastarMana(getCosteMana())) return false;

        switch (efecto) {
            case PROVOCAR_Y_GUARDIA -> {
                lanzador.setProvocando(true);
                lanzador.defender();
                RegistroCombate.registrar(lanzador.getNombre() + " activa " + getNombre()
                        + "! Ruge desafiante: atrae los ataques enemigos y adopta guardia férrea (+DEF).");
            }
            case CARGAR_KI -> {
                int recuperado = (int) (lanzador.getManaMaximo() * 0.45);
                lanzador.recuperarMana(recuperado);
                RegistroCombate.registrar(lanzador.getNombre() + " concentra su Ki al máximo! Recupera "
                        + recuperado + " de Ki/Maná (" + lanzador.getMana() + "/" + lanzador.getManaMaximo() + ").");
            }
            case GAS_VENENO_AREA -> {
                RegistroCombate.registrar(lanzador.getNombre() + " esparce " + getNombre() + " en todo el campo enemigo!");
                if (objetivos != null) {
                    for (Combatiente c : objetivos) {
                        if (c != null && c.estaVivo()) {
                            c.recibirDanio(15);
                            RegistroCombate.registrar("  " + c.getNombre() + " tose asfixiado por el gas (+15 de daño tóxico)!");
                        }
                    }
                }
            }
        }
        return true;
    }
}
