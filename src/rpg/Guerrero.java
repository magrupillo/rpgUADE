package rpg;

public class Guerrero extends Personaje {
    private int fuerza;
    private static final int COSTO_MANA = 20;

    public Guerrero(String nombre) {
        super(nombre, 220, 50, 1, 25, 15, 12);
        this.fuerza = 20;
    }

    public Guerrero(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad, int fuerza) {
        super(nombre, vida, mana, nivel, ataque, defensa, velocidad);
        this.fuerza = fuerza;
    }

    public int getFuerza() {
        return fuerza;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo) {
        if (!gastarMana(COSTO_MANA)) {
            System.out.println(getNombre() + " no pudo ejecutar Golpe Devastador.");
            return;
        }
        int danioHabilidad = (getAtaque() + this.fuerza) * getNivel();
        System.out.println(getNombre() + " usa ¡Golpe Devastador! contra " + objetivo.getNombre() + "!");
        objetivo.recibirDanio(danioHabilidad);
    }
}
