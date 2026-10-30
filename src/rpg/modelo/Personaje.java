package rpg.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase abstracta que representa a un héroe de la Party.
 * Implementa {@link Combatiente} para participar en el ciclo de turnos de {@link Batalla}.
 *
 * Cada subclase concreta ({@link Guerrero}, {@link Arquero}, {@link Mago}, {@link Curador})
 * define su propia lista de {@link Habilidad}es y estadísticas iniciales.
 */
public abstract class Personaje implements Combatiente {

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
    private boolean provocando;
    private boolean aturdido;
    private boolean envenenado;

    private int puntosAtributoDisponibles;

    // Slots de equipo
    private Arma arma;
    private Armadura armadura;

    // ─── Constante de fórmula ────────────────────────────────────────────────────
    private static final double PROB_CRITICO_BASE = 0.10; // 10% base
    private static final Random RND = new Random();

    public Personaje(String nombre, int vida, int mana, int nivel,
                     int ataque, int defensa, int velocidad) {
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
        this.provocando = false;
        this.aturdido = false;
        this.envenenado = false;
        this.puntosAtributoDisponibles = 0;
        this.arma = null;
        this.armadura = null;
    }

    // ─── Getters Combatiente ─────────────────────────────────────────────────────

    @Override public String getNombre()   { return nombre; }
    @Override public int getVida()        { return vida; }
    @Override public int getVidaMaxima()  { return vidaMaxima; }
    @Override public int getVelocidad()   { return velocidad; }
    @Override public boolean isDefendiendo() { return defendiendo; }

    @Override
    public int getAtaque() {
        int bonus = (arma != null) ? arma.getBonusAtaque() : 0;
        return ataqueBase + bonus;
    }

    @Override
    public int getDefensa() {
        int bonus = (armadura != null) ? armadura.getBonusDefensa() : 0;
        int total = defensaBase + bonus;
        if (defendiendo) total += defensaBase;
        return total;
    }

    @Override
    public boolean estaVivo() { return vida > 0; }

    // ─── Getters extras ──────────────────────────────────────────────────────────

    public int getMana()                 { return mana; }
    public int getManaMaximo()           { return manaMaximo; }
    public int getNivel()                { return nivel; }
    public int getAtaqueBase()           { return ataqueBase; }
    public int getDefensaBase()          { return defensaBase; }
    public int getExperiencia()          { return experiencia; }
    public int getExperienciaSiguienteNivel() { return experienciaSiguienteNivel; }
    public boolean isProvocando()        { return provocando; }
    public boolean isAturdido()          { return aturdido; }
    public boolean isEnvenenado()        { return envenenado; }
    public int getPuntosAtributoDisponibles() { return puntosAtributoDisponibles; }
    public Arma getArma()                { return arma; }
    public Armadura getArmadura()        { return armadura; }

    public String getTipo() { return getClass().getSimpleName(); }

    public String getRutaSprite() { return "sprites/" + getTipo().toLowerCase() + ".png"; }

    // ─── Setters de estado ───────────────────────────────────────────────────────

    public void setProvocando(boolean provocando) { this.provocando = provocando; }
    public void setAturdido(boolean aturdido)     { this.aturdido = aturdido; }
    public void setEnvenenado(boolean envenenado) { this.envenenado = envenenado; }

    // ─── Combatiente ─────────────────────────────────────────────────────────────

    @Override
    public void defender() {
        this.defendiendo = true;
        RegistroCombate.registrar("🛡️ " + nombre + " adopta una postura defensiva para reducir el daño.");
    }

    @Override
    public void resetearDefensaTemporal() {
        this.defendiendo = false;
        this.provocando = false;
    }

    /**
     * Fórmula unificada de daño recibido:
     *   daño_final = max(1, danio_bruto - defensa_efectiva)
     * El multiplicador de crítico y de habilidad se aplican antes de llamar aquí.
     */
    @Override
    public void recibirDanio(int danio) {
        int danioRecibido = Math.max(1, danio - getDefensa());
        this.vida -= danioRecibido;
        if (this.vida < 0) this.vida = 0;
        RegistroCombate.registrar("💔 " + nombre + " recibe " + danioRecibido
                + " de daño (DEF: " + getDefensa() + "). Vida: " + vida + "/" + vidaMaxima);
        if (!estaVivo()) {
            RegistroCombate.registrar("☠ ¡" + nombre + " ha caído en combate!");
        }
    }

