# Configuração de Persistência

**Sistema Biblioteca — UEL Engenharia de Software**
**Issue:** #28 · **Sprint:** 2

---

## Objetivo

Este documento descreve como a persistência de dados está configurada no projeto: banco de dados por ambiente, estratégia de criação de tabelas, variáveis de ambiente e integração com a camada DAO implementada na Issue #7.

---

## Bancos de dados por ambiente

| Ambiente | Banco | Configuração |
|---|---|---|
| Desenvolvimento local | PostgreSQL | `src/main/resources/application.properties` |
| Testes / CI (GitHub Actions) | H2 em memória | `src/test/resources/application-test.properties` |

O perfil de testes é ativado via `@ActiveProfiles("test")` na classe `BibliotecaApplicationTests`. O CI (GitHub Actions) não depende de PostgreSQL externo.

---

## Configuração PostgreSQL (desenvolvimento)

Arquivo: `src/main/resources/application.properties`

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/biblioteca}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

As credenciais usam variáveis de ambiente com fallback para valores locais de desenvolvimento. **Nunca versionar senhas reais de produção.**

### Como criar o banco local

```sql
-- No psql ou pgAdmin:
CREATE DATABASE biblioteca;
```

### Variáveis de ambiente (opcional)

```bash
DB_URL=jdbc:postgresql://localhost:5432/biblioteca
DB_USER=postgres
DB_PASSWORD=postgres
```

Se as variáveis não forem definidas, os valores `localhost:5432/biblioteca`, `postgres`/`postgres` são usados automaticamente como fallback para desenvolvimento.

---

## Configuração H2 (testes e CI)

Arquivo: `src/test/resources/application-test.properties`

```properties
spring.datasource.url=jdbc:h2:mem:biblioteca_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

spring.thymeleaf.check-template-location=false
```

- H2 é banco em memória: não requer instalação, inicializa e destrói a cada execução de testes.
- `create-drop`: Hibernate cria as tabelas ao iniciar e as remove ao encerrar — ideal para testes isolados.
- `H2Dialect` declarado explicitamente para evitar ambiguidade na detecção automática.

---

## Estratégia de criação das tabelas: `ddl-auto`

### Decisão

O projeto usa `spring.jpa.hibernate.ddl-auto` em vez de Flyway.

| Ambiente | Valor |
|---|---|
| Desenvolvimento | `update` |
| Testes | `create-drop` |

### Justificativa

- **Contexto acadêmico:** o modelo de entidades ainda está evoluindo (Issues #8, #9, #10 adicionarão funcionalidades). O `update` permite que o Hibernate ajuste o schema automaticamente sem migrations manuais a cada alteração.
- **Testes isolados:** `create-drop` garante que cada execução de testes comece com schema limpo, sem interferência de dados anteriores.
- **Custo-benefício:** Flyway seria a escolha certa para produção (controle versionado de schema, rollback). Para este projeto acadêmico em fase inicial, `ddl-auto` é suficiente e reduz complexidade.

> Decisão a rever em produção real: substituir por `validate` + Flyway com migrations versionadas.

---

## Dependências no pom.xml

Todas as dependências necessárias já estavam presentes desde o início do projeto:

```xml
<!-- JPA + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- PostgreSQL (desenvolvimento) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- H2 (testes) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Relação com a camada DAO (Issue #7)

Os DAOs implementados na Issue #7 (`AlunoDAOImpl`, `LivroDAOImpl`, `EmprestimoDAOImpl`) usam `EntityManager` injetado via `@PersistenceContext`. O `EntityManager` é provido pelo Spring através do `LocalContainerEntityManagerFactoryBean`, que utiliza a fonte de dados configurada no `application.properties` (PostgreSQL em dev) ou `application-test.properties` (H2 em testes).

```
Controller/Service  →  AlunoDAO (interface)
                            ↓
                       AlunoDAOImpl (@Repository)
                            ↓
                       EntityManager  →  DataSource  →  PostgreSQL / H2
```

Services futuros devem depender das **interfaces** DAO (`AlunoDAO`, `LivroDAO`, `EmprestimoDAO`), nunca das implementações concretas — conforme DIP (Cap. 10, Menolli 2025) e padrão DAO Cap. 13.

---

## Como rodar o projeto

### Desenvolvimento local

```bash
# 1. Criar banco no PostgreSQL
createdb biblioteca

# 2. (Opcional) Definir variáveis de ambiente
export DB_URL=jdbc:postgresql://localhost:5432/biblioteca
export DB_USER=postgres
export DB_PASSWORD=postgres

# 3. Iniciar aplicação
./mvnw spring-boot:run     # Linux/macOS
mvnw.cmd spring-boot:run   # Windows
```

O Hibernate criará as tabelas automaticamente (`ddl-auto=update`).

### Executar testes

```bash
./mvnw test      # Linux/macOS
mvnw.cmd test    # Windows
```

Os testes usam H2 em memória — nenhuma configuração de banco externo necessária.

---

## GitHub Actions (CI)

O workflow `.github/workflows/ci.yml` executa `mvn test` sem PostgreSQL externo. Funciona porque:

1. A classe de teste tem `@ActiveProfiles("test")`.
2. O Spring carrega `application-test.properties` com H2.
3. H2 tem escopo `test` no pom.xml e está disponível no classpath durante testes.
4. Nenhuma conexão com PostgreSQL é tentada em tempo de teste.
