package rpg;

public class Arma extends Equipable {
    private int bonusAtaque;

    public Arma(String nombre, String descripcion, int bonusAtaque) {
        super(nombre, descripcion);
        this.bonusAtaque = bonusAtaque;
    }

    public int getBonusAtaque() {
        return bonusAtaque;
    }
}