    // ─── Ataque básico ───────────────────────────────────────────────────────────

    /**
     * Fórmula de ataque básico unificada:
     *   daño = (ataque * nivel) * (1 + bonusCritico)
     * Probabilidad de crítico: {@value #PROB_CRITICO_BASE} + bonificaciones de clase.
     */
    public void atacar(Combatiente objetivo) {
        boolean critico = RND.nextDouble() < (PROB_CRITICO_BASE + getCriticoBonus());
        int danioBase = getAtaque() * nivel;
        double multiplicador = critico ? 2.0 : 1.0;
        int danioTotal = (int) (danioBase * multiplicador);

        if (critico) {
            RegistroCombate.registrar("⚡ ¡CRÍTICO! " + nombre + " ataca a "
                    + objetivo.getNombre() + " por " + danioTotal + " de daño.");
        } else {
            RegistroCombate.registrar("⚔️ " + nombre + " ataca a "
                    + objetivo.getNombre() + " por " + danioTotal + " de daño.");
        }
        objetivo.recibirDanio(danioTotal);
    }

    /**
     * Porcentaje adicional de probabilidad de crítico (override en subclases especializadas).
     */
    protected double getCriticoBonus() { return 0.0; }

    // ─── Habilidades ─────────────────────────────────────────────────────────────

    /**
     * Devuelve la lista de habilidades del personaje.
     * Cada subclase concreta define sus propias instancias de {@link Habilidad}.
     */
    public abstract List<Habilidad> getHabilidades();

    /**
     * Devuelve los nombres de las habilidades para la interfaz gráfica.
     */
    public List<String> getNombresHabilidades() {
        List<String> nombres = new ArrayList<>();
        for (Habilidad h : getHabilidades()) {
            nombres.add(h.toString());
        }
        return nombres;
    }

    /**
     * Ejecuta la habilidad en el índice dado.
     * Delega a {@link Habilidad#ejecutar(Personaje, List, Batalla)}.
     *
     * @param indiceHabilidad índice en la lista de habilidades
     * @param objetivos       lista de combatientes afectados
     * @param batalla         contexto actual de la batalla
     * @return {@code true} si la habilidad se ejecutó con éxito
     */
    public boolean ejecutarHabilidad(int indiceHabilidad, List<Combatiente> objetivos, Batalla batalla) {
        List<Habilidad> habs = getHabilidades();
        if (indiceHabilidad < 0 || indiceHabilidad >= habs.size()) return false;
        return habs.get(indiceHabilidad).ejecutar(this, objetivos, batalla);
    }

    // ─── Maná ────────────────────────────────────────────────────────────────────

    public boolean gastarMana(int costo) {
        if (this.mana >= costo) {
            this.mana -= costo;
            RegistroCombate.registrar("🔹 " + nombre + " consumió " + costo
                    + " de maná (" + mana + "/" + manaMaximo + ").");
            return true;
        }
        RegistroCombate.registrar("⚠️ " + nombre + " no tiene suficiente maná ("
                + mana + "/" + costo + " requerido).");
        return false;
    }

    public void curarVida(int cantidad) {
        this.vida = Math.min(vida + cantidad, vidaMaxima);
    }

    public void recuperarMana(int cantidad) {
        this.mana = Math.min(mana + cantidad, manaMaximo);
    }

    // ─── Puntos de atributo (subida de nivel) ────────────────────────────────────

    public void agregarPuntosAtributo(int puntos) { this.puntosAtributoDisponibles += puntos; }

