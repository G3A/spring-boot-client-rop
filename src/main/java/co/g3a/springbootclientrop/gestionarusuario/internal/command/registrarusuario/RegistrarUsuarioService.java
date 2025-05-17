package co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario;


import co.g3a.functionalrop.core.DeadEnd;
import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.data.IUsuarioRepository;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.error.RegistrarUsuarioErrors;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.event.UsuarioRegistradoEvent;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.data.Usuario;
import co.g3a.springbootclientrop.gestionarusuario.internal.command.registrarusuario.mapper.EmailErrorToRegistrarUsuarioErrorsMapper;
import co.g3a.springbootclientrop.shared.domain.vo.email.Email;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class RegistrarUsuarioService  {
    private final IUsuarioRepository usuarioRepository;
    private final DeadEnd deadEnd;


    @Observed(name = "registrar.usuario", contextualName = "registrar-usuario")
    public Result<UsuarioRegistradoEvent, RegistrarUsuarioErrors> registrarUsuario(RegistrarUsuarioCommand commando) {
        var emailResult = Email.create(commando.email())
                .mapFailure(EmailErrorToRegistrarUsuarioErrorsMapper::map);

        if (!emailResult.isSuccess()) {
            return Result.failure(emailResult.getError());
        }

        var email = emailResult.getValue();

        Usuario usuario = new Usuario();
        usuario.setName(commando.name());
        usuario.setEmail(email.value());
        usuario.setAge(commando.age());
        usuario.setPassword(commando.password());

        try{
            log.info("🚀 Iniciando proceso de registro para el usuario: {}", commando.name());
            Usuario saved = usuarioRepository.save(usuario);
            log.info("✔️ Registro del usuario completado exitosamente. Usuario: {}", commando.name());
            return Result.success(new UsuarioRegistradoEvent(saved.getId(),
                    saved.getName(),
                    saved.getEmail(),
                    saved.getPassword()));
        } catch (Exception e) {
            if(e instanceof DataIntegrityViolationException){
                return Result.failure(new RegistrarUsuarioErrors.ConflictoUsuarioEmailRepetidoError(email.value(), e.getMessage()));
            }
            return Result.failure(new RegistrarUsuarioErrors.DbError("❌ Error al registrar el usuario", e.getMessage()));
        }
    }
}