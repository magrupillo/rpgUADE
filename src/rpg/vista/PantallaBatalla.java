package rpg.vista;

import rpg.controlador.ControladorBatalla;
import rpg.controlador.ControladorJuego;
import rpg.dto.*;
import rpg.dto.RecompensaDTO;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación GUI (JFrame) del RPG por Turnos (MVC - Clase 8).
 * Contiene un CardLayout para gestionar las pantallas del flujo de juego:
 * 1. Pantalla Inicial (Nueva partida, Cargar, Salir)
 * 2. Pantalla de Creación y Configuración de Party
 * 3. Pantalla de Mapa de la Torre (Nodos y progresión)
 * 4. Pantalla de Batalla (Escena, Estados, Comandos y Registro)
 * 5. Pantalla de Resultados Post-Combate
 * 6. Pantalla de Estado de la Party y Slots de Equipamiento
 * 7. Pantalla de Evento Narrativo
 * 8. Pantalla de Descanso / Fogata
 */
public class PantallaBatalla extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel panelContenedorCards;

    // Pantallas del juego
    private final PantallaInicio pantallaInicio;
    private final PantallaCreacionParty pantallaCreacion;
    private final PantallaMapa pantallaMapa;
    private final PantallaResultados pantallaResultados;
    private final PantallaEstadoParty pantallaEstadoParty;
    private final PantallaEvento pantallaEvento;
    private final PantallaDescanso pantallaDescanso;

    // Componentes del Combate
    private final PanelEscenaBatalla panelEscena;
    private final PanelEstadoEntidad panelEstado;
    private final PanelAcciones panelAcciones;
    private final PanelRegistro panelRegistro;

    public PantallaBatalla() {
        super("⚔️ Tower Crawler RPG - Motor Táctico por Turnos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 840);
        setMinimumSize(new Dimension(1050, 740));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenedorCards = new JPanel(cardLayout);
        panelContenedorCards.setBackground(EstilosUI.FONDO_OSCURO);

        // 1. Instanciación de Pantallas
        pantallaInicio = new PantallaInicio();
        pantallaCreacion = new PantallaCreacionParty();
        pantallaMapa = new PantallaMapa();
        pantallaResultados = new PantallaResultados();
        pantallaEstadoParty = new PantallaEstadoParty();
        pantallaEvento = new PantallaEvento();
        pantallaDescanso = new PantallaDescanso();

        // 2. Construcción del Panel de Combate Integrado
        JPanel panelCombateCompleto = new JPanel(new BorderLayout(10, 10));
        panelCombateCompleto.setBackground(EstilosUI.FONDO_OSCURO);
        panelCombateCompleto.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        panelEscena = new PanelEscenaBatalla();
        panelEstado = new PanelEstadoEntidad();
        panelAcciones = new PanelAcciones();
        panelRegistro = new PanelRegistro();

        JPanel panelSuperiorCentral = new JPanel(new BorderLayout(0, 10));
        panelSuperiorCentral.setOpaque(false);
        panelSuperiorCentral.add(panelEscena, BorderLayout.CENTER);
        panelSuperiorCentral.add(panelEstado, BorderLayout.SOUTH);
        panelCombateCompleto.add(panelSuperiorCentral, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setOpaque(false);
        panelInferior.add(panelRegistro, BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);
        panelCombateCompleto.add(panelInferior, BorderLayout.SOUTH);

        // 3. Registrar pantallas en el CardLayout
        panelContenedorCards.add(pantallaInicio, "INICIO");
        panelContenedorCards.add(pantallaCreacion, "CREACION");
        panelContenedorCards.add(pantallaMapa, "MAPA");
        panelContenedorCards.add(panelCombateCompleto, "COMBATE");
        panelContenedorCards.add(pantallaResultados, "RESULTADOS");
        panelContenedorCards.add(pantallaEstadoParty, "PARTY");
        panelContenedorCards.add(pantallaEvento, "EVENTO");
        panelContenedorCards.add(pantallaDescanso, "DESCANSO");

        setContentPane(panelContenedorCards);
    }

    public void setControlador(ControladorBatalla controlador) {
        panelAcciones.setControlador(controlador);
    }

    public void setControladorJuego(ControladorJuego controladorJuego) {
        pantallaInicio.setControlador(controladorJuego);
        pantallaCreacion.setControlador(controladorJuego);
        pantallaMapa.setControlador(controladorJuego);
        pantallaResultados.setControlador(controladorJuego);
        pantallaEstadoParty.setControlador(controladorJuego);
        pantallaEvento.setControlador(controladorJuego);
        pantallaDescanso.setControlador(controladorJuego);
    }

    public void mostrarPantalla(String id) {
        cardLayout.show(panelContenedorCards, id);
    }

    public void actualizarVista(EstadoBatallaDTO estadoDTO) {
        setTitle("⚔️ RPG Táctico - [" + estadoDTO.getLugar() + "] Ronda " + estadoDTO.getRondaActual());
        panelEscena.actualizar(estadoDTO);
        panelEstado.actualizar(estadoDTO);
        panelAcciones.actualizar(estadoDTO);
        panelRegistro.actualizar(estadoDTO);
    }

    public void actualizarMapa(MapaDTO mapaDTO, int metaPuntos) {
        pantallaMapa.actualizar(mapaDTO, metaPuntos);
    }

    public void actualizarResultados(EstadoBatallaDTO estadoDTO, RecompensaDTO recompensa) {
        pantallaResultados.actualizar(estadoDTO, recompensa);
    }

    public void actualizarGestionParty(PartyGestionDTO dto) {
        pantallaEstadoParty.actualizar(dto);
    }

    public void actualizarEvento(EventoDTO dto) {
        pantallaEvento.actualizar(dto);
    }

    public void reiniciarVistaFogata() {
        pantallaDescanso.reiniciarVista();
    }

    public void actualizarEstadoGuardado() {
        pantallaInicio.actualizarEstadoGuardado();
    }
}
