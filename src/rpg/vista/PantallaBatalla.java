package rpg.vista;

import rpg.controlador.ControladorBatalla;
import rpg.controlador.ControladorJuego;
import rpg.dto.*;
import rpg.dto.RecompensaDTO;
import rpg.modelo.Juego;
import rpg.modelo.Party;
import rpg.util.EstilosUI;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación GUI (JFrame) de "El Último Ascenso" (MVC - Clase 8 / Clase 13).
 * Contiene un CardLayout para gestionar las pantallas del flujo de juego:
 * 1. Pantalla Inicial (INICIO)
 * 2. Pantalla de la Torre (TORRE)
 * 3. Pantalla de Mapa del Piso (MAPA)
 * 4. Pantalla de Kiosco / Mercader (KIOSCO)
 * 5. Pantalla de Batalla (COMBATE)
 * 6. Pantalla de Resultados Post-Combate (RESULTADOS)
 * 7. Pantalla de Estado de la Party y Slots de Equipamiento (PARTY)
 * 8. Pantalla de Evento Narrativo (EVENTO)
 * 9. Pantalla de Descanso / Fogón (DESCANSO)
 */
public class PantallaBatalla extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel panelContenedorCards;

    // Pantallas del juego
    private final PantallaInicio pantallaInicio;
    private final PantallaTorre pantallaTorre;
    private final PantallaCreacionParty pantallaCreacion;
    private final PantallaMapa pantallaMapa;
    private final PantallaKiosco pantallaKiosco;
    private final PantallaResultados pantallaResultados;
    private final PantallaEstadoParty pantallaEstadoParty;
    private final PantallaEvento pantallaEvento;
    private final PantallaDescanso pantallaDescanso;

    // Componentes del Combate
    private final PanelEscenaBatalla panelEscena;
    private final PanelAcciones panelAcciones;
    private final PanelRegistro panelRegistro;

    public PantallaBatalla() {
        super("El Último Ascenso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 840);
        setMinimumSize(new Dimension(1050, 740));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenedorCards = new JPanel(cardLayout);
        panelContenedorCards.setBackground(EstilosUI.FONDO_OSCURO);

        // 1. Instanciación de Pantallas
        pantallaInicio = new PantallaInicio();
        pantallaTorre = new PantallaTorre();
        pantallaCreacion = new PantallaCreacionParty();
        pantallaMapa = new PantallaMapa();
        pantallaKiosco = new PantallaKiosco();
        pantallaResultados = new PantallaResultados();
        pantallaEstadoParty = new PantallaEstadoParty();
        pantallaEvento = new PantallaEvento();
        pantallaDescanso = new PantallaDescanso();

        // 2. Construcción del Panel de Combate Integrado (sin duplicar entidades)
        JPanel panelCombateCompleto = new JPanel(new BorderLayout(10, 10));
        panelCombateCompleto.setBackground(EstilosUI.FONDO_OSCURO);
        panelCombateCompleto.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        panelEscena = new PanelEscenaBatalla();
        panelAcciones = new PanelAcciones();
        panelRegistro = new PanelRegistro();

        // Escena de batalla arriba en el centro
        panelCombateCompleto.add(panelEscena, BorderLayout.CENTER);

        // Registro de combate y acciones abajo
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setOpaque(false);
        panelInferior.add(panelRegistro, BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);
        panelCombateCompleto.add(panelInferior, BorderLayout.SOUTH);

        // 3. Registrar pantallas en el CardLayout
        panelContenedorCards.add(pantallaInicio, "INICIO");
        panelContenedorCards.add(pantallaTorre, "TORRE");
        panelContenedorCards.add(pantallaCreacion, "CREACION");
        panelContenedorCards.add(pantallaMapa, "MAPA");
        panelContenedorCards.add(pantallaKiosco, "KIOSCO");
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
        pantallaTorre.setControlador(controladorJuego);
        pantallaCreacion.setControlador(controladorJuego);
        pantallaMapa.setControlador(controladorJuego);
        pantallaKiosco.setControlador(controladorJuego);
        pantallaResultados.setControlador(controladorJuego);
        pantallaEstadoParty.setControlador(controladorJuego);
        pantallaEvento.setControlador(controladorJuego);
        pantallaDescanso.setControlador(controladorJuego);
    }

    public void mostrarPantalla(String id) {
        cardLayout.show(panelContenedorCards, id);
    }

    public void actualizarVista(EstadoBatallaDTO estadoDTO) {
        setTitle("El Último Ascenso - [" + estadoDTO.getLugar() + "] Ronda " + estadoDTO.getRondaActual());
        panelEscena.actualizar(estadoDTO);
        panelAcciones.actualizar(estadoDTO);
        panelRegistro.actualizar(estadoDTO);
    }

    public void actualizarTorre(Juego juego) {
        pantallaTorre.actualizar(juego);
    }

    public void actualizarMapa(MapaDTO mapaDTO, int metaPuntos) {
        pantallaMapa.actualizar(mapaDTO, metaPuntos);
    }

    public void actualizarMapa(MapaDTO mapaDTO, int oroActual, boolean tienePuntosPendientes) {
        pantallaMapa.actualizar(mapaDTO, oroActual, tienePuntosPendientes);
    }

    public void actualizarKiosco(Party party) {
        pantallaKiosco.actualizar(party);
    }

    public void actualizarResultados(EstadoBatallaDTO estadoDTO, RecompensaDTO recompensa) {
        pantallaResultados.actualizar(estadoDTO, recompensa);
    }

    public void actualizarResultados(EstadoBatallaDTO estadoDTO, RecompensaDTO recompensa, Juego juego) {
        pantallaResultados.actualizar(estadoDTO, recompensa, juego);
    }

    public void actualizarGestionParty(PartyGestionDTO dto) {
        pantallaEstadoParty.actualizar(dto);
    }

    public void actualizarGestionParty(PartyGestionDTO dto, Party party) {
        pantallaEstadoParty.actualizar(dto, party);
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
