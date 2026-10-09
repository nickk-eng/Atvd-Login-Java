# SISTEMA WEB DE CONTROLE DE ACESSO COM SPRING BOOT E MONGODB

**Relatório técnico acadêmico**  
**Autor(a):** Nicholas Moura
**Instituição:** UMC  
**Curso:** Sistemas de informação
**Cidade:** Mogi das cruzes 
**Ano:** 2026

## Resumo

Este relatório apresenta o desenvolvimento de um sistema web de controle de acesso construído com Java 17 e Spring Boot. A aplicação permite o cadastro e a autenticação de usuários e restringe áreas do sistema conforme três perfis: aluno, professor e administrador. A arquitetura separa controladores, serviços, repositórios, modelos e objetos de transferência de dados, enquanto o Thymeleaf renderiza as páginas HTML no servidor e permite a reutilização de fragmentos de interface. O Spring Security administra a autenticação, a autorização por perfil e a sessão autenticada. As senhas dos usuários são armazenadas como hashes BCrypt, e os documentos de usuário e sessão são persistidos no MongoDB, com possibilidade de conexão ao MongoDB Atlas por meio de variável de ambiente. A solução foi organizada como uma base genérica, com páginas e regras iniciais que podem ser substituídas ou ampliadas para atender ao projeto gamificado ConectaSaude. Conclui-se que a separação entre infraestrutura de acesso e conteúdo de domínio facilita a evolução do sistema, desde que as futuras regras de negócio, requisitos de segurança e experiências de usuário sejam incorporados e avaliados de acordo com o escopo do projeto final.

**Palavras-chave:** controle de acesso; Spring Boot; Spring Security; Thymeleaf; MongoDB.

## 1 Introdução

Aplicações web que oferecem áreas diferentes a usuários distintos precisam identificar cada pessoa e controlar quais recursos podem ser acessados. O projeto descrito neste relatório implementa uma base de autenticação e autorização para demonstrar esse controle em uma aplicação Java.

O objetivo geral é disponibilizar cadastro, login e restrição de rotas por perfil, mantendo a persistência de usuários e sessões em banco de documentos. Os objetivos específicos são separar responsabilidades entre as camadas da aplicação, aplicar validação aos dados de cadastro, proteger senhas com hash e organizar as páginas de forma que possam ser substituídas por interfaces de domínio em uma etapa futura.

O sistema contempla três papéis: `ALUNO`, `PROFESSOR` e `ADMIN`. O aluno pode criar uma conta e, após o cadastro ser persistido, é autenticado e direcionado à página `/home`. O cadastro de professor é disponibilizado pelo formulário, mas o fluxo atual direciona esse usuário ao login. A conta administrativa inicial é criada no início da aplicação quando ainda não há usuário com o e-mail configurado.

## 2 Estrutura do sistema

### 2.1 Arquitetura modular

A aplicação segue uma organização em camadas. O pacote `controller` recebe requisições HTTP, aplica o fluxo web e seleciona as páginas a serem renderizadas. O pacote `service` concentra regras de cadastro e administração. O pacote `repository` define a comunicação com os documentos MongoDB por meio do Spring Data. O pacote `model` contém as estruturas persistidas, enquanto `dto` mantém os dados de entrada e as regras de validação dos formulários. O pacote `config` reúne configuração de segurança e inicialização do administrador.

O fluxo de persistência pode ser resumido como:

```text
Navegador → Controller → Service → Repository → MongoDB
                         ↓
                    Thymeleaf
```

Essa separação reduz o acoplamento entre apresentação, regras de negócio e armazenamento. A estrutura de pacotes também mantém a classe principal em um pacote raiz, permitindo que a configuração do Spring Boot encontre componentes e repositórios em seus subpacotes (SPRING BOOT, s. d.-b).

### 2.2 Thymeleaf e apresentação

O Thymeleaf é usado como mecanismo de templates integrado ao Spring MVC. Os controladores retornam nomes de páginas e disponibilizam atributos de modelo que podem ser utilizados nas expressões Thymeleaf. No projeto, os templates ficam em `src/main/resources/templates`, os estilos estáticos em `src/main/resources/static` e o fragmento compartilhado de cabeçalho e navegação em `fragments.html`.

Essa organização permite substituir ou acrescentar páginas sem misturar marcação HTML às regras de autenticação. A identidade visual atual é definida pelo CSS compartilhado e pode ser trocada ou estendida para incorporar layouts, componentes e conteúdos específicos do ConectaSaude. O Thymeleaf também integra formulários com objetos de entrada e mensagens de validação (THYMELEAF, s. d.).

### 2.3 Fluxo de cadastro e autenticação

O cadastro é recebido pelo `AuthController` e validado com Bean Validation. O `CadastroUsuarioService` normaliza o e-mail, verifica duplicidade, codifica a senha, atribui o perfil e persiste o usuário. Para um cadastro de aluno, o controlador autentica as credenciais recém-criadas, salva o contexto de segurança na sessão e redireciona para `/home`. No login convencional, o Spring Security valida as credenciais e o controlador `/painel` encaminha o usuário conforme as autoridades associadas à conta.

## 3 Integração com MongoDB Atlas

O projeto utiliza Spring Data MongoDB para persistir usuários como documentos. A classe `Usuario` é mapeada para a coleção `usuarios` e contém identificador, nome, e-mail, hash da senha, perfis, estado da conta e data de criação. O campo de e-mail tem índice único para impedir a criação de contas duplicadas no banco.

