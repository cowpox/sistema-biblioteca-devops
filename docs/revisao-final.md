# Relatório de Revisão Final da Documentação

**Projeto:** Sistema Biblioteca — UEL Engenharia de Software  
**Disciplina:** Engenharia de Software · **Professor:** André Menolli  
**Branch:** `docs/revisao-final-documentacao`  
**Data:** 02/07/2026  
**Entrega:** 04/07/2026

---

## 1. Contexto

Com todas as funcionalidades do sistema implementadas ao longo dos 5 sprints
(cadastro de alunos e livros, empréstimo, devolução, busca auxiliar por título
e por aluno), esta revisão tem como objetivo garantir que a documentação final
esteja coerente com o estado real do código antes da entrega e da publicação
da Release `v1.0.0`.

A revisão incidiu exclusivamente sobre arquivos de documentação. Nenhum arquivo
de código-fonte, teste, template, configuração ou workflow foi alterado.

---

## 2. Diagnóstico inicial

### 2.1 README.md

Antes desta revisão, o `README.md` apresentava cinco inconsistências:

| # | Localização | Problema identificado |
|---|---|---|
| 1 | Estrutura de pastas | Pasta `exception/` citada como subpacote de `br.uel.biblioteca` — não existe no código-fonte |
| 2 | Estrutura de pastas | Comentário `# Planejado Sprint 1: executa mvn test em todo PR` em `.github/workflows/` — o CI está operacional desde o Sprint 1 |
| 3 | Seção GitHub Actions | Texto no futuro: "será configurado no Sprint 1" — o workflow já está configurado e executando em todos os PRs |
| 4 | Seção Documentação | Listava apenas os 6 diagramas PlantUML, omitindo 9 documentos existentes em `docs/`: `arquitetura.md`, `devops.md`, `casos-de-teste.md`, `testes-unitarios.md`, `emprestar-livro.md`, `modelo-dominio-inicial.md`, `camada-dao-inicial.md`, `persistencia.md`, `ui-inicial.md`, além dos diretórios `casos-de-uso/` |
| 5 | Seção Evidências | Intitulada "Evidências iniciais de organização do projeto" com descrição restrita ao Sprint 1, ignorando as evidências dos Sprints 2 a 5 presentes em `docs/evidence/` |

### 2.2 docs/arquitetura.md

O `docs/arquitetura.md` apresentava seis inconsistências:

| # | Localização | Problema identificado |
|---|---|---|
| 6 | Diagrama ASCII — seção 2 | Linha `ItemEmprestimo · Debito · Area · Autor` — as entidades `Area` e `Autor` não existem no modelo de domínio implementado |
| 7 | Tabela GRASP — seção 6.4 | Linhas de `Area` ("Categoriza títulos") e `Autor` ("Dados do autor da obra") — classes inexistentes no código |
| 8 | Tabela MVC — seção 4 | Templates citados como `emprestar.html` e `devolver.html` — os arquivos reais são `emprestimos/form.html` e `emprestimos/devolver.html` |
| 9 | Seção 6.1 (Controller) | "Controladores **previstos**" — todos estão implementados; `HomeController` existia mas não estava listado |
| 10 | Seção 6.2 (Service) | "Serviços **previstos**" — todos estão implementados |
| 11 | Seção 6.5 (View) | "Views **previstas**: telas de cadastro de aluno e livro, tela de empréstimo, tela de devolução e comprovantes" — todas implementadas, sem citar os nomes reais dos templates |

### 2.3 Demais documentos

Os demais documentos foram lidos e conferidos. Nenhuma alteração foi necessária:

| Documento | Situação |
|---|---|
| `docs/devops.md` | Correto e completo |
| `docs/casos-de-teste.md` | 18 casos formais (CT-EMP-01 a CT-EMP-10, CT-DEV-01 a CT-DEV-08) — correto |
| `docs/testes-unitarios.md` | Inventário com 84 testes — correto e atualizado |
| `docs/casos-de-uso/emprestar-livro.md` | Completo, com fluxos principal e alternativos |
| `docs/casos-de-uso/devolver-livro.md` | Completo, com fluxos principal e alternativos |

---

## 3. Alterações realizadas

### 3.1 README.md

**Correção 1 — Estrutura de pastas**

Removida a entrada `└── exception/ # Exceções de domínio` do bloco de estrutura,
pois esse pacote não existe em `src/main/java/br/uel/biblioteca/`.

Atualizado o comentário de `.github/workflows/` de:
```
# Planejado Sprint 1: executa mvn test em todo PR
```
para:
```
# CI: executa mvn test em todo PR para develop e main
```

A estrutura de `docs/` na árvore de pastas foi expandida para refletir os
subdiretórios e arquivos reais:

```
├── docs/
│   ├── diagramas/
│   ├── casos-de-uso/
│   ├── evidence/
│   ├── arquitetura.md
│   ├── devops.md
│   ├── casos-de-teste.md
│   └── testes-unitarios.md
```

**Correção 2 — Seção GitHub Actions**

Substituído:
> "O workflow `.github/workflows/ci.yml` **será configurado** no Sprint 1 para executar `mvn test`..."

por:
> "O workflow `.github/workflows/ci.yml` **executa** `mvn test` automaticamente..."

**Correção 3 — Seção Documentação**

A seção `## Documentação` foi reescrita para listar todos os documentos presentes
em `docs/`, organizados por categoria:

- **Arquitetura e DevOps:** `arquitetura.md`, `devops.md`
- **Casos de uso:** `casos-de-uso/emprestar-livro.md`, `casos-de-uso/devolver-livro.md`
- **Implementação:** `emprestar-livro.md`, `modelo-dominio-inicial.md`,
  `camada-dao-inicial.md`, `persistencia.md`, `ui-inicial.md`
