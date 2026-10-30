package rpg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO que representa un héroe de la Party con información adicional
 * como experiencia, slots de equipamiento, puntos de atributo libres y lista de habilidades.
 */
public class PersonajeDTO extends EntidadDTO {
    private final int experiencia;
    private final int experienciaSiguienteNivel;
    private final String armaEquipada;
    private final String armaduraEquipada;
    private final int puntosAtributoDisponibles;
    private final List<String> habilidades;
    private final List<HabilidadDTO> habilidadesDTO;

    public PersonajeDTO(String nombre, String tipo, int vidaActual, int vidaMaxima,
                        int manaActual, int manaMaximo, int nivel, int ataque,
                        int defensa, int velocidad, boolean estaVivo, boolean defendiendo,
                        String rutaSprite, int experiencia, int experienciaSiguienteNivel,
                        String armaEquipada, String armaduraEquipada,
                        int puntosAtributoDisponibles, List<String> habilidades,
                        List<HabilidadDTO> habilidadesDTO) {
        super(nombre, tipo, vidaActual, vidaMaxima, manaActual, manaMaximo, nivel,
              ataque, defensa, velocidad, estaVivo, defendiendo, rutaSprite, true);
        this.experiencia = experiencia;
        this.experienciaSiguienteNivel = experienciaSiguienteNivel;
        this.armaEquipada = armaEquipada;
        this.armaduraEquipada = armaduraEquipada;
        this.puntosAtributoDisponibles = puntosAtributoDisponibles;
        this.habilidades = habilidades != null ? Collections.unmodifiableList(new ArrayList<>(habilidades)) : Collections.emptyList();
        this.habilidadesDTO = habilidadesDTO != null ? Collections.unmodifiableList(new ArrayList<>(habilidadesDTO)) : Collections.emptyList();
    }

    public PersonajeDTO(String nombre, String tipo, int vidaActual, int vidaMaxima,
                        int manaActual, int manaMaximo, int nivel, int ataque,
                        int defensa, int velocidad, boolean estaVivo, boolean defendiendo,
                        String rutaSprite, int experiencia, int experienciaSiguienteNivel,
                        String armaEquipada, String armaduraEquipada,
                        int puntosAtributoDisponibles, List<String> habilidades) {
        this(nombre, tipo, vidaActual, vidaMaxima, manaActual, manaMaximo, nivel,
             ataque, defensa, velocidad, estaVivo, defendiendo, rutaSprite, experiencia,
             experienciaSiguienteNivel, armaEquipada, armaduraEquipada, puntosAtributoDisponibles,
             habilidades, Collections.emptyList());
    }

    public PersonajeDTO(String nombre, String tipo, int vidaActual, int vidaMaxima,
                        int manaActual, int manaMaximo, int nivel, int ataque,
                        int defensa, int velocidad, boolean estaVivo, boolean defendiendo,
                        String rutaSprite, int experiencia, int experienciaSiguienteNivel,
                        String armaEquipada, String armaduraEquipada) {
        this(nombre, tipo, vidaActual, vidaMaxima, manaActual, manaMaximo, nivel,
             ataque, defensa, velocidad, estaVivo, defendiendo, rutaSprite, experiencia,
             experienciaSiguienteNivel, armaEquipada, armaduraEquipada, 0, Collections.emptyList());
    }

    public int getExperiencia() {
        return experiencia;
    }

    public int getExperienciaSiguienteNivel() {
        return experienciaSiguienteNivel;
    }

    public String getArmaEquipada() {
        return armaEquipada;
    }

    public String getArmaduraEquipada() {
        return armaduraEquipada;
    }

    public int getPuntosAtributoDisponibles() {
        return puntosAtributoDisponibles;
    }

    public List<String> getHabilidades() {
        return habilidades;
    }

    public List<HabilidadDTO> getHabilidadesDTO() {
        return habilidadesDTO;
    }
}
