package co.edu.poli.Examen2.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/**
 * Parámetros de arranque, leídos de {@code application.properties} y
 * sobrescribibles con propiedades del sistema ({@code -Dclave=valor}).
 *
 * @param archivoPersistencia ruta del archivo de serialización
 * @param validacion          política de validación a usar
 */
public record ConfiguracionAplicacion(Path archivoPersistencia, ModoValidacion validacion) {

    /** Clave de la ruta del archivo de serialización. */
    public static final String CLAVE_ARCHIVO = "persistencia.archivo";
    /** Clave de la política de validación ({@code estricta} o {@code basica}). */
    public static final String CLAVE_VALIDACION = "validacion.modo";

    private static final String RECURSO = "/application.properties";
    private static final String ARCHIVO_POR_DEFECTO = "data/examenes.dat";

    /** Políticas de validación disponibles. */
    public enum ModoValidacion {
        /** Política permisiva ({@code ValidacionBasicaStrategy}). */
        BASICA,
        /** Política estricta ({@code ValidacionEstrictaStrategy}). */
        ESTRICTA
    }

    /**
     * Valida que los componentes no sean nulos.
     */
    public ConfiguracionAplicacion {
        Objects.requireNonNull(archivoPersistencia, "archivoPersistencia");
        Objects.requireNonNull(validacion, "validacion");
    }

    /**
     * Carga la configuración del recurso del classpath y de las propiedades del
     * sistema (estas tienen prioridad).
     *
     * @return configuración resultante
     */
    public static ConfiguracionAplicacion cargar() {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConfiguracionAplicacion.class.getResourceAsStream(RECURSO)) {
            if (entrada != null) {
                propiedades.load(entrada);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + RECURSO, e);
        }
        for (String clave : new String[] {CLAVE_ARCHIVO, CLAVE_VALIDACION}) {
            String valor = System.getProperty(clave);
            if (valor != null) {
                propiedades.setProperty(clave, valor);
            }
        }
        return desde(propiedades);
    }

    /**
     * Construye la configuración desde propiedades, con valores por defecto
     * para las que falten.
     *
     * @param propiedades propiedades de origen
     * @return configuración resultante
     * @throws IllegalArgumentException si {@code validacion.modo} no es {@code estricta} ni {@code basica}
     */
    public static ConfiguracionAplicacion desde(Properties propiedades) {
        String archivo = propiedades.getProperty(CLAVE_ARCHIVO, ARCHIVO_POR_DEFECTO).trim();
        String modo = propiedades.getProperty(CLAVE_VALIDACION, "estricta").trim().toUpperCase(Locale.ROOT);
        try {
            return new ConfiguracionAplicacion(Path.of(archivo), ModoValidacion.valueOf(modo));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para " + CLAVE_VALIDACION + ": '" + modo.toLowerCase(Locale.ROOT)
                            + "' (use 'estricta' o 'basica').", e);
        }
    }
}
