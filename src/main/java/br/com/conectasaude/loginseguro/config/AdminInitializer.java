package br.com.conectasaude.loginseguro.config;

import br.com.conectasaude.loginseguro.model.Role;
import br.com.conectasaude.loginseguro.model.Usuario;
import br.com.conectasaude.loginseguro.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminSenha;

    public AdminInitializer(
            UsuarioRepository usuarios,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email}") String adminEmail,
            @Value("${app.admin.password}") String adminSenha) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminSenha = adminSenha;
    }

    @Override
    public void run(String... args) {
        String emailNormalizado = adminEmail.trim().toLowerCase();
        if (emailNormalizado.isBlank() || usuarios.existsByEmail(emailNormalizado)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail(emailNormalizado);
        admin.setSenhaHash(passwordEncoder.encode(adminSenha));
        admin.setRoles(Set.of(Role.ADMIN));
        admin.setAtivo(true);
        admin.setCriadoEm(Instant.now());
        usuarios.save(admin);
    }
}
