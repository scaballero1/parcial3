package co.edu.poli.Examen2.application.strategy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidacionStrategyTest {

    private final ValidacionExamenStrategy basica = new ValidacionBasicaStrategy();
    private final ValidacionExamenStrategy estricta = new ValidacionEstrictaStrategy();

    private static ExamenMedico examen(String codigo, String nombre, double costo) {
        return ExamenMedico.builder().codigoExamen(codigo).nombrePaciente(nombre).costo(costo)
                .tipoSangre(TipoSangre.O).factorRh(FactorRh.NEGATIVO).build();
    }

    // ----- básica -----

    @Test
    void basicaAceptaDatosRazonables() {
        assertDoesNotThrow(() -> basica.validar(examen("EXM-001", "Juan Pérez", 10)));
        assertDoesNotThrow(() -> basica.validar(examen("lab_77", "Juan123", 10)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABCDEFGHIJKLMNOPQRSTU"})
    void basicaRechazaCodigosDeLargoInvalido(String codigo) {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> basica.validar(examen(codigo, "Juan Pérez", 10)));

        assertTrue(e.getMessage().contains("entre 3 y 20"));
    }

    @Test
    void basicaRechazaNombresDeLargoInvalido() {
        assertThrows(ReglaNegocioException.class, () -> basica.validar(examen("EXM-001", "Al", 10)));
        assertThrows(ReglaNegocioException.class, () -> basica.validar(examen("EXM-001", "a".repeat(101), 10)));
    }

    @Test
    void basicaAcumulaLosErrores() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> basica.validar(examen("AB", "Al", 10)));

        assertEquals(2, e.getMessage().split("\\R").length);
    }

    // ----- estricta -----

    @Test
    void estrictaAceptaDatosConFormatoCorrecto() {
        assertDoesNotThrow(() -> estricta.validar(examen("EXM-001", "María José O'Connor", 150.5)));
        assertDoesNotThrow(() -> estricta.validar(examen("LAB-20240001", "Peña-Núñez", 99999999.99)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"EXM001", "EX-001", "EXM-01", "EXM-ABC", "1XM-001", "EXMA-001"})
    void estrictaRechazaCodigosSinFormatoAaa123(String codigo) {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> estricta.validar(examen(codigo, "Juan Pérez", 10)));

        assertTrue(e.getMessage().contains("AAA-123"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan123", "1Juan", "<script>", "Ana_Ruiz"})
    void estrictaRechazaNombresConCaracteresNoPermitidos(String nombre) {
        assertTrue(assertThrows(ReglaNegocioException.class,
                () -> estricta.validar(examen("EXM-001", nombre, 10))).getMessage().contains("solo puede contener letras"));
    }

    @Test
    void estrictaRechazaCostosEnormesOConMuchosDecimales() {
        assertTrue(assertThrows(ReglaNegocioException.class,
                () -> estricta.validar(examen("EXM-001", "Juan Pérez", 100_000_000.01))).getMessage().contains("superar"));
        assertTrue(assertThrows(ReglaNegocioException.class,
                () -> estricta.validar(examen("EXM-001", "Juan Pérez", 1.234))).getMessage().contains("dos decimales"));
        assertDoesNotThrow(() -> estricta.validar(examen("EXM-001", "Juan Pérez", 1.5)));
        assertDoesNotThrow(() -> estricta.validar(examen("EXM-001", "Juan Pérez", 100)));
    }

    @Test
    void estrictaIncluyeLasReglasDeLaBasicaYAcumulaTodo() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> estricta.validar(examen("AB", "Al1", 1.234)));

        String[] lineas = e.getMessage().split("\\R");
        assertTrue(lineas.length >= 4, e.getMessage());
        assertTrue(e.getMessage().contains("entre 3 y 20"));
    }

    @Test
    void estrictaExigeUnaPoliticaBase() {
        assertThrows(NullPointerException.class, () -> new ValidacionEstrictaStrategy(null));
    }

    @Test
    void estrictaPuedeApoyarseEnCualquierEstrategia() {
        ValidacionExamenStrategy siempreFalla = ex -> {
            throw new ReglaNegocioException("base rechaza");
        };

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> new ValidacionEstrictaStrategy(siempreFalla).validar(examen("EXM-001", "Juan Pérez", 10)));

        assertEquals("base rechaza", e.getMessage());
    }

    // ----- intercambiabilidad -----

    @Test
    void lasEstrategiasSonIntercambiablesYDecidenDistinto() {
        ExamenMedico codigoLibre = examen("lab_77", "Juan Pérez", 10);

        assertDoesNotThrow(() -> basica.validar(codigoLibre));
        assertThrows(ReglaNegocioException.class, () -> estricta.validar(codigoLibre));
    }
}
