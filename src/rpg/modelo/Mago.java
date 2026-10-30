package rpg.modelo;

public class Mago extends Personaje {

    private static final long serialVersionUID = 1L;

    private int inteligencia;

    public Mago(String nombre) {
        super(nombre, 140, 110, 1, 22, 8, 16);
        this.inteligencia = 28;
        inicializarCatalogoHabilidades();
    }

    public int getInteligencia() {
        return inteligencia;
    }

    @Override
    protected void inicializarCatalogoHabilidades() {
        catalogoHabilidades.clear();
        habilidadesDesbloqueadas.clear();

        // 1. Kamehameha (Default): potente ráfaga de Ki concentrada a un objetivo
        Habilidad kamehameha = new HabilidadOfensiva(
                "Kamehameha",
                "¡KA... ME... HA... ME... HA! Dispara una colosal onda de Ki que pulveriza al objetivo.",
                22,
                TipoObjetivo.ENEMIGO_INDIVIDUAL,
                2.4,
                false
        );

        // 2. Genkidama: devastación en área
        Habilidad genkidama = new HabilidadOfensiva(
                "Genkidama",
                "Reúne la energía de los seres vivos y arroja una inmensa esfera de energía que arrasa a todos los enemigos.",
                38,
                TipoObjetivo.ENEMIGOS_TODOS,
                1.7,
                false
        );

        // 3. Cargar Ki: concentración y recarga de maná
        Habilidad cargarKi = new HabilidadDeEstado(
                "Cargar Ki",
                "Expulsa un aura blanca y grita con determinación recargando rápidamente gran porción de su Ki/Maná.",
                0,
                TipoObjetivo.PROPIO,
                HabilidadDeEstado.EfectoEstado.CARGAR_KI
        );

        catalogoHabilidades.add(kamehameha);
        catalogoHabilidades.add(genkidama);
        catalogoHabilidades.add(cargarKi);

        // Comienza con la primera habilidad por defecto
        habilidadesDesbloqueadas.add(kamehameha);
    }
}
