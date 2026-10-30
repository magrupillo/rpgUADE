package rpg.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GestorRecursos {
    private static final Map<String, Image> cacheSprites = new HashMap<>();

    private static final String[] DIRECTORIOS_BUSQUEDA = {
            "recursos/sprites/",
            "sprites/",
            "src/recursos/sprites/",
            "src/sprites/",
            "resources/sprites/"
    };

    private static final String[] EXTENSIONES = {
            ".png", ".jpg", ".jpeg", ".gif", ".webp"
    };

    public static Image obtenerSprite(String tipoEntidad, int ancho, int alto) {
        if (tipoEntidad == null || tipoEntidad.trim().isEmpty()) {
            tipoEntidad = "generico";
        }
        String clave = tipoEntidad.trim().toLowerCase() + "_" + ancho + "x" + alto;
        if (cacheSprites.containsKey(clave)) {
            return cacheSprites.get(clave);
        }

        // 1. Intentar cargar desde archivo físico o classpath en múltiples rutas y extensiones
        BufferedImage imgCargada = buscarYCargarImagen(tipoEntidad.trim());
        if (imgCargada != null) {
            Image escalada = escalarImagen(imgCargada, ancho, alto);
            cacheSprites.put(clave, escalada);
            return escalada;
        }

        // 2. Si no hay archivo físico o recurso, generar arte procedural vectorial de alta calidad
        BufferedImage procedimental = generarSpriteProcedural(tipoEntidad, ancho, alto);
        cacheSprites.put(clave, procedimental);
        return procedimental;
    }

    private static BufferedImage buscarYCargarImagen(String tipoEntidad) {
        java.util.List<String> nombresCandidatos = generarNombresCandidatos(tipoEntidad);

        // 1. Buscar en carpetas del sistema de archivos
        for (String nombre : nombresCandidatos) {
            for (String dir : DIRECTORIOS_BUSQUEDA) {
                for (String ext : EXTENSIONES) {
                    try {
                        File archivo = new File(dir + nombre + ext);
                        if (archivo.exists() && archivo.isFile()) {
                            BufferedImage leida = ImageIO.read(archivo);
                            if (leida != null) return leida;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // 2. Buscar en el Classpath (recursos empaquetados en JAR o carpeta de clases)
        for (String nombre : nombresCandidatos) {
            for (String dir : DIRECTORIOS_BUSQUEDA) {
                for (String ext : EXTENSIONES) {
                    try {
                        String rutaRecurso = "/" + (dir.startsWith("/") ? dir.substring(1) : dir) + nombre + ext;
                        java.net.URL url = GestorRecursos.class.getResource(rutaRecurso);
                        if (url == null) {
                            url = Thread.currentThread().getContextClassLoader().getResource(
                                    rutaRecurso.startsWith("/") ? rutaRecurso.substring(1) : rutaRecurso
                            );
                        }
                        if (url != null) {
                            BufferedImage leida = ImageIO.read(url);
                            if (leida != null) return leida;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return null;
    }

    private static java.util.List<String> generarNombresCandidatos(String tipoEntidad) {
        java.util.List<String> nombres = new java.util.ArrayList<>();
        String base = tipoEntidad.toLowerCase().trim();
        nombres.add(base);

        // Normalización sin acentos (á -> a, etc.)
        String normalizado = base.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
        if (!nombres.contains(normalizado)) {
            nombres.add(normalizado);
        }

        // Alias comunes según las entidades del juego
        if (normalizado.contains("jefe")) {
            if (!nombres.contains("dragon")) nombres.add("dragon");
            if (!nombres.contains("jefe_dragon")) nombres.add("jefe_dragon");
        }
        if (normalizado.contains("dragon")) {
            if (!nombres.contains("jefe")) nombres.add("jefe");
        }
        if (normalizado.contains("curador")) {
            if (!nombres.contains("sacerdote")) nombres.add("sacerdote");
            if (!nombres.contains("clerigo")) nombres.add("clerigo");
        }

        return nombres;
    }

    private static Image escalarImagen(BufferedImage origen, int ancho, int alto) {
        BufferedImage escalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = escalada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(origen, 0, 0, ancho, alto, null);
        g2.dispose();
        return escalada;
    }

    private static BufferedImage generarSpriteProcedural(String tipo, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        String t = tipo.toLowerCase();

        // Sombra en el suelo
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(w / 2 - w / 3, h - h / 6, (int) (w * 0.66), h / 8);

        // Fondo circular suave para el sprite
        GradientPaint fondoGrad = new GradientPaint(0, 0, new Color(35, 40, 55), w, h, new Color(22, 26, 38));
        g2.setPaint(fondoGrad);
        g2.fillOval(4, 4, w - 8, h - 8);
        g2.setColor(new Color(65, 75, 105));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(4, 4, w - 8, h - 8);

        int cx = w / 2;
        int cy = h / 2;

        if (t.contains("guerrero")) {
            // Yelmo de caballero y escudo
            g2.setColor(new Color(41, 128, 185)); // Azul armadura
            g2.fillRoundRect(cx - 24, cy - 10, 48, 40, 16, 16);
            // Detalle de armadura
            g2.setColor(new Color(52, 152, 219));
            g2.fillRoundRect(cx - 18, cy - 6, 36, 8, 6, 6);
            g2.setColor(new Color(189, 195, 199)); // Casco de acero
            g2.fillArc(cx - 20, cy - 35, 40, 40, 0, 180);
            g2.fillRect(cx - 20, cy - 15, 40, 16);
            // Reflejos del casco
            g2.setColor(new Color(220, 220, 230, 80));
            g2.fillArc(cx - 16, cy - 33, 20, 20, 30, 120);
            // Visor
            g2.setColor(new Color(44, 62, 80));
            g2.fillRect(cx - 14, cy - 10, 28, 6);
            // Ojos brillantes tras el visor
            g2.setColor(new Color(52, 152, 219, 200));
            g2.fillOval(cx - 10, cy - 9, 4, 4);
            g2.fillOval(cx + 6, cy - 9, 4, 4);
            // Pluma dorada
            g2.setColor(new Color(241, 196, 15));
            g2.fillOval(cx - 4, cy - 40, 8, 12);
            // Espada cruzada
            g2.setColor(new Color(236, 240, 241));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(cx + 16, cy + 20, cx + 32, cy - 10);
            g2.setColor(new Color(230, 126, 34));
            g2.drawLine(cx + 12, cy + 14, cx + 22, cy + 22);

        } else if (t.contains("mago")) {
            // Mago: Túnica violeta y sombrero puntiagudo
            g2.setColor(new Color(142, 68, 173));
            g2.fillRoundRect(cx - 22, cy - 8, 44, 42, 14, 14);
            // Detalle de la túnica
            g2.setColor(new Color(125, 55, 155));
            g2.fillRoundRect(cx - 16, cy + 10, 32, 12, 8, 8);
            // Sombrero puntiagudo
            int[] xHat = {cx - 26, cx, cx + 26};
            int[] yHat = {cy - 12, cy - 42, cy - 12};
            g2.setColor(new Color(91, 44, 111));
            g2.fillPolygon(xHat, yHat, 3);
            // Borde del sombrero
            g2.setColor(new Color(142, 68, 173));
            g2.fillRoundRect(cx - 28, cy - 14, 56, 6, 4, 4);
            g2.setColor(new Color(241, 196, 15));
            g2.fillOval(cx - 4, cy - 25, 8, 8); // Estrella / broche
            // Orbe místico en la mano con brillo
            g2.setColor(new Color(52, 152, 219, 200));
            g2.fillOval(cx + 14, cy + 2, 18, 18);
            g2.setColor(new Color(100, 200, 255, 120));
            g2.fillOval(cx + 16, cy + 4, 14, 14);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillOval(cx + 18, cy + 6, 8, 8);

        } else if (t.contains("arquero")) {
            // Arquero: Capucha esmeralda y arco
            g2.setColor(new Color(39, 174, 96));
            g2.fillRoundRect(cx - 20, cy - 10, 40, 42, 12, 12);
            // Detalle del torso
            g2.setColor(new Color(30, 140, 80));
            g2.fillRoundRect(cx - 14, cy + 6, 28, 12, 8, 8);
            g2.setColor(new Color(30, 132, 73));
            g2.fillOval(cx - 18, cy - 32, 36, 30);
            // Pluma en capucha
            g2.setColor(new Color(231, 76, 60));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx - 10, cy - 35, cx - 18, cy - 22);
            // Arco de madera
            g2.setColor(new Color(160, 82, 45));
            g2.setStroke(new BasicStroke(3));
            g2.drawArc(cx + 10, cy - 20, 22, 50, -90, 180);
            // Cuerda del arco
            g2.setColor(new Color(240, 240, 240));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(cx + 21, cy - 20, cx + 21, cy + 30);
            // Flecha
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx + 5, cy + 5, cx + 25, cy + 5);
            // Punta de flecha
            g2.setColor(new Color(192, 57, 43));
            int[] xPunta = {cx + 25, cx + 30, cx + 25};
            int[] yPunta = {cy + 2, cy + 5, cy + 8};
            g2.fillPolygon(xPunta, yPunta, 3);

        } else if (t.contains("curador")) {
            // Curador / Sacerdote: Túnica blanca, halo y báculo santo
            g2.setColor(new Color(236, 240, 241));
            g2.fillRoundRect(cx - 20, cy - 10, 40, 42, 12, 12);
            // Detalles de la túnica
            g2.setColor(new Color(241, 196, 15, 80));
            g2.fillRoundRect(cx - 14, cy - 4, 28, 8, 6, 6);
            // Halo dorado
            g2.setColor(new Color(241, 196, 15, 220));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(cx - 16, cy - 40, 32, 14);
            // Brillo del halo
            g2.setColor(new Color(241, 196, 15, 60));
            g2.fillOval(cx - 20, cy - 44, 40, 22);
            // Cruz sagrada
            g2.setColor(new Color(241, 196, 15));
            g2.fillRect(cx - 3, cy, 6, 20);
            g2.fillRect(cx - 8, cy + 5, 16, 6);
            // Báculo con gema esmeralda
            g2.setColor(new Color(189, 195, 199));
            g2.fillRect(cx - 26, cy - 25, 4, 55);
            g2.setColor(new Color(46, 204, 113));
            g2.fillOval(cx - 30, cy - 32, 12, 12);
            // Brillo de la gema
            g2.setColor(new Color(46, 204, 113, 100));
            g2.fillOval(cx - 34, cy - 36, 20, 20);

        } else if (t.contains("goblin")) {
            // Goblin: Piel verde, orejas puntiagudas, daga
            g2.setColor(new Color(76, 175, 80));
            g2.fillOval(cx - 18, cy - 25, 36, 32);
            // Manchas de piel
            g2.setColor(new Color(60, 150, 65));
            g2.fillOval(cx - 10, cy - 10, 8, 8);
            // Cuerpo
            g2.setColor(new Color(56, 142, 60));
            g2.fillRoundRect(cx - 14, cy + 5, 28, 22, 10, 10);
            // Orejas
            int[] xOrejaIzq = {cx - 16, cx - 34, cx - 14};
            int[] yOrejaIzq = {cy - 18, cy - 26, cy - 8};
            g2.setColor(new Color(76, 175, 80));
            g2.fillPolygon(xOrejaIzq, yOrejaIzq, 3);
            int[] xOrejaDer = {cx + 16, cx + 34, cx + 14};
            int[] yOrejaDer = {cy - 18, cy - 26, cy - 8};
            g2.fillPolygon(xOrejaDer, yOrejaDer, 3);
            // Ojos rojos pícaros
            g2.setColor(new Color(231, 76, 60));
            g2.fillOval(cx - 10, cy - 16, 6, 8);
            g2.fillOval(cx + 4, cy - 16, 6, 8);
            // Pupilas
            g2.setColor(Color.BLACK);
            g2.fillOval(cx - 8, cy - 14, 3, 4);
            g2.fillOval(cx + 6, cy - 14, 3, 4);
            // Sonrisa malvada
            g2.setColor(new Color(40, 80, 40));
            g2.setStroke(new BasicStroke(1));
            g2.drawArc(cx - 8, cy - 8, 16, 10, 200, 140);
            // Dagas
            g2.setColor(new Color(189, 195, 199));
            g2.fillRect(cx - 24, cy + 8, 12, 4);
            g2.fillRect(cx + 12, cy + 8, 12, 4);

        } else if (t.contains("orco")) {
            // Orco: Verde oscuro imponente, colmillos gigantes, armadura con pinchos
            g2.setColor(new Color(46, 117, 89));
            g2.fillRoundRect(cx - 26, cy - 15, 52, 45, 16, 16);
            // Hombreras con pinchos
            g2.setColor(new Color(100, 100, 100));
            g2.fillOval(cx - 30, cy - 12, 14, 14);
            g2.fillOval(cx + 16, cy - 12, 14, 14);
            g2.setColor(new Color(180, 180, 180));
            int[] xPincho1 = {cx - 25, cx - 23, cx - 21};
            int[] yPincho1 = {cy - 12, cy - 22, cy - 12};
            g2.fillPolygon(xPincho1, yPincho1, 3);
            int[] xPincho2 = {cx + 21, cx + 23, cx + 25};
            int[] yPincho2 = {cy - 12, cy - 22, cy - 12};
            g2.fillPolygon(xPincho2, yPincho2, 3);
            // Cabeza
            g2.setColor(new Color(39, 100, 75));
            g2.fillOval(cx - 22, cy - 35, 44, 38);
            // Ojos furiosos
            g2.setColor(new Color(241, 196, 15));
            g2.fillOval(cx - 12, cy - 24, 8, 6);
            g2.fillOval(cx + 4, cy - 24, 8, 6);
            g2.setColor(Color.BLACK);
            g2.fillOval(cx - 9, cy - 23, 3, 4);
            g2.fillOval(cx + 7, cy - 23, 3, 4);
            // Colmillos inferiores
            g2.setColor(new Color(250, 250, 250));
            int[] xColmillo1 = {cx - 10, cx - 7, cx - 4};
            int[] yColmillo1 = {cy - 10, cy - 18, cy - 10};
            g2.fillPolygon(xColmillo1, yColmillo1, 3);
            int[] xColmillo2 = {cx + 4, cx + 7, cx + 10};
            int[] yColmillo2 = {cy - 10, cy - 18, cy - 10};
            g2.fillPolygon(xColmillo2, yColmillo2, 3);
            // Hacha doble de batalla
            g2.setColor(new Color(149, 165, 166));
            g2.fillArc(cx + 18, cy - 15, 18, 28, 90, 180);

        } else if (t.contains("esqueleto")) {
            // Esqueleto: Cráneo, costillas y espada quebrada
            // Cuerpo óseo
            g2.setColor(new Color(220, 215, 200));
            g2.fillRoundRect(cx - 16, cy - 4, 32, 34, 8, 8);
            // Costillas
            g2.setColor(new Color(180, 175, 160));
            for (int i = 0; i < 4; i++) {
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(cx - 12, cy + 2 + i * 7, cx + 12, cy + 2 + i * 7);
            }
            // Cráneo
            g2.setColor(new Color(235, 230, 215));
            g2.fillOval(cx - 18, cy - 35, 36, 36);
            // Cuencas de los ojos (negras con brillo rojo)
            g2.setColor(new Color(20, 20, 20));
            g2.fillOval(cx - 12, cy - 25, 10, 10);
            g2.fillOval(cx + 2, cy - 25, 10, 10);
            g2.setColor(new Color(231, 76, 60, 200));
            g2.fillOval(cx - 9, cy - 22, 4, 4);
            g2.fillOval(cx + 5, cy - 22, 4, 4);
            // Nariz
            g2.setColor(new Color(20, 20, 20));
            int[] xNariz = {cx - 3, cx, cx + 3};
            int[] yNariz = {cy - 12, cy - 16, cy - 12};
            g2.fillPolygon(xNariz, yNariz, 3);
            // Mandíbula
            g2.setColor(new Color(200, 195, 180));
            g2.fillRect(cx - 10, cy - 8, 20, 6);
            g2.setColor(new Color(30, 30, 30));
            for (int i = 0; i < 5; i++) {
                g2.fillRect(cx - 8 + i * 4, cy - 7, 2, 4);
            }
            // Espada quebrada
            g2.setColor(new Color(160, 160, 170));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(cx + 14, cy + 20, cx + 28, cy - 5);
            g2.drawLine(cx + 28, cy - 5, cx + 25, cy - 8);

        } else if (t.contains("chaman")) {
            // Chamán: Máscara de calavera, humo violeta
            g2.setColor(new Color(125, 60, 152));
            g2.fillRoundRect(cx - 20, cy - 10, 40, 42, 10, 10);
            // Collar tribal
            g2.setColor(new Color(200, 170, 50));
            g2.setStroke(new BasicStroke(2));
            g2.drawArc(cx - 14, cy - 6, 28, 16, 0, -180);
            // Máscara tribal
            g2.setColor(new Color(245, 238, 220));
            g2.fillOval(cx - 16, cy - 30, 32, 30);
            // Marcas tribales
            g2.setColor(new Color(192, 57, 43));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx - 6, cy - 30, cx - 6, cy - 24);
            g2.drawLine(cx + 6, cy - 30, cx + 6, cy - 24);
            // Ojos oscuros
            g2.setColor(new Color(24, 24, 24));
            g2.fillOval(cx - 10, cy - 22, 6, 8);
            g2.fillOval(cx + 4, cy - 22, 6, 8);
            // Brillo de ojos
            g2.setColor(new Color(155, 89, 182, 200));
            g2.fillOval(cx - 8, cy - 20, 3, 3);
            g2.fillOval(cx + 6, cy - 20, 3, 3);
            // Fuego púrpura
            g2.setColor(new Color(187, 143, 206, 180));
            g2.fillOval(cx - 28, cy - 10, 16, 20);
            g2.setColor(new Color(155, 89, 182, 100));
            g2.fillOval(cx - 32, cy - 15, 22, 28);

        } else if (t.contains("jefe") || t.contains("dragon")) {
            // Dragón / Jefe supremo: Alas rojas, cuernos y aliento ígneo
            g2.setColor(new Color(192, 57, 43));
            g2.fillRoundRect(cx - 26, cy - 15, 52, 46, 16, 16);
            // Escamas
            g2.setColor(new Color(170, 45, 35));
            for (int i = 0; i < 3; i++) {
                g2.fillOval(cx - 18 + i * 14, cy - 8, 10, 10);
            }
            // Cabeza draconiana
            g2.setColor(new Color(150, 40, 27));
            g2.fillOval(cx - 22, cy - 35, 44, 38);
            // Cuernos
            g2.setColor(new Color(243, 156, 18));
            int[] xCuerno1 = {cx - 20, cx - 32, cx - 14};
            int[] yCuerno1 = {cy - 25, cy - 42, cy - 30};
            g2.fillPolygon(xCuerno1, yCuerno1, 3);
            int[] xCuerno2 = {cx + 20, cx + 32, cx + 14};
            int[] yCuerno2 = {cy - 25, cy - 42, cy - 30};
            g2.fillPolygon(xCuerno2, yCuerno2, 3);
            // Ojo de reptil
            g2.setColor(new Color(241, 196, 15));
            g2.fillOval(cx - 12, cy - 24, 8, 10);
            g2.fillOval(cx + 4, cy - 24, 8, 10);
            g2.setColor(Color.BLACK);
            g2.fillRect(cx - 9, cy - 24, 2, 10);
            g2.fillRect(cx + 7, cy - 24, 2, 10);
            // Llamas
            g2.setColor(new Color(230, 126, 34, 220));
            g2.fillOval(cx - 10, cy + 5, 20, 15);
            g2.setColor(new Color(241, 196, 15, 160));
            g2.fillOval(cx - 6, cy + 8, 12, 10);
        } else {
            // Genérico
            g2.setColor(new Color(120, 140, 160));
            g2.fillOval(cx - 18, cy - 25, 36, 36);
            g2.fillRoundRect(cx - 20, cy + 10, 40, 25, 10, 10);
        }

        g2.dispose();
        return img;
    }
}
