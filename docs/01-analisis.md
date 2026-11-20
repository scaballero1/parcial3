# 1. Análisis del proyecto original

Proyecto analizado: `parcial3-main/Examen2` — aplicación JavaFX "Exámenes de Sangre" (Maven, Java 11, JavaFX 13).

## 1.1 Funcionalidades existentes

El proyecto original es un **esqueleto de interfaz**: la pantalla está terminada, pero ninguna operación está implementada.

| Elemento | Estado en el original |
|---|---|
| Formulario "Registro de Examen" (`primary.fxml`) | **Completo.** Campos: código, nombre del paciente, costo, tipo de sangre (`A`, `B`, `AB`, `O`) y factor RH (positivo/negativo). |
| Botón **Guardar** (`handleGuardar`) | Declarado, **cuerpo vacío**. |
| Botón **Limpiar** (`handleLimpiar`) | Declarado, **cuerpo vacío** (no hay botón en el FXML que lo invoque). |
| Botón **Listar** (`handleListar`) | Declarado, **cuerpo vacío**. |
| Botón **Serializar** (`handleSerializar`) | Declarado, **cuerpo vacío**. |
| Botón **Deserializar** (`handleDeserializar`) | Declarado, **cuerpo vacío**. |
| Área de mensajes (`txtAreaMensajes`) | Declarada, sin uso. |
| Modelo de dominio (examen, tipo de sangre, factor RH) | **No existe.** |
| Persistencia / serialización | **No existe.** |
| Pruebas, JavaDoc, README de uso | **No existen** (el README es solo una guía de *fork*). |

Operaciones del sistema que el proyecto pretende ofrecer (deducidas de los nombres de los handlers y de la pantalla):

1. Registrar un examen de sangre a partir del formulario.
2. Listar los exámenes registrados.
3. Serializar (guardar en archivo) los exámenes.
4. Deserializar (recuperar de archivo) los exámenes.
5. Limpiar el formulario.

## 1.2 Defectos encontrados

Como no hay lógica todavía, parte de los problemas son **estructurales** (ya están en el código) y parte son **inevitables a corto plazo** (la única clase disponible para recibir la lógica es el controlador).

### Acoplamiento

| # | Problema | Dónde |
|---|---|---|
| A1 | El **único lugar posible** para implementar las 5 operaciones es `PrimaryController`, una clase de la capa de presentación. Validación, almacenamiento y archivo quedarían acoplados a `TextField`, `ComboBox` y `TextArea`. | `PrimaryController` |
| A2 | Las reglas del dominio ya están **duplicadas en la vista**: los tipos de sangre están escritos a mano en el FXML y el factor RH está fijado en dos `RadioButton`. Si cambia el dominio, hay que editar la interfaz. | `primary.fxml` |
| A3 | `App` guarda la escena en un campo **estático mutable** (`private static Scene scene`) y expone `setRoot` estático: estado global y acoplamiento de cualquier clase a la clase de arranque. | `App` |
| A4 | El controlador se crea por reflexión con constructor vacío (FXML por defecto), lo que obliga a que **cree o localice él mismo** sus dependencias (`new ...`), impidiendo inyectarlas. | `PrimaryController` / `App` |
| A5 | Los datos viajan como `String` sueltos (primitive obsession): no hay un tipo que represente "un examen". | `PrimaryController` |

### Cohesión

| # | Problema |
|---|---|
| C1 | `PrimaryController` acumularía **cinco responsabilidades** (leer formulario, validar, guardar en colección, serializar a disco, formatear mensajes) → baja cohesión. |
| C2 | Los paquetes `vista` (con `App`) y `controlador` separan por *tipo técnico de clase*, no por responsabilidad: la clase de arranque que **compone la aplicación** vive en la "vista". |
| C3 | El área de mensajes mezclaría presentación (formato del texto) con los resultados de la operación. |

### Extensibilidad

| # | Problema |
|---|---|
| E1 | Agregar una regla de validación (p. ej. un límite de costo) obligaría a editar el método que guarda (`if/else` creciente): viola Abierto/Cerrado. |
| E2 | Cambiar el formato o destino de la serialización (JSON, base de datos, nube) o añadir comprimir/cifrar implicaría **reescribir** el controlador. |
| E3 | No hay abstracciones (interfaces) en ningún punto: no existe un lugar donde "enchufar" otra implementación. |

### Mantenibilidad

| # | Problema |
|---|---|
| M1 | **No es testeable**: la lógica estaría detrás de widgets JavaFX, que exigen el toolkit gráfico para instanciarse. |
| M2 | **Configuración inconsistente**: `pom.xml` declara `mainClass = co.edu.poli.Examen2.App`, pero la clase real es `co.edu.poli.Examen2.vista.App` → `mvn javafx:run` falla. |
| M3 | **Versiones inconsistentes**: el FXML declara JavaFX 21 (`xmlns="http://javafx.com/javafx/21"`) y el `pom.xml` usa JavaFX 13 con Java 11. |
| M4 | Sin JavaDoc, sin pruebas y con un README que no explica cómo ejecutar el proyecto. |
| M5 | Los handlers vacíos son deuda técnica invisible: la aplicación compila y "funciona" sin hacer nada. |

## 1.3 Resumen

El proyecto tiene una **buena interfaz y ninguna arquitectura**. Si se implementa tal como está, el resultado previsible es un controlador monolítico, sin pruebas y sin puntos de extensión. La refactorización propuesta ([02-arquitectura-hexagonal.md](02-arquitectura-hexagonal.md)) ataca A1–A5 y C1–C3 con la arquitectura hexagonal, y E1–E3 / M1 con tres patrones GoF ([03-patrones-gof.md](03-patrones-gof.md)). M2–M4 se corrigen en el `pom.xml`, las pruebas, el JavaDoc y el README.
