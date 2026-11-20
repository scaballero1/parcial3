# Exámenes de Sangre — Registro de Examen (JavaFX · Arquitectura Hexagonal)

Aplicación de escritorio para registrar exámenes de sangre de pacientes. Permite **guardar**, **listar**, **serializar** a archivo, **deserializar** desde archivo y **limpiar** el formulario.

El proyecto original (`parcial3-main`) era un esqueleto: la pantalla JavaFX estaba completa pero los cinco *handlers* del controlador estaban vacíos y no existía modelo de dominio. Esta versión lo **implementa y refactoriza** con **Arquitectura Hexagonal (Puertos y Adaptadores)** y **tres patrones GoF**.

## Contenido

- [Diagnóstico breve](#diagnóstico-breve)
- [Arquitectura hexagonal](#arquitectura-hexagonal)
- [Patrones GoF implementados](#patrones-gof-implementados)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Compilar, probar, JavaDoc y ejecutar](#compilar-probar-javadoc-y-ejecutar)
- [Uso de la aplicación](#uso-de-la-aplicación)
- [SonarQube y wiki](#sonarqube-y-wiki)
- [Documentación adicional](#documentación-adicional)

## Diagnóstico breve

Detalle completo en [`docs/01-analisis.md`](docs/01-analisis.md).

| Dimensión | Problema en el original |
|---|---|
| Acoplamiento | Único lugar posible para la lógica: el controlador JavaFX; reglas del dominio duplicadas en el FXML; escena en campo estático. |
| Cohesión | El controlador acumularía lectura de formulario, validación, almacenamiento, archivo y formato de mensajes. |
| Extensibilidad | Sin interfaces: añadir una regla o cambiar el formato de archivo obliga a reescribir el controlador. |
| Mantenibilidad | No testeable, `mainClass` del `pom.xml` apuntaba a una clase inexistente, versiones de JavaFX inconsistentes, sin JavaDoc ni pruebas. |

## Arquitectura hexagonal

![Arquitectura](docs/diagramas/01-arquitectura-hexagonal.png)

El núcleo (`domain` + `application`) **no depende** de JavaFX ni de `java.io`. Habla con el exterior por puertos; los adaptadores de `infrastructure` los implementan. Detalle y decisiones en [`docs/02-arquitectura-hexagonal.md`](docs/02-arquitectura-hexagonal.md).

## Patrones GoF implementados

| Categoría | Patrón | Dónde | Para qué |
|---|---|---|---|
| Creacional | **Builder** | `ExamenMedico.Builder` | Crear exámenes siempre válidos; todos los invariantes y errores en `build()`. |
| Estructural | **Adapter** | `ArchivoExamenAdapter`, `ExamenFormMapper` | Adaptar la API de archivos de Java al puerto `ExamenRepositoryPort` y la pantalla al dominio. |
| Comportamiento | **Strategy** | `ValidacionExamenStrategy` (`Basica`, `Estricta`) | Validación intercambiable y configurable, sin `if/else` en servicio ni controlador. |

Problema, justificación, UML y beneficios de cada uno en [`docs/03-patrones-gof.md`](docs/03-patrones-gof.md).

## Estructura del proyecto

```
parcial3-main/
├── README.md
├── docs/
│   ├── 01-analisis.md
│   ├── 02-arquitectura-hexagonal.md
│   ├── 03-patrones-gof.md
│   ├── diagramas/                 (*.mmd = fuente Mermaid, *.png = renderizado)
│   └── wiki/Analisis-SonarQube.md
└── Examen2/
    ├── pom.xml
    ├── sonar-project.properties
    ├── docker-compose.sonarqube.yml
    └── src/
        ├── main/java/co/edu/poli/Examen2/
        │   ├── domain/            model (ExamenMedico, TipoSangre, FactorRh) · exception (ReglaNegocioException)
        │   ├── application/       port/in · port/out · strategy · usecase (GestionExamenService) · exception
        │   └── infrastructure/    adapter/in/javafx · adapter/out/file · config
        ├── main/resources/        application.properties · co/edu/poli/Examen2/primary.fxml
        └── test/java/...          pruebas unitarias (misma estructura de paquetes)
```

## Requisitos previos

| Herramienta | Versión |
|---|---|
| JDK | **17 o superior** (probado con 21) |
| Maven | 3.8 o superior |
| JavaFX | Lo descarga Maven (`org.openjfx` 21.0.5); no hace falta instalarlo |
| Docker (opcional) | Solo para levantar SonarQube local |

### Abrir y trabajar en Antigravity (o cualquier IDE basado en VS Code)

1. **File → Open Folder…** y elige la carpeta **`Examen2`** (donde está el `pom.xml`), no la raíz del repositorio.
2. Instala las extensiones recomendadas (Antigravity las sugerirá desde `.vscode/extensions.json`): *Extension Pack for Java*, *Maven for Java* y *Test Runner for Java*. Si no aparecen en su marketplace (que puede ser Open VSX), búscalas por nombre o instala el `.vsix` desde el sitio de cada extensión.
3. Espera a que termine la importación del proyecto Maven (barra de estado: *Java: Ready*). Si cambiaste de versión de Java: `Ctrl+Shift+P → Java: Clean Java Language Server Workspace`.
4. Configura un **JDK 17+** (`JAVA_HOME` o la propiedad `java.jdt.ls.java.home`).
5. Ejecuta con **Terminal → Run Task…** (definidas en `.vscode/tasks.json`):

| Tarea | Equivale a |
|---|---|
| Ejecutar aplicación (JavaFX) | `mvn clean javafx:run` |
| Maven: pruebas | `mvn clean test` |
| Maven: JavaDoc | `mvn javadoc:javadoc` |
| Maven: compilar | `mvn clean compile` |
| Maven: SonarQube | `mvn clean verify sonar:sonar` (pide el token) |

> **Recomendación:** ejecuta la aplicación con `mvn javafx:run` (la tarea o la terminal integrada) en lugar del botón *Run* de la extensión de Java. El proyecto es modular (`module-info.java`) y JavaFX necesita su *module path*, que Maven ya configura. Las pruebas también se pueden lanzar desde el panel *Testing*, pero si alguna falla por acceso a módulos, `mvn clean test` es la referencia (en Maven se ejecutan en classpath).
>
> Los archivos `.classpath`, `.project` y `.settings/` de Eclipse se eliminaron del proyecto: el IDE los regenera si los necesita y están en `.gitignore`.

## Configuración

`Examen2/src/main/resources/application.properties`:

| Propiedad | Por defecto | Descripción |
|---|---|---|
| `persistencia.archivo` | `data/examenes.dat` | Archivo de "Serializar" / "Deserializar" (relativo al directorio de ejecución). |
| `validacion.modo` | `estricta` | Política de validación: `estricta` o `basica`. |

Se pueden sobrescribir al ejecutar: `mvn javafx:run -Dvalidacion.modo=basica -Dpersistencia.archivo=/tmp/examenes.dat`

**Políticas de validación**

| Regla | `basica` | `estricta` |
|---|---|---|
| Código | 3 a 20 caracteres | Formato `AAA-123` (3 letras, guion, ≥ 3 dígitos) |
| Nombre | 3 a 100 caracteres | Además, solo letras, espacios, `.`, `'` y `-` |
| Costo | > 0 (siempre exigido por el Builder) | Además, ≤ 100 000 000 y máximo 2 decimales |

## Compilar, probar, JavaDoc y ejecutar

Desde `Examen2/`:

```bash
mvn clean compile            # compilar
mvn clean test               # pruebas unitarias + informe JaCoCo (target/site/jacoco/index.html)
mvn javadoc:javadoc          # JavaDoc en target/site/apidocs/index.html
mvn clean javafx:run         # ejecutar la aplicación
```

## Uso de la aplicación

1. Completar código (p. ej. `EXM-001`), nombre, costo, tipo de sangre y factor RH.
2. **Guardar** registra el examen (si hay errores, el área de mensajes los lista todos).
3. **Listar** muestra los exámenes registrados.
4. **Serializar** los guarda en el archivo configurado (no permite serializar una lista vacía).
5. **Deserializar** recupera el archivo y **reemplaza** lo que haya en memoria.
6. **Limpiar** restablece el formulario.

## SonarQube y wiki

```bash
cd Examen2
docker compose -f docker-compose.sonarqube.yml up -d      # http://localhost:9000  (admin / admin)
mvn clean verify sonar:sonar -Dsonar.token=<TU_TOKEN>
```

Luego captura el resumen del dashboard (`http://localhost:9000/dashboard?id=parcial3-examenes-sangre`), guárdalo como `sonarqube-resumen.png` y súbelo a la **wiki del repositorio**. Instrucciones paso a paso y plantilla de la página en [`docs/wiki/Analisis-SonarQube.md`](docs/wiki/Analisis-SonarQube.md).

> La imagen resumen **no está incluida** en este repositorio: debe generarse con tu propio servidor SonarQube.

## Documentación adicional

- [`docs/01-analisis.md`](docs/01-analisis.md) — funcionalidades existentes y problemas de diseño.
- [`docs/02-arquitectura-hexagonal.md`](docs/02-arquitectura-hexagonal.md) — capas, puertos, adaptadores y decisiones.
- [`docs/03-patrones-gof.md`](docs/03-patrones-gof.md) — Builder, Adapter y Strategy con UML.
- [`docs/diagramas/`](docs/diagramas) — diagramas UML (Mermaid + PNG).
