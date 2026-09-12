package rpg;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private List<Item> items;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public void agregarItem(Item item) {
        items.add(item);
        System.out.println("Se guardó en el inventario: " + item.getNombre());
    }

    public boolean removerItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Consumible> getConsumibles() {
        List<Consumible> consumibles = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof Consumible) {
                consumibles.add((Consumible) item);
            }
        }
        return consumibles;
    }

    public boolean consumirItem(Consumible consumible, Personaje objetivo) {
        if (items.contains(consumible)) {
            consumible.aplicar(objetivo);
            items.remove(consumible);
            return true;
        }
        System.out.println("El ítem no está en el inventario.");
        return false;
    }

    public boolean equiparArma(Arma arma, Personaje objetivo) {
        if (items.contains(arma)) {
            items.remove(arma);
            Arma anterior = objetivo.equiparArma(arma);
            if (anterior != null) {
                items.add(anterior);
                System.out.println("El arma anterior (" + anterior.getNombre() + ") volvió al inventario.");
            }
            return true;
        }
        System.out.println("El arma no está en el inventario.");
        return false;
    }

    public boolean desequiparArma(Personaje objetivo) {
        Arma desequipada = objetivo.desequiparArma();
        if (desequipada != null) {
            items.add(desequipada);
            System.out.println(desequipada.getNombre() + " se guardó en el inventario.");
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
                System.out.println("La armadura anterior (" + anterior.getNombre() + ") volvió al inventario.");
            }
            return true;
        }
        System.out.println("La armadura no está en el inventario.");
        return false;
    }

    public boolean desequiparArmadura(Personaje objetivo) {
        Armadura desequipada = objetivo.desequiparArmadura();
        if (desequipada != null) {
            items.add(desequipada);
            System.out.println(desequipada.getNombre() + " se guardó en el inventario.");
            return true;
        }
        return false;
    }

    public void listarItems() {
        System.out.println("--- INVENTARIO ---");
        if (items.isEmpty()) {
            System.out.println("(Vacío)");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
    }
}
