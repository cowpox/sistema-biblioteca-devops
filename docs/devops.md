# Planejamento DevOps e Fluxo de Trabalho da Equipe

**Sistema Biblioteca — UEL Engenharia de Software**
**Disciplina:** Engenharia de Software · **Professor:** André Menolli

---

## 1. Objetivo

Este documento define o fluxo de trabalho adotado pela equipe no projeto Sistema Biblioteca DevOps, utilizando as práticas de DevOps estudadas na disciplina. São descritos o uso do GitHub, a organização de Issues, o quadro Kanban, a estratégia de branches, o processo de Pull Requests, a revisão de código, a integração contínua com GitHub Actions e o processo de release final.

O objetivo é garantir rastreabilidade, colaboração organizada e validação contínua das entregas ao longo das cinco sprints do projeto (06/06/2026 – 04/07/2026).

---

## 2. Organização do repositório

O projeto utiliza o **GitHub** como plataforma central de desenvolvimento, integrando:

- **Controle de versão** — histórico de commits, branches e tags;
- **Rastreamento de tarefas** — Issues vinculadas a milestones e labels;
- **Planejamento visual** — GitHub Projects (Kanban);
- **Integração contínua** — GitHub Actions executando testes automaticamente;
- **Documentação** — arquivos Markdown versionados na pasta `docs/`.

**Repositório:** `cowpox/sistema-biblioteca-devops`

---

## 3. Fluxo de branches

O projeto adota o seguinte fluxo:

```
main
└── develop
    ├── feature/*    ← funcionalidades do sistema
    ├── docs/*       ← documentação e diagramas
    ├── ci/*         ← configuração de CI/CD
    ├── test/*       ← casos de teste e testes automatizados
    └── fix/*        ← correções de bugs
```

| Branch | Finalidade |
|---|---|
| `main` | Versão estável e entregável. Recebe merge apenas de `develop` na entrega final. |
| `develop` | Branch de integração das entregas das sprints. Todo PR de feature aponta para cá. |
| Branches temporárias | Criadas a partir de `develop` para cada issue. Descartadas após o merge. |

**Exemplos de branches já utilizadas no projeto:**

```
docs/readme-inicial
docs/arquitetura-pacotes
docs/planejamento-devops
ci/github-actions-inicial
```

**Regras:**

1. Toda implementação parte de uma branch própria criada a partir de `develop`.
2. A branch deve ter nome descritivo no padrão `tipo/descricao-curta`.
3. Nenhum commit deve ser feito diretamente em `develop` ou `main`.
4. O merge em `develop` ocorre exclusivamente via Pull Request revisado.
5. O merge em `main` ocorre apenas na entrega final (tag `v1.0.0`).

---

## 4. Uso de Issues

As **Issues** são a unidade básica de trabalho do projeto. Cada tarefa — funcionalidade, documentação, teste ou configuração — corresponde a uma Issue no GitHub.

### Estrutura de uma Issue

Toda issue deve conter:

- **Objetivo** — o que deve ser entregue;
- **Tarefas** — lista de passos para implementar;
- **Critérios de aceite** — condições para considerar a issue concluída;
- **Responsável** (*assignee*) — integrante responsável pela implementação;
- **Label** — categoria da tarefa;
- **Milestone** — sprint à qual pertence;
- **Vínculo com o Kanban** — card correspondente no GitHub Projects.

### Labels utilizadas

| Label | Uso |
|---|---|
| `feature` | Nova funcionalidade do sistema |
| `bug` | Correção de erro |
| `docs` | Documentação e diagramas |
| `test` | Casos de teste e testes automatizados |
| `devops` | CI/CD e configuração de infraestrutura |
| `uml` | Diagramas PlantUML |
| `dao` | Camada de acesso a dados |
| `review` | Revisão de código |
| `chore` | Tarefas técnicas de suporte |
| `priority:high` | Prioridade alta — sprint corrente |

### Fechamento automático de Issues

