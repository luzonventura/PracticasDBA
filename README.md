# Desarrollo Basado en Agentes (DBA)

Repositorio de prácticas de la asignatura **Desarrollo Basado en Agentes (DBA)**, donde se exploran conceptos y aplicaciones del paradigma de agentes utilizando la plataforma **JADE**. Las prácticas abarcan desde la implementación básica de agentes hasta la creación de sistemas multi-agentes con comportamientos complejos y comunicación.

## Prácticas

### Práctica 1: Primeros Pasos con JADE
En esta práctica se implementaron agentes básicos utilizando los diferentes tipos de comportamientos que ofrece JADE:
- **One-shot Behavior**: Realización de una tarea única.
- **Cyclic Behavior**: Ejecución continua de tareas.
- **WakerBehavior** y **TickerBehavior**: Control temporal de tareas.

Ejercicios destacados:
1. Crear un agente que imprime un mensaje en consola.
2. Implementar agentes con comportamientos que manejan cálculos y repeticiones.
3. Resolver problemas como cálculo de promedios utilizando múltiples comportamientos.

---

### Práctica 2: Agente Pac-Man
Se desarrolló un agente inspirado en Pac-Man que se mueve en un mapa hasta alcanzar un objetivo, evitando obstáculos. Este proyecto incluyó:
- **Interfaz gráfica amigable** con un menú interactivo.
- Elección de mapas personalizados.
- Navegación eficiente del agente utilizando algoritmos heurísticos (como A* parcial).
- Visualización del progreso del agente, incluyendo su traza y el consumo energético.

Ejercicios destacados:
1. Resolución de mapas sin obstáculos, con obstáculos básicos, cóncavos y convexos.
2. Implementación de un sensor de percepción limitada para las celdas contiguas.
3. Cálculo y visualización de la energía consumida al alcanzar el objetivo.

---

### Práctica 3: Comunicación entre Agentes
En esta práctica, se implementó un sistema multi-agente ambientado en la historia de Santa Claus, donde el objetivo principal era rescatar a los renos perdidos. Se incluyeron las siguientes características:
- **Agentes involucrados**: Santa Claus, Rudolph, un elfo traductor y un agente buscador.
- Diseño de un protocolo de comunicación basado en los estándares FIPA.
- Traducción de mensajes entre Santa Claus y los agentes para asegurar la comunicación correcta.
- Coordinación con Rudolph para localizar y rescatar a los renos en diferentes puntos del mapa.
- Finalización de la misión con un mensaje de éxito ("HoHoHo!") al llegar al establo.

Tareas clave:
1. Validación del agente por Santa Claus, estableciendo un canal de comunicación seguro.
2. Uso de un código secreto para interactuar con Rudolph.
3. Implementación de un flujo de mensajes con un elfo traductor para asegurar la claridad en las comunicaciones.
4. Rescate secuencial de los renos y por último encuentro con Santa Claus.

---

## Requisitos
- **Lenguaje**: Java
- **Framework**: JADE (Java Agent DEvelopment Framework)
- **Entorno**: IntelliJ IDEA o Visual Studio Code
- **JDK**: OpenJDK o JDK de Oracle

---

## Ejecución
1. Configura el entorno de desarrollo con la librería JADE.
2. Sigue las instrucciones específicas de cada práctica para compilar y ejecutar los agentes.
3. Verifica las salidas en consola y las interfaces gráficas para evaluar el funcionamiento de los agentes.

---

## Contribución
Cada práctica ha sido desarrollada en equipo, aplicando los conocimientos adquiridos en clase para diseñar sistemas multi-agentes funcionales y eficientes.

---

¡Gracias por explorar este repositorio y el mundo de los sistemas multi-agentes!
