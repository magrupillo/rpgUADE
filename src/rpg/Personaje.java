package rpg;

public abstract class Personaje {

    private String nombre;
    private int vida;
    private int vidaMaxima;
    private int mana;
    private int manaMaximo;
    private int nivel;
    private int ataqueBase;
    private int defensaBase;
    private int velocidad;

    private int experiencia;
    private int experienciaSiguienteNivel;

    private boolean defendiendo;

    // Slots de equipo
    private Arma arma;
    private Armadura armadura;

    public Personaje(String nombre, int vida, int mana, int nivel, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.mana = mana;
        this.manaMaximo = mana;
        this.nivel = nivel;
        this.ataqueBase = ataque;
        this.defensaBase = defensa;
        this.velocidad = velocidad;
        this.experiencia = 0;
        this.experienciaSiguienteNivel = 100 * nivel;
        this.defendiendo = false;
        this.arma = null;
        this.armadura = null;
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

    public int getMana() {
        return mana;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    public int getNivel() {
        return nivel;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public int getExperienciaSiguienteNivel() {
        return experienciaSiguienteNivel;
    }

    public boolean isDefendiendo() {
        return defendiendo;
    }

    public Arma getArma() {
        return arma;
    }

    public Armadura getArmadura() {
        return armadura;
    }

    // El cálculo de ataque ajusta dinámicamente el valor según el arma equipada
    public int getAtaque() {
        int bonus = (arma != null) ? arma.getBonusAtaque() : 0;
        return ataqueBase + bonus;
    }

    // El cálculo de defensa ajusta dinámicamente el valor según la armadura equipada y postura defensiva
    public int getDefensa() {
        int bonus = (armadura != null) ? armadura.getBonusDefensa() : 0;
        int defensaTotal = defensaBase + bonus;
        if (defendiendo) {
            defensaTotal += defensaBase;
        }
        return defensaTotal;
    }

    public boolean estaVivo() {
        return this.vida > 0;
    }

    public void defender() {
        this.defendiendo = true;
        System.out.println(nombre + " adopta una postura defensiva para reducir el daño recibido.");
    }

    public void resetearDefensaTemporal() {
        this.defendiendo = false;
    }

    public boolean gastarMana(int costo) {
        if (this.mana >= costo) {
            this.mana -= costo;
            System.out.println(nombre + " consumió " + costo + " de maná. Maná restante: " + this.mana + "/" + this.manaMaximo);
            return true;
        } else {
            System.out.println(nombre + " no tiene suficiente maná (" + this.mana + "/" + costo + " necesario).");
            return false;
        }
    }

    public void curarVida(int cantidad) {
        this.vida += cantidad;
        if (this.vida > this.vidaMaxima) {
            this.vida = this.vidaMaxima;
        }
    }

    public void recuperarMana(int cantidad) {
        this.mana += cantidad;
        if (this.mana > this.manaMaximo) {
            this.mana = this.manaMaximo;
        }
    }

    // Métodos del Sistema de Slots (Equipar / Desequipar)
    public Arma equiparArma(Arma nuevaArma) {
        Arma armaAnterior = this.arma;
        this.arma = nuevaArma;
        System.out.println(nombre + " se equipó el arma: " + nuevaArma.getNombre() + " (+" + nuevaArma.getBonusAtaque() + " ATQ | Ataque total: " + getAtaque() + ")");
        return armaAnterior;
    }

    public Arma desequiparArma() {
        if (this.arma != null) {
            Arma desequipada = this.arma;
            this.arma = null;
            System.out.println(nombre + " se desequipó el arma: " + desequipada.getNombre() + " (Ataque total: " + getAtaque() + ")");
            return desequipada;
        }
        System.out.println(nombre + " no tiene ningún arma equipada.");
        return null;
    }

    public Armadura equiparArmadura(Armadura nuevaArmadura) {
        Armadura armaduraAnterior = this.armadura;
        this.armadura = nuevaArmadura;
        System.out.println(nombre + " se equipó la armadura: " + nuevaArmadura.getNombre() + " (+" + nuevaArmadura.getBonusDefensa() + " DEF | Defensa total: " + getDefensa() + ")");
        return armaduraAnterior;
    }

    public Armadura desequiparArmadura() {
        if (this.armadura != null) {
            Armadura desequipada = this.armadura;
            this.armadura = null;
            System.out.println(nombre + " se desequipó la armadura: " + desequipada.getNombre() + " (Defensa total: " + getDefensa() + ")");
            return desequipada;
        }
        System.out.println(nombre + " no tiene ninguna armadura equipada.");
        return null;
    }

    public void ganarExperiencia(int xp) {
        this.experiencia += xp;
        System.out.println(nombre + " ganó " + xp + " puntos de experiencia. (Total: " + experiencia + "/" + experienciaSiguienteNivel + ")");
        while (this.experiencia >= this.experienciaSiguienteNivel) {
            subirNivel();
        }
    }

    private void subirNivel() {
        this.experiencia -= this.experienciaSiguienteNivel;
        this.nivel++;
        this.experienciaSiguienteNivel = (int) (this.experienciaSiguienteNivel * 1.5);

        int aumentoVida = (int) (this.vidaMaxima * 0.20) + 15;
        int aumentoMana = (int) (this.manaMaximo * 0.20) + 10;
        int aumentoAtaque = Math.max(2, (int) (this.ataqueBase * 0.20));
        int aumentoDefensa = Math.max(2, (int) (this.defensaBase * 0.20));
        int aumentoVelocidad = Math.max(1, (int) (this.velocidad * 0.15));

        this.vidaMaxima += aumentoVida;
        this.manaMaximo += aumentoMana;
        this.ataqueBase += aumentoAtaque;
        this.defensaBase += aumentoDefensa;
        this.velocidad += aumentoVelocidad;

        this.vida = this.vidaMaxima;
        this.mana = this.manaMaximo;

        System.out.println("\n★ ¡SUBIDA DE NIVEL! " + nombre + " ha alcanzado el Nivel " + nivel + "!");
        System.out.println("  + Vida Máxima: +" + aumentoVida + " (" + vidaMaxima + ")");
        System.out.println("  + Maná Máximo: +" + aumentoMana + " (" + manaMaximo + ")");
        System.out.println("  + Ataque Base: +" + aumentoAtaque + " (" + ataqueBase + ")");
        System.out.println("  + Defensa Base: +" + aumentoDefensa + " (" + defensaBase + ")");
        System.out.println("  + Velocidad: +" + aumentoVelocidad + " (" + velocidad + ")\n");
    }

    public void atacar(Enemigo enemigo) {
        int danioTotal = getAtaque() * this.nivel;
        System.out.println(nombre + " ataca a " + enemigo.getNombre() + " causando " + danioTotal + " de daño.");
        enemigo.recibirDanio(danioTotal);
    }

    public void recibirDanio(int danio) {
        int danioRecibido = Math.max(1, danio - getDefensa());
        this.vida -= danioRecibido;
        if (this.vida < 0) {
            this.vida = 0;
        }
        System.out.println(nombre + " recibe " + danioRecibido + " de daño (Defensa total: " + getDefensa() + "). Vida restante: " + this.vida + "/" + this.vidaMaxima);
        if (!estaVivo()) {
            System.out.println("☠ ¡" + nombre + " ha caído en combate!");
        }
    }

    public void usarHabilidad(Enemigo objetivo) {
        System.out.println(nombre + " no puede usar esta habilidad sobre un enemigo.");
    }

    public void usarHabilidad(Personaje aliado) {
        System.out.println(nombre + " no tiene una habilidad de apoyo para aliados.");
    }

    @Override
    public String toString() {
        String armaTxt = (arma != null) ? arma.getNombre() : "Ninguna";
        String armaduraTxt = (armadura != null) ? armadura.getNombre() : "Ninguna";
        return nombre + " [Nivel: " + nivel + " | HP: " + vida + "/" + vidaMaxima + " | MP: " + mana + "/" + manaMaximo + " | Vel: " + velocidad + " | ATQ: " + getAtaque() + " | DEF: " + getDefensa() + " | Arma: " + armaTxt + " | Armadura: " + armaduraTxt + "]";
    }
}