O corpo da Pull Request deve conter `Closes #<número>` para que a issue seja fechada automaticamente ao realizar o merge:

```
Closes #5
```

---

## 5. Uso do Kanban

O projeto utiliza o **GitHub Projects** como quadro Kanban para acompanhar visualmente o andamento das tarefas.

### Colunas do Kanban

| Coluna | Quando usar |
|---|---|
| **To Do** | Issue aberta, ainda não iniciada |
| **In Progress** | Branch criada, implementação em andamento |
| **Review** | Pull Request aberta, aguardando revisão |
| **Done** | PR mergeada, issue fechada |

### Regras de movimentação

- O card vai para **In Progress** quando a branch da issue é criada.
- O card vai para **Review** quando a Pull Request é aberta.
- O card vai para **Done** somente após o merge da PR e o fechamento da issue.
- Nenhum card deve ficar em **Done** sem PR mergeada correspondente.

---

## 6. Padrão de commits

Os commits devem ser pequenos, objetivos e com mensagens descritivas no padrão:

```
tipo: descrição curta no imperativo
```

### Tipos utilizados no projeto

| Tipo | Uso |
|---|---|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Alteração em documentação |
| `ci` | Configuração de CI/CD |
| `test` | Adição ou alteração de testes |
| `refactor` | Refatoração sem mudança de comportamento |
| `chore` | Ajuste técnico sem impacto funcional |

### Exemplos de commits do projeto

```
docs: cria README inicial com estrutura, tecnologias e evidencias do Sprint 1
docs: cria documentacao de arquitetura do sistema
docs: adicionar diagrama de arquitetura em PlantUML
ci: configurar workflow inicial do GitHub Actions
docs: documentar fluxo DevOps da equipe
```

---

## 7. Pull Requests

Toda alteração deve chegar à branch `develop` por meio de uma **Pull Request (PR)**. PRs diretas em `develop` ou `main` sem revisão não são permitidas.

### Estrutura de uma PR

| Campo | Conteúdo esperado |
|---|---|
| **Título** | Mesmo padrão do commit principal (`tipo: descrição`) |
| **Descrição** | Resumo do que foi feito, arquivos alterados, validação realizada |
| **Referência à issue** | `Closes #<número>` |
| **Revisão** | Deve ter aprovação de ao menos um integrante antes do merge |
| **CI** | O workflow do GitHub Actions deve estar passando |

### Fluxo completo de uma PR

```
1. Criar branch a partir de develop
2. Implementar a issue
3. Confirmar que os testes passam localmente (mvnw.cmd test)
4. git add <arquivos>
5. git commit -m "tipo: mensagem"
6. git push origin <nome-da-branch>
7. Abrir PR para develop no GitHub
8. Aguardar revisão e aprovação
9. Verificar se o CI passou
10. Realizar o merge
```

---

## 8. Code review

Toda PR deve ser revisada por ao menos um integrante da equipe antes do merge.

### O que o revisor deve verificar

- Se a implementação atende ao objetivo e aos critérios de aceite da issue;
- Se não há arquivos desnecessários (logs, pastas `target/`, arquivos de IDE, credenciais);
- Se o código ou documentação está claro e alinhado com o padrão do projeto;
- Se o histórico de commits é limpo e descritivo;
- Se os checks do GitHub Actions passaram com sucesso.

### Boas práticas de revisão

- Deixar comentários construtivos e objetivos;
- Aprovar com `Approve` somente quando todos os critérios forem atendidos;
- Solicitar ajustes com `Request changes` quando necessário, descrevendo o que deve ser corrigido;
- O autor da PR deve aguardar a aprovação antes de realizar o merge.

Ao longo do projeto, serão registradas no mínimo **duas revisões formais de código** entre os membros da equipe, conforme definido no README.

---

## 9. Integração contínua com GitHub Actions

O projeto possui um workflow de CI configurado em:

```
.github/workflows/ci.yml
```

### Funcionamento

