package rpg;

public class Mago extends Personaje {
    private int inteligencia;
    private static final int COSTO_MANA = 25;

    public Mago(String nombre) {
        super(nombre, 120, 120, 1, 15, 6, 16);
        this.inteligencia = 30;
    }

    public Mago(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int inteligencia) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.inteligencia = inteligencia;
    }

    public int getInteligencia() {
        return inteligencia;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo) {
        if (!gastarMana(COSTO_MANA)) {
            System.out.println(getNombre() + " no pudo conjurar Bola de Fuego.");
            return;
        }
        int danioHabilidad = (getAtaque() + this.inteligencia) * getNivel();
        System.out.println(getNombre() + " lanza una abrasadora ¡Bola de Fuego! contra " + objetivo.getNombre() + "!");
        objetivo.recibirDanio(danioHabilidad);
    }
}
