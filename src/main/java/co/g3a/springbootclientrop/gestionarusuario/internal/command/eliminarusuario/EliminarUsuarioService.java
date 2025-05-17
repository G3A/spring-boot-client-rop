package co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario;

import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.data.IUsuarioRepository;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.error.EliminarUsuarioErrors;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.eliminarusuario.event.UsuarioEliminadoEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
class EliminarUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    //Antes
/*
    @Observed(name = "eliminar.usuario", contextualName = "eliminar-usuario")
    public Result<UsuarioEliminadoEvent, EliminarUsuarioErrors> eliminarUsuario(EliminarUsuarioCommand comando) {
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(comando.id());
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                log.info("🚀 Iniciando proceso de eliminación para el usuario con ID: {}", comando.id());
                usuarioRepository.delete(usuario);
                log.info("✔️ Eliminación del usuario completada exitosamente. Usuario ID: {}", comando.id());
                return Result.success(new UsuarioEliminadoEvent(usuario.getId(),usuario.getName(), usuario.getEmail()));
            } else {
                return Result.failure(new EliminarUsuarioErrors.UsuarioNoEncontrado("Usuario no encontrado con el ID: " + comando.id()));
            }
        } catch (Exception e) {
            log.error("❌ Error al eliminar el usuario con ID: {}", comando.id(), e);
            return Result.failure(new EliminarUsuarioErrors.DbError("Error al eliminar el usuario", e.getMessage()));
        }
    }

 */
    // Después
    /**
     * Versión que no requiere utilizar la palabra reservada if, usamos solamente un enfoque funcional muy elegante y legible
     * @param comando
     * @return
     */
    @Observed(name = "eliminar.usuario", contextualName = "eliminar-usuario")
    public Result<UsuarioEliminadoEvent, EliminarUsuarioErrors> eliminarUsuario(EliminarUsuarioCommand comando) {
        try {
            // Si no lo sabías, la función finById retorna un tipo Optional, entonces podemos luego usar orElseGet
            // en caso de no retornar un registro desde la bd para darle manejo al resultado, en este caso:UsuarioNoEncontrado
            return usuarioRepository.findById(comando.id())
                    .map(usuario -> {
                        log.info("🚀 Iniciando proceso de eliminación para el usuario con ID: {}", usuario.getId());
                        usuarioRepository.delete(usuario);
                        log.info("✔️ Eliminación del usuario completada exitosamente. Usuario ID: {}", usuario.getId());
                        return Result.<UsuarioEliminadoEvent, EliminarUsuarioErrors>success(
                                new UsuarioEliminadoEvent(usuario.getId(), usuario.getName(), usuario.getEmail()));
                    })
                    .orElseGet(() -> Result.failure(
                            new EliminarUsuarioErrors.UsuarioNoEncontrado("Usuario no encontrado con el ID: " + comando.id()))
                    );
        } catch (Exception e) {
            log.error("❌ Error al eliminar el usuario con ID: {}", comando.id(), e);
            return Result.failure(new EliminarUsuarioErrors.DbError("Error al eliminar el usuario", e.getMessage()));
        }
    }
}