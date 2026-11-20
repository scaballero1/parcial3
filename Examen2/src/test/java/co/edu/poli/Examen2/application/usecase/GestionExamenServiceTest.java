package co.edu.poli.Examen2.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.application.strategy.ValidacionBasicaStrategy;
import co.edu.poli.Examen2.application.strategy.ValidacionEstrictaStrategy;
import co.edu.poli.Examen2.application.strategy.ValidacionExamenStrategy;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestionExamenServiceTest {

    private RepositorioFalso repositorio;
    private GestionExamenService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioFalso();
        servicio = new GestionExamenService(repositorio, new ValidacionEstrictaStrategy());
    }

    private static ExamenMedico examen(String codigo) {
        return ExamenMedico.builder().codigoExamen(codigo).nombrePaciente("Juan Pérez").costo(150)
                .tipoSangre(TipoSangre.A).factorRh(FactorRh.POSITIVO).build();
    }

    // ----- guardar -----

    @Test
    void guardaUnExamenValido() {
        ExamenMedico guardado = servicio.guardar(examen("EXM-001"));

        assertEquals(List.of(guardado), repositorio.memoria);
    }

    @Test
    void rechazaCodigoDuplicadoSinGuardarDeNuevo() {
        servicio.guardar(examen("EXM-001"));

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.guardar(examen("EXM-001")));

        assertTrue(e.getMessage().contains("EXM-001"));
        assertEquals(1, repositorio.memoria.size());
    }

    @Test
    void laEstrategiaRechazaYNoSeGuardaNada() {
        assertThrows(ReglaNegocioException.class, () -> servicio.guardar(examen("lab_77")));

        assertTrue(repositorio.memoria.isEmpty());
    }

    @Test
    void cambiarDeEstrategiaCambiaElResultadoSinTocarElServicio() {
        ExamenMedico codigoLibre = examen("lab_77");
        GestionExamenService permisivo = new GestionExamenService(repositorio, new ValidacionBasicaStrategy());

        permisivo.guardar(codigoLibre);

        assertEquals(List.of(codigoLibre), repositorio.memoria);
    }

    @Test
    void invocaLaEstrategiaConElExamenRecibido() {
        List<ExamenMedico> vistos = new ArrayList<>();
        ValidacionExamenStrategy espia = vistos::add;
        ExamenMedico e = examen("EXM-001");

        new GestionExamenService(repositorio, espia).guardar(e);

        assertEquals(List.of(e), vistos);
    }

    @Test
    void rechazaExamenNulo() {
        assertThrows(NullPointerException.class, () -> servicio.guardar(null));
    }

    // ----- listar -----

    @Test
    void listaVacioYLuegoEnOrdenDeRegistro() {
        assertTrue(servicio.listar().isEmpty());

        servicio.guardar(examen("EXM-002"));
        servicio.guardar(examen("EXM-001"));

        assertEquals(List.of("EXM-002", "EXM-001"),
                servicio.listar().stream().map(ExamenMedico::getCodigoExamen).toList());
    }

    // ----- serializar / deserializar -----

    @Test
    void serializaYDevuelveElDestino() {
        servicio.guardar(examen("EXM-001"));

        assertEquals("archivo-falso", servicio.serializar());
        assertEquals(1, repositorio.archivo.size());
    }

    @Test
    void noSerializaSinExamenesParaNoBorrarDatosPrevios() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> servicio.serializar());

        assertTrue(e.getMessage().contains("No hay exámenes"));
        assertEquals(0, repositorio.serializaciones);
    }

    @Test
    void deserializaReemplazandoLaMemoria() {
        servicio.guardar(examen("EXM-001"));
        servicio.serializar();
        servicio.guardar(examen("EXM-002"));

        List<ExamenMedico> recuperados = servicio.deserializar();

        assertEquals(1, recuperados.size());
        assertEquals(recuperados, servicio.listar());
    }

    @Test
    void propagaLosFallosDePersistencia() {
        assertThrows(PersistenciaException.class, () -> servicio.deserializar());
    }

    // ----- construcción -----

    @Test
    void exigeDependencias() {
        assertThrows(NullPointerException.class, () -> new GestionExamenService(null, new ValidacionBasicaStrategy()));
        assertThrows(NullPointerException.class, () -> new GestionExamenService(repositorio, null));
    }
}
