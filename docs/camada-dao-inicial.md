# Camada DAO Inicial

**Sistema Biblioteca — UEL Engenharia de Software**
**Issue:** #7 · **Sprint:** 2

---

## Objetivo

Este documento registra as decisões de implementação da Issue #7 e serve como referência para revisão da Pull Request correspondente.

A camada DAO encapsula toda a lógica de acesso a dados, isolando as demais camadas (controllers, services futuros) das operações de persistência. Essa separação é o núcleo do padrão DAO conforme descrito no Cap. 13 do material da disciplina.

---

## Referência conceitual

A implementação foi elaborada com base no padrão DAO apresentado no Cap. 13 do livro-texto da disciplina (Menolli, 2025), especialmente:

- os 4 componentes do padrão DAO (Fig. 13.20): Client, DataAccessObject, DataSource e Data/TransferObject;
- a interface genérica `GenericDAO<T>` (Trecho 13.13), que define o contrato CRUD base;
- o princípio DIP (Cap. 10): services devem depender de interfaces DAO, não de implementações concretas;
- o uso de `EntityManager` via JPA como mecanismo de acesso ao DataSource.

O Graphify/livro-texto foi consultado como referência conceitual. Nenhum trecho foi copiado para o código.  
A pasta `references/` foi usada apenas como consulta local e não foi versionada.

---

## Estrutura criada

```
src/main/java/br/uel/biblioteca/
├── dao/
│   ├── GenericDAO.java         ← interface base (Trecho 13.13)
│   ├── AlunoDAO.java
│   ├── LivroDAO.java
│   ├── EmprestimoDAO.java
│   └── impl/
│       ├── AlunoDAOImpl.java
│       ├── LivroDAOImpl.java
│       └── EmprestimoDAOImpl.java
```

### Papel de cada componente no padrão DAO

| Componente (Cap. 13) | Classe no projeto |
|---|---|
| Client | `CBiblioteca` (futura) / controllers |
| DataAccessObject | `AlunoDAO`, `LivroDAO`, `EmprestimoDAO` (interfaces) |
| DataAccessObject (impl) | `AlunoDAOImpl`, `LivroDAOImpl`, `EmprestimoDAOImpl` |
| DataSource | PostgreSQL (via `EntityManager` / JPA) |
| Data / TransferObject | `Aluno`, `Livro`, `Emprestimo` (pacote `model`) |

---

## Interfaces DAO

### GenericDAO\<T\>

Interface base com CRUD completo. Todas as interfaces específicas a estendem.

| Método | Descrição |
|---|---|
| `T salvar(T entity)` | Persiste nova entidade |
| `Optional<T> buscarPorId(Long id)` | Busca por chave primária |
| `List<T> listarTodos()` | Retorna todos os registros |
| `T atualizar(T entity)` | Atualiza entidade existente |
| `void excluir(Long id)` | Remove entidade por ID |

### AlunoDAO

Estende `GenericDAO<Aluno>`. Métodos adicionais de domínio:

| Método | Descrição |
|---|---|
| `Optional<Aluno> buscarPorMatricula(String matricula)` | Busca por matrícula (identificador de negócio) |
| `List<Aluno> buscarAtivos()` | Retorna apenas alunos com `ativo = true` |

### LivroDAO

Estende `GenericDAO<Livro>`. Métodos adicionais de domínio:

| Método | Descrição |
|---|---|
| `List<Livro> buscarDisponiveis()` | Retorna exemplares disponíveis e não reservados para uso interno |
| `Optional<Livro> buscarPorCodigoPatrimonio(String codigo)` | Busca por código de patrimônio (identificador físico) |

### EmprestimoDAO

Estende `GenericDAO<Emprestimo>`. Métodos adicionais de domínio:

| Método | Descrição |
|---|---|
| `List<Emprestimo> buscarPorAluno(Long alunoId)` | Histórico de empréstimos de um aluno |
| `List<Emprestimo> buscarAtivos()` | Empréstimos ainda não devolvidos (`dataDevolucao IS NULL`) |

---

## Implementações

Todas as implementações seguem o mesmo padrão:

```java
@Repository                        // componente de persistência Spring
@Transactional                     // escrita transacional por padrão
public class AlunoDAOImpl implements AlunoDAO {

    @PersistenceContext
    private EntityManager em;      // injeção via JPA (jakarta.persistence)

    // ...métodos usam em.persist(), em.find(), em.merge(), em.remove(), JPQL
}
```

**Transações:**
- Métodos de escrita (`salvar`, `atualizar`, `excluir`): herdam `@Transactional` da classe.
- Métodos de leitura: anotados com `@Transactional(readOnly = true)` para otimizar o Hibernate.

**Imports:** todos os imports usam `jakarta.persistence.*` — compatível com Spring Boot 3.x.

---

## Dependência de interfaces (DIP)

Services e controllers futuros devem depender das **interfaces** DAO, nunca das implementações:

```java
// Correto — depende da interface (DIP)
private final AlunoDAO alunoDAO;

// Errado — acopla à implementação
private final AlunoDAOImpl alunoDAOImpl;
```

Essa decisão está alinhada com o princípio de Inversão de Dependência (Cap. 10) e com o padrão DAO do Cap. 13: a Fig. 13.20 mostra que o Client conhece apenas a interface `DataAccessObject`, nunca a implementação concreta.

---

## Limitações desta fase

Esta issue não implementa:

- services de negócio;
- controllers ou endpoints REST;
- casos de uso de empréstimo ou devolução;
- cálculo de multa;
- DAOs para `Titulo`, `ItemEmprestimo` e `Debito` (escopo reduzido às entidades principais);
- DAOFactory (o Spring gerencia injeção das implementações via DI).

---

## Conclusão

A camada DAO criada nesta issue provê o acesso à persistência para as três entidades principais do sistema — `Aluno`, `Livro` e `Emprestimo` — usando o padrão DAO explícito com separação clara entre interface e implementação, conforme exigido no enunciado do trabalho e alinhado com o Cap. 13 do livro-texto da disciplina.
