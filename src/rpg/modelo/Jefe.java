package rpg.modelo;

import java.util.List;

public class Jefe extends Enemigo {

    private static final long serialVersionUID = 1L;

    private boolean enFuria;
    private int contadorTurno;

    public Jefe(String nombre) {
        super(nombre, 620, 42, 22, 12, 250, 150);
        this.enFuria = false;
        this.contadorTurno = 0;
    }

    public Jefe() {
        this("Bowser");
    }

    public Jefe(String nombre, int vida, int ataque, int defensa, int velocidad, int xp, int oro) {
        super(nombre, vida, ataque, defensa, velocidad, xp, oro);
        this.enFuria = false;
        this.contadorTurno = 0;
    }

    public boolean isEnFuria() {
        return enFuria;
    }

    /**
     * IA de Bowser:
     * Fase 1 (Vida > 50%): Alterna entre zarpazo individual pesado y llamarada ardiente en área.
     * Fase 2 (Vida <= 50%): Entra en Furia: daño incrementado y exhala Nube Tóxica (veneno en área) cada 2 turnos.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        contadorTurno++;

        if (getVida() <= (getVidaMaxima() * 0.5) && !enFuria) {
            enFuria = true;
            RegistroCombate.registrar("\n¡" + getNombre().toUpperCase() + " ENTRA EN ESTADO DE FURIA INFERNAL!");
            RegistroCombate.registrar("Bowser ruge haciendo temblar la torre (+ATQ aumentado, Nube Tóxica en área)!\n");
        }

        if (enFuria) {
            if (contadorTurno % 2 == 0) {
                RegistroCombate.registrar("¡" + getNombre() + " exhala una Nube Tóxica Ardiente sobre TODA la Party!");
                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) {
                        p.recibirDanio(getAtaque() - 8);
                        p.setEnvenenado(true);
                    }
                }
            } else {
                Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
                if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
                if (objetivo != null) {
                    RegistroCombate.registrar("¡" + getNombre() + " ejecuta un Pisotón Sísmico Enfurecido contra "
                            + objetivo.getNombre() + "!");
                    objetivo.recibirDanio((int) Math.round(getAtaque() * 1.4));
                }
            }
        } else {
            // Fase 1: alternar ataque individual fuerte y llamarada en área
            if (contadorTurno % 2 == 1) {
                Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
                if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
                if (objetivo != null) {
                    RegistroCombate.registrar("¡" + getNombre() + " asesta un feroz Zarpazo Volcánico contra "
                            + objetivo.getNombre() + "!");
                    objetivo.recibirDanio((int) Math.round(getAtaque() * 1.2));
                }
            } else {
                RegistroCombate.registrar("¡" + getNombre() + " exhala una descomunal Llamarada de Fuego sobre la party!");
                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) {
                        p.recibirDanio(getAtaque() - 14);
                    }
                }
            }
        }
    }
}
