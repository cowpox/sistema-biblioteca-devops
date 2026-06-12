# Modelo de Domínio Inicial

**Sistema Biblioteca — UEL Engenharia de Software**
**Issue:** #6 · **Sprint:** 2

---

## Objetivo

Este documento registra as decisões de modelagem da Issue #6 e serve como referência para revisão da Pull Request correspondente.

O modelo de domínio inicial representa as principais entidades do Sistema de Biblioteca e constitui a base para as próximas funcionalidades: cadastro de alunos, cadastro de livros, empréstimo e devolução de livros. As classes criadas nesta issue são exclusivamente do pacote `model` — sem controllers, services, DAOs ou DTOs.

---

## Referência conceitual

A modelagem foi elaborada considerando:

- os requisitos da Issue #6 e os casos de uso já definidos para o sistema (emprestar livro, devolver livro, cadastrar aluno, cadastrar livro);
- o modelo de classes de análise do sistema de biblioteca descrito no material da disciplina, que identifica as entidades centrais e seus relacionamentos;
- as recomendações de modelagem orientada a objetos do livro-texto do professor, especialmente os critérios de atribuição de responsabilidades (GRASP Expert e Creator) e a cadeia de delegação para manter baixo acoplamento;
- a necessidade de manter o modelo simples nesta fase inicial, sem antecipar complexidade que pertence a issues futuras.

A pasta `references/` foi utilizada apenas como fonte de consulta local e não foi versionada.

---

## Classes modeladas

### Aluno

Representa o usuário que pode realizar empréstimos de livros na biblioteca.

**Responsabilidade:** armazenar os dados cadastrais do aluno e manter o vínculo com seus empréstimos e débitos.

**Principais atributos:**

| Atributo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador técnico gerado pelo banco |
| `matricula` | `String` | Identificador de negócio único do aluno |
| `nome` | `String` | Nome completo |
| `cpf` | `String` | CPF do aluno (único) |
| `endereco` | `String` | Endereço para contato |
| `email` | `String` | E-mail para contato |
| `ativo` | `Boolean` | Indica se o cadastro está ativo |

**Motivo:** o aluno é o ator principal do sistema e precisa ser identificado antes de qualquer operação de empréstimo ou devolução. O atributo `cpf` e `endereco` foram incluídos por estarem presentes no modelo de referência do material da disciplina. Os atributos `email` e `ativo` foram adicionados para suportar funcionalidades de cadastro e controle de acesso.

**Relacionamentos:**
- possui vários `Emprestimo` (`@OneToMany`)
- possui vários `Debito` (`@OneToMany`)

---

### Titulo

Representa a obra bibliográfica de forma conceitual — o "que" foi publicado, independentemente de quantos exemplares físicos existam.

**Responsabilidade:** armazenar os dados bibliográficos da obra e fornecer o prazo padrão de devolução para os exemplares associados.

**Principais atributos:**

| Atributo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador técnico |
| `nome` | `String` | Título da obra |
| `isbn` | `String` | Código ISBN único |
| `prazo` | `Integer` | Prazo padrão de devolução em dias |
| `autor` | `String` | Nome do autor |
| `editora` | `String` | Editora responsável |
| `edicao` | `String` | Edição da obra |
| `ano` | `Integer` | Ano de publicação |

**Motivo:** a separação entre `Titulo` e `Livro` é fundamental para o modelo: um mesmo título pode ter vários exemplares físicos (`Livro`), cada um com seu próprio código de patrimônio e disponibilidade. O atributo `prazo` é obrigatório nesta fase porque é utilizado no cálculo da data de devolução de cada empréstimo — sem ele, a regra central do sistema não pode ser implementada.

O atributo `autor` foi mantido como `String` nesta issue para preservar o escopo. Uma entidade `Autor` separada pode ser extraída futuramente caso o sistema precise de busca por autor ou de relacionamento N:N entre títulos e autores.

**Relacionamentos:**
- referenciado por vários `Livro` (`@ManyToOne` no lado do `Livro`)

---

### Livro

Representa o exemplar físico (ou controlável) de um título. Um mesmo título pode possuir vários exemplares na biblioteca.

