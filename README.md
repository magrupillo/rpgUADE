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