As sessões HTTP são armazenadas com Spring Session Data MongoDB na coleção `sessoes_login`. Isso permite que a sessão autenticada seja mantida pelo armazenamento MongoDB configurado pela aplicação, em vez de depender exclusivamente da memória do processo. A duração máxima da sessão é definida como oito horas na configuração de `@EnableMongoHttpSession`.

A conexão é configurada em `src/main/resources/application.properties` pela propriedade `spring.data.mongodb.uri`. O valor é externalizado por meio da variável de ambiente `MONGODB_URI`, mantendo usuário, senha e endereço do cluster fora do código-fonte. O Spring Boot permite externalizar propriedades para adaptar a configuração entre ambientes (SPRING BOOT, s. d.-a). Em desenvolvimento local, a aplicação pode utilizar a URI local padrão; para o Atlas, deve-se fornecer uma URI `mongodb+srv://` válida e autorizar o IP de origem no projeto Atlas (MONGODB, s. d.).

## 4 Decisões de design e segurança

### 4.1 Autenticação e autorização

O Spring Security é configurado por uma cadeia de filtros que define as rotas públicas e protegidas. As páginas `/login` e `/cadastro`, além dos recursos estáticos, são públicas. A rota `/home` e a área `/aluno` exigem o perfil `ALUNO`; as rotas de professor aceitam `PROFESSOR` ou `ADMIN`; e as rotas administrativas exigem `ADMIN`. As autoridades são carregadas pelo `MongoUserDetailsService` a partir dos perfis persistidos em cada usuário.

A autorização é aplicada no servidor, de modo que a ocultação de links na interface não é a única barreira de acesso. A separação entre perfil e rota permite adicionar outros módulos e regras quando as necessidades do ConectaSaude forem detalhadas.

### 4.2 Armazenamento de senhas

As senhas das contas não são persistidas em texto puro. O projeto utiliza `BCryptPasswordEncoder` com fator de custo 12 antes de salvar o campo `senhaHash`. BCrypt é uma função adaptativa de hash: a verificação compara a senha informada com o valor armazenado, sem exigir a recuperação da senha original. Esse procedimento é diferente de criptografia reversível e segue o modelo de `PasswordEncoder` usado pelo Spring Security (SPRING SECURITY, s. d.).

As credenciais administrativas e a URI de banco também podem ser definidas por variáveis de ambiente. Os valores padrão existentes servem à execução acadêmica local e devem ser substituídos por valores próprios em qualquer ambiente compartilhado.

### 4.3 Sessões e controles por perfil

O contexto de autenticação é associado à sessão HTTP após o login. No cadastro de aluno, o sistema cria uma sessão autenticada e altera o identificador da sessão antes de persistir o contexto, evitando manter o identificador anterior após a autenticação. O Spring Session integra a sessão HTTP ao MongoDB (SPRING SESSION, s. d.).

O uso de rotas protegidas e papéis explícitos fornece um ponto de partida para políticas de acesso mais específicas. Para um ambiente de produção, ainda devem ser avaliados requisitos como gerenciamento de segredos, recuperação de conta, política de bloqueio, monitoramento, implantação com HTTPS e revisão dos valores padrão.

## 5 Preparação para adaptação ao ConectaSaude

A base atual é genérica e pode receber o domínio gamificado do ConectaSaude por meio de evolução incremental. O mecanismo de autenticação, o armazenamento de sessões e a autorização por perfis podem continuar como infraestrutura. Em contrapartida, as páginas, os modelos de domínio e os serviços devem ser ampliados para representar funcionalidades próprias do projeto final, como atividades, progresso, pontuação, conquistas ou outros elementos que venham a ser definidos nos requisitos.

A separação dos templates Thymeleaf e dos fragmentos compartilhados permite desenvolver uma identidade visual e layouts específicos sem transferir regras de apresentação para os serviços. Antes da adaptação, é necessário definir os requisitos funcionais, o modelo de dados e as permissões correspondentes, preservando a validação no servidor e o armazenamento seguro de credenciais.

## 6 Considerações finais

O projeto implementa uma base web de controle de acesso com cadastro, autenticação, três perfis de autorização, páginas renderizadas por Thymeleaf e persistência MongoDB para usuários e sessões. A organização modular facilita a compreensão e a evolução do código, enquanto BCrypt e a configuração externalizada da URI estabelecem práticas iniciais de proteção de credenciais.

Como continuidade, recomenda-se detalhar os requisitos do sistema gamificado ConectaSaude e, a partir deles, desenvolver os modelos e serviços de domínio, atualizar as experiências de cada perfil e revisar a configuração de segurança do ambiente de implantação. Assim, a base de autenticação pode ser reaproveitada sem presumir que as funcionalidades genéricas já atendam a todos os requisitos do projeto final.

## Referências

MONGODB. **Connect to a cluster**. MongoDB Manual. Disponível em: <https://www.mongodb.com/docs/manual/connect-to-cluster/>. Acesso em: 8 out. 2026.

SPRING BOOT. **Externalized configuration**. Spring Boot Reference Documentation. Disponível em: <https://docs.spring.io/spring-boot/3.3/reference/features/external-config.html>. Acesso em: 8 out. 2026.

SPRING SECURITY. **Password storage**. Spring Security Reference. Disponível em: <https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html>. Acesso em: 8 out. 2026.

SPRING BOOT. **Structuring your code**. Spring Boot Reference Documentation. Disponível em: <https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html>. Acesso em: 8 out. 2026.

SPRING SESSION. **Spring Session MongoDB reference documentation**. Disponível em: <https://docs.spring.io/spring-session-data-mongodb/docs/current/reference/html/index.html>. Acesso em: 8 out. 2026.

THYMELEAF. **Thymeleaf + Spring**. Disponível em: <https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.pdf>. Acesso em: 8 out. 2026.


