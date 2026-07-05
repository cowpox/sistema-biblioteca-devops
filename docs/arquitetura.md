# Arquitetura do Sistema

**Sistema Biblioteca — UEL Engenharia de Software**
**Disciplina:** Engenharia de Software · **Professor:** André Menolli

---

## 1. Visão geral

O Sistema de Biblioteca foi estruturado com base em uma **arquitetura em camadas** combinada com o padrão **MVC** (*Model-View-Controller*), executando em uma topologia **cliente-servidor**.

Essa organização tem como objetivo separar responsabilidades, facilitar a manutenção do código e permitir maior clareza entre as partes responsáveis pela interface, regras de negócio, acesso a dados e persistência. Cada camada possui uma responsabilidade bem definida e se comunica apenas com a camada adjacente, conforme o padrão Camadas descrito por Menolli (2025, Cap. 7).

---

## 2. Visão conceitual da arquitetura

A visão conceitual (Hofmeister; Nord; Soni, 2000 *apud* Menolli, 2025) descreve o sistema em termos dos seus elementos de projeto e dos relacionamentos entre eles. No Sistema de Biblioteca, os elementos principais são:

```
┌────────────────────────────────────────────────────┐
│               NAVEGADOR (Cliente)                  │
│        Thymeleaf HTML · CSS · Bootstrap            │
└──────────────────────┬─────────────────────────────┘
                       │ HTTP (GET / POST)
┌──────────────────────▼─────────────────────────────┐
│              SPRING BOOT (Servidor)                │
│  ┌──────────────────────────────────────────────┐  │
│  │  VIEW (Thymeleaf)   <<Boundary>>             │  │
│  │  emprestar.html · devolver.html · listas     │  │
│  └──────────────────┬───────────────────────────┘  │
│                     │                              │
│  ┌──────────────────▼───────────────────────────┐  │
│  │  CONTROLLER (@Controller)  <<Control>>       │  │
│  │  AlunoController · LivroController           │  │
│  │  EmprestimoController                        │  │
│  └──────────────────┬───────────────────────────┘  │
│                     │ delega                       │
│  ┌──────────────────▼───────────────────────────┐  │
│  │  SERVICE (@Service)        <<Control>>       │  │
│  │  AlunoService · LivroService                 │  │
│  │  EmprestimoService  ← regras de negócio      │  │
│  └──────────────────┬───────────────────────────┘  │
│                     │ depende da interface         │
│  ┌──────────────────▼───────────────────────────┐  │
│  │  DAO (interface + implementação)             │  │
│  │  AlunoDAO / AlunoDAOImpl                     │  │
│  │  LivroDAO / LivroDAOImpl                     │  │
│  │  EmprestimoDAO / EmprestimoDAOImpl           │  │
│  └──────────────────┬───────────────────────────┘  │
│                     │ JPA / Hibernate              │
│  ┌──────────────────▼───────────────────────────┐  │
│  │  MODEL (@Entity)           <<Entity>>        │  │
│  │  Aluno · Livro · Titulo · Emprestimo         │  │
│  │  ItemEmprestimo · Debito                     │  │
│  └──────────────────┬───────────────────────────┘  │
└─────────────────────┼──────────────────────────────┘
                      │ JDBC
┌─────────────────────▼──────────────────────────────┐
│                  PostgreSQL                        │
└────────────────────────────────────────────────────┘
```

Os estereótipos UML anotados (`<<Boundary>>`, `<<Control>>`, `<<Entity>>`) seguem a classificação de Menolli (2025, Cap. 5) e identificam a natureza de cada elemento na arquitetura.

---

## 3. Arquitetura em camadas

O padrão **Camadas** (*Layers*) organiza o software em serviços agrupados em níveis de abstração, onde cada camada se comunica apenas com a camada adjacente. Segundo Menolli (2025, Cap. 7), esse padrão favorece a **compreensão** (níveis crescentes de abstração), a **manutenção** (camadas fracamente acopladas) e o **reúso** (camadas reutilizáveis em diferentes contextos).

No Sistema de Biblioteca, as camadas são distribuídas logicamente da seguinte forma:

| Camada | Responsabilidade | Tecnologia |
|---|---|---|
| **View** | Apresentação e interação com o usuário | Thymeleaf (.html) |
| **Controller** | Recebe requisições e coordena o fluxo | Spring MVC (`@Controller`) |
| **Service** | Aplica as regras de negócio | Spring (`@Service`) |
| **DAO** | Acesso e manipulação dos dados persistidos | JPA / Hibernate |
| **Model** | Representação das entidades do domínio | `@Entity` (JPA) |
| **Banco de Dados** | Armazenamento persistente das informações | PostgreSQL |

