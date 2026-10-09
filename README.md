# Sistema de Login Seguro

Aplicação web acadêmica de controle de acesso, desenvolvida com Java e Spring Boot. O sistema demonstra cadastro e autenticação de usuários, autorização por perfis e persistência de contas e sessões no MongoDB.

A base foi organizada para ser reutilizada e adaptada futuramente ao projeto ConectaSaude. O conteúdo e os templates atuais são genéricos; funcionalidades de gamificação e regras específicas do projeto final podem ser adicionadas em etapas posteriores.

## Funcionalidades

- Cadastro de alunos e professores.
- Autenticação por e-mail e senha.
- Acesso autorizado por três perfis: `ALUNO`, `PROFESSOR` e `ADMIN`.
- Autenticação automática do aluno após o cadastro, com redirecionamento para `/home`.
- Administração de usuários, perfis e status da conta.
- Armazenamento de usuários e sessões no MongoDB.
- Interface renderizada por Thymeleaf e estilos responsivos.

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring MVC
- Spring Security
- Spring Data MongoDB
- Spring Session Data MongoDB
- Thymeleaf e Thymeleaf Extras para Spring Security
- Bean Validation
- Maven

## Pré-requisitos

- JDK 17 ou superior.
- Maven instalado e disponível no `PATH`.
- MongoDB local ou uma conta e cluster no MongoDB Atlas.
- Para usar o Atlas: um usuário de banco de dados e o IP da máquina autorizado na lista de acesso do projeto.

## Configuração do MongoDB Atlas

1. No MongoDB Atlas, crie ou escolha um cluster e crie um **Database User** com permissões adequadas para o banco da aplicação.
2. Em **Network Access**, autorize o IP de saída da máquina que executará a aplicação.
3. No cluster, escolha **Connect → Drivers** e copie a connection string para Java.
4. Substitua o marcador `<db_password>` pela senha do usuário do banco. Se a senha tiver caracteres reservados na URI, codifique-os percentualmente.
5. Inclua o banco `conectasaude` no caminho da URI, antes dos parâmetros de consulta. O formato fica semelhante a:

   ```text
   mongodb+srv://USUARIO:SENHA@SEU_CLUSTER.mongodb.net/conectasaude?retryWrites=true&w=majority&appName=Cluster0
   ```

Não publique a URI com credenciais no Git. O arquivo `src/main/resources/application.properties` lê o valor pela variável de ambiente `MONGODB_URI`:

```properties
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/conectasaude}
```

O valor após os dois-pontos é usado somente como alternativa para uma instalação local. Para o Atlas, configure `MONGODB_URI` com a URI completa e privada.

### Windows PowerShell

Para iniciar a aplicação pelo terminal, defina as variáveis na mesma janela do PowerShell antes de executar o Maven:

```powershell
$env:MONGODB_URI = 'mongodb+srv://USUARIO:SENHA@SEU_CLUSTER.mongodb.net/conectasaude?retryWrites=true&w=majority&appName=Cluster0'
$env:APP_ADMIN_EMAIL = 'admin@exemplo.com'
$env:APP_ADMIN_PASSWORD = 'DefinaUmaSenhaForteAqui'
mvn spring-boot:run
```

No NetBeans, configure as variáveis de ambiente do sistema ou da configuração de execução como `MONGODB_URI`, `APP_ADMIN_EMAIL` e `APP_ADMIN_PASSWORD`; informe apenas o valor da URI em `MONGODB_URI`, sem escrever `MONGODB_URI=` dentro do valor. Reinicie o NetBeans se ele já estava aberto antes da configuração das variáveis.

O arquivo `.env.example` é uma referência para os nomes e formatos das variáveis. Spring Boot não carrega esse arquivo automaticamente.

## Configurações adicionais

O arquivo `src/main/resources/application.properties` centraliza as configurações da aplicação:

```properties
server.port=${PORT:8080}
spring.application.name=conectasaude
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/conectasaude}
spring.data.mongodb.auto-index-creation=true
spring.session.store-type=mongodb
spring.session.mongodb.collection-name=sessoes_login
spring.thymeleaf.cache=false
app.admin.email=${APP_ADMIN_EMAIL:admin@conectasaude.com}
app.admin.password=${APP_ADMIN_PASSWORD:Admin@12345}
```

O administrador inicial é criado quando a aplicação inicia, se o e-mail configurado ainda não existir no banco. Configure `APP_ADMIN_EMAIL` e `APP_ADMIN_PASSWORD` antes da primeira inicialização. Se essa conta já existir, alterar as variáveis não redefine a senha persistida.

Os documentos de usuário são armazenados na coleção `usuarios`; o Spring Session usa `sessoes_login`. A criação de índices está habilitada.

## Executar localmente

Na raiz do projeto, com Java, Maven e as variáveis de ambiente configurados:

```bash
mvn spring-boot:run
```

O servidor inicia por padrão em `http://localhost:8080`. A porta pode ser alterada pela variável `PORT`.

Para gerar o pacote executável:

```bash
mvn clean package
java -jar target/login-seguro-0.0.1-SNAPSHOT.jar
```

## Perfis e rotas principais

| Perfil | Acesso |
| --- | --- |
| `ALUNO` | `/home` e `/aluno` |
| `PROFESSOR` | `/professor` |
| `ADMIN` | `/admin`, `/admin/usuarios` e áreas de professor |

As páginas públicas de autenticação são `/login` e `/cadastro`. O controlador `/painel` encaminha cada usuário para sua área conforme o perfil.

## Organização do código

```text
src/main/java/br/com/conectasaude/loginseguro/
├── config/       # Segurança e inicialização do administrador
├── controller/   # Rotas e preparação dos dados das páginas
├── dto/          # Dados e validação do formulário de cadastro
├── model/        # Usuário e perfis
├── repository/   # Acesso aos documentos MongoDB
└── service/      # Regras de cadastro, administração e autenticação

src/main/resources/
├── static/css/   # Estilos da interface
├── templates/    # Páginas Thymeleaf e fragmentos compartilhados
└── application.properties
```

## Versionamento de branches

O versionamento do projeto segue Gitflow:

- `main`: versões estáveis e prontas para entrega.
- `develop`: integração das funcionalidades em desenvolvimento.
- `feature/nome-da-funcionalidade`: implementação isolada de novas funcionalidades.
- `release/versao`: preparação e estabilização de uma versão.
- `hotfix/nome-do-ajuste`: correções urgentes aplicadas a uma versão estável.

As alterações de uma funcionalidade são integradas em `develop`; releases validadas podem ser integradas em `main` e sincronizadas de volta com `develop`.

## Documentação acadêmica

O relatório técnico em Markdown, estruturado em seções acadêmicas e referências, está em [`docs/relatorio-tecnico.md`](docs/relatorio-tecnico.md).

## Segurança de credenciais

Não versione senhas, tokens ou URI de banco de dados com credenciais reais. Se uma credencial real já tiver sido incluída no código ou enviada em uma mensagem, troque-a no MongoDB Atlas e passe a usar a nova credencial somente por variável de ambiente.
