package rpg.modelo;

public class Armadura extends Equipable {

    private static final long serialVersionUID = 1L;

    private int bonusDefensa;

    public Armadura(String nombre, String descripcion, int bonusDefensa, int precioCompra, int precioVenta) {
        super(nombre, descripcion, precioCompra, precioVenta);
        this.bonusDefensa = bonusDefensa;
    }

    public Armadura(String nombre, String descripcion, int bonusDefensa) {
        super(nombre, descripcion, 55, 20);
        this.bonusDefensa = bonusDefensa;
    }

    public int getBonusDefensa() {
        return bonusDefensa;
    }

    @Override
    public TipoEquipo getTipoEquipo() {
        return TipoEquipo.ARMADURA;
    }
}