Essa organização é complementada pela **topologia cliente-servidor**: o navegador (cliente) envia requisições HTTP ao servidor Spring Boot embarcado, que processa e devolve as respostas via Thymeleaf.

---

## 4. Padrão MVC

O padrão **MVC** (*Model-View-Controller*) é amplamente utilizado em sistemas web com interfaces de usuário (Menolli, 2025, Cap. 7 — §7.4.4). Ele promove a separação entre a lógica de apresentação, a lógica de controle e a representação dos dados, favorecendo os atributos de qualidade **Usabilidade** e **Modificabilidade** (ISO/IEC 25010).

No Sistema de Biblioteca, o MVC é implementado da seguinte forma:

| Componente | Papel no MVC | Implementação |
|---|---|---|
| **Model** | Contém as entidades e dados do domínio. Encapsula o estado da aplicação e responde consultas sobre esse estado. | Classes `@Entity`: `Aluno`, `Livro`, `Titulo`, `Emprestimo`, `ItemEmprestimo`, `Debito` |
| **View** | Exibe informações ao usuário e captura suas entradas. Não contém regras de negócio. | Templates Thymeleaf: `emprestimos/form.html`, `emprestimos/devolver.html`, `alunos/form.html`, `livros/form.html`, comprovantes e demais telas |
| **Controller** | Medeia o Model e a View. Mapeia as ações do usuário para operações no sistema e seleciona a View de resposta. | Classes `@Controller`: `AlunoController`, `LivroController`, `EmprestimoController` |

A camada **Service** complementa o Controller ao concentrar as regras de negócio, evitando que o Controller fique sobrecarregado com lógica de domínio — seguindo o princípio de **Alta Coesão** (GRASP, Cap. 9).

---

## 5. Padrão DAO

O padrão **DAO** (*Data Access Object*) é de uso **obrigatório** neste projeto. Ele isola a lógica de acesso ao banco de dados em uma camada específica, impedindo que a camada de serviço ou os controladores acessem o banco diretamente.

A implementação segue o **Princípio da Inversão de Dependência** (DIP — SOLID, Menolli 2025, Cap. 10): a camada Service depende da *interface* DAO, não da implementação concreta, permitindo que a implementação seja substituída sem impacto nas regras de negócio.

```
interface AlunoDAO              ← contrato (dependência da camada Service)
    └── AlunoDAOImpl            ← implementação concreta (@Repository)

interface LivroDAO
    └── LivroDAOImpl

interface EmprestimoDAO
    └── EmprestimoDAOImpl
```

Cada interface define as operações necessárias (salvar, buscar, atualizar, remover). As implementações concretas utilizam JPA/Hibernate para comunicação com o PostgreSQL.

---

## 6. Descrição das camadas

### 6.1 Controller

Responsável por receber as requisições HTTP e coordenar o fluxo entre a interface e as regras de negócio. As classes são anotadas com `@Controller` (Spring MVC) e não devem conter lógica de negócio.

Controladores implementados: `AlunoController`, `LivroController`, `EmprestimoController`, `HomeController`.

### 6.2 Service

Concentra as regras de negócio do sistema. Atua entre o Controller e a camada DAO, garantindo que as regras sejam respeitadas antes de qualquer operação de persistência.

No contexto do Sistema de Biblioteca, essa camada aplica regras como: verificar se o aluno possui débito antes de um empréstimo, calcular a data de devolução e registrar multas em caso de atraso. Aplica o padrão GRASP **Controller (Facade)** — a classe `EmprestimoService` coordena o fluxo sem expor os detalhes internos das entidades.

Serviços implementados: `AlunoService`, `LivroService`, `EmprestimoService`.

### 6.3 DAO

Realiza a comunicação com o banco de dados PostgreSQL por meio do JPA/Hibernate. Encapsula todas as operações de persistência (inserção, consulta, atualização e remoção), mantendo as demais camadas independentes dos detalhes de acesso ao banco.

Cada entidade principal possui uma interface DAO e uma implementação concreta anotada com `@Repository`.

### 6.4 Model

Representa as entidades do domínio do sistema, mapeadas como tabelas no banco de dados por meio de anotações JPA (`@Entity`, `@ManyToOne`, `@OneToMany`, etc.).

As entidades previstas e suas responsabilidades no domínio são:

