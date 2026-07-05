o dod# Implementação — Caso de Uso Emprestar Livro

**Issue:** #10 — Implementar caso de uso Emprestar Livro  
**Sprint:** 2 (13/06–19/06/2026)  
**Referência:** Exercícios 2–6, p. 197, Cap. 9; Fig. 11.11, p. 226, Cap. 11 — Menolli (2025)

---

## Resumo da implementação

O caso de uso Emprestar Livro foi implementado seguindo a arquitetura Controller → Service → DAO já estabelecida nas issues anteriores. A implementação é baseada no `Codigo2` do professor (adaptado e corrigido para Spring Boot, JPA e Java 17).

A classe `Controle.java` do código de referência foi portada como `EmprestimoService`, que atua como **GRASP Facade/Controller** (Cap. 9, §9.6), orquestrando as validações e a persistência via DAOs.

---

## Regras de negócio implementadas

| Nº | Regra |
|---|---|
| 1 | Empréstimo apenas para aluno cadastrado e ativo |
| 2 | Empréstimo bloqueado se aluno possuir débito ativo (`Debito.pago = false` ou `null`) |
| 3 | Todos os livros devem existir no banco |
| 4 | Exemplar de biblioteca (`exemplarBiblioteca = true`) não pode ser emprestado |
| 5 | Livro indisponível (`disponivel = false`) não pode ser emprestado |
| 6 | Código de patrimônio duplicado na solicitação bloqueia o empréstimo |
| 7 | O empréstimo deve ter ao menos um item |
| 8 | Prazo base de cada livro vem de `Livro.verPrazo()` → `Titulo.prazo` (GRASP Expert) |
| 9 | Prazo final = maior prazo entre os livros + acréscimo por quantidade |
| 10 | Acréscimo: `(n – 2) × 2` dias para `n > 2` livros |
| 11 | Após empréstimo, todos os livros têm `disponivel = false` |
| 12 | Operação atômica via `@Transactional` no `EmprestimoService` |

---

## Cálculo do prazo final de devolução

O prazo é calculado em dias, conforme o Exercício 1 (Cap. 9):

```
prazoFinal = max(prazo_i para i em livros)
           + ((n > 2) ? (n - 2) * 2 : 0)
```

Decisão de implementação: o modelo já possui `dataPrevistaDevolucao : LocalDate`. Portanto, o prazo em dias é calculado primeiro, depois convertido para uma data (`dataEmprestimo + prazoFinalDias`). O comprovante exibe ambos: o prazo em dias e a data prevista, tornando a regra transparente.

---

## Fluxos alternativos implementados

| Fluxo | Condição | Comportamento |
|---|---|---|
| FA-1 | Aluno não cadastrado | Lança `IllegalArgumentException`; controller retorna ao formulário com erro |
| FA-1b | Aluno inativo | Idem |
| FA-2 | Aluno com débito ativo | Idem |
| FA-3 | Livro não encontrado | Idem; menciona o código que falhou |
| FA-4 | Exemplar de biblioteca | Idem; menciona o código |
| FA-5 | Livro indisponível | Idem; menciona o código |
| FA-6 | Código duplicado na solicitação | Detectado antes das consultas ao banco |

**Decisão sobre empréstimo parcial:** qualquer falha bloqueia o empréstimo inteiro. O `@Transactional` garante que nenhum dado seja persistido se uma exceção for lançada. O modelo atual não prevê empréstimo parcial.

**Decisão sobre "reservado" vs "indisponível":** o modelo possui apenas `disponivel : Boolean`. Não há distinção entre "reservado" e "emprestado" — ambos os estados são representados por `disponivel = false`. A data prevista de devolução do livro pode ser consultada através dos `ItemEmprestimo` ativos vinculados ao livro.

---

## Padrões GRASP/SOLID aplicados

| Padrão | Aplicação |
|---|---|
| **GRASP Facade/Controller** | `EmprestimoService` orquestra todo o CU sem expor detalhes internos ao controller |
| **GRASP Creator** | `Emprestimo` cria `ItemEmprestimo`; `Aluno` possui `Emprestimo` (Cap. 9, §9.8.1) |
| **GRASP Expert** | `Livro.verPrazo()` delega para `Titulo.prazo`; `calcularPrazoFinalDias` concentra o cálculo |
| **DIP** | `EmprestimoService` depende de `AlunoDAO`, `LivroDAO`, `EmprestimoDAO`, `DebitoDAO` (interfaces), nunca das `*DAOImpl` |
| **SRP** | Controller: recebe request e monta resposta; Service: regras de negócio; DAO: persistência |

---

## Arquivos criados

