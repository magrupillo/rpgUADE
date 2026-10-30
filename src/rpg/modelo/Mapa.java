package rpg.modelo;

import java.io.Serializable;
import java.util.*;

/**
 * Coordina el avance del jugador en la torre mediante un sistema de caminos ramificados
 * generados proceduralmente al inicio de cada run (estilo DAG como Inscryption / Slay the Spire).
 *
 * Cada piso cuenta con múltiples capas de profundidad (niveles intermedios) con entre 3 y 4
 * nodos por zona, permitiendo bifurcaciones y convergencias para que el jugador pueda alternar
 * de camino a lo largo de su travesía.
 *
 * Al avanzar a un nodo, los demás caminos de la misma columna quedan permanentemente bloqueados.
 */
public class Mapa implements Serializable {

    private int piso;
    private final Map<Integer, Nodo> nodos;
    private int idNodoActual;
    private int totalColumnas;

    public Mapa(int piso) {
        this.piso = piso;
        this.nodos = new LinkedHashMap<>();
        generarPiso(piso);
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public int getPiso() { return piso; }

    public int getIdNodoActual() { return idNodoActual; }

    public int getTotalColumnas() { return totalColumnas; }

    public Nodo getNodoActual() { return nodos.get(idNodoActual); }

    public Nodo getNodo(int id) { return nodos.get(id); }

    public List<Nodo> getTodosLosNodos() { return new ArrayList<>(nodos.values()); }

    /**
     * Devuelve los nodos destino disponibles (desbloqueados y no completados)
     * desde la posición actual del jugador.
     */
    public List<Nodo> getNodosSiguientesDisponibles() {
        List<Nodo> disponibles = new ArrayList<>();
        Nodo actual = getNodoActual();
        if (actual == null) {
            for (Nodo n : nodos.values()) {
                if (n.getColumna() == 0 && n.isDisponible()) disponibles.add(n);
            }
        } else {
            for (int idSig : actual.getConexiones()) {
                Nodo sig = nodos.get(idSig);
                if (sig != null && sig.isDisponible() && !sig.isCompletado()) {
                    disponibles.add(sig);
                }
            }
        }
        return disponibles;
    }

    /**
     * Avanza al nodo destino indicado y bloquea permanentemente los nodos
     * de la misma columna (las rutas alternativas de esa bifurcación).
     *
     * @return {@code true} si el avance fue exitoso
     */
    public boolean avanzarANodo(int idNodoDestino) {
        Nodo destino = nodos.get(idNodoDestino);
        if (destino == null || !destino.isDisponible() || destino.isCompletado()) return false;

        // Bloquear permanentemente los nodos alternativos de la misma columna
        for (Nodo n : nodos.values()) {
            if (n.getColumna() == destino.getColumna() && n.getId() != idNodoDestino) {
                n.setDisponible(false);
            }
        }

        this.idNodoActual = idNodoDestino;
        RegistroCombate.registrar("\n🗺️ Avanzando hacia: " + destino.getNombre()
                + " (" + destino.getDescripcion() + ")...");
        return true;
    }

    /**
     * Marca el nodo actual como completado y desbloquea sus conexiones directas
     * en la siguiente columna, permitiendo al jugador elegir entre las opciones conectadas.
     */
    public void completarNodoActual() {
        Nodo actual = getNodoActual();
        if (actual != null) {
            actual.setCompletado(true);
            RegistroCombate.registrar("✔️ Sala completada: " + actual.getNombre());

            for (int idConectado : actual.getConexiones()) {
                Nodo sig = nodos.get(idConectado);
                if (sig != null && !sig.isCompletado()) {
                    sig.setDisponible(true);
                }
            }
        }
    }

    /** @return {@code true} si el nodo de jefe del piso actual fue completado. */
    public boolean esJefePisoCompletado() {
        for (Nodo n : nodos.values()) {
            if (n instanceof NodoJefe && n.isCompletado()) return true;
        }
        return false;
    }

    /** Avanza al siguiente piso generando un nuevo mapa procedimental con nuevas rutas. */
    public void siguientePiso() {
        this.piso++;
        nodos.clear();
        generarPiso(piso);
        RegistroCombate.registrar("\n==========================================");
        RegistroCombate.registrar("🏰 ¡ASCENDIENDO AL PISO " + piso + " DE LA TORRE!");
        RegistroCombate.registrar("==========================================");
    }

    // ─── Generación Procedimental Estilo DAG ───────────────────────────────────────

    /**
     * Genera un grafo acíclico dirigido (DAG) de 7 columnas/niveles con entre 3 y 4 nodos
     * por nivel intermedio. Cada run genera un mapa con topología y salas totalmente únicas.
     */
    private void generarPiso(int numeroPiso) {
        Random rnd = new Random();
        int idCounter = 0;
        this.totalColumnas = 7;

        // Estructura de columnas
        List<List<Nodo>> capas = new ArrayList<>();

        // ── Capa 0: Entrada de la Torre (1 nodo inicial) ─────────────────────────
        List<Nodo> capa0 = new ArrayList<>();
        Nodo entrada = new NodoCombate(idCounter++, 0, nombreCombate(rnd));
        entrada.setDisponible(true);
        nodos.put(entrada.getId(), entrada);
        capa0.add(entrada);
        capas.add(capa0);

        // ── Capas 1 a 4: Niveles Intermedios con 3 a 4 nodos cada uno ────────────
        for (int col = 1; col <= 4; col++) {
            List<Nodo> capaActual = new ArrayList<>();
            // 3 o 4 nodos por columna para dar variedad
            int cantidadNodos = (col == 3) ? 4 : (3 + rnd.nextInt(2));

            for (int lane = 0; lane < cantidadNodos; lane++) {
                Nodo nodo = crearNodoProcedural(idCounter++, col, lane, cantidadNodos, rnd, numeroPiso);
                nodos.put(nodo.getId(), nodo);
                capaActual.add(nodo);
            }
            capas.add(capaActual);
        }

        // ── Capa 5: Antesala del Jefe / Pre-Jefe (2 a 3 nodos con Fogata garantizada) ──
        List<Nodo> capaPreJefe = new ArrayList<>();
        int cantPreJefe = 2 + rnd.nextInt(2); // 2 o 3 nodos

        // Siempre una fogata de preparación
        Nodo fogataPreJefe = new NodoFogata(idCounter++, 5, nombreFogata(rnd));
        nodos.put(fogataPreJefe.getId(), fogataPreJefe);
        capaPreJefe.add(fogataPreJefe);

        // Nodos adicionales de élite o evento
        for (int lane = 1; lane < cantPreJefe; lane++) {
            Nodo nodo = (lane == 1)
                    ? new NodoElite(idCounter++, 5, nombreElite(rnd))
                    : new NodoEvento(idCounter++, 5, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
            nodos.put(nodo.getId(), nodo);
            capaPreJefe.add(nodo);
        }
        capas.add(capaPreJefe);

        // ── Capa 6: Jefe de Piso (1 nodo culminante) ─────────────────────────────
        List<Nodo> capaJefe = new ArrayList<>();
        String nombreJefe = (numeroPiso > 1) ? "Rey Demonio" : "Dragón Carmesí";
        Nodo jefe = new NodoJefe(idCounter++, 6, nombreJefe);
        nodos.put(jefe.getId(), jefe);
        capaJefe.add(jefe);
        capas.add(capaJefe);

        // ── Conexión entre capas (Ramificación y Convergencia de Caminos) ─────────
        for (int c = 0; c < capas.size() - 1; c++) {
            conectarCapas(capas.get(c), capas.get(c + 1), rnd);
        }

        this.idNodoActual = entrada.getId();
    }

    /**
     * Conecta los nodos de una capa con la siguiente garantizando:
     * - Convergencia y divergencia (caminos que se cruzan y permiten alternar).
     * - Que todos los nodos tengan al menos un camino de entrada y uno de salida.
     */
    private void conectarCapas(List<Nodo> capaOrigen, List<Nodo> capaDestino, Random rnd) {
        int m = capaOrigen.size();
        int n = capaDestino.size();

        for (int i = 0; i < m; i++) {
            Nodo origen = capaOrigen.get(i);
            // Determinar carril objetivo principal proporcional
            int carrilCentro = (m == 1) ? (n / 2) : Math.round((float) i * (n - 1) / (m - 1));

            // Siempre conecta con su carril objetivo principal
            origen.conectarCon(capaDestino.get(carrilCentro).getId());

            // Posibilidad de ramificarse a los carriles vecinos (convergencia/cruce)
            if (carrilCentro - 1 >= 0 && rnd.nextDouble() < 0.70) {
                origen.conectarCon(capaDestino.get(carrilCentro - 1).getId());
            }
            if (carrilCentro + 1 < n && rnd.nextDouble() < 0.70) {
                origen.conectarCon(capaDestino.get(carrilCentro + 1).getId());
            }
        }

        // Garantizar que NINGÚN nodo de la capa destino quede sin conexión entrante
        for (int j = 0; j < n; j++) {
            Nodo destino = capaDestino.get(j);
            boolean conectado = false;
            for (Nodo origen : capaOrigen) {
                if (origen.getConexiones().contains(destino.getId())) {
                    conectado = true;
                    break;
                }
            }
            if (!conectado) {
                // Conectar con el nodo de origen más cercano
                int origenCercano = (n == 1) ? 0 : Math.round((float) j * (m - 1) / (n - 1));
                capaOrigen.get(origenCercano).conectarCon(destino.getId());
            }
        }
    }

    private Nodo crearNodoProcedural(int id, int columna, int lane, int totalLanes, Random rnd, int piso) {
        // Distribución ponderada de tipos según la columna
        int roll = rnd.nextInt(100);

        if (columna == 1) {
            // Capa 1: Mayormente combates estándar y eventos iniciales
            if (roll < 60) return new NodoCombate(id, columna, nombreCombate(rnd));
            else return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
        } else if (columna == 2) {
            // Capa 2: Combates, eventos y posibles élites
            if (roll < 45) return new NodoCombate(id, columna, nombreCombate(rnd));
            else if (roll < 80) return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
            else return new NodoElite(id, columna, nombreElite(rnd));
        } else if (columna == 3) {
            // Capa 3 (Encrucijada): Alta variedad (Combate, Élite, Evento, Fogata)
            if (lane == 0) return new NodoFogata(id, columna, nombreFogata(rnd));
            if (roll < 40) return new NodoCombate(id, columna, nombreCombate(rnd));
            else if (roll < 75) return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
            else return new NodoElite(id, columna, nombreElite(rnd));
        } else {
            // Capa 4: Zona peligrosa pre-jefe (Élites, Combates difíciles, Eventos)
            if (roll < 40) return new NodoElite(id, columna, nombreElite(rnd));
            else if (roll < 75) return new NodoCombate(id, columna, nombreCombate(rnd));
            else return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
        }
    }

    // ─── Catálogo de nombres generados ───────────────────────────────────────────

    private static final String[] NOMBRES_COMBATE = {
        "Cavernas Bajas", "Guarida de Goblins", "Pasillo Oscuro", "Foso de los Condenados",
        "Corredor de Huesos", "Pantano Chamanico", "Gruta de las Sombras", "Paso Estrecho de Piedra",
        "Galería Maldita", "Cripta Profanada", "Túnel de las Raíces", "Cueva del Eco"
    };

    private static final String[] NOMBRES_ELITE = {
        "Bastión de Orcos", "Emboscada de Esqueletos", "Sala del Teniente Carmesí",
        "Cripta del Campeón Olvidado", "Guarnición del Caos", "Mausoleo del Terror",
        "Salón de la Guardia Sombría"
    };

    private static final String[] NOMBRES_EVENTO = {
        "El Brebaje Misterioso", "El Altar de Sangre", "La Fuente de las Hadas",
        "El Cofre Rúnico", "El Mercader de las Sombras", "La Biblioteca Perdida"
    };

    private static final String[] NOMBRES_FOGATA = {
        "Fogata del Refugio", "Hogar de los Aventureros", "Sala del Descanso",
        "Campamento en las Ruinas", "Santuario de Calma", "Rincón de la Llama Azul"
    };

    private String nombreCombate(Random rnd) {
        return NOMBRES_COMBATE[rnd.nextInt(NOMBRES_COMBATE.length)];
    }

    private String nombreElite(Random rnd) {
        return NOMBRES_ELITE[rnd.nextInt(NOMBRES_ELITE.length)];
    }

    private String nombreEvento(Random rnd) {
        return NOMBRES_EVENTO[rnd.nextInt(NOMBRES_EVENTO.length)];
    }

    private String nombreFogata(Random rnd) {
        return NOMBRES_FOGATA[rnd.nextInt(NOMBRES_FOGATA.length)];
    }
}
