package rpg.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Party implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Personaje> miembros;
    private Inventario inventario;
    private int oro;
    private final int MAX_MIEMBROS = 4;
    private final int MIN_MIEMBROS = 2;
    private final Random random = new Random();

    public Party() {
        this.miembros = new ArrayList<>();
        this.inventario = new Inventario();
        this.oro = 50; // Oro inicial
    }

    public boolean agregarMiembro(Personaje personaje) {
        if (miembros.size() < MAX_MIEMBROS) {
            miembros.add(personaje);
            RegistroCombate.registrar(personaje.getNombre() + " se unió al equipo.");
            return true;
        } else {
            RegistroCombate.registrar("El equipo ya está lleno (máximo " + MAX_MIEMBROS + " miembros).");
            return false;
        }
    }

    public boolean tieneMinimoMiembros() {
        return miembros.size() >= MIN_MIEMBROS;
    }

    public List<Personaje> getMiembros() {
        return miembros;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public int getOro() {
        return oro;
    }

    public void ganarOro(int cantidad) {
        if (cantidad > 0) {
            this.oro += cantidad;
            RegistroCombate.registrar("Se obtuvieron $" + cantidad + " de oro. (Total: $" + this.oro + ")");
        }
    }

    public boolean gastarOro(int cantidad) {
        if (cantidad <= oro) {
            this.oro -= cantidad;
            return true;
        }
        return false;
    }

    public boolean usarConsumible(Consumible consumible, Personaje objetivo) {
        return inventario.consumirItem(consumible, objetivo);
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

    public Personaje obtenerHeroeConMayorAmenaza() {
        // Prioriza a los héroes que hayan activado Provocar
        for (Personaje p : miembros) {
            if (p.estaVivo() && p.isProvocando()) {
                return p;
            }
        }
        // Si nadie provocó, retorna un héroe vivo al azar
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : miembros) {
            if (p.estaVivo()) vivos.add(p);
        }
        if (!vivos.isEmpty()) {
            return vivos.get(random.nextInt(vivos.size()));
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

    public void recuperarPorcentaje(double porcentaje) {
        for (Personaje p : miembros) {
            if (p.estaVivo()) {
                int curacionHp = (int) (p.getVidaMaxima() * porcentaje);
                int recupMp = (int) (p.getManaMaximo() * porcentaje);
                p.curarVida(curacionHp);
                p.recuperarMana(recupMp);
                p.setAturdido(false);
                p.setEnvenenado(false);
            }
        }
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
            RegistroCombate.registrar("\n--- RECOMPENSA DE BATALLA ---");
            RegistroCombate.registrar("Se reparten " + xpTotal + " XP en total (" + xpPorMiembro + " XP por héroe superviviente).");
            for (Personaje p : miembros) {
                if (p.estaVivo()) {
                    p.ganarExperiencia(xpPorMiembro);
                }
            }
        }
    }

    public boolean tienePuntosAtributosPendientes() {
        for (Personaje p : miembros) {
            if (p.getPuntosAtributoDisponibles() > 0 || p.tieneEleccionHabilidadPendiente()) {
                return true;
            }
        }
        return false;
    }

    public int getTotalPuntosPendientes() {
        int total = 0;
        for (Personaje p : miembros) {
            total += p.getPuntosAtributoDisponibles();
            if (p.tieneEleccionHabilidadPendiente()) total++;
        }
        return total;
    }

    public void mostrarEstado() {
        RegistroCombate.registrar("\n--- Estado del Equipo (Oro: $" + oro + ") ---");
        for (Personaje p : miembros) {
            RegistroCombate.registrar(p.toString());
        }
    }
}
