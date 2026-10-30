package rpg.modelo;

public class Arma extends Equipable {

    private static final long serialVersionUID = 1L;

    private int bonusAtaque;

    public Arma(String nombre, String descripcion, int bonusAtaque, int precioCompra, int precioVenta) {
        super(nombre, descripcion, precioCompra, precioVenta);
        this.bonusAtaque = bonusAtaque;
    }

    public Arma(String nombre, String descripcion, int bonusAtaque) {
        super(nombre, descripcion, 60, 25);
        this.bonusAtaque = bonusAtaque;
    }

    public int getBonusAtaque() {
        return bonusAtaque;
    }

    @Override
    public TipoEquipo getTipoEquipo() {
        return TipoEquipo.ARMA;
    }
}
