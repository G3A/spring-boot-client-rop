package co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario;

import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.data.IUsuarioRepository;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.error.ActualizarPerfilUsuarioError;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.event.PerfilUsuarioActualizadoEvent;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.actualizarperfilusuario.mapper.EmailErrorToActualizarPerfilUsuarioErrorMapper;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.data.Usuario;
import co.g3a.springbootclientrop.shared.domain.vo.email.Email;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de dominio responsable de aplicar la lógica necesaria para actualizar el perfil de un usuario.
 *
 * Este servicio contiene:
 * - Métodos de solo lectura ejecutados fuera del contexto transaccional
 * - Métodos de escritura ejecutados dentro de una transacción
 *
 * El objetivo es garantizar que:
 * - Las lecturas no bloqueen conexiones ni contiendan con otras transacciones activas
 * - Las escrituras sean atómicas y seguras, con soporte para rollback automático en caso de error
 *
 * Este enfoque reduce el tiempo de vida útil de las transacciones y mejora el rendimiento general.
 */
@Service
@RequiredArgsConstructor
@Slf4j
class ActualizarPerfilUsuarioService {

    private final IUsuarioRepository usuarioRepository;


    /**
     * Normaliza el email del usuario: lo convierte a minúsculas y elimina espacios.
     * Esta operación es pura y no requiere acceso a base de datos ni transacción.
     */
    @Observed(name = "normalizar.email", contextualName = "normalizar-email")
    public ActualizarPerfilUsuarioCommand normalizarEmail(ActualizarPerfilUsuarioCommand comando) {
        return ActualizarPerfilUsuarioCommand.Builder.create()
                .setId(comando.id())
                .setEmail(comando.email().trim().toLowerCase())
                .setName(comando.name())
                .setPassword(comando.password())
                .setAge(comando.age())
                .build();
    }

    /**
     * Busca el perfil del usuario en la base de datos.
     *
     * ⚠️ Esta operación se ejecuta fuera del contexto transaccional
     * gracias a `Propagation.NOT_SUPPORTED` para evitar bloqueos o "idle in transaction".
     *
     * @param comando comando con el ID del usuario
     * @return resultado con el usuario o error
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    @Observed(name = "buscar.perfil", contextualName = "buscar-perfil")
    public Result<Usuario, ActualizarPerfilUsuarioError> buscarPerfil(ActualizarPerfilUsuarioCommand comando) {
        try {
            return usuarioRepository.findById(comando.id())
                    .<Result<Usuario, ActualizarPerfilUsuarioError>>map(Result::success)
                    .orElseGet(() -> Result.failure(
                            new ActualizarPerfilUsuarioError.DbError("❌ Usuario no encontrado con id: " + comando.id())
                    ));
        } catch (Exception e) {
            return Result.failure(new ActualizarPerfilUsuarioError.DbError("❌ Error al buscar usuario: " + e.getMessage()));
        }
    }

    /**
     * Actualiza el perfil del usuario. Esta operación se ejecuta dentro de una transacción.
     *
     * ✅ Se garantiza atomicidad: si ocurre una excepción no controlada, se hace rollback de forma automática.
     * ✅ Se valida el email antes de aplicar los cambios.
     *
     * @param comando datos nuevos del usuario
     * @param usuarioActual entidad cargada desde base de datos
     * @return evento con los datos actualizados o error
     */
    @Transactional
    @Observed(name = "actualizar.perfil", contextualName = "actualizar-perfil")
    public Result<PerfilUsuarioActualizadoEvent, ActualizarPerfilUsuarioError> actualizarPerfil(
            ActualizarPerfilUsuarioCommand comando,
            Usuario usuarioActual
    ) {
        Result<PerfilUsuarioActualizadoEvent, ActualizarPerfilUsuarioError> resultado;

        try {
            resultado = Email.create(comando.email())
                    .mapFailure(EmailErrorToActualizarPerfilUsuarioErrorMapper::map)
                    .map(email -> actualizarDatosDelUsuario(usuarioActual, comando)) // función pura
                    .flatMap(this::actualizarUsuario)
                    .map(this::crearEventoPerfilActualizado);
        } catch (Exception e) {
            resultado = manejarExcepcion(e);
        }

        return resultado;
    }

    private Usuario actualizarDatosDelUsuario(Usuario original, ActualizarPerfilUsuarioCommand comando) {
        return new Usuario(
                original.getId(), // Preservamos ID
                comando.name(),
                comando.email(),
                comando.password(),
                comando.age(),
                false
        );
    }

    private Result<Usuario, ActualizarPerfilUsuarioError> actualizarUsuario(Usuario usuario) {
        Result<Usuario, ActualizarPerfilUsuarioError> resultado;
        try {
            usuarioRepository.save(usuario);
            log.info("✔️ Usuario actualizado correctamente. Email: {}", usuario.getEmail());
            resultado= Result.success(usuario);
        } catch (Exception e) {
            resultado= Result.failure(new ActualizarPerfilUsuarioError.DbError("❌ Error al guardar en DB: " + e.getMessage()));
        }
        return resultado;
    }

    private PerfilUsuarioActualizadoEvent crearEventoPerfilActualizado(Usuario usuario) {
        return new PerfilUsuarioActualizadoEvent(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getName(),
                usuario.getPassword(),
                usuario.getAge()
        );
    }

    private Result<PerfilUsuarioActualizadoEvent, ActualizarPerfilUsuarioError> manejarExcepcion(Exception e) {
        log.error("❌ Error inesperado al actualizar perfil", e);
        return Result.failure(new ActualizarPerfilUsuarioError.DbError("❌ Error inesperado al actualizar perfil: " + e.getMessage()));
    }
}