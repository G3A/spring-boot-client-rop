package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import co.g3a.functionalrop.core.Result;
import co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario.ActualizarPerfilUsuarioApiErrorResponseBuilder.ErrorDefinitions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizarPerfilUsuarioUseCaseTest {

    @Mock
    private ActualizarPerfilUsuarioService service;
    
    private ActualizarPerfilUsuarioUseCase useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new ActualizarPerfilUsuarioUseCase(service);
    }
    
    @Test
    void execute_debeEjecutarFlujoCompletoYRetornarCodigoActivacion() {
        // Arrange
        ActualizarPerfilUsuarioCommand comando = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("test.user@domain.com")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        ActualizarPerfilUsuarioCommand comandoNormalizado = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("test.user@domain.com")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        String codigoEsperado = "ABC123";
        
        when(service.normalizarEmail(comando)).thenReturn(comandoNormalizado);
        when(service.actualizarPerfil(comandoNormalizado))
                .thenReturn(CompletableFuture.completedFuture(Result.success(comandoNormalizado)));
        when(service.enviarNotificacion(comandoNormalizado))
                .thenReturn(CompletableFuture.completedFuture(Result.success(comandoNormalizado)));
        when(service.generarCodigoActivacion(comandoNormalizado))
                .thenReturn(CompletableFuture.completedFuture(Result.success(codigoEsperado)));
                
        // Act
        var resultado = useCase.execute(comando);
        
        // Assert
        verify(service).normalizarEmail(comando);
        verify(service).actualizarPerfil(comandoNormalizado);
        verify(service).enviarNotificacion(comandoNormalizado);
        verify(service).generarCodigoActivacion(comandoNormalizado);
        
        assertThat(resultado.join().isSuccess()).isTrue();
        assertThat(resultado.join().getValue()).isEqualTo(codigoEsperado);
    }
    
    @Test
    void execute_debePropagaErrorCuandoActualizarPerfilFalla() {
        // Arrange
        ActualizarPerfilUsuarioCommand comando = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("test.user@domain.com")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        ActualizarPerfilUsuarioCommand comandoNormalizado = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("test.user@domain.com")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        ErrorDefinitions.DbError error = new ErrorDefinitions.DbError("Error en base de datos");
        
        when(service.normalizarEmail(comando)).thenReturn(comandoNormalizado);
        when(service.actualizarPerfil(comandoNormalizado))
                .thenReturn(CompletableFuture.completedFuture(Result.failure(error)));
                
        // Act
        var resultado = useCase.execute(comando);
        
        // Assert
        verify(service).normalizarEmail(comando);
        verify(service).actualizarPerfil(comandoNormalizado);
        verify(service, never()).enviarNotificacion(any());
        verify(service, never()).generarCodigoActivacion(any());
        
        assertThat(resultado.join().isSuccess()).isFalse();
        assertThat(resultado.join().getError()).isEqualTo(error);
    }
}