package rpg.modelo;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un evento narrativo o misterioso en la torre.
 * Ofrece dos opciones al jugador con recompensas o riesgos tácticos.
 */
public class EventoAleatorio implements Serializable {

    private final String id;
    private final String titulo;
    private final String descripcion;
    private final String opcionA;
    private final String opcionB;
    private final Random random = new Random();

    public EventoAleatorio(String id, String titulo, String descripcion, String opcionA, String opcionB) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.opcionA = opcionA;
        this.opcionB = opcionB;
    }

    public static EventoAleatorio crearBrebajeMisterioso() {
        return new EventoAleatorio(
                "BREBAJE",
                "🧪 El Brebaje Misterioso",
                "En una mesa de alquimia olvidada encuentras una ampolla de cristal con un líquido resplandeciente.\n" +
                "Una inscripción borrosa advierte sobre sus propiedades mágicas volátiles.\n\n" +
                "¿Qué decides hacer?",
                "Beber el brebaje (+50% MP a la party, 20% riesgo de Veneno)",
                "Ignorar y continuar con cautela (Sin riesgo)"
        );
    }

    public static EventoAleatorio crearAltarDeSangre() {
        return new EventoAleatorio(
                "ALTAR_SANGRE",
                "🩸 El Altar de Sangre Ancestral",
                "Un monolito de piedra obsidiana emite un zumbido carmesí. Runas antiguas susurran promesas de poder a cambio de vitalidad.\n\n" +
                "¿Deseas ofrecer un tributo de sangre a cambio de fuerza destructiva?",
                "Ofrenda de sangre (-25% Vida actual, +5 Ataque Base permanente a todos)",
                "Rezar con respeto (Bendición de +2 Defensa Base permanente, sin daño)"
        );
    }

    public static EventoAleatorio crearCofreMaldito() {
        return new EventoAleatorio(
                "COFRE_MALDITO",
                "📦 El Cofre con Cadenas Rúnicas",
                "En el centro de una cripta húmeda yace un arcón ornamentado rodeado de huesos y cadenas oxidadas.\n" +
                "Podría contener reliquias invaluables o una trampa mortal.\n\n" +
                "¿Cómo procedes?",
                "Forzar las cerraduras (70% Botín Valioso + Pociones, 30% Trampa Explosiva)",
                "Desactivar mecanismos con prudencia (Gana 1 Poción de Maná segura)"
        );
    }

    public static EventoAleatorio crearFuenteDeHadas() {
        return new EventoAleatorio(
                "FUENTE_HADAS",
                "✨ La Fuente Luminosa de las Hadas",
                "Un manantial de aguas cristalinas brota en una gruta subterránea iluminada por hongos fluorescentes.\n" +
                "El aire huele a lilas y rocío matutino.\n\n" +
                "¿Cómo aprovechas el manantial?",
                "Beber y bañarse (Restaura 60% HP a toda la party y purifica estados)",
                "Llenar frascos de cristal (Agrega 2 Pociones de Vida al Inventario compartido)"
        );
    }

    public static EventoAleatorio crearMercaderDeSombras() {
        return new EventoAleatorio(
                "MERCADER",
                "👤 El Mercader Errante de las Sombras",
                "Una figura encapuchada sentada junto a un farol morado te ofrece provisiones a cambio de tributos de experiencia.\n\n" +
                "¿Qué trato decides pactar?",
                "Trueque de reliquias (Entrega 80 XP de la party por una Espada de Acero +10 ATQ)",
                "Comprar suministros (Entrega 40 XP por 1 Poción de Vida y 1 de Maná)"
        );
    }

    public static EventoAleatorio crearBibliotecaArcana() {
        return new EventoAleatorio(
                "BIBLIOTECA",
                "📚 La Biblioteca de los Grimorios Perdidos",
                "Estanterías colosales repletas de pergaminos arcanos y códices antiguos aguardan en un silencio sepulcral.\n\n" +
                "¿En qué decides enfocar el tiempo del equipo?",
                "Estudiar tácticas y hechizos antiguos (Otorga 150 XP repartida equitativamente)",
                "Meditar en la sala de lectura (Recupera 40% de Vida y 40% de Maná a toda la party)"
        );
    }

    /**
     * Genera un evento procedural aleatorio garantizando variedad en la run.
     */
    public static EventoAleatorio crearAleatorio(Random rnd) {
        int eleccion = rnd.nextInt(6);
        return switch (eleccion) {
            case 0 -> crearBrebajeMisterioso();
            case 1 -> crearAltarDeSangre();
            case 2 -> crearCofreMaldito();
            case 3 -> crearFuenteDeHadas();
            case 4 -> crearMercaderDeSombras();
            default -> crearBibliotecaArcana();
        };
    }

    public static EventoAleatorio crearAleatorio() {
        return crearAleatorio(new Random());
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getOpcionA() { return opcionA; }
    public String getOpcionB() { return opcionB; }

    // ─── Resolución del evento ───────────────────────────────────────────────────

    public String resolverOpcion(int opcion, Party party) {
        StringBuilder sb = new StringBuilder();
        RegistroCombate.registrar("\n=== EVENTO: " + titulo + " ===");

        switch (id) {
            case "ALTAR_SANGRE" -> {
                if (opcion == 1) {
                    sb.append("🩸 La party entrega parte de su sangre al altar. Una furia incandescente los envuelve!\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            int danio = Math.max(5, (int) (p.getVida() * 0.25));
                            p.curarVida(-danio);
                            p.aumentarAtaqueBase(5);
                            sb.append("  • ").append(p.getNombre()).append(" sufrió ").append(danio)
                              .append(" de daño, pero su Ataque aumentó permanentemente en +5 (ATQ: ").append(p.getAtaque()).append(")!\n");
                        }
                    }
                } else {
                    sb.append("🛡️ La party inclina la cabeza con reverencia. Una bendición pétrea los protege.\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            p.aumentarDefensaBase(2);
                            sb.append("  • ").append(p.getNombre()).append(" ganó +2 de Defensa Base permanente (DEF: ").append(p.getDefensa()).append(").\n");
                        }
                    }
                }
            }
            case "COFRE_MALDITO" -> {
                if (opcion == 1) {
                    boolean exito = random.nextInt(100) < 70;
                    if (exito) {
                        sb.append("🎉 ¡Éxito! El cofre cede sin activar sus trampas rúnicas.\n");
                        party.getInventario().agregarItem(new PocionVida("Poción de Vida Mayor", "Restaura 80 HP", 80));
                        party.getInventario().agregarItem(new PocionMana("Poción de Maná Mayor", "Restaura 60 MP", 60));
                        party.getInventario().agregarItem(new Armadura("Coraza Encantada", "+12 DEF", 12));
                        sb.append("  + Se guardaron pociones mayores y una Coraza Encantada en el Inventario compartido!\n");
                    } else {
                        sb.append("💥 ¡TRAMPA! Una explosión arcana estalla al abrir el arcón!\n");
                        for (Personaje p : party.getMiembros()) {
                            if (p.estaVivo()) {
                                p.recibirDanio(25);
                                p.setAturdido(true);
                                sb.append("  ⚠️ ").append(p.getNombre()).append(" fue alcanzado por la explosión y aturdido.\n");
                            }
                        }
                    }
                } else {
                    sb.append("🔍 Con paciencia desarmas los seguros y extraes un compartimento secreto con 1 Poción de Maná.\n");
                    party.getInventario().agregarItem(new PocionMana("Poción de Maná", "Restaura 40 MP", 40));
                }
            }
            case "FUENTE_HADAS" -> {
                if (opcion == 1) {
                    sb.append("✨ Las aguas benditas revitalizan por completo a los aventureros.\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            int curacion = (int) (p.getVidaMaxima() * 0.60);
                            p.curarVida(curacion);
                            p.setAturdido(false);
                            p.setEnvenenado(false);
                            sb.append("  💚 ").append(p.getNombre()).append(" restauró ").append(curacion).append(" HP y purificó sus aflicciones.\n");
                        }
                    }
                } else {
                    sb.append("🧪 Llenan cuidadosamente dos ampollas de cristal del inventario con agua milagrosa.\n");
                    party.getInventario().agregarItem(new PocionVida("Elixir de Hada", "Restaura 70 HP", 70));
                    party.getInventario().agregarItem(new PocionVida("Elixir de Hada", "Restaura 70 HP", 70));
                    sb.append("  + 2 Elixires de Hada agregados al Inventario.\n");
                }
            }
            case "MERCADER" -> {
                if (opcion == 1) {
                    sb.append("⚔️ El mercader asiente con una sonrisa esquelética y entrega una formidable Espada de Acero.\n");
                    party.getInventario().agregarItem(new Arma("Espada Vorpal de Acero", "+12 ATQ", 12));
                    sb.append("  + Espada Vorpal de Acero agregada al Inventario compartido.\n");
                } else {
                    sb.append("🧪 El mercader te entrega un fardo con suministros médicos esenciales.\n");
                    party.getInventario().agregarItem(new PocionVida("Poción de Salud", "Restaura 50 HP", 50));
                    party.getInventario().agregarItem(new PocionMana("Poción de Maná", "Restaura 40 MP", 40));
                    sb.append("  + 1 Poción de Salud y 1 Poción de Maná añadidas al Inventario.\n");
                }
            }
            case "BIBLIOTECA" -> {
                if (opcion == 1) {
                    sb.append("📖 La party estudia antiguas estrategias bélicas y secretos arcanos.\n");
                    party.repartirExperiencia(160);
                    sb.append("  ✨ Se otorgaron 160 XP repartidos entre los miembros del equipo.\n");
                } else {
                    sb.append("🧘 Un descanso reparador en las silenciosas salas de la biblioteca renueva el vigor del equipo.\n");
                    party.recuperarPorcentaje(0.40);
                    sb.append("  💚 Toda la party recuperó el 40% de su Vida y Maná máximos.\n");
                }
            }
            default -> { // BREBAJE
                if (opcion == 1) {
                    sb.append("✨ La party ingiere el brebaje misterioso. Una potente energía mágica fluye en sus venas!\n");
                    for (Personaje p : party.getMiembros()) {
                        if (p.estaVivo()) {
                            int recup = (int) (p.getManaMaximo() * 0.50);
                            p.recuperarMana(recup);
                            sb.append("  + ").append(p.getNombre()).append(" recuperó ").append(recup).append(" MP.\n");

                            if (random.nextInt(100) < 20) {
                                p.setEnvenenado(true);
                                sb.append("  ⚠️ ¡Efecto adverso! ").append(p.getNombre()).append(" fue afectado por Veneno.\n");
                            }
                        }
                    }
                } else {
                    sb.append("Deciden no arriesgarse y avanzan con prudencia sin sufrir ningún percance.\n");
                }
            }
        }

        String res = sb.toString();
        RegistroCombate.registrar(res);
        return res;
    }
}
