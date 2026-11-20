package co.edu.poli.Examen2.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExamenMedicoTest {

    private static ExamenMedico.Builder valido() {
        return ExamenMedico.builder().codigoExamen("EXM-001").nombrePaciente("Juan Pérez")
                .costo(150.5).tipoSangre(TipoSangre.A).factorRh(FactorRh.POSITIVO);
    }

    @Test
    void construyeExamenValido() {
        ExamenMedico e = valido().build();

        assertEquals("EXM-001", e.getCodigoExamen());
        assertEquals("Juan Pérez", e.getNombrePaciente());
        assertEquals(150.5, e.getCosto());
        assertEquals(TipoSangre.A, e.getTipoSangre());
        assertEquals(FactorRh.POSITIVO, e.getFactorRh());
        assertEquals("A+", e.getGrupoSanguineo());
    }

    @Test
    void normalizaCodigoYNombre() {
        ExamenMedico e = valido().codigoExamen("  exm-002 ").nombrePaciente("  María   José  Gómez ").build();

        assertEquals("EXM-002", e.getCodigoExamen());
        assertEquals("María José Gómez", e.getNombrePaciente());
    }

    @Test
    void reportaTodosLosErroresJuntosUnoPorLinea() {
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> ExamenMedico.builder().build());

        assertEquals(5, ex.getMessage().split("\\R").length);
    }

    @Test
    void rechazaCamposDeTextoEnBlanco() {
        assertThrows(ReglaNegocioException.class, () -> valido().codigoExamen("   ").build());
        assertThrows(ReglaNegocioException.class, () -> valido().nombrePaciente("").build());
        assertThrows(ReglaNegocioException.class, () -> valido().codigoExamen(null).build());
        assertThrows(ReglaNegocioException.class, () -> valido().nombrePaciente(null).build());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1, -0.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rechazaCostosNoPositivosONoFinitos(double costo) {
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> valido().costo(costo).build());

        assertTrue(ex.getMessage().contains("costo"));
    }

    @Test
    void rechazaGrupoSanguineoIncompleto() {
        assertThrows(ReglaNegocioException.class, () -> valido().tipoSangre(null).build());
        assertThrows(ReglaNegocioException.class, () -> valido().factorRh(null).build());
    }

    @Test
    void laIgualdadSeBasaEnElCodigo() {
        ExamenMedico a = valido().build();

        assertEquals(a, valido().nombrePaciente("Otra Persona").build());
        assertEquals(a.hashCode(), valido().nombrePaciente("Otra Persona").build().hashCode());
        assertNotEquals(a, valido().codigoExamen("EXM-999").build());
        assertNotEquals(a, null);
        assertNotEquals(a, "EXM-001");
        assertEquals(a, a);
    }

    @Test
    void toStringMuestraLosDatosPrincipales() {
        assertEquals("EXM-001 | Juan Pérez | AB- | $1,234.50",
                valido().costo(1234.5).tipoSangre(TipoSangre.AB).factorRh(FactorRh.NEGATIVO).build().toString());
    }
}
