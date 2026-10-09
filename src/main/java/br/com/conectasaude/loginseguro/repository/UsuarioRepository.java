package br.com.conectasaude.loginseguro.repository;

import br.com.conectasaude.loginseguro.model.Usuario;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
