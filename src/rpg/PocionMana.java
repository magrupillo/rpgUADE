package rpg;

public class PocionMana extends Consumible {
    private int puntosMana;

    public PocionMana(String nombre, String descripcion, int puntosMana) {
        super(nombre, descripcion);
        this.puntosMana = puntosMana;
    }

    public int getPuntosMana() {
        return puntosMana;
    }

    @Override
    public void aplicar(Personaje objetivo) {
        objetivo.recuperarMana(this.puntosMana);
        System.out.println(objetivo.getNombre() + " usó " + getNombre() + " y recuperó " + this.puntosMana + " puntos de maná.");
    }
}
