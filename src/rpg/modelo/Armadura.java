package rpg.modelo;

public class Armadura extends Equipable {
    private int bonusDefensa;

    public Armadura(String nombre, String descripcion, int bonusDefensa) {
        super(nombre, descripcion);
        this.bonusDefensa = bonusDefensa;
    }

    public int getBonusDefensa() {
        return bonusDefensa;
    }
}
