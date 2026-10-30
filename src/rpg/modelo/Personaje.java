package rpg.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase abstracta que representa a un héroe de la Party.
 * Implementa {@link Combatiente} para participar en el ciclo de turnos de {@link Batalla}.
 */
public abstract class Personaje implements Combatiente {

    private static final long serialVersionUID = 1L;

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
    private boolean eleccionHabilidadPendiente;

    // Slots de equipo
    private Arma arma;
    private Armadura armadura;

    // Lista de habilidades desbloqueadas y catálogo completo (3 habilidades)
    protected final List<Habilidad> habilidadesDesbloqueadas = new ArrayList<>();
    protected final List<Habilidad> catalogoHabilidades = new ArrayList<>();

    private static final double PROB_CRITICO_BASE = 0.10;
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
        this.experienciaSiguienteNivel = 100;
        this.defendiendo = false;
        this.provocando = false;
        this.aturdido = false;
        this.envenenado = false;
        this.puntosAtributoDisponibles = 0;
        this.eleccionHabilidadPendiente = false;

        // Equipamiento inicial por defecto (básico y gastado)
        this.arma = new Arma("Navaja oxidada", "Arma inicial desgastada (+3 ATQ)", 3, 20, 8);
        this.armadura = new Armadura("Camiseta con agujeros", "Ropa gastada que apenas protege (+2 DEF)", 2, 20, 8);
    }

    // ─── Inicialización de habilidades en subclases ──────────────────────────────

    protected abstract void inicializarCatalogoHabilidades();

    public List<Habilidad> getHabilidades() {
        return habilidadesDesbloqueadas;
    }

    public List<Habilidad> getCatalogoHabilidades() {
        return catalogoHabilidades;
    }

    public List<Habilidad> getHabilidadesBloqueadas() {
        List<Habilidad> bloqueadas = new ArrayList<>();
        for (Habilidad h : catalogoHabilidades) {
            if (!habilidadesDesbloqueadas.contains(h)) {
                bloqueadas.add(h);
            }
        }
        return bloqueadas;
    }

    public boolean tieneEleccionHabilidadPendiente() {
        return eleccionHabilidadPendiente && !getHabilidadesBloqueadas().isEmpty();
    }

    public boolean desbloquearHabilidad(Habilidad h) {
        if (catalogoHabilidades.contains(h) && !habilidadesDesbloqueadas.contains(h)) {
            habilidadesDesbloqueadas.add(h);
            this.eleccionHabilidadPendiente = false;
            RegistroCombate.registrar(nombre + " aprendió la habilidad: " + h.getNombre() + "!");
            return true;
        }
        return false;
    }

    public boolean aprenderHabilidad(Habilidad h) {
        return desbloquearHabilidad(h);
    }

    public boolean puedeAprenderHabilidad() {
        return tieneEleccionHabilidadPendiente();
    }

    public List<String> getNombresHabilidades() {
        List<String> nombres = new ArrayList<>();
        for (Habilidad h : habilidadesDesbloqueadas) {
            nombres.add(h.toString());
        }
        return nombres;
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
        if (defendiendo) total += Math.max(4, defensaBase); // Incremento del 100% defensa base
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

    public void setProvocando(boolean provocando) { this.provocando = provocando; }
    public void setAturdido(boolean aturdido)     { this.aturdido = aturdido; }
    public void setEnvenenado(boolean envenenado) { this.envenenado = envenenado; }

    // ─── Acciones de Combate ─────────────────────────────────────────────────────

    @Override
    public void defender() {
        this.defendiendo = true;
        // Recupera 10% de maná al concentrarse y defender
        int manaRecup = Math.max(5, (int) (manaMaximo * 0.10));
        recuperarMana(manaRecup);
        RegistroCombate.registrar(nombre + " adopta una postura defensiva (+DEF y recupera " + manaRecup + " MP/Ki).");
    }

    @Override
    public void resetearDefensaTemporal() {
        this.defendiendo = false;
        this.provocando = false;
    }

    @Override
    public void recibirDanio(int danio) {
        int danioRecibido = Math.max(1, danio - getDefensa());
        this.vida -= danioRecibido;
        if (this.vida < 0) this.vida = 0;
        RegistroCombate.registrar(nombre + " recibe " + danioRecibido
                + " de daño (DEF: " + getDefensa() + "). Vida: " + vida + "/" + vidaMaxima);
        if (!estaVivo()) {
            RegistroCombate.registrar("¡" + nombre + " ha caído en combate!");
        }
    }

    public void atacar(Combatiente objetivo) {
        boolean critico = RND.nextDouble() < (PROB_CRITICO_BASE + getCriticoBonus());
        double multCritico = critico ? 1.5 : 1.0;
        int danio = (int) Math.round(getAtaque() * multCritico);

        if (critico) {
            RegistroCombate.registrar("[CRÍTICO] " + nombre + " asesta un golpe tremendo a "
                    + objetivo.getNombre() + " por " + danio + " de daño!");
        } else {
            RegistroCombate.registrar(nombre + " ataca a " + objetivo.getNombre() + " por " + danio + " de daño.");
        }
        objetivo.recibirDanio(danio);
    }

    public double getCriticoBonus() { return 0.0; }

    public boolean ejecutarHabilidad(int indiceHabilidad, List<Combatiente> objetivos, Batalla batalla) {
        List<Habilidad> habs = getHabilidades();
        if (indiceHabilidad < 0 || indiceHabilidad >= habs.size()) return false;
        return habs.get(indiceHabilidad).ejecutar(this, objetivos, batalla);
    }

    // ─── Maná y Salud ────────────────────────────────────────────────────────────

    public boolean gastarMana(int costo) {
        if (this.mana >= costo) {
            this.mana -= costo;
            RegistroCombate.registrar(nombre + " consumió " + costo
                    + " de maná/ki (" + mana + "/" + manaMaximo + ").");
            return true;
        }
        RegistroCombate.registrar(nombre + " no tiene suficiente maná/ki ("
                + mana + "/" + costo + " requerido).");
        return false;
    }

    public void curarVida(int cantidad) {
        this.vida = Math.min(vida + cantidad, vidaMaxima);
    }

    public void recibirDanioDirecto(int cantidad) {
        this.vida = Math.max(1, this.vida - cantidad);
    }

    public void revivir(int hp) {
        this.vida = Math.min(Math.max(1, hp), this.vidaMaxima);
        this.aturdido = false;
        this.envenenado = false;
        this.defendiendo = false;
        RegistroCombate.registrar("¡" + nombre + " ha resucitado de entre los muertos con " + vida + " HP!");
    }

    public void recuperarMana(int cantidad) {
        this.mana = Math.min(mana + cantidad, manaMaximo);
    }

    // ─── Subida de nivel y Atributos ─────────────────────────────────────────────

    public void ganarExperiencia(int xp) {
        this.experiencia += xp;
        RegistroCombate.registrar(nombre + " ganó " + xp + " XP. (" + experiencia + "/" + experienciaSiguienteNivel + ")");

        while (this.experiencia >= this.experienciaSiguienteNivel) {
            subirDeNivel();
        }
    }

    private void subirDeNivel() {
        this.experiencia -= this.experienciaSiguienteNivel;
        this.nivel++;
        this.experienciaSiguienteNivel = (int) (this.experienciaSiguienteNivel * 1.6);
        this.puntosAtributoDisponibles += 3;
        this.eleccionHabilidadPendiente = true;

        // Bonificaciones naturales al subir de nivel
        this.vidaMaxima += 10;
        this.vida = this.vidaMaxima;
        this.manaMaximo += 8;
        this.mana = this.manaMaximo;
        this.ataqueBase += 2;
        this.defensaBase += 1;

        RegistroCombate.registrar("\n==========================================");
        RegistroCombate.registrar("¡" + nombre + " HA SUBIDO AL NIVEL " + nivel + "!");
        RegistroCombate.registrar("+3 Puntos de Atributo disponibles y Nueva Habilidad a desbloquear.");
        RegistroCombate.registrar("==========================================\n");
    }

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
        manaMaximo += 12; mana += 12;
        RegistroCombate.registrar(nombre + " aumentó su Maná Máximo a " + manaMaximo);
        return true;
    }

    public boolean asignarPuntoAtaque() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        ataqueBase += 3;
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

    public boolean asignarPuntoVelocidad() {
        if (puntosAtributoDisponibles <= 0) return false;
        puntosAtributoDisponibles--;
        velocidad += 2;
        RegistroCombate.registrar(nombre + " aumentó su Velocidad a " + velocidad);
        return true;
    }

    public void aumentarAtaqueBase(int cantidad) { this.ataqueBase += cantidad; }
    public void aumentarDefensaBase(int cantidad) { this.defensaBase += cantidad; }

    // ─── Equipamiento ────────────────────────────────────────────────────────────

    public Arma equiparArma(Arma nuevaArma) {
        Arma anterior = this.arma;
        this.arma = nuevaArma;
        RegistroCombate.registrar(nombre + " equipó " + nuevaArma.getNombre()
                + " (+" + nuevaArma.getBonusAtaque() + " ATQ).");
        return anterior;
    }

    public Arma desequiparArma() {
        Arma anterior = this.arma;
        this.arma = null;
        if (anterior != null) {
            RegistroCombate.registrar(nombre + " desequipó " + anterior.getNombre() + ".");
        }
        return anterior;
    }

    public Armadura equiparArmadura(Armadura nuevaArmadura) {
        Armadura anterior = this.armadura;
        this.armadura = nuevaArmadura;
        RegistroCombate.registrar(nombre + " equipó " + nuevaArmadura.getNombre()
                + " (+" + nuevaArmadura.getBonusDefensa() + " DEF).");
        return anterior;
    }

    public Armadura desequiparArmadura() {
        Armadura anterior = this.armadura;
        this.armadura = null;
        if (anterior != null) {
            RegistroCombate.registrar(nombre + " desequipó " + anterior.getNombre() + ".");
        }
        return anterior;
    }

    @Override
    public String toString() {
        return nombre + " (Nv." + nivel + " " + getTipo() + ") HP: " + vida + "/" + vidaMaxima
                + " | MP: " + mana + "/" + manaMaximo + " | ATQ: " + getAtaque() + " | DEF: " + getDefensa()
                + " | VEL: " + velocidad + (defendiendo ? " [DEF]" : "") + (envenenado ? " [VEN]" : "");
    }
}
