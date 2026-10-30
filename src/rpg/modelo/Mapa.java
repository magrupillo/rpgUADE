package rpg.modelo;

import java.io.Serializable;
import java.util.*;

/**
 * Coordina el avance del jugador en la torre mediante un grafo acíclico dirigido (DAG).
 * Cada piso cuenta con 7 capas con bifurcaciones y convergencias.
 */
public class Mapa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int piso;
    private final Map<Integer, Nodo> nodos;
    private int idNodoActual;
    private int totalColumnas;

    public Mapa(int piso) {
        this.piso = piso;
        this.nodos = new LinkedHashMap<>();
        generarPiso(piso);
    }

    public int getPiso() { return piso; }
    public int getIdNodoActual() { return idNodoActual; }
    public int getTotalColumnas() { return totalColumnas; }
    public Nodo getNodoActual() { return nodos.get(idNodoActual); }
    public Nodo getNodo(int id) { return nodos.get(id); }
    public List<Nodo> getTodosLosNodos() { return new ArrayList<>(nodos.values()); }

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

    public boolean avanzarANodo(int idNodoDestino) {
        Nodo destino = nodos.get(idNodoDestino);
        if (destino == null || !destino.isDisponible() || destino.isCompletado()) return false;

        // Bloquear permanentemente los caminos alternativos de la misma columna
        for (Nodo n : nodos.values()) {
            if (n.getColumna() == destino.getColumna() && n.getId() != idNodoDestino) {
                n.setDisponible(false);
            }
        }

        this.idNodoActual = idNodoDestino;
        RegistroCombate.registrar("\nAvanzando hacia: " + destino.getNombre()
                + " (" + destino.getDescripcion() + ")...");
        return true;
    }

    public void completarNodoActual() {
        Nodo actual = getNodoActual();
        if (actual != null) {
            actual.setCompletado(true);
            RegistroCombate.registrar("Completado: " + actual.getNombre());

            for (int idConectado : actual.getConexiones()) {
                Nodo sig = nodos.get(idConectado);
                if (sig != null && !sig.isCompletado()) {
                    sig.setDisponible(true);
                }
            }
        }
    }

    /**
     * Verifica si el jefe del piso actual fue derrotado sin comprobación instanceof.
     */
    public boolean esJefePisoCompletado() {
        for (Nodo n : nodos.values()) {
            if (n.esJefe() && n.isCompletado()) return true;
        }
        return false;
    }

    public boolean esFinDePiso() {
        return esJefePisoCompletado();
    }

    public boolean isJefePisoCompletado() {
        return esJefePisoCompletado();
    }

    public void siguientePiso() {
        this.piso++;
        nodos.clear();
        generarPiso(piso);
        RegistroCombate.registrar("\n==========================================");
        RegistroCombate.registrar("¡ASCENDIENDO AL PISO " + piso + " DE LA TORRE!");
        RegistroCombate.registrar("==========================================");
    }

    public void generarPiso(int numeroPiso) {
        Random rnd = new Random();
        int idCounter = 0;
        this.totalColumnas = 7;
        this.kioscoCreadoEnPiso = false;

        List<List<Nodo>> capas = new ArrayList<>();

        // Capa 0: Entrada (Combate inicial)
        List<Nodo> capa0 = new ArrayList<>();
        Nodo entrada = new NodoCombate(idCounter++, 0, nombreCombate(rnd));
        entrada.setDisponible(true);
        nodos.put(entrada.getId(), entrada);
        capa0.add(entrada);
        capas.add(capa0);

        // Capas 1 a 4: Niveles Intermedios (Combates, Eventos, Kiosco, Élites)
        for (int col = 1; col <= 4; col++) {
            List<Nodo> capaActual = new ArrayList<>();
            int cantidadNodos = (col == 3) ? 4 : (3 + rnd.nextInt(2));

            for (int lane = 0; lane < cantidadNodos; lane++) {
                Nodo nodo = crearNodoProcedural(idCounter++, col, lane, cantidadNodos, rnd, numeroPiso);
                nodos.put(nodo.getId(), nodo);
                capaActual.add(nodo);
            }
            capas.add(capaActual);
        }

        // Capa 5: Antesala del Jefe (Fogón Criollo garantizado + 1 Élite)
        List<Nodo> capaPreJefe = new ArrayList<>();
        Nodo fogonPreJefe = new NodoFogata(idCounter++, 5, nombreFogon(rnd));
        nodos.put(fogonPreJefe.getId(), fogonPreJefe);
        capaPreJefe.add(fogonPreJefe);

        Nodo elitePreJefe = new NodoElite(idCounter++, 5, nombreElite(rnd));
        nodos.put(elitePreJefe.getId(), elitePreJefe);
        capaPreJefe.add(elitePreJefe);

        Nodo eventoPreJefe = new NodoEvento(idCounter++, 5, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
        nodos.put(eventoPreJefe.getId(), eventoPreJefe);
        capaPreJefe.add(eventoPreJefe);

        capas.add(capaPreJefe);

        // Capa 6: Jefe de Piso
        List<Nodo> capaJefe = new ArrayList<>();
        String nombreJefe = switch (numeroPiso) {
            case 1 -> "Dr. Eggman (Científico Loco)";
            case 2 -> "Darth Vader (Lord Oscuro)";
            default -> "Bowser (Amo Supremo de la Torre)";
        };
        Nodo jefe = new NodoJefe(idCounter++, 6, nombreJefe);
        nodos.put(jefe.getId(), jefe);
        capaJefe.add(jefe);
        capas.add(capaJefe);

        // Conectar capas
        for (int c = 0; c < capas.size() - 1; c++) {
            conectarCapas(capas.get(c), capas.get(c + 1), rnd);
        }

        this.idNodoActual = entrada.getId();
    }

    private void conectarCapas(List<Nodo> capaOrigen, List<Nodo> capaDestino, Random rnd) {
        int m = capaOrigen.size();
        int n = capaDestino.size();

        for (int i = 0; i < m; i++) {
            Nodo origen = capaOrigen.get(i);
            int carrilCentro = (m == 1) ? (n / 2) : Math.round((float) i * (n - 1) / (m - 1));
            origen.conectarCon(capaDestino.get(carrilCentro).getId());

            if (carrilCentro - 1 >= 0 && rnd.nextDouble() < 0.70) {
                origen.conectarCon(capaDestino.get(carrilCentro - 1).getId());
            }
            if (carrilCentro + 1 < n && rnd.nextDouble() < 0.70) {
                origen.conectarCon(capaDestino.get(carrilCentro + 1).getId());
            }
        }

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
                int origenCercano = (n == 1) ? 0 : Math.round((float) j * (m - 1) / (n - 1));
                capaOrigen.get(origenCercano).conectarCon(destino.getId());
            }
        }
    }

    private boolean kioscoCreadoEnPiso = false;

    private Nodo crearNodoProcedural(int id, int columna, int lane, int totalLanes, Random rnd, int piso) {
        int roll = rnd.nextInt(100);

        if (columna == 1) {
            if (roll < 65) return new NodoCombate(id, columna, nombreCombate(rnd));
            else return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
        } else if (columna == 2) {
            // Exactamente un Kiosco por piso en la columna 2
            if (!kioscoCreadoEnPiso && lane == 0) {
                kioscoCreadoEnPiso = true;
                return new NodoKiosco(id, columna, "Kiosco de Don Tito");
            }
            if (roll < 45) return new NodoCombate(id, columna, nombreCombate(rnd));
            else if (roll < 80) return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
            else return new NodoElite(id, columna, nombreElite(rnd));
        } else if (columna == 3) {
            if (lane == 0) return new NodoFogata(id, columna, nombreFogon(rnd));
            if (roll < 45) return new NodoCombate(id, columna, nombreCombate(rnd));
            else if (roll < 75) return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
            else return new NodoElite(id, columna, nombreElite(rnd));
        } else {
            if (roll < 45) return new NodoElite(id, columna, nombreElite(rnd));
            else if (roll < 75) return new NodoCombate(id, columna, nombreCombate(rnd));
            else return new NodoEvento(id, columna, nombreEvento(rnd), EventoAleatorio.crearAleatorio(rnd));
        }
    }

    private static final String[] NOMBRES_COMBATE = {
        "Callejón Oscuro", "Esquina Peligrosa", "Baldío Abandonado", "Galpón Ferroviario",
        "Pasaje del Miedo", "Túnel Subterráneo", "Sótano de las Sombras", "Paso Estrecho"
    };

    private static final String[] NOMBRES_ELITE = {
        "Búnker de los Pesados", "Emboscada Brava", "Guarida del Cocodrilo", "Mausoleo de Skeletor"
    };

    private static final String[] NOMBRES_EVENTO = {
        "La Bebida Misteriosa", "El Pasillo Peligroso", "La Estatua del Gauchito Gil",
        "El Regalo de la Suegra", "El Puesto de Diarios"
    };

    private static final String[] NOMBRES_FOGON = {
        "Fogón de los Gauchos", "Fogón del Descanso", "Rincón de los Mates", "Campamento Tranquilo"
    };

    private String nombreCombate(Random rnd) { return NOMBRES_COMBATE[rnd.nextInt(NOMBRES_COMBATE.length)]; }
    private String nombreElite(Random rnd)   { return NOMBRES_ELITE[rnd.nextInt(NOMBRES_ELITE.length)]; }
    private String nombreEvento(Random rnd)  { return NOMBRES_EVENTO[rnd.nextInt(NOMBRES_EVENTO.length)]; }
    private String nombreFogon(Random rnd)   { return NOMBRES_FOGON[rnd.nextInt(NOMBRES_FOGON.length)]; }
}
