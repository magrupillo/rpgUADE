package rpg.modelo;

/**
 * Interfaz que define el patrón Visitor para los nodos del mapa.
 * Permite el despacho polimórfico de acciones y transiciones de vista
 * sin requerir condicionales ni comprobaciones de tipo (instanceof).
 */
public interface NodoVisitor {
    void visitarCombate(Nodo nodo);
    void visitarEvento(NodoEvento nodo);
    void visitarFogata(NodoFogata nodo);
    void visitarKiosco(NodoKiosco nodo);
}