    public boolean asignarPuntoVida() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        vidaMaxima += 15; vida += 15;
        RegistroCombate.registrar(nombre + " aumentó su Vida Máxima a " + vidaMaxima);
        return true;
    }

    public boolean asignarPuntoMana() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        manaMaximo += 10; mana += 10;
        RegistroCombate.registrar(nombre + " aumentó su Maná Máximo a " + manaMaximo);
        return true;
    }

    public boolean asignarPuntoAtaque() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        ataqueBase += 2;
        RegistroCombate.registrar(nombre + " aumentó su Ataque Base a " + ataqueBase);
        return true;
    }

    public boolean asignarPuntoDefensa() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        defensaBase += 2;
        RegistroCombate.registrar(nombre + " aumentó su Defensa Base a " + defensaBase);
        return true;
    }

    public void aumentarAtaqueBase(int incremento) {
        this.ataqueBase += incremento;
    }

    public void aumentarDefensaBase(int incremento) {
        this.defensaBase += incremento;
    }

    public boolean asignarPuntoVelocidad() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        velocidad += 2;
        RegistroCombate.registrar(nombre + " aumentó su Velocidad a " + velocidad);
        return true;
    }

    // ─── Experiencia y nivel ──────────────────────────────────────────────────────

    public void ganarExperiencia(int xp) {
        this.experiencia += xp;
        RegistroCombate.registrar("⭐ " + nombre + " ganó " + xp
                + " XP (" + experiencia + "/" + experienciaSiguienteNivel + ")");
        while (this.experiencia >= this.experienciaSiguienteNivel) {
            subirNivel();
        }
    }

    private void subirNivel() {
        this.experiencia -= this.experienciaSiguienteNivel;
        this.nivel++;
        this.experienciaSiguienteNivel = (int) (this.experienciaSiguienteNivel * 1.5);
        this.puntosAtributoDisponibles += 3;
        int aumentoVida = (int) (vidaMaxima * 0.15) + 10;
        int aumentoMana = (int) (manaMaximo * 0.15) + 5;
        vidaMaxima += aumentoVida;
        manaMaximo += aumentoMana;
        vida = vidaMaxima;
        mana = manaMaximo;
        RegistroCombate.registrar("\n★ ¡SUBIDA DE NIVEL! " + nombre + " alcanzó el Nivel " + nivel + "!");
        RegistroCombate.registrar("  + Puntos de atributo libres: +3 (Total: " + puntosAtributoDisponibles + ")");
        RegistroCombate.registrar("  + Vida y Maná restaurados al máximo.\n");
    }

    // ─── Equipo ───────────────────────────────────────────────────────────────────

    public Arma equiparArma(Arma nuevaArma) {
        Arma anterior = this.arma;
        this.arma = nuevaArma;
        RegistroCombate.registrar("⚔️ " + nombre + " equipó " + nuevaArma.getNombre()
                + " (+" + nuevaArma.getBonusAtaque() + " ATQ | Total: " + getAtaque() + ")");
        return anterior;
    }

    public Arma desequiparArma() {
        if (this.arma == null) return null;
        Arma d = this.arma;
        this.arma = null;
        RegistroCombate.registrar(nombre + " desequipó: " + d.getNombre());
        return d;
    }

    public Armadura equiparArmadura(Armadura nuevaArmadura) {
        Armadura anterior = this.armadura;
        this.armadura = nuevaArmadura;
        RegistroCombate.registrar("🛡️ " + nombre + " equipó " + nuevaArmadura.getNombre()
                + " (+" + nuevaArmadura.getBonusDefensa() + " DEF | Total: " + getDefensa() + ")");
        return anterior;
    }

    public Armadura desequiparArmadura() {
        if (this.armadura == null) return null;
        Armadura d = this.armadura;
        this.armadura = null;
        RegistroCombate.registrar(nombre + " desequipó: " + d.getNombre());
        return d;
    }

    // ─── toString ─────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        String armaTxt = (arma != null) ? arma.getNombre() : "Ninguna";
        String armaduraTxt = (armadura != null) ? armadura.getNombre() : "Ninguna";
        return nombre + " [Nv:" + nivel + " | HP:" + vida + "/" + vidaMaxima
                + " | MP:" + mana + "/" + manaMaximo
                + " | Vel:" + velocidad + " | ATQ:" + getAtaque()
                + " | DEF:" + getDefensa() + " | Arma:" + armaTxt
                + " | Armadura:" + armaduraTxt + "]";
    }
}
