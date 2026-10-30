package rpg.modelo;

public class Arquero extends Personaje {

    private static final long serialVersionUID = 1L;

    private int punteria;

    public Arquero(String nombre) {
        super(nombre, 165, 55, 1, 24, 10, 20);
        this.punteria = 20;
        inicializarCatalogoHabilidades();
    }

    public Arquero() {
        this("Tommy Shelby");
    }

    public int getPunteria() {
        return punteria;
    }

    @Override
    public double getCriticoBonus() {
        return 0.15;
    }

    @Override
    protected void inicializarCatalogoHabilidades() {
        catalogoHabilidades.clear();
        habilidadesDesbloqueadas.clear();

        // 1. Disparo a la cabeza (Default): Headshot crítico directo
        Habilidad headshot = new HabilidadOfensiva(
                "Disparo a la Cabeza",
                "¡HEADSHOT! Apunta con frialdad y dispara un tiro letal con daño crítico asegurado.",
                18,
                TipoObjetivo.ENEMIGO_INDIVIDUAL,
                2.0,
                true
        );

        // 2. Llamar refuerzos: ataque en área
        Habilidad refuerzos = new HabilidadOfensiva(
                "Llamar Refuerzos",
                "Por orden de los Peaky Blinders: una ráfaga cerrada barre a todos los enemigos en el campo.",
                26,
                TipoObjetivo.ENEMIGOS_TODOS,
                1.3,
                false
        );

        // 3. Granada: explosión directa destructiva
        Habilidad granada = new HabilidadOfensiva(
                "Granada",
                "Arroja un explosivo militar que estalla en la cara del objetivo con fuerza desmedida.",
                22,
                TipoObjetivo.ENEMIGO_INDIVIDUAL,
                2.3,
                false
        );

        catalogoHabilidades.add(headshot);
        catalogoHabilidades.add(refuerzos);
        catalogoHabilidades.add(granada);

        // Comienza con la primera habilidad por defecto
        habilidadesDesbloqueadas.add(headshot);
    }
}
