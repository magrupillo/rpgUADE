package rpg.controlador;

import rpg.dto.EstadoBatallaDTO;
import rpg.modelo.Batalla;
import rpg.vista.PantallaBatalla;

/**
 * Controlador específico del combate en MVC.
 * Coordina las acciones tácticas entre la {@link PantallaBatalla} y el modelo {@link Batalla}.
 *
 * Durante el turno de los enemigos, todos los botones de la interfaz permanecen bloqueados
 * hasta que el controlador devuelva el turno a la party (manejado en la vista).
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

    public void ejecutarAtaqueBasico(int indiceHeroe, int indiceObjetivo) {
        if (batalla.verificarFin()) return;
        batalla.ejecutarAtaqueHeroe(indiceHeroe, indiceObjetivo);
        actualizarVista();
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

    public void defender(int indiceHeroe) {
        if (batalla.verificarFin()) return;
        batalla.defenderHeroe(indiceHeroe);
        actualizarVista();
    }

    // ─── Fin de combate ───────────────────────────────────────────────────────────

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