- **Testes:** `casos-de-teste.md` (18 casos formais), `testes-unitarios.md` (84 testes)
- **Diagramas PlantUML:** todos os 8 diagramas com nome e descrição
- **Evidências:** referência consolidada à pasta `docs/evidence/`

**Correção 4 — Seção Evidências**

Título atualizado de "Evidências iniciais de organização do projeto" para
"Evidências do processo de desenvolvimento". Descrição ampliada para referenciar
os 5 sprints, mantendo as 5 imagens do Sprint 1 que já estavam inlineadas.

**Correção 5 — Subseção Release**

Expandida de uma frase genérica para uma lista objetiva dos artefatos consolidados
na Release `v1.0.0`, removendo o tempo futuro ("será publicada" → presente com
a lista dos artefatos).

---

### 3.2 docs/arquitetura.md

**Correção 6 — Diagrama ASCII (seção 2)**

Removida a linha com as entidades inexistentes:

```
│  │  ItemEmprestimo · Debito · Area · Autor      │  │
```

Substituída por:

```
│  │  ItemEmprestimo · Debito                     │  │
```

**Correção 7 — Tabela MVC (seção 4)**

Linha da View corrigida de:
```
Templates Thymeleaf: `emprestar.html`, `devolver.html`, telas de cadastro
```
para:
```
Templates Thymeleaf: `emprestimos/form.html`, `emprestimos/devolver.html`,
`alunos/form.html`, `livros/form.html`, comprovantes e demais telas
```

**Correção 8 — Seção 6.1 (Controller)**

"Controladores **previstos**" → "Controladores **implementados**"  
Adicionado `HomeController` à lista, que existia no código mas estava omitido.

**Correção 9 — Seção 6.2 (Service)**

"Serviços **previstos**" → "Serviços **implementados**"

**Correção 10 — Tabela GRASP (seção 6.4)**

Removidas as linhas:
```markdown
| `Area` | Categoriza títulos | Entity |
| `Autor` | Dados do autor da obra | Entity |
```

O modelo de domínio implementado contém: `Aluno`, `Livro`, `Titulo`,
`Emprestimo`, `ItemEmprestimo`, `Debito`. As entidades `Area` e `Autor`
constavam no modelo de referência do livro (Fig. 9.20) mas não foram
implementadas no sistema.

**Correção 11 — Seção 6.5 (View)**

"Views **previstas**: telas de cadastro de aluno e livro, tela de empréstimo,
tela de devolução e comprovantes."

substituído por:

"Views **implementadas**: página inicial (`index.html`), telas de cadastro e
listagem de alunos e livros, busca auxiliar de livro e aluno, tela de empréstimo
(`emprestimos/form.html`), tela de devolução (`emprestimos/devolver.html`) e
comprovantes. Os templates estão em `src/main/resources/templates/`."

---

## 4. Verificação dos entregáveis do enunciado

O enunciado (Trabalho_ParteFinal.pdf) exige os seguintes artefatos. Estado
verificado após esta revisão:

| Entregável | Situação |
|---|---|
| Repositório GitHub público com código, histórico e branches | ✅ Operacional |
| Projeto Kanban configurado no GitHub Projects | ✅ Configurado e atualizado |
| Issues organizadas e vinculadas ao Kanban | ✅ Todas as issues com label, assignee e milestone |
| Código funcional (cadastro, empréstimo, devolução, DAO, GUI) | ✅ Implementado e testado |
| Workflow GitHub Actions configurado e em funcionamento | ✅ `ci.yml` ativo em todo PR |
| Documentação da arquitetura (visão conceitual, elementos, padrões) | ✅ `docs/arquitetura.md` — corrigido nesta revisão |
| Diagramas de Classe e Sequência atualizados e finalizados | ✅ 8 diagramas em `docs/diagramas/` |
| Casos de teste implementados | ✅ 18 casos formais + 84 testes unitários |
| Release publicada no GitHub | ⏳ Pendente — a ser publicada após merge para `main` |

O único item ainda pendente é a publicação da Release `v1.0.0`, que está
fora do escopo desta issue e deve ocorrer após o merge final para `main`
e o fechamento de todas as issues abertas.

---

## 5. Resultado dos testes

Após as alterações, o projeto foi validado com:

```
./mvnw test
```

Resultado:

```
Tests run: 84, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

As 84 suítes de testes passaram sem nenhuma falha. As alterações documentais
não impactam o código de produção nem os testes.

---

## 6. Escopo das alterações — confirmações

### Arquivos alterados nesta issue

| Arquivo | Tipo | Natureza da alteração |
|---|---|---|
| `README.md` | Documentação | 5 correções de inconsistência |
| `docs/arquitetura.md` | Documentação | 6 correções de inconsistência |
| `docs/revisao-final.md` | Documentação | Este relatório  |



---

## 7. Preparação para a Release v1.0.0

Com esta revisão, a documentação está alinhada ao estado real do sistema.
O caminho para a Release `v1.0.0` envolve:

1. **Merge desta branch** (`docs/revisao-final-documentacao`) para `develop` via PR
2. **Incluir as evidências untracked** (`docs/evidence/21` a `26`) no commit desta branch,
   pois registram PRs dos Sprints 3–5 ainda não versionados
3. **Merge final de `develop` para `main`** após validação final
4. **Publicar a Release `v1.0.0`** no GitHub com tag `v1.0.0` apontando para `main`,
   incluindo descrição dos artefatos entregues conforme o enunciado (seção 8. Release)
5. **Fechar todas as issues abertas** e mover cards para `Done` no Kanban

---
