package rpg;

public class Curador extends Personaje {
    private int poderCurativo;
    private static final int COSTO_MANA = 20;

    public Curador(String nombre) {
        super(nombre, 140, 100, 1, 10, 8, 14);
        this.poderCurativo = 35;
    }

    public Curador(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int poderCurativo) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.poderCurativo = poderCurativo;
    }

    public int getPoderCurativo() {
        return poderCurativo;
    }

    @Override
    public void usarHabilidad(Personaje aliado) {
        if (!gastarMana(COSTO_MANA)) {
            System.out.println(getNombre() + " no tiene maná para realizar la curación.");
            return;
        }
        int curacion = (this.poderCurativo + getAtaque()) * getNivel();
        System.out.println(getNombre() + " canaliza ¡Rezo de Sanación! sobre " + aliado.getNombre() + " restaurando " + curacion + " puntos de vida.");
        aliado.curarVida(curacion);
    }
}
