package rpg.modelo;

import java.util.List;

public class Orco extends Enemigo {

    private static final long serialVersionUID = 1L;

    public Orco(String nombre) {
        super(nombre, 240, 32, 16, 10, 70, 16);
    }

    /**
     * IA de Cocodrilo Asesino: Ataca con brutalidad al héroe de mayor amenaza (Kratos si provocó)
     * o al primer héroe vivo disponible.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
        if (objetivo == null) {
            objetivo = party.obtenerPrimerMiembroVivo();
        }

        if (objetivo != null) {
            RegistroCombate.registrar(getNombre() + " da un coletazo salvaje contra "
                    + objetivo.getNombre() + "!");
            atacar(objetivo);
        }
    }
}
