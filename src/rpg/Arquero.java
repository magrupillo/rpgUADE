package rpg;

public class Arquero extends Personaje {
    private int destreza;
    private static final int COSTO_MANA = 15;

    public Arquero(String nombre) {
        super(nombre, 160, 60, 1, 20, 10, 22);
        this.destreza = 25;
    }

    public Arquero(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int destreza) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.destreza = destreza;
    }

    public int getDestreza() {
        return destreza;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo) {
        if (!gastarMana(COSTO_MANA)) {
            System.out.println(getNombre() + " no pudo ejecutar Disparo Certero.");
            return;
        }
        int danioHabilidad = (getAtaque() + this.destreza) * getNivel();
        System.out.println(getNombre() + " usa ¡Disparo Certero! contra " + objetivo.getNombre() + "!");
        objetivo.recibirDanio(danioHabilidad);
    }
}
