package rpg.modelo;

public class PocionVida extends Consumible {
    private int puntosCuracion;

    public PocionVida(String nombre, String descripcion, int puntosCuracion) {
        super(nombre, descripcion);
        this.puntosCuracion = puntosCuracion;
    }

    public int getPuntosCuracion() {
        return puntosCuracion;
    }

    @Override
    public void aplicar(Personaje objetivo) {
        objetivo.curarVida(this.puntosCuracion);
        RegistroCombate.registrar(objetivo.getNombre() + " usó " + getNombre() + " y recuperó " + this.puntosCuracion + " puntos de vida.");
    }
}
