package rpg;

import java.util.ArrayList;
import java.util.List;

public class Party {
    private List<Personaje> miembros;
    private Inventario inventario;
    private final int MAX_MIEMBROS = 4;

    public Party() {
        this.miembros = new ArrayList<>();
        this.inventario = new Inventario();
    }

    public boolean agregarMiembro(Personaje personaje) {
        if (miembros.size() < MAX_MIEMBROS) {
            miembros.add(personaje);
            System.out.println(personaje.getNombre() + " se unió al equipo.");
            return true;
        } else {
            System.out.println("El equipo ya está lleno (máximo " + MAX_MIEMBROS + " miembros).");
            return false;
        }
    }

    public List<Personaje> getMiembros() {
        return miembros;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public boolean estaDerrotada() {
        for (Personaje p : miembros) {
            if (p.estaVivo()) {
                return false;
            }
        }
        return true;
    }

    public Personaje obtenerPrimerMiembroVivo() {
        for (Personaje p : miembros) {
            if (p.estaVivo()) {
                return p;
            }
        }
        return null;
    }

    public Personaje obtenerMiembroMasHerido() {
        Personaje masHerido = null;
        for (Personaje p : miembros) {
            if (p.estaVivo() && p.getVida() < p.getVidaMaxima()) {
                if (masHerido == null || p.getVida() < masHerido.getVida()) {
                    masHerido = p;
                }
            }
        }
        return masHerido;
    }

    public void repartirExperiencia(int xpTotal) {
        int miembrosVivos = 0;
        for (Personaje p : miembros) {
            if (p.estaVivo()) {
                miembrosVivos++;
            }
        }

        if (miembrosVivos > 0) {
            int xpPorMiembro = xpTotal / miembrosVivos;
            System.out.println("\n--- RECOMPENSA DE BATALLA ---");
            System.out.println("Se reparten " + xpTotal + " XP en total (" + xpPorMiembro + " XP por héroe superviviente).");
            for (Personaje p : miembros) {
                if (p.estaVivo()) {
                    p.ganarExperiencia(xpPorMiembro);
                }
            }
        }
    }

    public void mostrarEstado() {
        System.out.println("\n--- Estado del Equipo ---");
        for (Personaje p : miembros) {
            System.out.println(p);
        }
    }
}
