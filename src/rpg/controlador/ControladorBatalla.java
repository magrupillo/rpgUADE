package rpg.controlador;

import rpg.dto.EstadoBatallaDTO;
import rpg.modelo.Batalla;
import rpg.vista.PantallaBatalla;

/**
 * Controlador específico del combate en MVC.
 * Coordina las acciones tácticas entre la {@link PantallaBatalla} y el modelo {@link Batalla}.
 */
public class ControladorBatalla {

    private final Batalla batalla;
    private final PantallaBatalla pantallaBatalla;
    private ControladorJuego controladorJuego;

    public ControladorBatalla(Batalla batalla, PantallaBatalla pantallaBatalla) {
        this.batalla = batalla;
        this.pantallaBatalla = pantallaBatalla;
        this.pantallaBatalla.setControlador(this);
    }

    public void setControladorJuego(ControladorJuego controladorJuego) {
        this.controladorJuego = controladorJuego;
    }

    public void iniciar() {
        actualizarVista();
    }

    // ─── Acciones del jugador ─────────────────────────────────────────────────────

    public void atacar(int indiceHeroe, int indiceObjetivo) {
        ejecutarAtaqueBasico(indiceHeroe, indiceObjetivo);
    }

    public void ejecutarAtaqueBasico(int indiceHeroe, int indiceObjetivo) {
        if (batalla.verificarFin()) return;
        batalla.ejecutarAtaqueHeroe(indiceHeroe, indiceObjetivo);
        actualizarVista();
    }

    public void usarHabilidad(int indiceHeroe, int indiceHabilidad, int indiceObjetivo) {
        ejecutarHabilidad(indiceHeroe, indiceHabilidad, indiceObjetivo);
    }

    public void ejecutarHabilidad(int indiceHeroe, int indiceObjetivo) {
        ejecutarHabilidad(indiceHeroe, 0, indiceObjetivo);
    }

    public void ejecutarHabilidad(int indiceHeroe, int indiceHabilidad, int indiceObjetivo) {
        if (batalla.verificarFin()) return;
        batalla.ejecutarHabilidadHeroe(indiceHeroe, indiceHabilidad, indiceObjetivo);
        actualizarVista();
    }

    public void usarItem(int indiceHeroe, int indiceItem) {
        if (batalla.verificarFin()) return;
        batalla.usarItemHeroe(indiceHeroe, indiceItem);
        actualizarVista();
    }

    public void usarItem(int indiceHeroe, int indiceItem, int indiceObjetivo) {
        usarItem(indiceHeroe, indiceItem);
    }

    public void defender(int indiceHeroe) {
        if (batalla.verificarFin()) return;
        batalla.defenderHeroe(indiceHeroe);
        actualizarVista();
    }

    // ─── Fin de combate ───────────────────────────────────────────────────────────

    public void procesarFinDeCombate(boolean victoria) {
        continuarFinDeCombate();
    }

    public void continuarFinDeCombate() {
        if (controladorJuego != null) {
            controladorJuego.procesarFinDeCombate(batalla.isVictoria());
        }
    }

    // ─── Vista ────────────────────────────────────────────────────────────────────

    private void actualizarVista() {
        EstadoBatallaDTO estadoDTO = batalla.crearEstadoDTO();
        pantallaBatalla.actualizarVista(estadoDTO);
    }
}
