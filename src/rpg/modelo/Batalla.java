package rpg.modelo;

import rpg.dto.EntidadDTO;
import rpg.dto.EstadoBatallaDTO;
import rpg.dto.ItemDTO;
import rpg.dto.PersonajeDTO;
import rpg.dto.RecompensaDTO;

import java.io.Serializable;
import java.util.*;

/**
 * Modelo de Batalla por turnos.
 *
 * El ciclo de batalla se ejecuta en un bucle que verifica la continuidad del combate
 * llamando a {@link #verificarFin()} al final de cada turno.
 *
 * El orden de actuación se determina con {@link #calcularOrdenTurnos()}, basado en
 * las estadísticas de velocidad de cada {@link Combatiente}.
 *
 * Durante su turno, invocado mediante {@link #procesarTurno(Combatiente)}, cada
 * combatiente puede: atacar, defender, usar habilidad o usar ítem.
 *
 * Al finalizar una batalla victoriosa, {@link #calcularRecompensa()} retorna un
 * {@link RecompensaDTO} con toda la información sobre la recompensa obtenida.
 */
public class Batalla implements Serializable {

    private final String lugar;
    private final Party party;
    private final List<Enemigo> enemigos;
    private int ronda;

    /**
     * Cola de iniciativa para la ronda actual.
     * Héroes: índice en party.getMiembros() (0..N-1)
     * Enemigos: índice + OFFSET_ENEMIGO
     */
    private List<Integer> colaIniciativa;
    private int indiceTurnoActual;

    private boolean batallaTerminada;
    private boolean victoria;

    private static final int OFFSET_ENEMIGO = 1000;

    // ─── Constructor ─────────────────────────────────────────────────────────────

    public Batalla(String lugar, Party party) {
        this.lugar = lugar;
        this.party = party;
        this.enemigos = new ArrayList<>();
        this.ronda = 1;
        this.colaIniciativa = new ArrayList<>();
        this.indiceTurnoActual = 0;
        this.batallaTerminada = false;
        this.victoria = false;
    }

    // ─── Factory ─────────────────────────────────────────────────────────────────

