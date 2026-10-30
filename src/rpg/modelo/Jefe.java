package rpg.modelo;

import java.util.List;

public class Jefe extends Enemigo {

    private static final long serialVersionUID = 1L;

    private final int piso;
    private boolean enFuria;
    private int contadorTurno;

    public Jefe(int piso) {
        super(
                determinarNombre(piso),
                determinarVida(piso),
                determinarAtaque(piso),
                determinarDefensa(piso),
                determinarVelocidad(piso),
                determinarXp(piso),
                determinarOro(piso)
        );
        this.piso = piso;
        this.enFuria = false;
        this.contadorTurno = 0;
    }

    public static Jefe crearJefeParaPiso(int piso) {
        return new Jefe(piso);
    }

    private static String determinarNombre(int piso) {
        return switch (piso) {
            case 1 -> "Dr. Eggman";
            case 2 -> "Darth Vader";
            default -> "Bowser";
        };
    }

    private static int determinarVida(int piso) {
        return switch (piso) {
            case 1 -> 480;
            case 2 -> 620;
            default -> 780;
        };
    }

    private static int determinarAtaque(int piso) {
        return switch (piso) {
            case 1 -> 32;
            case 2 -> 40;
            default -> 48;
        };
    }

    private static int determinarDefensa(int piso) {
        return switch (piso) {
            case 1 -> 16;
            case 2 -> 22;
            default -> 28;
        };
    }

    private static int determinarVelocidad(int piso) {
        return switch (piso) {
            case 1 -> 11;
            case 2 -> 13;
            default -> 14;
        };
    }

    private static int determinarXp(int piso) {
        return switch (piso) {
            case 1 -> 220;
            case 2 -> 340;
            default -> 500;
        };
    }

    private static int determinarOro(int piso) {
        return switch (piso) {
            case 1 -> 40;
            case 2 -> 65;
            default -> 90;
        };
    }

    public int getPiso() {
        return piso;
    }

    public boolean isEnFuria() {
        return enFuria;
    }

    @Override
    public String getTipo() {
        return switch (piso) {
            case 1 -> "jefe2"; // Dr. Eggman (jefe2.png)
            case 2 -> "jefe3"; // Darth Vader (jefe3.png)
            default -> "jefe"; // Bowser (jefe.png)
        };
    }

    /**
     * IA del Jefe según el piso y fase:
     * Fase 1 (HP > 50%): Alterna ataque pesado y habilidad en área.
     * Fase 2 (HP <= 50%): Furia desatada con daño aumentado y veneno en área cada 2 turnos.
     */
    @Override
    public void ejecutarTurno(Party party, List<Enemigo> aliados) {
        contadorTurno++;

        if (getVida() <= (getVidaMaxima() * 0.5) && !enFuria) {
            enFuria = true;
            RegistroCombate.registrar("\n==========================================");
            RegistroCombate.registrar("¡¡" + getNombre().toUpperCase() + " ENTRA EN ESTADO DE FURIA CRÍTICA!!");
            if (piso == 1) {
                RegistroCombate.registrar("¡Dr. Eggman sobrecarga los reactores del Egg-Mobile y desata un pulso electromagnético!");
            } else if (piso == 2) {
                RegistroCombate.registrar("¡Darth Vader se entrega por completo al Lado Oscuro de la Fuerza!");
            } else {
                RegistroCombate.registrar("¡Bowser ruge haciendo temblar los cimientos de la torre con magma ardiente!");
            }
            RegistroCombate.registrar("==========================================\n");
        }

        if (enFuria) {
            if (contadorTurno % 2 == 0) {
                // Veneno / Daño en área furioso
                if (piso == 1) {
                    RegistroCombate.registrar("¡Dr. Eggman dispara una Salva de Misiles Tóxicos sobre TODA la Party!");
                } else if (piso == 2) {
                    RegistroCombate.registrar("¡Darth Vader desata una Tormenta del Lado Oscuro asfixiando a la Party!");
                } else {
                    RegistroCombate.registrar("¡Bowser exhala una Nube Tóxica Ardiente sobre TODA la Party!");
                }

                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) {
                        p.recibirDanio(getAtaque() - 6);
                        p.setEnvenenado(true);
                    }
                }
            } else {
                // Golpe individual masivo
                Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
                if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
                if (objetivo != null) {
                    if (piso == 1) {
                        RegistroCombate.registrar("¡Dr. Eggman embiste con el Taladro Titánico del Egg-Mobile a " + objetivo.getNombre() + "!");
                    } else if (piso == 2) {
                        RegistroCombate.registrar("¡Darth Vader ejecuta una Estocada Devastadora de Sable Carmesí contra " + objetivo.getNombre() + "!");
                    } else {
                        RegistroCombate.registrar("¡Bowser ejecuta un Pisotón Sísmico Aplastante contra " + objetivo.getNombre() + "!");
                    }
                    objetivo.recibirDanio((int) Math.round(getAtaque() * 1.45));
                }
            }
        } else {
            // Fase 1: alternancia entre fuerte individual y barrido en área
            if (contadorTurno % 2 == 1) {
                Personaje objetivo = party.obtenerHeroeConMayorAmenaza();
                if (objetivo == null) objetivo = party.obtenerPrimerMiembroVivo();
                if (objetivo != null) {
                    if (piso == 1) {
                        RegistroCombate.registrar("¡Dr. Eggman dispara un Rayo Láser Focalizado contra " + objetivo.getNombre() + "!");
                    } else if (piso == 2) {
                        RegistroCombate.registrar("¡Darth Vader lanza un Mandoble de la Fuerza contra " + objetivo.getNombre() + "!");
                    } else {
                        RegistroCombate.registrar("¡Bowser descarga un feroz Zarpazo Volcánico contra " + objetivo.getNombre() + "!");
                    }
                    objetivo.recibirDanio((int) Math.round(getAtaque() * 1.25));
                }
            } else {
                if (piso == 1) {
                    RegistroCombate.registrar("¡Dr. Eggman lanza Bombas de Racimo Mecánicas sobre toda la party!");
                } else if (piso == 2) {
                    RegistroCombate.registrar("¡Darth Vader desata una Onda Expansiva de la Fuerza sobre la party!");
                } else {
                    RegistroCombate.registrar("¡Bowser exhala una descomunal Llamarada de Fuego sobre la party!");
                }
                for (Personaje p : party.getMiembros()) {
                    if (p.estaVivo()) {
                        p.recibirDanio(getAtaque() - 12);
                    }
                }
            }
        }
    }
}
