package co.edu.poli.Examen2.application.usecase;

import co.edu.poli.Examen2.application.exception.PersistenciaException;
import co.edu.poli.Examen2.application.port.out.ExamenRepositoryPort;
import co.edu.poli.Examen2.domain.model.ExamenMedico;
import java.util.ArrayList;
import java.util.List;

/** Doble de prueba del puerto de salida {@link ExamenRepositoryPort}. */
class RepositorioFalso implements ExamenRepositoryPort {

    final List<ExamenMedico> memoria = new ArrayList<>();
    List<ExamenMedico> archivo;
    int serializaciones;

    @Override
    public ExamenMedico guardar(ExamenMedico examen) {
        memoria.add(examen);
        return examen;
    }

    @Override
    public List<ExamenMedico> listarTodos() {
        return List.copyOf(memoria);
    }

    @Override
    public String serializar() {
        archivo = new ArrayList<>(memoria);
        serializaciones++;
        return "archivo-falso";
    }

    @Override
    public List<ExamenMedico> deserializar() {
        if (archivo == null) {
            throw new PersistenciaException("nada guardado");
        }
        memoria.clear();
        memoria.addAll(archivo);
        return List.copyOf(memoria);
    }
}
