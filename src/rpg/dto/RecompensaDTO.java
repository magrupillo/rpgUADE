package rpg.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO de transferencia de datos que encapsula toda la información sobre la
 * recompensa obtenida al finalizar un combate victorioso.
 *
 * Devuelto por {@code Batalla.calcularRecompensa()}.
 */
public class RecompensaDTO {

    private final int experienciaTotalOtorgada;
    private final int experienciaPorHeroe;
    private final int oro;
    private final List<String> itemsObtenidos;
    private final List<String> suviventeNombres;

    public RecompensaDTO(int experienciaTotalOtorgada, int experienciaPorHeroe,
                         int oro, List<String> itemsObtenidos, List<String> suviventeNombres) {
        this.experienciaTotalOtorgada = experienciaTotalOtorgada;
        this.experienciaPorHeroe = experienciaPorHeroe;
        this.oro = oro;
        this.itemsObtenidos = Collections.unmodifiableList(new ArrayList<>(itemsObtenidos));
        this.suviventeNombres = Collections.unmodifiableList(new ArrayList<>(suviventeNombres));
    }

    public int getExperienciaTotalOtorgada() { return experienciaTotalOtorgada; }
    public int getExperienciaPorHeroe()      { return experienciaPorHeroe; }
    public int getOro()                       { return oro; }
    public List<String> getItemsObtenidos()   { return itemsObtenidos; }
    public List<String> getSuviventeNombres() { return suviventeNombres; }
}
