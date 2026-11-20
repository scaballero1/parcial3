package co.edu.poli.Examen2.infrastructure.adapter.in.javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;
import java.util.List;
import org.junit.jupiter.api.Test;

class MensajesPresentadorTest {

    private final MensajesPresentador presentador = new MensajesPresentador();
    private final String salto = System.lineSeparator();

    private static ExamenMedico examen(String codigo) {
        return ExamenMedico.builder().codigoExamen(codigo).nombrePaciente("Juan Pérez").costo(150)
                .tipoSangre(TipoSangre.A).factorRh(FactorRh.POSITIVO).build();
    }

    @Test
    void confirmaElRegistro() {
        assertEquals("✔ Examen registrado: EXM-001 | Juan Pérez | A+ | $150.00",
                presentador.examenGuardado(examen("EXM-001")));
    }

    @Test
    void listadoVacioYNumerado() {
        assertEquals("No hay exámenes registrados.", presentador.listado(List.of()));
        assertEquals("Exámenes registrados (2):" + salto
                + "  1. EXM-001 | Juan Pérez | A+ | $150.00" + salto
                + "  2. EXM-002 | Juan Pérez | A+ | $150.00",
                presentador.listado(List.of(examen("EXM-001"), examen("EXM-002"))));
    }

    @Test
    void serializadoIndicaElDestino() {
        assertEquals("✔ Exámenes serializados en: /tmp/x.dat", presentador.serializado("/tmp/x.dat"));
    }

    @Test
    void deserializadoConcuerdaEnSingularYPlural() {
        String uno = presentador.deserializado(List.of(examen("EXM-001")));
        assertTrue(uno.startsWith("✔ Se recuperó 1 examen." + salto));
        assertTrue(uno.contains("EXM-001"));
        assertTrue(presentador.deserializado(List.of(examen("A-1"), examen("A-2"))).startsWith("✔ Se recuperaron 2 exámenes."));
    }

    @Test
    void formularioLimpio() {
        assertEquals("Formulario limpio.", presentador.formularioLimpio());
    }

    @Test
    void errorDeUnaLineaSeMuestraTalCual() {
        assertEquals("✖ Ya existe.", presentador.error(new ReglaNegocioException("Ya existe.")));
        assertEquals("✖ fallo", presentador.error(new PersistenciaException("fallo")));
    }

    @Test
    void erroresMultiplesSeListanComoVinetas() {
        String texto = presentador.error(new ReglaNegocioException("Falta A." + salto + "Falta B."));

        assertEquals("✖ Se encontraron problemas:" + salto + "  - Falta A." + salto + "  - Falta B.", texto);
    }

    @Test
    void errorSinMensajeNoFalla() {
        assertEquals("✖ null", presentador.error(new IllegalStateException()));
    }
}