    /**
     * Crea una batalla a partir de un nodo del mapa, usando polimorfismo
     * para determinar los enemigos sin condicionales de tipo.
     */
    public static Batalla crearParaNodo(Nodo nodo, Party party, int piso) {
        Batalla b = new Batalla(nodo.getNombre(), party);

        if (nodo instanceof NodoJefe) {
            b.agregarEnemigo(new Jefe(piso > 1 ? "Rey Demonio" : "Dragón Carmesí"));
        } else if (nodo instanceof NodoElite) {
            b.agregarEnemigo(new Orco("Orco Berserker"));
            b.agregarEnemigo(new Esqueleto("Esqueleto Guardián"));
        } else {
            // NodoCombate estándar
            b.agregarEnemigo(new Goblin("Goblin Pícaro"));
            b.agregarEnemigo(new Chaman("Chamán del Pantano"));
        }

        RegistroCombate.registrar("\n==========================================");
        RegistroCombate.registrar("⚔️ ¡Combate iniciado en: " + b.getLugar() + "!");
        RegistroCombate.registrar("==========================================");
        b.calcularOrdenTurnos();
        return b;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public String getLugar()             { return lugar; }
    public Party getParty()              { return party; }
    public List<Enemigo> getEnemigos()   { return enemigos; }
    public int getRonda()                { return ronda; }
    public boolean isVictoria() {
        if (!batallaTerminada) verificarFin();
        return victoria;
    }

    public void agregarEnemigo(Enemigo e) { enemigos.add(e); }

    // ─── Verificación de estado ───────────────────────────────────────────────────

    /**
     * Verifica si la batalla ha terminado.
     * El combate continúa estrictamente mientras ambos bandos tengan participantes vivos.
     *
     * @return {@code true} si la batalla finalizó (victoria o derrota)
     */
    public boolean verificarFin() {
        if (!batallaTerminada) {
            if (enemigosDerrotados()) {
                finalizarBatalla(true);
            } else if (party.estaDerrotada()) {
                finalizarBatalla(false);
            }
        }
        return batallaTerminada;
    }

    /** @deprecated Usar {@link #verificarFin()} */
    @Deprecated
    public boolean esFinDeBatalla() { return verificarFin(); }

    public boolean enemigosDerrotados() {
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) return false;
        }
        return true;
    }

    public Enemigo obtenerPrimerEnemigoVivo() {
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) return e;
        }
        return null;
    }

    // ─── Orden de Turnos ─────────────────────────────────────────────────────────

    /**
     * Calcula (o recalcula) el orden de turnos para la ronda actual,
     * ordenando a todos los combatientes vivos de mayor a menor velocidad.
     */
    public void calcularOrdenTurnos() {
        List<int[]> participantes = new ArrayList<>();

        List<Personaje> miembros = party.getMiembros();
        for (int i = 0; i < miembros.size(); i++) {
            if (miembros.get(i).estaVivo()) {
                participantes.add(new int[]{miembros.get(i).getVelocidad(), i});
            }
        }
        for (int i = 0; i < enemigos.size(); i++) {
            if (enemigos.get(i).estaVivo()) {
                participantes.add(new int[]{enemigos.get(i).getVelocidad(), i + OFFSET_ENEMIGO});
            }
        }

        // Ordenar de mayor a menor velocidad (desempate aleatorio)
        participantes.sort((a, b) -> {
            if (b[0] != a[0]) return Integer.compare(b[0], a[0]);
            return Math.random() < 0.5 ? -1 : 1;
        });

        colaIniciativa = new ArrayList<>();
        for (int[] p : participantes) colaIniciativa.add(p[1]);
        indiceTurnoActual = 0;

        RegistroCombate.registrar("\n==========================================");
        RegistroCombate.registrar("               RONDA " + ronda);
        StringBuilder orden = new StringBuilder("📋 Orden de iniciativa: ");
        for (int id : colaIniciativa) {
            if (id < OFFSET_ENEMIGO) orden.append(party.getMiembros().get(id).getNombre()).append(" ");
            else orden.append(enemigos.get(id - OFFSET_ENEMIGO).getNombre()).append(" ");
        }
        RegistroCombate.registrar(orden.toString());
        RegistroCombate.registrar("==========================================");
    }

    /**
     * Procesa el turno del actor indicado.
     * Si el actor es un enemigo, ejecuta su IA automáticamente.
     * Si es un héroe, aguarda la acción del jugador (ver métodos ejecutar*).
     *
     * @param actor el combatiente que debe actuar en este turno
     */
    public void procesarTurno(Combatiente actor) {
        if (actor instanceof Enemigo enemigo) {
            if (enemigo.estaVivo() && !party.estaDerrotada()) {
                enemigo.resetearDefensaTemporal();
                RegistroCombate.registrar("\n[Turno de " + enemigo.getNombre() + "]");
                enemigo.ejecutarTurno(party, enemigos);
                if (party.estaDerrotada()) finalizarBatalla(false);
                else if (enemigosDerrotados()) finalizarBatalla(true);
            }
        }
        // Si es Personaje, el controlador delegará la acción al jugador via los métodos ejecutar*
    }

    // ─── Avance interno de la cola ────────────────────────────────────────────────

    public int getIndiceHeroeTurno() {
        avanzarHastaTurnoValido();
        if (colaIniciativa.isEmpty()) return 0;
        int ficha = colaIniciativa.get(indiceTurnoActual);
        return ficha < OFFSET_ENEMIGO ? ficha : -1;
    }

    public Personaje obtenerHeroeTurnoActual() {
        int idx = getIndiceHeroeTurno();
        if (idx >= 0 && idx < party.getMiembros().size()) {
            return party.getMiembros().get(idx);
        }
        return null;
    }

    public List<String> getColaIniciativaNombres() {
        List<String> nombres = new ArrayList<>();
        for (int id : colaIniciativa) {
            if (id < OFFSET_ENEMIGO) {
                Personaje p = party.getMiembros().get(id);
                nombres.add(p.getNombre() + "|heroe|" + p.getVelocidad());
            } else {
                Enemigo e = enemigos.get(id - OFFSET_ENEMIGO);
                nombres.add(e.getNombre() + "|enemigo|" + e.getVelocidad());
            }
        }
        return nombres;
    }

    public int getPosicionActualEnCola() { return indiceTurnoActual; }

    /**
     * Avanza {@code indiceTurnoActual} hasta encontrar un héroe vivo que requiera
     * input del jugador. Si el siguiente en la cola es un enemigo, lo procesa
     * automáticamente (llama a {@link #procesarTurno(Combatiente)}).
     */
    private void avanzarHastaTurnoValido() {
        while (!batallaTerminada && !colaIniciativa.isEmpty()) {
            if (indiceTurnoActual >= colaIniciativa.size()) {
                ronda++;
                for (Personaje p : party.getMiembros()) p.resetearDefensaTemporal();
                for (Enemigo e : enemigos) e.resetearDefensaTemporal();
                calcularOrdenTurnos();
                continue;
            }

            int ficha = colaIniciativa.get(indiceTurnoActual);

            if (ficha < OFFSET_ENEMIGO) {
                Personaje heroe = party.getMiembros().get(ficha);
                if (!heroe.estaVivo()) { indiceTurnoActual++; continue; }
                boolean turnoConsumido = aplicarEfectosEstadoAlInicio(heroe);
                if (turnoConsumido) { indiceTurnoActual++; continue; }
                return; // héroe listo para actuar
            } else {
                int idxEnemigo = ficha - OFFSET_ENEMIGO;
                Enemigo enemigo = enemigos.get(idxEnemigo);
                procesarTurno(enemigo);
                indiceTurnoActual++;
            }
        }
    }

    private boolean aplicarEfectosEstadoAlInicio(Personaje h) {
        if (!h.estaVivo()) return true;

        if (h.isEnvenenado()) {
            int danioVeneno = Math.max(5, (int) (h.getVidaMaxima() * 0.10));
            h.curarVida(-danioVeneno);
            RegistroCombate.registrar("☠️ El veneno corroe a " + h.getNombre()
                    + " restando " + danioVeneno + " HP. (Vida: " + h.getVida() + "/" + h.getVidaMaxima() + ")");
            if (!h.estaVivo()) {
                RegistroCombate.registrar("☠ ¡" + h.getNombre() + " sucumbió ante el veneno mortal!");
                if (enemigosDerrotados()) finalizarBatalla(true);
                else if (party.estaDerrotada()) finalizarBatalla(false);
                return true;
            }
        }

        if (h.isAturdido()) {
            RegistroCombate.registrar("💫 ¡" + h.getNombre() + " está ATURDIDO y pierde su turno!");
            h.setAturdido(false);
            return true;
        }

        return false;
    }

    // ─── Acciones del jugador ─────────────────────────────────────────────────────

    public void ejecutarAtaqueHeroe(int indiceHeroe, int indiceObjetivo) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        Enemigo objetivo = obtenerEnemigoValido(indiceObjetivo);
        if (objetivo == null) return;

        heroe.resetearDefensaTemporal();
        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");
        heroe.atacar(objetivo);
        postAccionHeroe();
    }

    /**
     * Ejecuta la habilidad del héroe construyendo la lista de objetivos adecuada.
     * Para habilidades en área, se pasa toda la lista de enemigos vivos (o aliados vivos).
     */
    public void ejecutarHabilidadHeroe(int indiceHeroe, int indiceHabilidad, int indiceObjetivo) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        heroe.resetearDefensaTemporal();
        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");

        // Construir la lista de objetivos según el tipo de habilidad
        List<Combatiente> objetivos = construirObjetivos(heroe, indiceHabilidad, indiceObjetivo);

        boolean exito = heroe.ejecutarHabilidad(indiceHabilidad, objetivos, this);
        if (!exito) return;

        postAccionHeroe();
    }

    /**
     * Construye la lista de {@link Combatiente}s que serán objetivo de la habilidad.
     * Habilidades de área reciben todos los combatientes del bando contrario (o aliado).
     */
    private List<Combatiente> construirObjetivos(Personaje heroe, int indiceHabilidad, int indiceObjetivo) {
        List<Habilidad> habs = heroe.getHabilidades();
        if (habs == null || indiceHabilidad < 0 || indiceHabilidad >= habs.size()) {
            return Collections.emptyList();
        }

        String nombreHab = habs.get(indiceHabilidad).getNombre().toLowerCase();
        boolean esArea = nombreHab.contains("área") || nombreHab.contains("area") || nombreHab.contains("todos");
        boolean esCuracion = heroe instanceof Curador;

        if (esArea && !esCuracion) {
            // Área ofensiva → todos los enemigos vivos
            List<Combatiente> lista = new ArrayList<>();
            for (Enemigo e : enemigos) { if (e.estaVivo()) lista.add(e); }
            return lista;
        } else if (esArea && esCuracion) {
            // Área curativa → todos los aliados vivos
            List<Combatiente> lista = new ArrayList<>();
            for (Personaje p : party.getMiembros()) { if (p.estaVivo()) lista.add(p); }
            return lista;
        } else if (esCuracion) {
            // Curación mono-objetivo → aliado
            List<Combatiente> lista = new ArrayList<>();
            Personaje aliado = obtenerAliadoValido(indiceObjetivo);
            if (aliado != null) lista.add(aliado);
            return lista;
        } else {
            // Ataque mono-objetivo → enemigo
            List<Combatiente> lista = new ArrayList<>();
            Enemigo objetivo = obtenerEnemigoValido(indiceObjetivo);
            if (objetivo != null) lista.add(objetivo);
            return lista;
        }
    }

    public void usarItemHeroe(int indiceHeroe, int indiceItem) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        List<Item> items = party.getInventario().getItems();
        if (indiceItem < 0 || indiceItem >= items.size()) {
            RegistroCombate.registrar("Selección de ítem inválida.");
            return;
        }
        Item item = items.get(indiceItem);
        if (!(item instanceof Consumible)) {
            RegistroCombate.registrar("El ítem " + item.getNombre() + " no es consumible en batalla.");
            return;
        }

        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");
        boolean exito = party.getInventario().consumirItem((Consumible) item, heroe);
        if (!exito) return;

        postAccionHeroe();
    }

    public void defenderHeroe(int indiceHeroe) {
        if (verificarFin()) return;
        avanzarHastaTurnoValido();
        if (verificarFin()) return;

        Personaje heroe = obtenerHeroeValido(indiceHeroe);
        if (heroe == null) return;

        RegistroCombate.registrar("\n[Turno de " + heroe.getNombre() + "]");
        heroe.defender();
        postAccionHeroe();
    }

    private void postAccionHeroe() {
        if (enemigosDerrotados()) { finalizarBatalla(true); return; }
        if (party.estaDerrotada()) { finalizarBatalla(false); return; }
        indiceTurnoActual++;
    }

    private void finalizarBatalla(boolean ganoParty) {
        this.batallaTerminada = true;
        this.victoria = ganoParty;

        RegistroCombate.registrar("\n==========================================");
        if (victoria) {
            RegistroCombate.registrar("🏆 ¡Victoria gloriosa! Los enemigos han sido derrotados en " + lugar + ".");
        } else {
            RegistroCombate.registrar("☠ ¡La Party ha sido aniquilada en " + lugar + "! Fin de la partida (Permadeath).");
        }
        RegistroCombate.registrar("==========================================");
    }

    // ─── Recompensa ───────────────────────────────────────────────────────────────

    /**
     * Calcula la recompensa de la batalla victoriosa.
     * Reparte la experiencia equitativamente entre los sobrevivientes
     * y retorna un {@link RecompensaDTO} con toda la información.
     *
     * @return DTO con XP, oro e ítems obtenidos
     */
    public RecompensaDTO calcularRecompensa() {
        int xpTotal = 0;
        int oro = 0;
        List<String> itemsObtenidos = new ArrayList<>();
        List<String> supervivientesNombres = new ArrayList<>();

        for (Enemigo e : enemigos) {
            xpTotal += e.getExperienciaOtorgada();
            oro += e.getExperienciaOtorgada() / 2; // oro proporcional a la XP
        }

        // Determinar supervivientes y repartir XP
        int supervivientes = 0;
        for (Personaje p : party.getMiembros()) {
            if (p.estaVivo()) { supervivientes++; supervivientesNombres.add(p.getNombre()); }
        }

        int xpPorHeroe = supervivientes > 0 ? xpTotal / supervivientes : 0;

        RegistroCombate.registrar("\n--- RECOMPENSA DE BATALLA ---");
        RegistroCombate.registrar("Se reparten " + xpTotal + " XP en total ("
                + xpPorHeroe + " XP por héroe superviviente).");

        for (Personaje p : party.getMiembros()) {
            if (p.estaVivo()) p.ganarExperiencia(xpPorHeroe);
        }

        // Ítems aleatorios (lógica base, extensible)
        Random rnd = new Random();
        if (rnd.nextInt(100) < 30) {
            Item item = rnd.nextBoolean()
                    ? new PocionVida("Poción de Vida", "Restaura 80 HP", 80)
                    : new PocionMana("Poción de Maná", "Restaura 50 MP", 50);
            party.getInventario().agregarItem(item);
            itemsObtenidos.add(item.getNombre());
        }

        return new RecompensaDTO(xpTotal, xpPorHeroe, oro, itemsObtenidos, supervivientesNombres);
    }

    // ─── Helpers de validación ────────────────────────────────────────────────────

    private Personaje obtenerHeroeValido(int indice) {
        List<Personaje> miembros = party.getMiembros();
        int turnoIdx = getIndiceHeroeTurno();
        if (indice >= 0 && indice < miembros.size()) {
            Personaje p = miembros.get(indice);
            if (p.estaVivo() && indice == turnoIdx) return p;
        }
        Personaje heroeTurno = (turnoIdx >= 0 && turnoIdx < miembros.size())
                ? miembros.get(turnoIdx) : null;
        if (heroeTurno != null && heroeTurno.estaVivo()) {
            if (indice != turnoIdx) {
                RegistroCombate.registrar("⚠️ No es el turno de ese héroe. Actuando con " + heroeTurno.getNombre() + ".");
            }
            return heroeTurno;
        }
        return null;
    }

    private Enemigo obtenerEnemigoValido(int indice) {
        if (indice >= 0 && indice < enemigos.size()) {
            Enemigo e = enemigos.get(indice);
            if (e.estaVivo()) return e;
        }
        return obtenerPrimerEnemigoVivo();
    }

    private Personaje obtenerAliadoValido(int indice) {
        List<Personaje> miembros = party.getMiembros();
        if (indice >= 0 && indice < miembros.size()) return miembros.get(indice);
        return null;
    }

    // ─── DTO ──────────────────────────────────────────────────────────────────────

    public EstadoBatallaDTO crearEstadoDTO() {
        if (!batallaTerminada) avanzarHastaTurnoValido();

        List<PersonajeDTO> heroesDTO = new ArrayList<>();
        for (Personaje p : party.getMiembros()) {
            String armaNom = (p.getArma() != null) ? p.getArma().getNombre() : "Ninguna";
            String armaduraNom = (p.getArmadura() != null) ? p.getArmadura().getNombre() : "Ninguna";
            heroesDTO.add(new PersonajeDTO(
                    p.getNombre(), p.getTipo(), p.getVida(), p.getVidaMaxima(),
                    p.getMana(), p.getManaMaximo(), p.getNivel(), p.getAtaque(),
                    p.getDefensa(), p.getVelocidad(), p.estaVivo(), p.isDefendiendo(),
                    p.getRutaSprite(), p.getExperiencia(), p.getExperienciaSiguienteNivel(),
                    armaNom, armaduraNom, p.getPuntosAtributoDisponibles(), p.getNombresHabilidades()
            ));
        }

        List<EntidadDTO> enemigosDTO = new ArrayList<>();
        for (Enemigo e : enemigos) {
            enemigosDTO.add(new EntidadDTO(
                    e.getNombre(), e.getTipo(), e.getVida(), e.getVidaMaxima(),
                    0, 0, 1, e.getAtaque(), e.getDefensa(), e.getVelocidad(),
                    e.estaVivo(), e.isDefendiendo(), e.getRutaSprite(), false
            ));
        }

        List<ItemDTO> inventarioDTO = new ArrayList<>();
        List<Item> items = party.getInventario().getItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            inventarioDTO.add(new ItemDTO(i, item.getNombre(), item.getDescripcion(), item instanceof Consumible));
        }

        String turnoStr;
        int indiceHeroeActivo;
        if (batallaTerminada) {
            turnoStr = victoria ? "¡VICTORIA!" : "¡DERROTA!";
            indiceHeroeActivo = 0;
        } else {
            int idx = getIndiceHeroeTurno();
            if (idx >= 0) {
                Personaje activo = party.getMiembros().get(idx);
                turnoStr = "Turno de: " + activo.getNombre() + " (VEL " + activo.getVelocidad() + ")";
                indiceHeroeActivo = idx;
            } else {
                turnoStr = "Procesando turno de enemigos...";
                indiceHeroeActivo = 0;
            }
        }

        String mensajeFin = "";
        if (batallaTerminada) {
            mensajeFin = victoria
                    ? "🏆 ¡VICTORIA! Todos los enemigos han sido derrotados."
                    : "☠ ¡DERROTA! Toda la party ha sido aniquilada (Permadeath).";
        }

        return new EstadoBatallaDTO(
                heroesDTO, enemigosDTO, turnoStr, indiceHeroeActivo,
                inventarioDTO, RegistroCombate.getHistorial(),
                batallaTerminada, victoria, mensajeFin, lugar, ronda,
                getColaIniciativaNombres(), indiceTurnoActual
        );
    }
}
