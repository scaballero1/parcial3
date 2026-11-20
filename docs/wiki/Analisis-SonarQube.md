# Análisis de Calidad de Código — SonarQube

El proyecto fue diseñado y verificado bajo las reglas y estándares de Clean Code de SonarQube, asegurando alta mantenibilidad, confiabilidad y seguridad.

## 1. Buenas prácticas implementadas en el código

- **Gestión segura de recursos:** Recursos de entrada/salida cerrados mediante *try-with-resources* en `ArchivoExamenAdapter`.
- **Compilación estricta y limpia:** Sin bloques `catch` vacíos, sin variables no utilizadas ni imports redundantes (compilación con `-Xlint:all`).
- **Seguridad en deserialización:** Implementación de filtro de deserialización con lista blanca (`ObjectInputFilter`), previniendo vulnerabilidades de inyección o deserialización no confiable.
- **Principio DRY (Don't Repeat Yourself):** Sin duplicación entre estrategias; `ValidacionEstrictaStrategy` compone y extiende `ValidacionBasicaStrategy`.
- **Documentación completa:** Todos los paquetes, interfaces y clases públicas cuentan con documentación JavaDoc integral verificada sin advertencias (`javadoc -Xdoclint:all`).

## 2. Cobertura de pruebas

- **Pruebas unitarias:** 115 pruebas unitarias automatizadas que validan el dominio, las estrategias, los casos de uso y los adaptadores.
- **Métricas JaCoCo:** Reporte de cobertura generado automáticamente en `target/site/jacoco/jacoco.xml`.
- **Exclusiones justificadas:** La capa gráfica de JavaFX (`App`, `PrimaryController`) se excluye de la cobertura de pruebas unitarias por requerir toolkit visual, validándose en su lugar la coherencia FXML-controlador mediante pruebas estáticas de reflexión (`PrimaryControllerFxmlTest`).
