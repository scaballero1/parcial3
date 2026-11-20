package co.edu.poli.Examen2.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TipoSangreYFactorRhTest {

    @ParameterizedTest
    @CsvSource({"A,A", "b,B", "' ab ',AB", "o,O"})
    void tipoSangreReconoceTexto(String texto, TipoSangre esperado) {
        assertEquals(esperado, TipoSangre.desdeTexto(texto).orElseThrow());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Z", "AA", "+"})
    void tipoSangreDesconocido(String texto) {
        assertTrue(TipoSangre.desdeTexto(texto).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"+,POSITIVO", "-,NEGATIVO", "positivo,POSITIVO", "NEGATIVO,NEGATIVO", "' + ',POSITIVO"})
    void factorRhReconoceSimboloONombre(String texto, FactorRh esperado) {
        assertEquals(esperado, FactorRh.desdeTexto(texto).orElseThrow());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"x", "++", "neutro"})
    void factorRhDesconocido(String texto) {
        assertTrue(FactorRh.desdeTexto(texto).isEmpty());
    }

    @Test
    void factorRhExponeSuSimbolo() {
        assertEquals("+", FactorRh.POSITIVO.getSimbolo());
        assertEquals("-", FactorRh.NEGATIVO.getSimbolo());
    }
}
