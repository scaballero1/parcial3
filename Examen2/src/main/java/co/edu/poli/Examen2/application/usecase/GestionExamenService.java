package co.edu.poli.Examen2.application.usecase;

import co.edu.poli.Examen2.application.port.in.DeserializarExamenesUseCase;
import co.edu.poli.Examen2.application.port.in.GuardarExamenUseCase;
import co.edu.poli.Examen2.application.port.in.ListarExamenesUseCase;
import co.edu.poli.Examen2.application.port.in.SerializarExamenesUseCase;
import co.edu.poli.Examen2.application.port.out.ExamenRepositoryPort;
import co.edu.poli.Examen2.application.strategy.ValidacionExamenStrategy;
import co.edu.poli.Examen2.domain.exception.ReglaNegocioException;
import co.edu.poli.Examen2.domain.model.ExamenMedico;

import java.util.List;
import java.util.Objects;

/**
 * Implementación de los cuatro casos de uso del sistema. Orquesta el dominio,
 * la {@link ValidacionExamenStrategy} y el {@link ExamenRepositoryPort}; no
 * contiene detalles de interfaz gráfica ni de archivos.
 */
public class GestionExamenService implements GuardarExamenUseCase, ListarExamenesUseCase,
        SerializarExamenesUseCase, DeserializarExamenesUseCase {

    private final ExamenRepositoryPort repositorio;
    private final ValidacionExamenStrategy validacion;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param repositorio puerto de salida donde viven los exámenes
     * @param validacion  política de validación (estrategia) a aplicar al guardar
     */
    public GestionExamenService(ExamenRepositoryPort repositorio, ValidacionExamenStrategy validacion) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio");
        this.validacion = Objects.requireNonNull(validacion, "validacion");
    }

    @Override
    public ExamenMedico guardar(ExamenMedico examen) {
        Objects.requireNonNull(examen, "examen");
        validacion.validar(examen);
        boolean duplicado = repositorio.listarTodos().stream()
                .anyMatch(existente -> existente.equals(examen));
        if (duplicado) {
            throw new ReglaNegocioException(
                    "Ya existe un examen registrado con el código " + examen.getCodigoExamen() + ".");
        }
        return repositorio.guardar(examen);
    }

    @Override
    public List<ExamenMedico> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public String serializar() {
        if (repositorio.listarTodos().isEmpty()) {
            throw new ReglaNegocioException("No hay exámenes registrados para serializar.");
        }
        return repositorio.serializar();
    }

    @Override
    public List<ExamenMedico> deserializar() {
        return repositorio.deserializar();
    }
}
