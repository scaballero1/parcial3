package co.edu.poli.Examen2.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;
import co.edu.poli.Examen2.infrastructure.config.ConfiguracionAplicacion.ModoValidacion;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfiguracionYComposicionTest {

    @TempDir
    Path directorio;

    private static ExamenMedico examen(String codigo) {
        return ExamenMedico.builder().codigoExamen(codigo).nombrePaciente("Juan Pérez").costo(150.5)
                .tipoSangre(TipoSangre.A).factorRh(FactorRh.POSITIVO).build();
    }

    // ----- ConfiguracionAplicacion -----

    @Test
    void usaValoresPorDefecto() {
        ConfiguracionAplicacion c = ConfiguracionAplicacion.desde(new Properties());

        assertEquals(Path.of("data/examenes.dat"), c.archivoPersistencia());
        assertEquals(ModoValidacion.ESTRICTA, c.validacion());
    }

    @Test
    void leeLasPropiedadesIndicadas() {
        Properties p = new Properties();
        p.setProperty(ConfiguracionAplicacion.CLAVE_ARCHIVO, " /tmp/x.dat ");
        p.setProperty(ConfiguracionAplicacion.CLAVE_VALIDACION, " Basica ");

        ConfiguracionAplicacion c = ConfiguracionAplicacion.desde(p);

        assertEquals(Path.of("/tmp/x.dat"), c.archivoPersistencia());
        assertEquals(ModoValidacion.BASICA, c.validacion());
    }

    @Test
    void rechazaUnModoDeValidacionDesconocido() {
        Properties p = new Properties();
        p.setProperty(ConfiguracionAplicacion.CLAVE_VALIDACION, "relajada");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ConfiguracionAplicacion.desde(p));

        assertTrue(e.getMessage().contains("relajada"));
    }

    @Test
    void cargarLeeElRecursoYAceptaSobrescrituraPorPropiedadDelSistema() {
        ConfiguracionAplicacion porRecurso = ConfiguracionAplicacion.cargar();
        assertEquals(Path.of("data/examenes.dat"), porRecurso.archivoPersistencia());
        assertEquals(ModoValidacion.ESTRICTA, porRecurso.validacion());

        System.setProperty(ConfiguracionAplicacion.CLAVE_ARCHIVO, "otro/lugar.dat");
        System.setProperty(ConfiguracionAplicacion.CLAVE_VALIDACION, "basica");
        try {
            ConfiguracionAplicacion sobrescrita = ConfiguracionAplicacion.cargar();
            assertEquals(Path.of("otro/lugar.dat"), sobrescrita.archivoPersistencia());
            assertEquals(ModoValidacion.BASICA, sobrescrita.validacion());
        } finally {
            System.clearProperty(ConfiguracionAplicacion.CLAVE_ARCHIVO);
            System.clearProperty(ConfiguracionAplicacion.CLAVE_VALIDACION);
        }
    }

    @Test
    void exigeComponentes() {
        assertThrows(NullPointerException.class, () -> new ConfiguracionAplicacion(null, ModoValidacion.BASICA));
        assertThrows(NullPointerException.class, () -> new ConfiguracionAplicacion(Path.of("x"), null));
    }

    // ----- ComposicionRaiz (integración de todo el hexágono) -----

    @Test
    void flujoCompletoGuardarListarSerializarYDeserializar() {
        Path archivo = directorio.resolve("datos/examenes.dat");
        ComposicionRaiz sesion1 = new ComposicionRaiz(new ConfiguracionAplicacion(archivo, ModoValidacion.ESTRICTA));

        sesion1.guardarExamen().guardar(examen("EXM-001"));
        sesion1.guardarExamen().guardar(examen("EXM-002"));
        assertEquals(2, sesion1.listarExamenes().listar().size());
        assertEquals(archivo.toAbsolutePath().toString(), sesion1.serializarExamenes().serializar());

        ComposicionRaiz sesion2 = new ComposicionRaiz(new ConfiguracionAplicacion(archivo, ModoValidacion.ESTRICTA));
        assertTrue(sesion2.listarExamenes().listar().isEmpty());
        List<ExamenMedico> recuperados = sesion2.deserializarExamenes().deserializar();

        assertEquals(2, recuperados.size());
        assertEquals(recuperados, sesion2.listarExamenes().listar());
        assertThrows(ReglaNegocioException.class, () -> sesion2.guardarExamen().guardar(examen("EXM-001")));
    }

    @Test
    void elModoDeValidacionConfiguradoSeAplica() {
        ComposicionRaiz estricta = new ComposicionRaiz(
                new ConfiguracionAplicacion(directorio.resolve("a.dat"), ModoValidacion.ESTRICTA));
        ComposicionRaiz basica = new ComposicionRaiz(
                new ConfiguracionAplicacion(directorio.resolve("b.dat"), ModoValidacion.BASICA));
        ExamenMedico codigoLibre = examen("lab_77");

        assertThrows(ReglaNegocioException.class, () -> estricta.guardarExamen().guardar(codigoLibre));
        assertEquals(codigoLibre, basica.guardarExamen().guardar(codigoLibre));
    }

    @Test
    void lasCuatroOperacionesSonAtendidasPorElMismoServicio() {
        ComposicionRaiz raiz = new ComposicionRaiz(
                new ConfiguracionAplicacion(directorio.resolve("c.dat"), ModoValidacion.BASICA));

        assertSame(raiz.guardarExamen(), raiz.listarExamenes());
        assertSame(raiz.listarExamenes(), raiz.serializarExamenes());
        assertSame(raiz.serializarExamenes(), raiz.deserializarExamenes());
    }

    @Test
    void losErroresDeNegocioYPersistenciaLleganAlAdaptadorDeEntrada() {
        ComposicionRaiz raiz = new ComposicionRaiz(
                new ConfiguracionAplicacion(directorio.resolve("nada.dat"), ModoValidacion.ESTRICTA));

        assertThrows(ReglaNegocioException.class, () -> raiz.serializarExamenes().serializar());
        assertThrows(PersistenciaException.class, () -> raiz.deserializarExamenes().deserializar());
    }

    @Test
    void exigeConfiguracion() {
        assertThrows(NullPointerException.class, () -> new ComposicionRaiz(null));
    }
}
