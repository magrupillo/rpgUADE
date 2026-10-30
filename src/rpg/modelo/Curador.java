package rpg.modelo;

public class Curador extends Personaje {

    private static final long serialVersionUID = 1L;

    private int poderCurativo;

    public Curador(String nombre) {
        super(nombre, 150, 90, 1, 16, 9, 14);
        this.poderCurativo = 30;
        inicializarCatalogoHabilidades();
    }

    public Curador() {
        this("El Guasón");
    }

    public int getPoderCurativo() {
        return poderCurativo;
    }

    @Override
    protected void inicializarCatalogoHabilidades() {
        catalogoHabilidades.clear();
        habilidadesDesbloqueadas.clear();

        // 1. Inyección de Titán (Default): cura al aliado más herido y purga aflicciones
        Habilidad inyeccionTitan = new HabilidadCurativa(
                "Inyección de Titán",
                "El Guasón inyecta una dosis revitalizante al aliado más herido restaurando gran salud y purgando veneno/aturdimiento.",
                20,
                TipoObjetivo.ALIADO_MAS_HERIDO,
                55,
                true
        );

        // 2. Gas de la Risa: daño y asfixia en área
        Habilidad gasRisa = new HabilidadDeEstado(
                "Gas de la Risa",
                "Libera una nube verde intoxicante que daña y asfixia a todos los enemigos presentes.",
                25,
                TipoObjetivo.ENEMIGOS_TODOS,
                HabilidadDeEstado.EfectoEstado.GAS_VENENO_AREA
        );

        // 3. Regalo Sorpresa: ataque directo explosivo
        Habilidad regaloSorpresa = new HabilidadOfensiva(
                "Regalo Sorpresa",
                "Entrega una caja de broma con resortes y dinamita que revienta en las manos del enemigo.",
                22,
                TipoObjetivo.ENEMIGO_INDIVIDUAL,
                2.1,
                false
        );

        catalogoHabilidades.add(inyeccionTitan);
        catalogoHabilidades.add(gasRisa);
        catalogoHabilidades.add(regaloSorpresa);

        // Comienza con la primera habilidad por defecto
        habilidadesDesbloqueadas.add(inyeccionTitan);
    }
}
