package rpg;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("           PROYECTO RPG - SISTEMA COMPLETO      ");
        System.out.println("=================================================\n");

        // 1. Creación de Personajes (Guerrero, Arquero, Mago, Curador)
        Personaje arthur = new Guerrero("Arthur (Guerrero)");
        Personaje legolas = new Arquero("Legolas (Arquero)");
        Personaje gandalf = new Mago("Gandalf (Mago)");
        Personaje elena = new Curador("Elena (Curadora)");

        // 2. Creación de la Party
        Party party = new Party();
        party.agregarMiembro(arthur);
        party.agregarMiembro(legolas);
        party.agregarMiembro(gandalf);
        party.agregarMiembro(elena);

        // 3. Sistema de Ítems (Clases concretas: Arma, Armadura, PocionVida, PocionMana)
        Inventario inventario = party.getInventario();
        System.out.println("\n--- Adquiriendo Ítems y Equipamiento ---");

        Arma espadaHierro = new Arma("Espada de Acero", "Aumenta el ataque físico", 15);
        Armadura cotaMallas = new Armadura("Cota de Mallas", "Aumenta la defensa física", 10);
        PocionVida pocionVida1 = new PocionVida("Poción de Salud Mayor", "Restaura 70 puntos de vida", 70);
        PocionMana pocionMana1 = new PocionMana("Poción de Maná", "Restaura 50 puntos de maná", 50);

        inventario.agregarItem(espadaHierro);
        inventario.agregarItem(cotaMallas);
        inventario.agregarItem(pocionVida1);
        inventario.agregarItem(pocionMana1);

        // Equipar ítems usando métodos de Inventario
        System.out.println("\n--- Equipando a los Héroes ---");
        inventario.equiparArma(espadaHierro, arthur);
        inventario.equiparArmadura(cotaMallas, arthur);

        // Listar inventario restante
        System.out.println();
        inventario.listarItems();

        // Mostrar estado inicial de la Party
        party.mostrarEstado();

        // 4. BATALLA 1: Mazmorra (Goblins y Chamán)
        System.out.println("\n\n>>> INICIANDO BATALLA 1 <<<");
        Batalla batalla1 = new Batalla("Cavernas Sombrías", party);
        batalla1.agregarEnemigo(new Goblin("Goblin Pícaro"));
        batalla1.agregarEnemigo(new Chaman("Chamán del Pantano"));
        batalla1.iniciar();

        // Mostrar estado tras la primera batalla
        party.mostrarEstado();

        // 5. BATALLA 2: Guarida del Orco y el Jefe
        System.out.println("\n\n>>> INICIANDO BATALLA 2 (JEFE) <<<");
        Batalla batalla2 = new Batalla("Cámara del Trono del Dragón", party);
        batalla2.agregarEnemigo(new Orco("Orco Berserker"));
        batalla2.agregarEnemigo(new Jefe("Dragón de Fuego"));
        batalla2.iniciar();

        // Estado final de la Party
        System.out.println("\n=================================================");
        System.out.println("              ESTADO FINAL DE LA AVENTURA        ");
        System.out.println("=================================================");
        party.mostrarEstado();
    }
}