**Responsabilidade:** controlar a disponibilidade do exemplar e indicar se ele pode ou não ser emprestado.

**Principais atributos:**

| Atributo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador técnico |
| `codigoPatrimonio` | `String` | Código único de identificação do exemplar |
| `disponivel` | `Boolean` | Indica se o exemplar está disponível para empréstimo |
| `exemplarBiblioteca` | `Boolean` | Indica que o exemplar é de uso interno e não pode ser emprestado |
| `titulo` | `Titulo` | Obra à qual o exemplar pertence |

**Motivo:** o atributo `disponivel` controla o fluxo de empréstimo e devolução — é alterado para `false` ao emprestar e para `true` ao devolver. O atributo `exemplarBiblioteca` implementa o fluxo alternativo do caso de uso Emprestar Livro: quando `true`, o sistema informa que o exemplar não pode ser emprestado e continua para o próximo item da lista.

O método `verPrazo()` delega a consulta ao `Titulo` associado, seguindo o padrão GRASP Expert: quem tem a informação (`Titulo.prazo`) é responsável por fornecê-la.

**Relacionamentos:**
- pertence a um `Titulo` (`@ManyToOne`)
- referenciado por `ItemEmprestimo`

---

### Emprestimo

Representa o ato de empréstimo realizado por um aluno, agrupando os livros retirados, as datas envolvidas e o status da operação.

**Responsabilidade:** registrar o empréstimo e manter o conjunto de itens associados.

**Principais atributos:**

| Atributo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador técnico |
| `aluno` | `Aluno` | Aluno que realizou o empréstimo |
| `dataEmprestimo` | `LocalDate` | Data em que o empréstimo foi realizado |
| `dataPrevistaDevolucao` | `LocalDate` | Data calculada para devolução de todos os itens |
| `dataDevolucao` | `LocalDate` | Data efetiva de encerramento (nula até devolução completa) |
| `status` | `String` | Estado atual (`ATIVO`, `DEVOLVIDO`, etc.) |
| `itens` | `List<ItemEmprestimo>` | Itens do empréstimo |

**Motivo:** o `Emprestimo` agrega os `ItemEmprestimo` e é o responsável natural por coordenar o cálculo da data de devolução — seguindo o GRASP Expert, pois é quem conhece todos os itens e seus prazos. O campo `dataDevolucao` permanece nulo enquanto o empréstimo estiver ativo e é preenchido quando todos os itens forem devolvidos.

**Relacionamentos:**
- pertence a um `Aluno` (`@ManyToOne`)
- contém vários `ItemEmprestimo` (`@OneToMany`, cascade ALL)
- referenciado por `Debito`

---

### ItemEmprestimo

Representa cada livro associado a um empréstimo, permitindo que um único empréstimo contenha um ou mais livros, cada um com sua própria data de devolução.

**Responsabilidade:** registrar qual livro foi emprestado em qual empréstimo e controlar as datas individuais de devolução de cada exemplar.

**Principais atributos:**

| Atributo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador técnico |
| `emprestimo` | `Emprestimo` | Empréstimo ao qual o item pertence |
| `livro` | `Livro` | Exemplar emprestado |
| `dataPrevistaDevolucao` | `LocalDate` | Data calculada para devolução deste exemplar |
| `dataDevolucao` | `LocalDate` | Data efetiva de devolução (nula até ser devolvido) |

**Motivo:** a separação entre `Emprestimo` e `ItemEmprestimo` é necessária porque cada livro pode ter um prazo diferente (definido pelo `Titulo`) e precisa ser devolvido individualmente. Essa estrutura também suporta a cadeia de delegação da devolução: `Aluno.devolver()` → `Emprestimo.devolver()` → `ItemEmprestimo.devolver()`, conforme descrito no material da disciplina para manter baixo acoplamento entre as classes.

**Relacionamentos:**
- pertence a um `Emprestimo` (`@ManyToOne`)
- referencia um `Livro` (`@ManyToOne`)

---

### Debito

Representa pendências financeiras ou administrativas associadas ao aluno, geradas por atraso na devolução de um empréstimo.

