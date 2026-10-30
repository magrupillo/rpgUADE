package rpg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyGestionDTO {
    private final List<PersonajeDTO> heroes;
    private final List<ItemDTO> inventarioArmas;
    private final List<ItemDTO> inventarioArmaduras;
    private final List<ItemDTO> inventarioConsumibles;
    private final int metaPuntos;

    public PartyGestionDTO(List<PersonajeDTO> heroes,
                           List<ItemDTO> inventarioArmas,
                           List<ItemDTO> inventarioArmaduras,
                           List<ItemDTO> inventarioConsumibles,
                           int metaPuntos) {
        this.heroes = Collections.unmodifiableList(new ArrayList<>(heroes));
        this.inventarioArmas = Collections.unmodifiableList(new ArrayList<>(inventarioArmas));
        this.inventarioArmaduras = Collections.unmodifiableList(new ArrayList<>(inventarioArmaduras));
        this.inventarioConsumibles = Collections.unmodifiableList(new ArrayList<>(inventarioConsumibles));
        this.metaPuntos = metaPuntos;
    }

    public List<PersonajeDTO> getHeroes() {
        return heroes;
    }

    public List<ItemDTO> getInventarioArmas() {
        return inventarioArmas;
    }

    public List<ItemDTO> getInventarioArmaduras() {
        return inventarioArmaduras;
    }

    public List<ItemDTO> getInventarioConsumibles() {
        return inventarioConsumibles;
    }

    public int getMetaPuntos() {
        return metaPuntos;
    }
}