| Entidade | Papel | Padrão GRASP |
|---|---|---|
| `Aluno` | Representa o usuário do sistema | Entity + Expert (`verificaDebito()`) |
| `Livro` | Exemplar físico do acervo | Entity |
| `Titulo` | Obra bibliográfica (ISBN, prazo) | Entity + Expert (`verPrazo()`) |
| `Emprestimo` | Agrupa os itens de um empréstimo | Expert (`calcularDataDevolucao()`) + Creator |
| `ItemEmprestimo` | Relaciona livro e empréstimo | Expert (`calculaDataDevolucao()`, `devolver()`) |
| `Debito` | Registra multas por atraso | Entity |

### 6.5 View

Responsável pela apresentação das informações ao usuário, implementada com templates **Thymeleaf** que recebem os dados processados pelo Controller e os renderizam em HTML. Não contém regras de negócio nem lógica de persistência.

Views implementadas: página inicial (`index.html`), telas de cadastro e listagem de alunos e livros, busca auxiliar de livro e aluno, tela de empréstimo (`emprestimos/form.html`), tela de devolução (`emprestimos/devolver.html`) e comprovantes. Os templates estão em `src/main/resources/templates/`.

---

## 7. Dependência com PostgreSQL

O sistema utiliza **PostgreSQL** como banco de dados relacional para armazenamento persistente. A comunicação ocorre exclusivamente por meio da camada DAO, via **JPA/Hibernate**, respeitando a separação de responsabilidades.

O perfil de **produção** conecta-se ao PostgreSQL configurado via variáveis de ambiente:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/biblioteca}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=update
```

O perfil de **testes** utiliza banco H2 em memória (`scope: test`), eliminando a dependência do PostgreSQL na execução dos testes automatizados:

```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 8. Fluxo geral de uma requisição

O fluxo abaixo ilustra o caminho de uma requisição no sistema, seguindo a cadeia de delegação entre camadas:

```
1. Usuário interage com a View (navegador)
       ↓ HTTP GET / POST
2. Controller recebe a requisição
       ↓ delega
3. Service aplica as regras de negócio
       ↓ acessa (quando necessário)
4. DAO executa a operação no banco de dados
       ↓ via JPA/Hibernate
5. PostgreSQL retorna o resultado
       ↑
6. DAO retorna os dados para a Service
       ↑
7. Service retorna o resultado para o Controller
       ↑
8. Controller prepara o modelo e seleciona a View
       ↑
9. View renderiza a resposta e exibe ao usuário
```

Esse fluxo segue o princípio da **Lei de Deméter** (Menolli, 2025, Cap. 9): cada camada fala apenas com seu vizinho direto, evitando acoplamento desnecessário.

---

## 9. Atributos de qualidade

A arquitetura adotada busca satisfazer os seguintes atributos de qualidade (ISO/IEC 25010, conforme Menolli, 2025, Cap. 7):

| Atributo | Como é atingido |
|---|---|
| **Modificabilidade** | Interface DAO separada da implementação (OCP + DIP); camadas fracamente acopladas — mudanças na persistência não afetam as regras de negócio |
| **Usabilidade** | Interface Thymeleaf com fluxos simples e feedback claro; separação entre interface e lógica via MVC |
| **Manutenibilidade** | SRP: cada classe com responsabilidade única; alta coesão por camada |
| **Confiabilidade** | Testes JUnit cobrindo fluxo principal e alternativo; validações na camada Service; banco H2 para testes isolados |
| **Desempenho** | JPA com consultas otimizadas; sessão Hibernate gerenciada pelo Spring |

---

## 10. Considerações finais

A arquitetura proposta para o Sistema de Biblioteca aplica os padrões e princípios estudados na disciplina:

- **Padrão Camadas** e **MVC**: organização, separação de responsabilidades e facilidade de manutenção (Menolli, 2025, Cap. 7).
- **Padrão DAO** (obrigatório pelo enunciado): isolamento da persistência e inversão de dependência (Cap. 13).
- **Padrões GRASP**: Controller/Facade (`EmprestimoService`), Expert (`Emprestimo`, `ItemEmprestimo`), Creator (Cap. 9).
- **Princípios SOLID**: SRP, OCP e DIP são diretamente observáveis na separação entre interfaces DAO e suas implementações (Cap. 10).
- **Estereótipos UML** `<<Boundary>>`, `<<Control>>` e `<<Entity>>`: identificam a natureza de cada elemento nos diagramas de classes (Cap. 5).

Essa estrutura permite que o sistema evolua de forma controlada ao longo das sprints, reduzindo o impacto de mudanças entre as camadas e facilitando a atribuição de tarefas entre os integrantes da equipe.
