========================================================================
 GUÍA PARA INSERTAR SPRITES EN EL JUEGO (RPG TÁCTICO)
========================================================================

El motor del juego utiliza la clase GestorRecursos para cargar automáticamente
los sprites de héroes y enemigos. Si no encuentra una imagen física para una entidad,
el juego genera automáticamente un sprite vectorial procedural como fallback.

------------------------------------------------------------------------
1. DÓNDE COLOCAR LAS IMÁGENES
------------------------------------------------------------------------
Puedes colocar los archivos de imagen en cualquiera de estas ubicaciones:
  • recursos/sprites/     <--- (RECOMENDADO: ya creada en la raíz del proyecto)
  • sprites/
  • src/recursos/sprites/
  • resources/sprites/

------------------------------------------------------------------------
2. FORMATOS COMPATIBLES
------------------------------------------------------------------------
Se admiten los formatos:
  • .png (Recomendado, con canal alfa / transparencia)
  • .jpg / .jpeg
  • .gif
  • .webp

------------------------------------------------------------------------
3. NOMBRES EXACTOS DE LOS ARCHIVOS
------------------------------------------------------------------------
El sistema busca el nombre de la clase de la entidad en minúsculas (sin distinguir mayúsculas ni acentos).

HÉROES (PARTY):
  • Guerrero   ->  guerrero.png
  • Mago       ->  mago.png
  • Arquero    ->  arquero.png
  • Curador    ->  curador.png  (o clerigo.png / sacerdote.png)

ENEMIGOS:
  • Goblin     ->  goblin.png
  • Orco       ->  orco.png
  • Esqueleto  ->  esqueleto.png
  • Chamán     ->  chaman.png   (o chamán.png)
  • Jefe Dragón->  jefe.png     (o dragon.png / jefe_dragon.png)

------------------------------------------------------------------------
4. DIMENSIONES RECOMENDADAS
------------------------------------------------------------------------
  • Relación de aspecto cuadrada (1:1), por ejemplo:
      - 64x64 píxeles (Pixel Art estándar)
      - 80x80 píxeles
      - 128x128 píxeles
      - 256x256 píxeles (Alta definición)

  El motor gráfico escala automáticamente cualquier imagen de forma suave
  y manteniendo la transparencia para:
    - Escena de batalla: 80x80 px
    - Panel de acción / turno actual: 44x44 px
    - Panel de selección de Party: 80x80 px
    - Mini barras de estado: 24x24 px
========================================================================
