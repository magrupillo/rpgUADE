# El Último Ascenso - RPG UADE

Proyecto Obligatorio de Programación III (UADE).  
Juego de rol táctico por turnos con interfaz gráfica completa desarrollada en **Java Swing**, estructurado bajo el patrón de arquitectura **MVC (Modelo - Vista - Controlador)** y principios avanzados de **Programación Orientada a Objetos**.

---

## Características Principales

- **Arquitectura MVC y DTOs:** Total desacoplamiento entre el Dominio/Modelo y la capa gráfica. La vista consume objetos de transferencia inmutables (`EstadoBatallaDTO`, `PersonajeDTO`, `HabilidadDTO`, `MapaDTO`, `PartyGestionDTO`, etc.).
- **Sistema de Turnos e Iniciativa Táctica:** Cola de velocidad dinámica con visualización en tiempo real del orden de acción de héroes y enemigos.
- **Selección de Objetivos Intuitiva:** Se puede seleccionar y deseleccionar enemigos haciendo clic directo sobre sus sprites en el campo de batalla, con halo visual y etiqueta de retícula.
- **Habilidades y Gestión de Maná:** 3 habilidades activas por personaje con bloqueo visual si no han sido aprendidas. Tooltips tácticos detallados al posar el cursor indicando descripción, gasto de maná y tipo de objetivo.
- **Torre de Ascensión (3 Pisos):**
  - **Piso 1:** Base de la Torre - Jefe: *Dr. Eggman* (sprite `jefe2.png`).
  - **Piso 2:** Profundidades de la Torre - Jefe: *Darth Vader* (sprite `jefe3.png`).
  - **Piso 3:** Cúspide de la Torre - Jefe Final: *Bowser* (sprite `jefe.png`).
  - Bloqueo de rejugabilidad de pisos completados y preservación del avance del mapa sin reseteos accidentales.
- **Grafo de Nodos Procedural con Niebla de Exploración:** Caminos ramificados que contienen Combates Normales, Combates Élite, Kiosco de Don Tito (compra/venta de suministros), Descansos en Fogata y Eventos Narrativos (incluyendo el evento *"El Sacrificio"*).
- **Persistencia de Partida:** Guardado y carga del estado del juego mediante serialización con excepciones controladas (`PersistenciaException`).

---

## Estructura del Código Fuente

```text
src/
└── rpg/
    ├── app/               # Punto de entrada principal (Main.java)
    ├── controlador/       # Controladores del flujo de juego y combate (MVC)
    │   ├── ControladorBatalla.java
    │   └── ControladorJuego.java
    ├── dto/               # Data Transfer Objects (desacople Modelo-Vista)
    │   ├── EntidadDTO.java
    │   ├── EstadoBatallaDTO.java
    │   ├── EventoDTO.java
    │   ├── HabilidadDTO.java
    │   ├── ItemDTO.java
    │   ├── MapaDTO.java
    │   ├── NodoDTO.java
    │   ├── PartyGestionDTO.java
    │   ├── PersonajeDTO.java
    │   └── RecompensaDTO.java
    ├── excepcion/         # Excepciones personalizadas del dominio
    │   ├── BatallaException.java
    │   ├── InventarioLlenoException.java
    │   ├── ItemNoEquipableException.java
    │   ├── ManaInsuficienteException.java
    │   ├── ObjetoNoEncontradoException.java
    │   └── PersistenciaException.java
    ├── modelo/            # Dominio del juego (POO, Polimorfismo, Visitor)
    │   ├── Armadura.java
    │   ├── Arma.java
    │   ├── Arquero.java
    │   ├── Batalla.java
    │   ├── Chaman.java
    │   ├── Clerigo.java
    │   ├── Combatiente.java
    │   ├── Curador.java
    │   ├── Enemigo.java
    │   ├── Esqueleto.java
    │   ├── EventoAleatorio.java
    │   ├── Goblin.java
    │   ├── Guerrero.java
    │   ├── HabilidadCurativa.java
    │   ├── HabilidadDeEstado.java
    │   ├── Habilidad.java
    │   ├── HabilidadOfensiva.java
    │   ├── Inventario.java
    │   ├── Item.java
    │   ├── Jefe.java
    │   ├── Juego.java
    │   ├── Mago.java
    │   ├── Mapa.java
    │   ├── NodoBatallaElite.java
    │   ├── NodoBatalla.java
    │   ├── NodoDescanso.java
    │   ├── NodoEvento.java
    │   ├── Nodo.java
    │   ├── NodoJefe.java
    │   ├── NodoKiosco.java
    │   ├── NodoVisitor.java
    │   ├── Orco.java
    │   ├── Party.java
    │   ├── Personaje.java
    │   ├── Picaro.java
    │   ├── PocionMana.java
    │   ├── PocionVida.java
    │   ├── RegistroCombate.java
    │   └── TipoObjetivo.java
    ├── util/              # Gestión de fuentes, colores e imágenes
    │   ├── EstilosUI.java
    │   ├── GestorPersistencia.java
    │   └── GestorRecursos.java
    └── vista/             # Capa de presentación Swing (CardLayout y paneles)
        ├── PanelAcciones.java
        ├── PanelColaIniciativa.java
        ├── PanelEscenaBatalla.java
        ├── PanelRegistro.java
        ├── PanelSprite.java
        ├── PantallaBatalla.java
        ├── PantallaCreacionParty.java
        ├── PantallaDescanso.java
        ├── PantallaEstadoParty.java
        ├── PantallaEvento.java
        ├── PantallaInicio.java
        ├── PantallaKiosco.java
        ├── PantallaMapa.java
        ├── PantallaResultados.java
        └── PantallaTorre.java
```

---

## Requisitos y Ejecución

- **Java Development Kit (JDK):** Versión 17 o superior.
- **Entorno recomendado:** IntelliJ IDEA, Eclipse, NetBeans o terminal.

### Compilar y Ejecutar por Consola

1. **Compilar el proyecto:**
   ```bash
   javac -encoding UTF-8 -d bin $(find src -name "*.java")
   ```
   *(En Windows PowerShell / CMD)*:
   ```cmd
   dir /s /b src\*.java > sources.txt
   javac -encoding UTF-8 -d bin @sources.txt
   del sources.txt
   ```

2. **Ejecutar la aplicación:**
   ```bash
   java -cp bin rpg.app.Main
   ```
