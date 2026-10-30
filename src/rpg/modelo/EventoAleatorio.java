package rpg.modelo;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un evento narrativo con sabor criollo y jerga argentina.
 * Ofrece dos opciones al jugador con recompensas o riesgos tácticos.
 */
public class EventoAleatorio implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String titulo;
    private final String descripcion;
    private final OpcionEvento opcionA;
    private final OpcionEvento opcionB;
    private final Random random = new Random();

    public EventoAleatorio(String id, String titulo, String descripcion, String textoA, String textoB) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.opcionA = new OpcionEvento(textoA, "");
        this.opcionB = new OpcionEvento(textoB, "");
    }

    public static EventoAleatorio crearBebidaMisteriosa() {
        return new EventoAleatorio(
                "BEBIDA",
                "La Bebida Misteriosa",
                "Sobre una mesa de fórmica encontrás una jarra con un menjunje oscuro que despide olor a fernet con hierbas del conurbano.\n" +
                "Una servilleta arrugada dice: 'Tomalo si te la bancás'.\n\n" +
                "¿Qué hacés?",
                "Pegarle un trago al elixir (+50% MP a toda la party, 20% riesgo de resaca/veneno)",
                "Pasar de largo y tomar agua de la canilla (Sin riesgo)"
        );
    }

    public static EventoAleatorio crearPasilloPeligroso() {
        return new EventoAleatorio(
                "PASILLO_PELIGROSO",
                "El Pasillo Peligroso",
                "Un callejón oscuro sin luz donde se escuchan murmullos y motos sin silenciador a lo lejos.\n" +
                "Si te hacés el loco podés salir fortalecido, o te podés ganar un disgusto.\n\n" +
                "¿Cómo cruzan?",
                "Cruzar con el pecho inflado y cara de malo (-20% HP actual por el susto, +6 ATQ permanente)",
                "Ir pegado a la pared con cautela (+3 Defensa permanente por estar alerta)"
        );
    }

    public static EventoAleatorio crearRegaloDeLaSuegra() {
        return new EventoAleatorio(
                "REGALO_SUEGRA",
                "El Regalo de la Suegra",
                "Una caja gigante envuelta con papel de diario y un moño desprolijo te corta el paso.\n" +
                "Una tarjeta dice: 'Para que no digan que nunca les regalo nada'. Puede ser una masa o un fiasco total.\n\n" +
                "¿La abrís?",
                "Abrir el paquete con entusiasmo (70% Botín copado y pociones, 30% explosión de naftalina y aturdimiento)",
                "Desarmarlo con guantes de cocina (Extraés 1 Poción de Maná sana y salva)"
        );
    }

    public static EventoAleatorio crearEstatuaGauchitoGil() {
        return new EventoAleatorio(
                "GAUCHITO_GIL",
                "La Estatua del Gauchito Gil",
                "Al costado del sendero brilla un santuario rojo con velas encendidas, cintas rojas y flores frescas.\n" +
                "La presencia del santo popular llena el ambiente de respeto y protección gaucha.\n\n" +
                "¿Cómo le rendís tributo?",
                "Dejarle un cigarrillo y persignarse (Restaura 60% HP a toda la party y limpia estados alterados)",
                "Cargar las botellas de agua bendecida (+2 Pociones de Vida para el inventario)"
        );
    }

    public static EventoAleatorio crearKioscoErrante() {
        return new EventoAleatorio(
                "KIOSCO_EVENTO",
                "El Kiosco Errante de la Noche",
                "Una ventana enrejada con luz de tubo fluorescente atiende las 24 horas. Don Tito te mira desde adentro.\n" +
                "'Tengo caramelos que no te devuelven vuelto, o algún fierro que te puede servir'.\n\n" +
                "¿Qué le pedís?",
                "Pegarle una ojeada a las armas pesadas (Entrega 75 XP por un Zarpado Machete +16 ATQ)",
                "Comprar un combo de supervivencia (Entrega 40 XP por 1 Poción de Vida y 1 de Maná)"
        );
    }

    public static EventoAleatorio crearPuestoDeDiarios() {
        return new EventoAleatorio(
                "PUESTO_DIARIOS",
                "El Puesto de Diarios",
                "Un puesto de chapa repleto de revistas de crucigramas, el diario del día y fascículos coleccionables.\n" +
                "El canillita te deja ojear mientras te cebás unos mates con bizcochitos.\n\n" +
                "¿En qué se ponen a leer?",
                "Leer los suplementos de táctica y fútbol (Otorga 160 XP repartida equitativamente)",
                "Comerse unos bizcochitos en el banco (Recupera 40% de Vida y Maná a toda la party)"
        );
    }

    public static EventoAleatorio crearElSacrificio() {
        return new EventoAleatorio(
                "SACRIFICIO",
                "El Sacrificio de Sangre",
                "Frente a un altar umbrío iluminado por fuego espectral, una presencia arcana susurra:\n" +
                "'Puedo traer de vuelta a un compañero caído de tu party... pero todos los vivos deberán pagar con su propia sangre'.\n\n" +
                "¿Aceptan realizar el sacrificio?",
                "Sellar el pacto (Héroes vivos sacrifican 50% de HP actual; un compañero caído revive con 40% HP)",
                "Rechazar el ritual y rezar por los caídos (+90 XP grupal por templanza)"
        );
    }

    public static EventoAleatorio crearAleatorio(Random rnd) {
        int eleccion = rnd.nextInt(7);
        return switch (eleccion) {
            case 0 -> crearBebidaMisteriosa();
            case 1 -> crearPasilloPeligroso();
            case 2 -> crearRegaloDeLaSuegra();
            case 3 -> crearEstatuaGauchitoGil();
            case 4 -> crearKioscoErrante();
            case 5 -> crearElSacrificio();
            default -> crearPuestoDeDiarios();
        };
    }

    public static EventoAleatorio crearAleatorio() {
        return crearAleatorio(new Random());
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getOpcionA() { return opcionA.getTexto(); }
    public String getOpcionB() { return opcionB.getTexto(); }

    public String resolverOpcion(int opcion, Party party) {
        StringBuilder sb = new StringBuilder();
        RegistroCombate.registrar("\n=== EVENTO: " + titulo.toUpperCase() + " ===");

        switch (id) {
            case "PASILLO_PELIGROSO" -> {
                if (opcion == 1) {
                    sb.append("La party cruza a paso firme. La adrenalina despierta su instinto de combate!\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            int danio = Math.max(5, (int) (p.getVida() * 0.20));
                            p.curarVida(-danio);
                            p.aumentarAtaqueBase(6);
                            sb.append("  • ").append(p.getNombre()).append(" sintió la tensión (-").append(danio)
                              .append(" HP), pero ganó +6 Ataque Base permanente (ATQ: ").append(p.getAtaque()).append(")!\n");
                        }
                    }
                } else {
                    sb.append("Avanzan con sigilo esquivando peligros. Se vuelven más atentos y precavidos.\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            p.aumentarDefensaBase(3);
                            sb.append("  • ").append(p.getNombre()).append(" ganó +3 Defensa Base permanente (DEF: ").append(p.getDefensa()).append(").\n");
                        }
                    }
                }
            }
            case "REGALO_SUEGRA" -> {
                if (opcion == 1) {
                    boolean exito = random.nextInt(100) < 70;
                    if (exito) {
                        sb.append("¡Milagro! Adentro había cosas útiles y comida casera!\n");
                        party.getInventario().agregarItem(new PocionVida("Poción de Vida Mayor", "Restaura 80 HP", 80, 40, 20));
                        party.getInventario().agregarItem(new PocionMana("Poción de Maná Mayor", "Restaura 60 MP", 60, 35, 18));
                        party.getInventario().agregarItem(new Armadura("Chaleco kevlar", "Protección táctica reforzada (+14 DEF)", 14, 85, 40));
                        sb.append("  + Se guardaron pociones mayores y un Chaleco Kevlar en el inventario!\n");
                    } else {
                        sb.append("¡Pffff! Una nube de naftalina vieja y resortes oxidados salta a la cara del equipo!\n");
                        for (Personaje p : party.getMiembros()) {
                            if (p.estaVivo()) {
                                p.recibirDanio(20);
                                p.setAturdido(true);
                                sb.append("  ⚠️ ").append(p.getNombre()).append(" quedó aturdido tosiendo por la naftalina.\n");
                            }
                        }
                    }
                } else {
                    sb.append("Desarmaron la caja con pinzas y rescataron una Poción de Maná intacta.\n");
                    party.getInventario().agregarItem(new PocionMana("Poción de Maná", "Restaura 40 MP", 40, 20, 10));
                }
            }
            case "GAUCHITO_GIL" -> {
                if (opcion == 1) {
                    sb.append("La bendición del Gauchito Gil llena el aire. Una calidez fraternal sana las heridas del equipo.\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            int curacion = (int) (p.getVidaMaxima() * 0.60);
                            p.curarVida(curacion);
                            p.setAturdido(false);
                            p.setEnvenenado(false);
                            sb.append("  ").append(p.getNombre()).append(" recuperó ").append(curacion).append(" HP y purificó sus aflicciones.\n");
                        }
                    }
                } else {
                    sb.append("Cargan dos botellitas con el agua bendita del manantial rojo.\n");
                    party.getInventario().agregarItem(new PocionVida("Poción del Gauchito", "Restaura 70 HP", 70, 35, 18));
                    party.getInventario().agregarItem(new PocionVida("Poción del Gauchito", "Restaura 70 HP", 70, 35, 18));
                    sb.append("  + 2 Pociones del Gauchito agregadas al inventario.\n");
                }
            }
            case "KIOSCO_EVENTO" -> {
                if (opcion == 1) {
                    sb.append("Don Tito les guiña el ojo y saca de abajo del mostrador un tremendo machete.\n");
                    party.getInventario().agregarItem(new Arma("Zarpado machete", "Hoja afilada de gran filo (+16 ATQ)", 16, 90, 45));
                    sb.append("  + Zarpado machete (+16 ATQ) agregado al inventario!\n");
                } else {
                    sb.append("Don Tito les prepara una bolsita con provisiones básicas.\n");
                    party.getInventario().agregarItem(new PocionVida("Poción de Vida", "Restaura 50 HP", 50, 25, 12));
                    party.getInventario().agregarItem(new PocionMana("Poción de Maná", "Restaura 40 MP", 40, 20, 10));
                    sb.append("  + 1 Poción de Vida y 1 Poción de Maná añadidas al inventario.\n");
                }
            }
            case "PUESTO_DIARIOS" -> {
                if (opcion == 1) {
                    sb.append("La party debate tácticas de juego y se empapa de información valiosa.\n");
                    party.repartirExperiencia(160);
                    sb.append("  Se repartieron 160 XP entre los miembros del equipo.\n");
                } else {
                    sb.append("Un buen mate cocido con bizcochitos en la vereda revitaliza a toda la muchachada.\n");
                    party.recuperarPorcentaje(0.40);
                    sb.append("  Toda la party recuperó el 40% de su Vida y Maná máximos.\n");
                }
            }
            case "SACRIFICIO" -> {
                if (opcion == 1) {
                    Personaje muerto = null;
                    for (Personaje p : party.getMiembros()) {
                        if (!p.estaVivo()) {
                            muerto = p;
                            break;
                        }
                    }
                    if (muerto != null) {
                        sb.append("¡La sangre sella el pacto! Toda la party entrega la mitad de sus fuerzas vitales.\n");
                        for (Personaje p : party.getMiembros()) {
                            if (p.estaVivo() && p != muerto) {
                                int sacrificio = p.getVida() / 2;
                                p.recibirDanioDirecto(sacrificio);
                                sb.append("  • ").append(p.getNombre()).append(" sacrificó ").append(sacrificio).append(" HP (HP actual: ").append(p.getVida()).append(").\n");
                            }
                        }
                        int hpRevivido = Math.max(25, (int) (muerto.getVidaMaxima() * 0.40));
                        muerto.revivir(hpRevivido);
                        sb.append("  ¡").append(muerto.getNombre().toUpperCase()).append(" HA SIDO RESUCITADO con ").append(hpRevivido).append(" HP!\n");
                    } else {
                        sb.append("No hay aliados caídos en la party. El altar recompensa la vitalidad del grupo otorgando +60 MP a cada uno.\n");
                        for (Personaje p : party.getMiembros()) {
                            p.recuperarMana(60);
                        }
                    }
                } else {
                    sb.append("Rechazan la magia oscura y siguen adelante fortalecidos en su convicción moral (+90 XP grupal).\n");
                    party.repartirExperiencia(90);
                }
            }
            default -> { // BEBIDA
                if (opcion == 1) {
                    sb.append("Se mandan el vaso al seco. Una chispa eléctrica les recorre la garganta!\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            int recup = (int) (p.getManaMaximo() * 0.50);
                            p.recuperarMana(recup);
                            sb.append("  + ").append(p.getNombre()).append(" recuperó ").append(recup).append(" MP/Ki.\n");

                            if (random.nextInt(100) < 20) {
                                p.setEnvenenado(true);
                                sb.append("  ⚠️ ").append(p.getNombre()).append(" sintió la resaca y quedó envenenado.\n");
                            }
                        }
                    }
                } else {
                    sb.append("Deciden no arriesgar el estómago y siguen derecho sin drama.\n");
                }
            }
        }

        String res = sb.toString();
        RegistroCombate.registrar(res);
        return res;
    }
}
