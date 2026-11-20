package co.edu.poli.Examen2.infrastructure.adapter.out.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import co.edu.poli.Examen2.domain.model.FactorRh;
import co.edu.poli.Examen2.domain.model.TipoSangre;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ArchivoExamenAdapterTest {

    @TempDir
    Path directorio;

    private static ExamenMedico examen(String codigo, String nombre, double costo, TipoSangre t, FactorRh rh) {
        return ExamenMedico.builder().codigoExamen(codigo).nombrePaciente(nombre).costo(costo)
                .tipoSangre(t).factorRh(rh).build();
    }

    private static ExamenMedico ejemplo(String codigo) {
        return examen(codigo, "Juan Pérez", 150.5, TipoSangre.A, FactorRh.POSITIVO);
    }

    private static void escribirObjeto(Path ruta, Object objeto) throws IOException {
        try (OutputStream salida = Files.newOutputStream(ruta); ObjectOutputStream oos = new ObjectOutputStream(salida)) {
            oos.writeObject(objeto);
        }
    }

    // ----- memoria de trabajo -----

    @Test
    void empiezaVacio() {
        assertTrue(new ArchivoExamenAdapter(directorio.resolve("e.dat")).listarTodos().isEmpty());
    }

    @Test
    void guardaDevuelveElExamenYLoConservaEnOrden() {
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(directorio.resolve("e.dat"));

        ExamenMedico devuelto = adapter.guardar(ejemplo("EXM-003"));
        adapter.guardar(ejemplo("EXM-001"));
        adapter.guardar(ejemplo("EXM-002"));

        assertEquals(ejemplo("EXM-003"), devuelto);
        assertEquals(List.of("EXM-003", "EXM-001", "EXM-002"),
                adapter.listarTodos().stream().map(ExamenMedico::getCodigoExamen).toList());
    }

    @Test
    void elListadoEsUnaCopiaDeSoloLectura() {
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(directorio.resolve("e.dat"));
        adapter.guardar(ejemplo("EXM-001"));
        List<ExamenMedico> copia = adapter.listarTodos();

        assertThrows(UnsupportedOperationException.class, () -> copia.add(ejemplo("EXM-002")));
        adapter.guardar(ejemplo("EXM-002"));
        assertEquals(1, copia.size());
    }

    // ----- serializar / deserializar -----

    @Test
    void recuperaExactamenteLoQueSerializoEnUnaInstanciaNueva() {
        Path ruta = directorio.resolve("datos/e.dat");
        ArchivoExamenAdapter origen = new ArchivoExamenAdapter(ruta);
        origen.guardar(ejemplo("EXM-001"));
        origen.guardar(examen("EXM-002", "María Gómez", 99, TipoSangre.AB, FactorRh.NEGATIVO));

        String destino = origen.serializar();

        assertEquals(ruta.toAbsolutePath().toString(), destino);
        ArchivoExamenAdapter nuevo = new ArchivoExamenAdapter(ruta);
        assertTrue(nuevo.listarTodos().isEmpty());
        List<ExamenMedico> recuperados = nuevo.deserializar();
        assertEquals(origen.listarTodos(), recuperados);
        assertEquals("María Gómez", recuperados.get(1).getNombrePaciente());
        assertEquals(99.0, recuperados.get(1).getCosto());
        assertEquals("AB-", recuperados.get(1).getGrupoSanguineo());
        assertEquals(recuperados, nuevo.listarTodos());
    }

    @Test
    void deserializarReemplazaLaMemoria() {
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(directorio.resolve("e.dat"));
        adapter.guardar(ejemplo("EXM-001"));
        adapter.serializar();
        adapter.guardar(ejemplo("EXM-002"));

        adapter.deserializar();

        assertEquals(List.of(ejemplo("EXM-001")), adapter.listarTodos());
    }

    @Test
    void informaSiNoExisteElArchivo() {
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(directorio.resolve("no.dat"));

        PersistenciaException e = assertThrows(PersistenciaException.class, adapter::deserializar);

        assertTrue(e.getMessage().contains("No existe un archivo serializado"));
    }

    @Test
    void siFallaLaLecturaLaMemoriaQuedaIntacta() throws IOException {
        Path ruta = directorio.resolve("basura.dat");
        Files.write(ruta, "no soy un objeto serializado".getBytes(StandardCharsets.UTF_8));
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(ruta);
        adapter.guardar(ejemplo("EXM-001"));

        PersistenciaException e = assertThrows(PersistenciaException.class, adapter::deserializar);

        assertTrue(e.getMessage().contains("No se pudo leer"));
        assertEquals(1, adapter.listarTodos().size());
    }

    @Test
    void rechazaClasesQueNoEstanEnLaListaBlanca() throws IOException {
        Path ruta = directorio.resolve("malicioso.dat");
        ArrayList<Object> contenido = new ArrayList<>();
        contenido.add(new Date());
        escribirObjeto(ruta, contenido);

        PersistenciaException e = assertThrows(PersistenciaException.class,
                () -> new ArchivoExamenAdapter(ruta).deserializar());

        assertTrue(e.getMessage().contains("datos no permitidos"));
    }

    @Test
    void rechazaUnObjetoRaizQueNoEsUnaLista() throws IOException {
        Path ruta = directorio.resolve("texto.dat");
        escribirObjeto(ruta, "solo un texto");

        assertTrue(assertThrows(PersistenciaException.class,
                () -> new ArchivoExamenAdapter(ruta).deserializar()).getMessage().contains("formato esperado"));
    }

    @Test
    void rechazaUnaListaConElementosDeOtroTipo() throws IOException {
        Path ruta = directorio.resolve("mezcla.dat");
        ArrayList<Object> contenido = new ArrayList<>();
        contenido.add("no soy un registro");
        escribirObjeto(ruta, contenido);

        assertTrue(assertThrows(PersistenciaException.class,
                () -> new ArchivoExamenAdapter(ruta).deserializar()).getMessage().contains("formato esperado"));
    }

    @Test
    void unArchivoManipuladoNoPuedeIntroducirExamenesInvalidos() throws IOException {
        Path ruta = directorio.resolve("manipulado.dat");
        ArrayList<ExamenRegistro> contenido = new ArrayList<>();
        contenido.add(new ExamenRegistro("EXM-001", "Juan Pérez", -5, "A", "+"));
        escribirObjeto(ruta, contenido);

        assertTrue(assertThrows(PersistenciaException.class,
                () -> new ArchivoExamenAdapter(ruta).deserializar()).getMessage().contains("examen inválido"));
    }

    @Test
    void traduceLosFallosDeEscrituraAPersistenciaException() throws IOException {
        Path archivo = Files.createFile(directorio.resolve("archivo.txt"));
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(archivo.resolve("hijo.dat"));
        adapter.guardar(ejemplo("EXM-001"));

        PersistenciaException e = assertThrows(PersistenciaException.class, adapter::serializar);

        assertTrue(e.getMessage().contains("No se pudo escribir"));
        assertNotNull(e.getCause());
    }

    @Test
    void sobrescribeElArchivoAnterior() throws IOException {
        Path ruta = directorio.resolve("e.dat");
        ArchivoExamenAdapter adapter = new ArchivoExamenAdapter(ruta);
        adapter.guardar(ejemplo("EXM-001"));
        adapter.guardar(ejemplo("EXM-002"));
        adapter.serializar();
        long grande = Files.size(ruta);

        ArchivoExamenAdapter otro = new ArchivoExamenAdapter(ruta);
        otro.guardar(ejemplo("EXM-009"));
        otro.serializar();

        assertTrue(Files.size(ruta) < grande);
        assertEquals(1, new ArchivoExamenAdapter(ruta).deserializar().size());
    }

    @Test
    void exigeRuta() {
        assertThrows(NullPointerException.class, () -> new ArchivoExamenAdapter(null));
    }

    // ----- ExamenRegistro -----

    @Test
    void elRegistroConvierteDeIdaYVuelta() {
        ExamenMedico original = examen("EXM-009", "Luis Mora", 10, TipoSangre.O, FactorRh.NEGATIVO);
        ExamenRegistro registro = ExamenRegistro.desde(original);

        assertEquals("O", registro.tipoSangre());
        assertEquals("-", registro.factorRh());
        assertEquals(original, registro.aDominio());
        assertFalse(registro.aDominio().getNombrePaciente().isEmpty());
    }
}
