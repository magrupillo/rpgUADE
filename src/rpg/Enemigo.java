package rpg;

import java.util.List;

public abstract class Enemigo {
    private String nombre;
    private int vida;
    private int vidaMaxima;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int experienciaOtorgada;
    private boolean defendiendo;

    public Enemigo(String nombre, int vida, int ataque, int defensa, int velocidad, int experienciaOtorgada) {
        this.nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.experienciaOtorgada = experienciaOtorgada;
        this.defendiendo = false;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defendiendo ? (defensa * 2) : defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getExperienciaOtorgada() {
        return experienciaOtorgada;
    }

    public boolean estaVivo() {
        return this.vida > 0;
    }

    public void defender() {
        this.defendiendo = true;
        System.out.println(nombre + " se pone a la defensiva.");
    }

    public void resetearDefensaTemporal() {
        this.defendiendo = false;
    }

    public void curarVida(int cantidad) {
        this.vida += cantidad;
        if (this.vida > this.vidaMaxima) {
            this.vida = this.vidaMaxima;
        }
        System.out.println(nombre + " recuperó " + cantidad + " de vida. Vida actual: " + this.vida + "/" + this.vidaMaxima);
    }

    public void recibirDanio(int danio) {
        int danioFinal = Math.max(1, danio - getDefensa());
        this.vida -= danioFinal;
        if (this.vida < 0) {
            this.vida = 0;
        }
        System.out.println(nombre + " recibe " + danioFinal + " de daño (Defensa: " + getDefensa() + "). Vida restante: " + this.vida + "/" + this.vidaMaxima);
        if (!estaVivo()) {
            System.out.println("☠ ¡" + nombre + " ha sido derrotado!");
        }
    }

    public void atacar(Personaje objetivo) {
        System.out.println(nombre + " ataca ferozmente a " + objetivo.getNombre() + " con poder " + ataque + ".");
        objetivo.recibirDanio(this.ataque);
    }

    // Comportamiento e inteligencia polimórfica
    public abstract void tomarDecision(Party party, List<Enemigo> aliados);

    @Override
    public String toString() {
        return nombre + " [HP: " + vida + "/" + vidaMaxima + " | ATQ: " + ataque + " | DEF: " + defensa + " | Vel: " + velocidad + "]";
    }
}
