package rpg.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Item> items;
    private final int capacidadMaxima;

    public Inventario(int capacidadMaxima) {
        this.items = new ArrayList<>();
        this.capacidadMaxima = capacidadMaxima;
    }

    public Inventario() {
        this(20);
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean estaLleno() {
        return items.size() >= capacidadMaxima;
    }

    public boolean agregarItem(Item item) {
        if (estaLleno()) {
            RegistroCombate.registrar("El inventario está lleno (capacidad máxima: " + capacidadMaxima + ").");
            return false;
        }
        items.add(item);
        RegistroCombate.registrar("Se guardó en el inventario: " + item.getNombre());
        return true;
    }

    public boolean removerItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Consumible> getConsumibles() {
        List<Consumible> consumibles = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Consumible) {
                consumibles.add((Consumible) item);
            }
        }
        return consumibles;
    }

    public List<Arma> getArmas() {
        List<Arma> armas = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Arma) {
                armas.add((Arma) item);
            }
        }
        return armas;
    }

    public List<Armadura> getArmaduras() {
        List<Armadura> armaduras = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Armadura) {
                armaduras.add((Armadura) item);
            }
        }
        return armaduras;
    }

    public boolean consumirItem(Consumible consumible, Personaje objetivo) {
        if (items.contains(consumible)) {
            consumible.aplicar(objetivo);
            items.remove(consumible);
            return true;
        }
        RegistroCombate.registrar("El ítem no está en el inventario.");
        return false;
    }

    public boolean equiparArma(Arma arma, Personaje objetivo) {
        if (items.contains(arma)) {
            items.remove(arma);
            Arma anterior = objetivo.equiparArma(arma);
            if (anterior != null) {
                items.add(anterior);
                RegistroCombate.registrar("El arma anterior (" + anterior.getNombre() + ") volvió al inventario.");
            }
            return true;
        }
        RegistroCombate.registrar("El arma no está en el inventario.");
        return false;
    }

    public boolean desequiparArma(Personaje objetivo) {
        Arma desequipada = objetivo.desequiparArma();
        if (desequipada != null) {
            items.add(desequipada);
            RegistroCombate.registrar(desequipada.getNombre() + " se guardó en el inventario.");
            return true;
        }
        return false;
    }

    public boolean equiparArmadura(Armadura armadura, Personaje objetivo) {
        if (items.contains(armadura)) {
            items.remove(armadura);
            Armadura anterior = objetivo.equiparArmadura(armadura);
            if (anterior != null) {
                items.add(anterior);
                RegistroCombate.registrar("La armadura anterior (" + anterior.getNombre() + ") volvió al inventario.");
            }
            return true;
        }
        RegistroCombate.registrar("La armadura no está en el inventario.");
        return false;
    }

    public boolean desequiparArmadura(Personaje objetivo) {
        Armadura desequipada = objetivo.desequiparArmadura();
        if (desequipada != null) {
            items.add(desequipada);
            RegistroCombate.registrar(desequipada.getNombre() + " se guardó en el inventario.");
            return true;
        }
        return false;
    }

    public void listarItems() {
        RegistroCombate.registrar("--- INVENTARIO ---");
        if (items.isEmpty()) {
            RegistroCombate.registrar("(Vacío)");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            RegistroCombate.registrar((i + 1) + ". " + items.get(i));
        }
    }
}