**Responsabilidade:** registrar o valor da multa, a data de geração e o status de pagamento.

**Principais atributos:**

| Atributo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador técnico |
| `aluno` | `Aluno` | Aluno devedor |
| `emprestimo` | `Emprestimo` | Empréstimo que originou o débito (opcional) |
| `valor` | `BigDecimal` | Valor do débito em reais |
| `dataGeracao` | `LocalDate` | Data em que o débito foi gerado |
| `pago` | `Boolean` | Indica se o débito foi quitado |

**Motivo:** o `Debito` é criado quando uma devolução ocorre com atraso. A verificação de débito ativo é pré-condição do caso de uso Emprestar Livro — um aluno com débito pendente não pode realizar novos empréstimos. O tipo `BigDecimal` foi escolhido para representar valores monetários com precisão, evitando erros de arredondamento que ocorrem com `double` ou `float`.

**Relacionamentos:**
- pertence a um `Aluno` (`@ManyToOne`)
- pode estar vinculado a um `Emprestimo` (`@ManyToOne`, opcional)

---

## Relacionamentos principais

```
Titulo (1) ────────────── (N) Livro
Aluno  (1) ────────────── (N) Emprestimo
Aluno  (1) ────────────── (N) Debito
Emprestimo (1) ─────────── (N) ItemEmprestimo
ItemEmprestimo (N) ──────── (1) Livro
Debito (N) ──────────────── (1) Emprestimo  [opcional]
```

Todos os relacionamentos são unidirecionais no sentido da dependência (lado N aponta para o lado 1), exceto `Aluno → Emprestimo` e `Aluno → Debito`, que são bidirecionais para suportar a cadeia de delegação da devolução.

---

## Decisões de implementação

| Decisão | Justificativa |
|---|---|
| `@Entity` em todas as classes | Necessário para o mapeamento JPA/Hibernate reconhecer as classes como entidades persistíveis |
| `@Table(name = "...")` | Define explicitamente o nome da tabela em snake_case, evitando dependência do comportamento padrão do Hibernate |
| `Long` como identificador | Tipo recomendado para chaves primárias em aplicações Spring Boot com JPA; suporta volumes maiores que `int` |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Delega a geração do ID ao banco de dados (auto increment), compatível com PostgreSQL e H2 |
| `LocalDate` para datas | API moderna do Java 8+, preferível a `java.util.Date` — sem problemas de fuso horário em datas sem hora |
| `BigDecimal` para valores monetários | Precisão exata para cálculos financeiros; evita erros de arredondamento de `double` e `float` |
| Tipos simples (`String`, `Integer`, `Boolean`) | Mantém o modelo legível e sem dependências externas nesta fase inicial |
| Sem Lombok | O projeto não utiliza Lombok; getters, setters e construtor vazio escritos manualmente, conforme exigido pelo JPA |
| Construtor vazio em todas as classes | Exigência da especificação JPA para instanciação por reflexão |
| Sem controllers, services, DAOs ou DTOs | Escopo da issue limitado ao modelo de domínio; as demais camadas serão implementadas nas issues seguintes |

---

## Limitações desta fase

Esta issue não implementa:

- cadastro de alunos (formulários e persistência);
- cadastro de livros e títulos;
- fluxo completo de empréstimo;
- fluxo completo de devolução;
- cálculo de multa por atraso;
- regras de negócio (`verificaDebito()`, `calcularDataDevolucao()`);
- endpoints REST ou controllers Spring MVC;
- camada DAO/repository;
- testes de integração das entidades.

As entidades `Autor` e `Area`, presentes no modelo de referência do livro-texto, não foram criadas nesta issue para preservar o escopo. Podem ser adicionadas em uma issue futura, caso o sistema precise de categorização por área ou busca por autor.

---

## Conclusão

O modelo criado nesta issue é uma base inicial, simples e extensível, alinhada com os casos de uso centrais do sistema — emprestar e devolver livros — e com as recomendações do material da disciplina. As entidades estão prontas para receber DAOs, services e controllers nas próximas issues da Sprint 2.
