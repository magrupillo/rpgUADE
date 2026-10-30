package rpg.modelo;

public class Guerrero extends Personaje {

    private static final long serialVersionUID = 1L;

    private int fuerza;

    public Guerrero(String nombre) {
        super(nombre, 210, 45, 1, 26, 14, 12);
        this.fuerza = 15;
        inicializarCatalogoHabilidades();
    }

    public int getFuerza() {
        return fuerza;
    }

    @Override
    public double getCriticoBonus() {
        return 0.05;
    }

    @Override
    protected void inicializarCatalogoHabilidades() {
        catalogoHabilidades.clear();
        habilidadesDesbloqueadas.clear();

        // 1. Ira de los Dioses (Default): taunt y defensa férrea
        Habilidad iraDioses = new HabilidadDeEstado(
                "Ira de los Dioses",
                "Kratos ruge con furor espartano: obliga a los enemigos a atacarlo y adopta postura defensiva.",
                15,
                TipoObjetivo.PROPIO,
                HabilidadDeEstado.EfectoEstado.PROVOCAR_Y_GUARDIA
        );

        // 2. Espadas del Caos: ataque en área
        Habilidad espadasCaos = new HabilidadOfensiva(
                "Espadas del Caos",
                "Gira sus cadenas incandescentes causando daño cortante y quemaduras a todos los enemigos.",
                25,
                TipoObjetivo.ENEMIGOS_TODOS,
                1.3,
                false
        );

        // 3. Hacha Leviatán: ataque directo brutal
        Habilidad hachaLeviatan = new HabilidadOfensiva(
                "Hacha Leviatán",
                "Arroja el hacha imbuida de escarcha helada asestando un impacto demoledor a un enemigo.",
                22,
                TipoObjetivo.ENEMIGO_INDIVIDUAL,
                2.2,
                false
        );

        catalogoHabilidades.add(iraDioses);
        catalogoHabilidades.add(espadasCaos);
        catalogoHabilidades.add(hachaLeviatan);

        // Comienza con la primera habilidad por defecto
        habilidadesDesbloqueadas.add(iraDioses);
    }
}
