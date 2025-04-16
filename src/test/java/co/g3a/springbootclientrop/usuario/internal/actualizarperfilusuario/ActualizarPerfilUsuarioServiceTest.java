package co.g3a.springbootclientrop.usuario.internal.actualizarperfilusuario;

import co.g3a.functionalrop.core.DeadEnd;
import co.g3a.functionalrop.core.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizarPerfilUsuarioServiceTest {

    @Mock
    private DeadEnd deadEnd;
    
    @Mock
    private Executor executor;
    
    private ActualizarPerfilUsuarioService service;
    
    @BeforeEach
    void setUp() {
        service = new ActualizarPerfilUsuarioService(deadEnd);
    }
    
    @Test
    void normalizarEmail_debeConvertirAMinusculasYEliminarEspacios() {
        // Arrange
        ActualizarPerfilUsuarioCommand comando = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("  Test.User@DOMAIN.com  ")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        // Act
        ActualizarPerfilUsuarioCommand resultado = service.normalizarEmail(comando);
        
        // Assert
        assertThat(resultado.email()).isEqualTo("test.user@domain.com");
        assertThat(resultado.name()).isEqualTo("Usuario Test");
        assertThat(resultado.password()).isEqualTo("password123");
        assertThat(resultado.age()).isEqualTo(25);
    }
    
    @Test
    void actualizarPerfil_debeRetornarExitoCuandoEmailEsValido() {
        // Arrange
        ActualizarPerfilUsuarioCommand comando = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("test.user@domain.com")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        when(deadEnd.runSafeResultTransform(eq(comando), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Result.success(comando)));
                
        // Act
        var resultado = service.actualizarPerfil(comando);
        
        // Assert
        verify(deadEnd).runSafeResultTransform(eq(comando), any(), any());
        assertThat(resultado.toCompletableFuture().join().isSuccess()).isTrue();
    }
    
    @Test
    void generarCodigoActivacion_debeGenerarCodigoCuandoEmailEsValido() {
        // Arrange
        ActualizarPerfilUsuarioCommand comando = ActualizarPerfilUsuarioCommand.Builder.create()
                .setEmail("test.user@domain.com")
                .setName("Usuario Test")
                .setPassword("password123")
                .setAge(25)
                .build();
                
        String codigoEsperado = "ABC123";
                
        when(deadEnd.runSafeResultTransform(eq(comando), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(Result.success(codigoEsperado)));
                
        // Act
        var resultado = service.generarCodigoActivacion(comando);
        
        // Assert
        verify(deadEnd).runSafeResultTransform(eq(comando), any(), any());
        assertThat(resultado.toCompletableFuture().join().isSuccess()).isTrue();
        assertThat(resultado.toCompletableFuture().join().getValue()).isEqualTo(codigoEsperado);
    }
}