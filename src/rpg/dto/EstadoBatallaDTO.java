package rpg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO que encapsula el estado completo del combate para actualizar la interfaz gráfica.
 * Incluye la colaIniciativa (nombres ordenados por velocidad) para visualizar el orden de turnos.
 */
public class EstadoBatallaDTO {
    private final List<PersonajeDTO> heroes;
    private final List<EntidadDTO> enemigos;
    private final String turnoActual;
    private final int indiceHeroeActivo;
    private final List<ItemDTO> inventario;
    private final List<String> registroDeCombate;
    private final boolean batallaTerminada;
    private final boolean victoria;
    private final String mensajeFinBatalla;
    private final String lugar;
    private final int rondaActual;
    /** Cola de iniciativa: lista de "nombre|esHeroe" en orden de actuación para esta ronda */
    private final List<String> colaIniciativa;
    private final int posicionActualEnCola;

    public EstadoBatallaDTO(List<PersonajeDTO> heroes, List<EntidadDTO> enemigos,
                            String turnoActual, int indiceHeroeActivo,
                            List<ItemDTO> inventario, List<String> registroDeCombate,
                            boolean batallaTerminada, boolean victoria,
                            String mensajeFinBatalla, String lugar, int rondaActual,
                            List<String> colaIniciativa, int posicionActualEnCola) {
        this.heroes = Collections.unmodifiableList(new ArrayList<>(heroes));
        this.enemigos = Collections.unmodifiableList(new ArrayList<>(enemigos));
        this.turnoActual = turnoActual;
        this.indiceHeroeActivo = indiceHeroeActivo;
        this.inventario = Collections.unmodifiableList(new ArrayList<>(inventario));
        this.registroDeCombate = Collections.unmodifiableList(new ArrayList<>(registroDeCombate));
        this.batallaTerminada = batallaTerminada;
        this.victoria = victoria;
        this.mensajeFinBatalla = mensajeFinBatalla;
        this.lugar = lugar;
        this.rondaActual = rondaActual;
        this.colaIniciativa = Collections.unmodifiableList(new ArrayList<>(colaIniciativa));
        this.posicionActualEnCola = posicionActualEnCola;
    }

    public List<PersonajeDTO> getHeroes() { return heroes; }
    public List<EntidadDTO> getEnemigos() { return enemigos; }
    public String getTurnoActual() { return turnoActual; }
    public int getIndiceHeroeActivo() { return indiceHeroeActivo; }
    public List<ItemDTO> getInventario() { return inventario; }
    public List<String> getRegistroDeCombate() { return registroDeCombate; }
    public boolean isBatallaTerminada() { return batallaTerminada; }
    public boolean isVictoria() { return victoria; }
    public String getMensajeFinBatalla() { return mensajeFinBatalla; }
    public String getLugar() { return lugar; }
    public int getRondaActual() { return rondaActual; }
    public List<String> getColaIniciativa() { return colaIniciativa; }
    public int getPosicionActualEnCola() { return posicionActualEnCola; }
}
