package rpg;

import java.util.ArrayList;
import java.util.List;

public class Batalla {
    private String lugar;
    private Party party;
    private List<Enemigo> enemigos;

    public Batalla(String lugar, Party party) {
        this.lugar = lugar;
        this.party = party;
        this.enemigos = new ArrayList<>();
    }

    public void agregarEnemigo(Enemigo enemigo) {
        this.enemigos.add(enemigo);
    }

    public void cargarBatalla(int nroBatalla) {
        enemigos.clear();
        if (nroBatalla == 1) {
            enemigos.add(new Goblin("Goblin Pícaro"));
            enemigos.add(new Goblin("Goblin Asesino"));
        } else if (nroBatalla == 2) {
            enemigos.add(new Orco("Orco Berserker"));
            enemigos.add(new Chaman("Chamán del Pantano"));
        } else {
            enemigos.add(new Jefe("Dragón Carmesí"));
        }
    }

    public void cargarBatalla1(int nroBatalla) {
        cargarBatalla(nroBatalla);
    }

    public boolean enemigosDerrotados() {
        for (int i = 0; i < enemigos.size(); i++) {
            if (enemigos.get(i).estaVivo()) {
                return false;
            }
        }
        return true;
    }

    public Enemigo obtenerPrimerEnemigoVivo() {
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            if (e.estaVivo()) {
                return e;
            }
        }
        return null;
    }

    private int obtenerMayorVelocidadParty() {
        int maxVel = 0;
        List<Personaje> miembros = party.getMiembros();
        for (int i = 0; i < miembros.size(); i++) {
            Personaje p = miembros.get(i);
            if (p.estaVivo() && p.getVelocidad() > maxVel) {
                maxVel = p.getVelocidad();
            }
        }
        return maxVel;
    }

    private int obtenerMayorVelocidadEnemigos() {
        int maxVel = 0;
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            if (e.estaVivo() && e.getVelocidad() > maxVel) {
                maxVel = e.getVelocidad();
            }
        }
        return maxVel;
    }

    public void ejecutarTurnoHeroe(Personaje heroe) {
        Enemigo objetivoEnemigo = obtenerPrimerEnemigoVivo();
        if (objetivoEnemigo == null) return;

        heroe.resetearDefensaTemporal();

        // 1. Usar Ítem si la vida es crítica (< 35%)
        if (heroe.getVida() < (heroe.getVidaMaxima() * 0.35)) {
            List<Consumible> pociones = party.getInventario().getConsumibles();
            Consumible pocionVida = null;
            for (int i = 0; i < pociones.size(); i++) {
                if (pociones.get(i) instanceof PocionVida) {
                    pocionVida = pociones.get(i);
                    break;
                }
            }
            if (pocionVida != null) {
                System.out.println(">> [ACCIÓN: USAR ÍTEM] " + heroe.getNombre() + " usa una poción.");
                party.getInventario().consumirItem(pocionVida, heroe);
                return;
            }
        }

        // 2. Curador usa Habilidad sobre aliado herido
        if (heroe instanceof Curador) {
            Personaje aliadoHerido = party.obtenerMiembroMasHerido();
            if (aliadoHerido != null && heroe.getMana() >= 20) {
                System.out.println(">> [ACCIÓN: USAR HABILIDAD] " + heroe.getNombre() + " cura a un aliado.");
                heroe.usarHabilidad(aliadoHerido);
                return;
            }
        }

        // 3. Defenderse si la vida es baja (< 25%) y no hay pociones
        if (heroe.getVida() < (heroe.getVidaMaxima() * 0.25)) {
            System.out.println(">> [ACCIÓN: DEFENDER] " + heroe.getNombre() + " se pone a la defensiva.");
            heroe.defender();
            return;
        }

        // 4. Usar Habilidad si tiene maná suficiente
        if (heroe.getMana() >= 15 && !(heroe instanceof Curador)) {
            System.out.println(">> [ACCIÓN: USAR HABILIDAD] " + heroe.getNombre() + " usa su habilidad especial.");
            heroe.usarHabilidad(objetivoEnemigo);
            return;
        }

        // 5. Atacar normalmente
        System.out.println(">> [ACCIÓN: ATACAR] " + heroe.getNombre() + " realiza un ataque regular.");
        heroe.atacar(objetivoEnemigo);
    }

    private void turnoParty() {
        System.out.println("\n--- FASE DE LA PARTY ---");
        List<Personaje> miembros = party.getMiembros();
        for (int i = 0; i < miembros.size(); i++) {
            Personaje heroe = miembros.get(i);
            if (heroe.estaVivo() && !enemigosDerrotados()) {
                System.out.println("\n[Turno de " + heroe.getNombre() + "]");
                ejecutarTurnoHeroe(heroe);
            }
        }
    }

    private void turnoEnemigos() {
        System.out.println("\n--- FASE DE LOS ENEMIGOS ---");
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo enemigo = enemigos.get(i);
            if (enemigo.estaVivo() && !party.estaDerrotada()) {
                enemigo.resetearDefensaTemporal();
                System.out.println("\n[Turno de " + enemigo.getNombre() + "]");
                enemigo.tomarDecision(party, enemigos);
            }
        }
    }

    public void iniciar() {
        System.out.println("\n==========================================");
        System.out.println("¡Comienza la batalla en: " + lugar + "!");
        System.out.println("==========================================");

        int xpTotalBatalla = 0;
        for (int i = 0; i < enemigos.size(); i++) {
            xpTotalBatalla += enemigos.get(i).getExperienciaOtorgada();
        }

        int ronda = 1;
        while (!party.estaDerrotada() && !enemigosDerrotados()) {
            System.out.println("\n------------------------------------------");
            System.out.println("               RONDA " + ronda);
            System.out.println("------------------------------------------");

            int velParty = obtenerMayorVelocidadParty();
            int velEnemigos = obtenerMayorVelocidadEnemigos();

            System.out.println("Velocidad máxima Party: " + velParty + " vs Velocidad máxima Enemigos: " + velEnemigos);

            if (velParty >= velEnemigos) {
                System.out.println(">> ¡La Party es más rápida y toma la iniciativa!");
                turnoParty();
                if (!enemigosDerrotados()) {
                    turnoEnemigos();
                }
            } else {
                System.out.println(">> ¡Los Enemigos son más rápidos y toman la iniciativa!");
                turnoEnemigos();
                if (!party.estaDerrotada()) {
                    turnoParty();
                }
            }

            party.mostrarEstado();
            ronda++;
        }

        System.out.println("\n==========================================");
        if (party.estaDerrotada()) {
            System.out.println("☠ ¡La Party ha sido aniquilada en " + lugar + "! Fin del juego.");
        } else {
            System.out.println("🏆 ¡Victoria gloriosa! Los enemigos han sido derrotados en " + lugar + ".");
            party.repartirExperiencia(xpTotalBatalla);
        }
        System.out.println("==========================================");
    }
}
