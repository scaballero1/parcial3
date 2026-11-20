package co.edu.poli.Examen2.infrastructure.adapter.out.file;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.application.port.out.ExamenRepositoryPort;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Adaptador de salida (patrón <b>Adapter</b>): adapta la API de serialización
 * y archivos de Java ({@code ObjectOutputStream}, {@code Files}) a la interfaz
 * {@link ExamenRepositoryPort} que espera la aplicación.
 * <p>
 * Mantiene los exámenes de trabajo en memoria (en orden de registro) y los
 * vuelca a / recupera de un archivo serializado. Al leer aplica un
 * {@link ObjectInputFilter} de lista blanca para que un archivo malicioso no
 * pueda instanciar clases arbitrarias. Es seguro para uso concurrente.
 */
public class ArchivoExamenAdapter implements ExamenRepositoryPort {

    private static final ObjectInputFilter FILTRO = ObjectInputFilter.Config.createFilter(
            "maxdepth=10;maxrefs=100000;maxarray=1000000;"
                    + ExamenRegistro.class.getName()
                    + ";java.util.ArrayList;java.lang.Object;!*");

    private final Path rutaArchivo;
    private final Map<String, ExamenMedico> memoria = new LinkedHashMap<>();

    /**
     * Crea el adaptador.
     *
     * @param rutaArchivo archivo donde se serializa y de donde se deserializa
     */
    public ArchivoExamenAdapter(Path rutaArchivo) {
        this.rutaArchivo = Objects.requireNonNull(rutaArchivo, "rutaArchivo");
    }

    @Override
    public synchronized ExamenMedico guardar(ExamenMedico examen) {
        memoria.put(examen.getCodigoExamen(), examen);
        return examen;
    }

    @Override
    public synchronized List<ExamenMedico> listarTodos() {
        return List.copyOf(memoria.values());
    }

    @Override
    public synchronized String serializar() {
        ArrayList<ExamenRegistro> registros = new ArrayList<>();
        for (ExamenMedico examen : memoria.values()) {
            registros.add(ExamenRegistro.desde(examen));
        }
        try {
            Path padre = rutaArchivo.toAbsolutePath().getParent();
            if (padre != null) {
                Files.createDirectories(padre);
            }
            try (OutputStream salida = Files.newOutputStream(rutaArchivo);
                    ObjectOutputStream objetos = new ObjectOutputStream(salida)) {
                objetos.writeObject(registros);
            }
        } catch (IOException e) {
            throw new PersistenciaException(
                    "No se pudo escribir el archivo " + descripcion() + ": " + e.getMessage(), e);
        }
        return descripcion();
    }

    @Override
    public synchronized List<ExamenMedico> deserializar() {
        List<ExamenMedico> leidos;
        try (InputStream entrada = Files.newInputStream(rutaArchivo);
                ObjectInputStream objetos = new ObjectInputStream(entrada)) {
            objetos.setObjectInputFilter(FILTRO);
            leidos = convertir(objetos.readObject());
        } catch (NoSuchFileException e) {
            throw new PersistenciaException("No existe un archivo serializado en " + descripcion()
                    + ". Serialice primero los exámenes.", e);
        } catch (InvalidClassException e) {
            throw new PersistenciaException("El archivo " + descripcion() + " contiene datos no permitidos.", e);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException(
                    "No se pudo leer el archivo " + descripcion() + ": " + e.getMessage(), e);
        }
        memoria.clear();
        for (ExamenMedico examen : leidos) {
            memoria.put(examen.getCodigoExamen(), examen);
        }
        return List.copyOf(memoria.values());
    }

    private List<ExamenMedico> convertir(Object leido) {
        if (!(leido instanceof List<?>)) {
            throw formatoInesperado();
        }
        List<ExamenMedico> examenes = new ArrayList<>();
        for (Object elemento : (List<?>) leido) {
            if (!(elemento instanceof ExamenRegistro)) {
                throw formatoInesperado();
            }
            try {
                examenes.add(((ExamenRegistro) elemento).aDominio());
            } catch (ReglaNegocioException e) {
                throw new PersistenciaException(
                        "El archivo " + descripcion() + " contiene un examen inválido: " + e.getMessage(), e);
            }
        }
        return examenes;
    }

    private PersistenciaException formatoInesperado() {
        return new PersistenciaException("El archivo " + descripcion() + " no tiene el formato esperado.");
    }

    private String descripcion() {
        return rutaArchivo.toAbsolutePath().toString();
    }
}
