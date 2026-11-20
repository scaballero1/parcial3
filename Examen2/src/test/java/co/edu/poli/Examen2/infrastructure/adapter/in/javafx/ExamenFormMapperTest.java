package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ExamenFormMapperTest {

    private final ExamenFormMapper mapper = new ExamenFormMapper();

    @Test
    void traduceElFormularioAlDominio() {
        ExamenMedico e = mapper.aDominio(new FormularioExamen(" exm-001 ", "  Juan   Pérez ", "150.50", "AB", false));

        assertEquals("EXM-001", e.getCodigoExamen());
        assertEquals("Juan Pérez", e.getNombrePaciente());
        assertEquals(150.5, e.getCosto());
        assertEquals(TipoSangre.AB, e.getTipoSangre());
        assertEquals(FactorRh.NEGATIVO, e.getFactorRh());
    }

    @Test
    void factorPositivoSeTraduceAPositivo() {
        assertEquals(FactorRh.POSITIVO,
                mapper.aDominio(new FormularioExamen("EXM-001", "Juan Pérez", "10", "A", true)).getFactorRh());
    }

    @Test
    void aceptaComaDecimal() {
        assertEquals(99.5, mapper.aDominio(new FormularioExamen("EXM-001", "Juan Pérez", " 99,5 ", "O", true)).getCosto());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "abc", "1.2.3", "NaN", "Infinity", "-5", "0"})
    void costosNoValidosSeReportanComoErrorDeNegocio(String costo) {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> mapper.aDominio(new FormularioExamen("EXM-001", "Juan Pérez", costo, "A", true)));

        assertTrue(e.getMessage().contains("costo"));
    }

    @Test
    void sinTipoDeSangreSeguidoDeTodosLosDemasErrores() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> mapper.aDominio(new FormularioExamen("", "", "x", null, true)));

        assertEquals(4, e.getMessage().split("\\R").length);
    }
}
