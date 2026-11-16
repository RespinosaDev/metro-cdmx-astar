# Metro CDMX · Rutas óptimas con A*

Aplicación de escritorio en **Java + JavaFX** que calcula la ruta óptima entre dos estaciones del Metro de la Ciudad de México con el algoritmo de búsqueda informada **A\***. Sobre un mapa se dibujan la red y la ruta resultante.

> 🎓 **Proyecto académico**: práctica de la asignatura *Inteligencia Artificial* (3.º del Grado en Ingeniería Informática)<br>
> Universidad Politécnica de Madrid · Campus de Montegancedo

---

## Características

- **Búsqueda A\*** con lista abierta (cola de prioridad ordenada por `f(n) = g(n) + h(n)`) y lista cerrada.
- **Coste real `g(n)`**: minutos de trayecto entre estaciones. Los transbordos son aristas propias del grafo, y todas las vías funcionan en ambos sentidos.
- **Heurística `h(n)`**: distancia en línea recta entre estaciones (fórmula de *haversine* sobre latitud/longitud), dividida por la velocidad máxima de la red. Así queda en **minutos**, en la misma escala que `g(n)`, y nunca sobreestima el coste real: es **admisible y consistente**, y por tanto A\* encuentra siempre la ruta óptima.
- **Preferencias del usuario**:
  - *Minimizar transbordos*: penaliza cada cambio de línea (+10).
  - *Evitar escaleras*: penaliza las estaciones sin ascensor (+15).
- **Interfaz gráfica** con selección de origen y destino, el detalle de la ruta (estaciones, transbordos y tiempo total) y un mapa con la ruta resaltada en rojo.
- **Tests unitarios** con JUnit 5 que, entre otras cosas, comprueban que la heurística es consistente y que A\* da el mismo tiempo mínimo que Dijkstra para todos los pares de estaciones.

## Red modelada

La práctica trabaja sobre la zona suroeste de la red (la zona marcada en el mapa de [`src/images/mapa_metro.png`](src/images/mapa_metro.png)):

| Línea | Estaciones |
|-------|------------|
| L1  | Observatorio · Tacubaya · Balderas |
| L3  | Universidad · Zapata · Centro Médico · Juárez |
| L7  | Barranca del Muerto · Mixcoac · Tacubaya · Polanco |
| L9  | Tacubaya · Centro Médico · Lázaro Cárdenas |
| L12 | Mixcoac · Zapata · Eje Central |

Transbordos: **Tacubaya** (L1/L7/L9), **Mixcoac** (L7/L12), **Zapata** (L3/L12), **Centro Médico** (L3/L9) y **Balderas–Juárez** (L1/L3).

Cada estación se modela **por línea** (`L7-TAC`, `L9-TAC`…). Así, un cambio de línea es una arista explícita del grafo con su propio coste.

## Estructura

```
src/
├── main/
│   ├── Main.java                 # Punto de entrada
│   ├── algorithm/
│   │   ├── AStar.java            # Implementación de A*
│   │   ├── Heuristica.java       # h(n): distancia en línea recta
│   │   └── Nodo.java             # Nodo de búsqueda (g, h, f, padre)
│   ├── data/
│   │   └── DataLoader.java       # Estaciones, conexiones y transbordos
│   ├── gui/
│   │   ├── MainWindow.java       # Ventana principal (JavaFX)
│   │   └── MapaPanel.java        # Dibujo del mapa y de la ruta
│   └── model/
│       ├── Estacion.java
│       └── Conexion.java
├── images/
│   └── mapa_metro.png
├── test/
│   ├── AStarTest.java
│   └── DataLoaderTest.java
└── module-info.java
```

## Cómo funciona A* aquí

```
abiertos ← {origen}            (cola de prioridad por f)
mientras abiertos no esté vacía:
    actual ← nodo con menor f
    si actual es el destino → reconstruir la ruta siguiendo los padres
    mover actual a cerrados
    para cada conexión saliente de actual:
        g' = g(actual) + coste(conexión, preferencias)
        si el vecino no está en cerrados y g' mejora su mejor coste:
            añadir el vecino a abiertos con f = g' + h(vecino)
```

## Requisitos

- **JDK 21**
- **JavaFX SDK 21** ([descarga](https://gluonhq.com/products/javafx/))
- JUnit 5 para los tests

## Ejecución

El proyecto se desarrolló en **Eclipse** con JavaFX añadido como *User Library* llamada `javafx`. Se puede importar tal cual con *File → Import → Existing Projects into Workspace*.

Desde la línea de comandos (sustituye la ruta de JavaFX por la tuya):

```bash
# Compilar
javac --module-path /ruta/a/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml \
      -d bin $(find src/main -name "*.java")
cp -r src/images bin/

# Ejecutar
java --module-path /ruta/a/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml \
     -cp bin main.Main
```

### Tests

Con el [JUnit Console Launcher](https://central.sonatype.com/artifact/org.junit.platform/junit-platform-console-standalone) (no necesitan JavaFX):

```bash
javac -cp junit-platform-console-standalone.jar -d out $(find src/main/algorithm src/main/data src/main/model src/test -name "*.java")
java -jar junit-platform-console-standalone.jar execute -cp out --select-package test
```

## Posibles mejoras

- Cargar la red completa desde un fichero (CSV/JSON) en lugar de tenerla en el código.
- Rellenar los datos de accesibilidad (ascensor/escaleras) de cada estación.
- Migrar a Maven o Gradle para gestionar JavaFX y JUnit.
