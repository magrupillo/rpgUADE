package rpg;

public abstract class Consumible extends Item {

    public Consumible(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    public abstract void aplicar(Personaje objetivo);
}