| Arquivo | Responsabilidade |
|---|---|
| `dao/DebitoDAO.java` | Interface DAO para `Debito`; método `alunoTemDebitoAtivo(Long)` |
| `dao/impl/DebitoDAOImpl.java` | Implementação com JPQL para verificar débitos ativos |
| `service/EmprestimoService.java` | Orquestra o CU; `realizarEmprestimo()` + `calcularPrazoFinalDias()` |
| `controller/EmprestimoForm.java` | DTO do formulário (matricula + codigosPatrimonio) |
| `controller/EmprestimoController.java` | `GET /emprestimos/novo`, `POST /emprestimos`, `GET /emprestimos/{id}` |
| `templates/emprestimos/form.html` | Formulário de empréstimo (Bootstrap 5) |
| `templates/emprestimos/comprovante.html` | Comprovante com aluno, livros, prazo em dias e data prevista |
| `docs/casos-de-uso/emprestar-livro.md` | Caso de uso formal (fluxo principal, FAs, pós-condições) |
| `docs/diagramas/seq-emprestar-livro.puml` | Diagrama de sequência (completa Fig. 11.11) |
| `docs/diagramas/diagrama-classes-emprestar.puml` | Diagrama de classes com camada DAO |

---

## Arquivos modificados

| Arquivo | Modificação |
|---|---|
| `dao/EmprestimoDAO.java` | Adicionado `buscarComItens(Long id)` (JOIN FETCH para comprovante) |
| `dao/impl/EmprestimoDAOImpl.java` | Implementação de `buscarComItens` com `LEFT JOIN FETCH` encadeado |

---

## Testes criados

Arquivo: `src/test/java/br/uel/biblioteca/service/EmprestimoServiceTest.java`

| Nº | Teste | Cenário |
|---|---|---|
| 1 | `realizarEmprestimo_deveRealizarEmprestimo_comUmLivro` | 1 livro, prazo 7 dias |
| 2 | `realizarEmprestimo_deveRealizarEmprestimo_comDoisLivros` | 2 livros, prazos 7 e 10 → 10 dias |
| 3 | `realizarEmprestimo_deveAplicarAcrescimoDeDoisDias_comTresLivros` | 3 livros, prazos 7, 10, 5 → 12 dias |
| 4 | `realizarEmprestimo_deveAplicarAcrescimoDeQuatroDias_comQuatroLivros` | 4 livros → 14 dias |
| 5 | `realizarEmprestimo_deveUsarMaiorPrazo_naCalculoPrazoFinal` | Prazos 5 e 15 → resultado 15 (não 5) |
| 6 | `realizarEmprestimo_deveLancarExcecao_quandoAlunoNaoCadastrado` | FA-1 — aluno não cadastrado |
| 7 | `realizarEmprestimo_deveLancarExcecao_quandoAlunoInativo` | FA-1b — aluno inativo |
| 8 | `realizarEmprestimo_deveLancarExcecao_quandoAlunoTemDebitoAtivo` | FA-2 — aluno com débito ativo |
| 9 | `realizarEmprestimo_deveLancarExcecao_quandoLivroNaoEncontrado` | FA-3 — código não existe |
| 10 | `realizarEmprestimo_deveLancarExcecao_quandoLivroIndisponivel` | FA-5 — livro indisponível |
| 11 | `realizarEmprestimo_deveLancarExcecao_quandoLivroEExemplarBiblioteca` | FA-4 — exemplar de biblioteca |
| 12 | `realizarEmprestimo_deveLancarExcecao_quandoCodigoPatrimonioDuplicado` | FA-6 — código duplicado |

---

## Diagramas gerados

Os arquivos `.puml` e `.png` correspondentes estão versionados em `docs/diagramas/`:

| Arquivo | Conteúdo |
|---|---|
| `seq-emprestar-livro.puml` + `.png` | Diagrama de sequência (completa Fig. 11.11, Cap. 11) |
| `diagrama-classes-emprestar.puml` + `.png` | Diagrama de classes com camada DAO |

Para regenerar localmente (JAR do PlantUML disponível via extensão VS Code):

```powershell
$jar = "C:\Users\adria\.vscode\extensions\jebbs.plantuml-2.18.1\plantuml.jar"
java -jar $jar docs/diagramas/seq-emprestar-livro.puml
java -jar $jar docs/diagramas/diagrama-classes-emprestar.puml
```

---

## Pendências e limitações

1. **Devolver Livro (issue #11):** o caso de uso de devolução ainda não foi implementado. O campo `dataDevolucao` em `Emprestimo` e `ItemEmprestimo` está preparado para recebê-la.
2. **Geração de Débito:** a entidade `Debito` existe mas a geração automática de multa por atraso não foi implementada (será parte da issue #11).
3. **Casos de Testes formais:** os 12 testes JUnit cobrem todos os cenários exigidos; a descrição formal em formato tabular está no arquivo `docs/casos-de-uso/emprestar-livro.md`.