O workflow é disparado automaticamente em todo Pull Request aberto para as branches `develop` ou `main`. Ele pode também ser executado manualmente via `workflow_dispatch`.

### Etapas do workflow

```
1. Checkout do código-fonte
2. Configuração do Java 17 (distribuição Temurin) com cache Maven
3. Execução de mvn test
```

Os testes utilizam banco H2 em memória, configurado no perfil `test` (`src/test/resources/application-test.properties`). A anotação `@ActiveProfiles("test")` nas classes de teste garante que o PostgreSQL não seja necessário no ambiente de CI.

### Objetivo do CI

Garantir que nenhuma alteração com erro de compilação ou falha de teste seja integrada ao projeto. O merge só deve ser realizado com o CI passando (ícone verde no GitHub).

---

## 10. Sprints e milestones

O projeto está organizado em **5 sprints**, cada uma correspondendo a uma milestone no GitHub:

| Sprint | Período | Foco principal |
|---|---|---|
| Sprint 1 | 06/06 – 12/06 | Setup, README, arquitetura, GitHub Actions |
| Sprint 2 | 13/06 – 19/06 | Modelo de domínio, DAO, cadastros |
| Sprint 3 | 20/06 – 26/06 | Emprestar Livro (diagrama + implementação) |
| Sprint 4 | 27/06 – 01/07 | Devolver Livro (diagrama + implementação) |
| Sprint 5 | 02/07 – 04/07 | Testes, release e entrega final |

Cada issue deve estar vinculada à milestone da sprint em que será entregue. O critério de aceite de cada sprint é: todos os PRs mergeados em `develop`, build do CI passando e cards correspondentes em **Done** no Kanban.

---

## 11. Processo de release final

Ao término da Sprint 5, a branch `develop` será integrada à `main` por meio de um Pull Request final de entrega.

### Passos para a release

```
1. Verificar que todas as issues estão fechadas
2. Verificar que todos os cards do Kanban estão em Done
3. Verificar que mvn test passa com BUILD SUCCESS
4. Abrir PR de develop → main
5. Realizar code review final
6. Mergear em main
7. Criar tag de versão:
       git tag -a v1.0.0 -m "Entrega final — Sistema Biblioteca UEL"
       git push origin v1.0.0
8. Publicar Release no GitHub com título, descrição e referência às issues
```

### Conteúdo da Release `v1.0.0`

- Título: `v1.0.0 — Entrega Final Sistema Biblioteca`
- Descrição com as principais entregas (funcionalidades, diagramas, testes)
- Referência a todas as issues concluídas
- Evidências do processo em `docs/evidence/`

---

## 12. Evidências

As evidências do processo de desenvolvimento são armazenadas em `docs/evidence/` e versionadas no repositório.

### Evidências esperadas ao longo do projeto

| Arquivo | Conteúdo |
|---|---|
| `01-spring-initializr-setup.png` | Configuração inicial do projeto no Spring Initializr |
| `02-issue-labels.png` | Labels configuradas no repositório |
| `03-milestones-sprints.png` | Milestones e sprints criados |
| `04-kanban-creation.png` | Quadro Kanban criado no GitHub Projects |
| `05-issues-created.png` | Issues organizadas por sprint e label |
| `0N-pr-*.png` | PRs abertas, revisadas e mergeadas |
| `0N-ci-passing.png` | GitHub Actions executando com sucesso |
| `0N-release.png` | Release `v1.0.0` publicada |

---

## 13. Conclusão

O fluxo de trabalho adotado no projeto aplica práticas de DevOps estudadas na disciplina: rastreabilidade por Issues e Kanban, colaboração via Pull Requests com revisão de código, e validação automática por GitHub Actions. A separação entre `develop` e `main`, combinada com branches temporárias por issue, garante que a branch principal esteja sempre em estado estável e que o histórico reflita o progresso real do projeto.

A entrega final em `main`, com tag `v1.0.0` e Release publicada, documenta formalmente o resultado do trabalho e fecha o ciclo DevOps definido para a disciplina.
