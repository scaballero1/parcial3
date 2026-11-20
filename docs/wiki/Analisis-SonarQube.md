# Análisis con SonarQube

> Esta página está pensada para copiarse a la **wiki del repositorio** (GitHub/GitLab → *Wiki* → *New Page*).
> La imagen resumen **debe generarla quien ejecute el análisis**: no se incluye aquí porque depende de un servidor SonarQube propio.

## 1. Levantar SonarQube

Desde `Examen2/`:

```bash
docker compose -f docker-compose.sonarqube.yml up -d
```

Abrir <http://localhost:9000> (usuario/clave iniciales `admin` / `admin`; pedirá cambiarla).
Crear un token: *My Account → Security → Generate Token*.

## 2. Ejecutar el análisis

```bash
cd Examen2
mvn clean verify sonar:sonar \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.token=<TU_TOKEN>
```

`verify` ejecuta las pruebas y genera la cobertura JaCoCo (`target/site/jacoco/jacoco.xml`), que Sonar lee automáticamente.
Los controladores JavaFX (`App`, `PrimaryController`) se excluyen de la cobertura (`sonar.coverage.exclusions` en `pom.xml`) porque requieren interfaz gráfica.

## 3. Subir la imagen resumen a la wiki

1. Abrir <http://localhost:9000/dashboard?id=parcial3-examenes-sangre> y capturar el resumen (*Quality Gate*, Bugs, Vulnerabilities, Code Smells, Coverage, Duplications).
2. Guardar la captura como `sonarqube-resumen.png`.
3. Subirla a la wiki (arrastrándola al editor o clonando `<repo>.wiki.git` y haciendo *push*).
4. Referenciarla en esta página:

```markdown
![Resumen SonarQube](sonarqube-resumen.png)
```

## 4. Qué se hizo en el código para pasar el análisis

- Recursos siempre cerrados con *try-with-resources* (`ArchivoExamenAdapter`).
- Sin bloques `catch` vacíos ni variables/imports sin usar (el compilador se ejecutó con `-Xlint:all`).
- Deserialización con lista blanca (`ObjectInputFilter`), que evita la regla de seguridad sobre deserialización no confiable.
- Sin duplicación entre estrategias (la estricta compone la básica).
- JavaDoc completo (verificado con `javadoc -Xdoclint:all`, sin advertencias).
