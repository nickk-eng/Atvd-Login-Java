package br.com.conectasaude.loginseguro.service;

import br.com.conectasaude.loginseguro.model.Role;
import br.com.conectasaude.loginseguro.model.Usuario;
import br.com.conectasaude.loginseguro.repository.UsuarioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class AdminUsuarioService {

    private final UsuarioRepository usuarios;

    public AdminUsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public List<Usuario> listarTodos() {
        return usuarios.findAll().stream()
                .sorted(Comparator.comparing(Usuario::getCriadoEm).reversed())
                .toList();
    }

    public void atualizarPerfis(String usuarioId, Set<Role> roles, String emailAdminLogado) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos um perfil.");
        }

        Usuario usuario = buscarUsuario(usuarioId);
        String emailUsuario = usuario.getEmail();

        if (emailUsuario != null && emailUsuario.equalsIgnoreCase(emailAdminLogado) && !roles.contains(Role.ADMIN)) {
            throw new IllegalArgumentException("O administrador logado nao pode remover o proprio perfil ADMIN.");
        }

        usuario.setRoles(roles);
        usuarios.save(usuario);
    }

    public void alternarStatus(String usuarioId, String emailAdminLogado) {
        Usuario usuario = buscarUsuario(usuarioId);
        String emailUsuario = usuario.getEmail();

        if (emailUsuario != null && emailUsuario.equalsIgnoreCase(emailAdminLogado)) {
            throw new IllegalArgumentException("O administrador logado nao pode desativar a propria conta.");
        }

        usuario.setAtivo(!usuario.isAtivo());
        usuarios.save(usuario);
    }

    private Usuario buscarUsuario(String usuarioId) {
        return usuarios.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
    }
}
