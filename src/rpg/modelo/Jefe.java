package rpg.modelo;

import java.util.List;

public class Jefe extends Enemigo {

    private int contadorTurno = 0;
    private boolean enFuria = false;

    public Jefe(String nombre) {
        super(nombre, 380, 45, 18, 14, 250);
    }

    public Jefe(String nombre, int vida, int ataque, int defensa, int velocidad, int xp) {
        super(nombre, vida, ataque, defensa, velocidad, xp);
    }

    /**
     * IA del Jefe con dos fases:
     * Fase 1: alterna ataque físico pesado y aliento de fuego en área.
     * Fase 2 (HP ≤ 50%): entra en furia, ataques más potentes y veneno en área cada 2 turnos.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        contadorTurno++;

        if (getVida() <= (getVidaMaxima() * 0.5) && !enFuria) {
            enFuria = true;
            RegistroCombate.registrar("\n🔥 ¡EL JEFE ENTRA EN ESTADO DE FURIA! "
                    + getNombre() + " ruge con furor infernal (+ATQ, Veneno en área)!\n");
        }

        if (enFuria) {
            if (contadorTurno % 2 == 0) {
                RegistroCombate.registrar("☠️ ¡" + getNombre()
                        + " exhala una Nube Tóxica Carmesí sobre toda la Party!");
                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) {
                        p.recibirDanio(getAtaque() - 15);
                        p.setEnvenenado(true);
                        RegistroCombate.registrar("  ⚠️ " + p.getNombre() + " ha sido Envenenado.");
                    }
                }
            } else {
                Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
                if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
                if (objetivo != null) {
                    RegistroCombate.registrar("🐉 " + getNombre()
                            + " en Furia desgarra a " + objetivo.getNombre() + "!");
                    atacar(objetivo);
                }
            }
        } else {
            if (contadorTurno % 2 == 0) {
                RegistroCombate.registrar("🔥 ¡" + getNombre()
                        + " desata un ¡Aliento de Fuego en Área! dañando a toda la Party!");
                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) p.recibirDanio(getAtaque() - 12);
                }
            } else {
                Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
                if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
                if (objetivo != null) {
                    RegistroCombate.registrar("🐉 " + getNombre()
                            + " concentra su ataque aplastante sobre " + objetivo.getNombre() + "!");
                    atacar(objetivo);
                }
            }
        }
    }
}
