package br.com.conectasaude.loginseguro.service;

import br.com.conectasaude.loginseguro.dto.CadastroUsuarioForm;
import br.com.conectasaude.loginseguro.model.Role;
import br.com.conectasaude.loginseguro.model.Usuario;
import br.com.conectasaude.loginseguro.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CadastroUsuarioService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public CadastroUsuarioService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    public void cadastrar(CadastroUsuarioForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (usuarios.existsByEmail(email)) {
            throw new IllegalArgumentException("Ja existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(form.getSenha()));
        usuario.setRoles(Set.of(Role.valueOf(form.getPerfil())));
        usuario.setAtivo(true);
        usuario.setCriadoEm(Instant.now());
        usuarios.save(usuario);
    }
}
