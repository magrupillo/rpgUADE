package rpg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapaDTO {
    private final int piso;
    private final int idNodoActual;
    private final List<NodoDTO> nodos;

    public MapaDTO(int piso, int idNodoActual, List<NodoDTO> nodos) {
        this.piso = piso;
        this.idNodoActual = idNodoActual;
        this.nodos = Collections.unmodifiableList(new ArrayList<>(nodos));
    }

    public int getPiso() {
        return piso;
    }

    public int getIdNodoActual() {
        return idNodoActual;
    }

    public List<NodoDTO> getNodos() {
        return nodos;
    }
}
