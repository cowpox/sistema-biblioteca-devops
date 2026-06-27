# Testes Unitários Automatizados

Inventário dos testes unitários implementados com JUnit 5 e Mockito para as funcionalidades de Emprestar Livro e Devolver Livro.

## Estratégia de teste

- Framework: JUnit 5 com `@ExtendWith(MockitoExtension.class)`
- Dependências externas (banco de dados) substituídas por mocks via Mockito
- Nenhum contexto Spring carregado — testes rápidos e isolados
- Execução: `mvn test` (local) e GitHub Actions (CI em todo PR para `develop` e `main`)

## Emprestar Livro

Classe: `EmprestimoService.realizarEmprestimo(String matricula, List<String> codigos)`  
Arquivo de teste: `src/test/java/br/uel/biblioteca/service/EmprestimoServiceTest.java`

### Fluxo principal

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `realizarEmprestimo_deveRealizarEmprestimo_comUmLivro` | Aluno ativo, sem débito, 1 livro disponível | Empréstimo criado, livro marcado como indisponível |
| `realizarEmprestimo_deveRealizarEmprestimo_comDoisLivros` | 2 livros com prazos 7 e 10 dias | Prazo final = 10 dias (maior prazo, sem acréscimo) |

### Cálculo de data de devolução

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `realizarEmprestimo_deveAplicarAcrescimoDeDoisDias_comTresLivros` | 3 livros, prazos 7/10/5 | Prazo = max(10) + (3-2)×2 = 12 dias |
| `realizarEmprestimo_deveAplicarAcrescimoDeQuatroDias_comQuatroLivros` | 4 livros, prazos 7/10/5/8 | Prazo = max(10) + (4-2)×2 = 14 dias |
| `realizarEmprestimo_deveUsarMaiorPrazo_naCalculoPrazoFinal` | 2 livros, prazos 5 e 15 | Prazo = 15 dias (maior prevalece) |

### Fluxos alternativos — aluno inválido

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `realizarEmprestimo_deveLancarExcecao_quandoAlunoNaoCadastrado` | Matrícula não existe | `IllegalArgumentException` com matrícula na mensagem |
| `realizarEmprestimo_deveLancarExcecao_quandoAlunoInativo` | Aluno com `ativo = false` | `IllegalArgumentException` com "inativo" na mensagem |
| `realizarEmprestimo_deveLancarExcecao_quandoAlunoTemDebitoAtivo` | Aluno com débito em aberto | `IllegalArgumentException` com "débito" na mensagem |

### Fluxos alternativos — livro inválido

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `realizarEmprestimo_deveLancarExcecao_quandoLivroNaoEncontrado` | Código de patrimônio inexistente | `IllegalArgumentException` com código na mensagem |
| `realizarEmprestimo_deveLancarExcecao_quandoLivroIndisponivel` | Livro com `disponivel = false` | `IllegalArgumentException` com "indisponível" na mensagem |
| `realizarEmprestimo_deveLancarExcecao_quandoLivroEExemplarBiblioteca` | Livro com `exemplarBiblioteca = true` | `IllegalArgumentException` com "exemplar" na mensagem |
| `realizarEmprestimo_deveLancarExcecao_quandoCodigoPatrimonioDuplicado` | Mesmo código informado duas vezes | `IllegalArgumentException` com "duplicado" na mensagem |

**Subtotal: 12 testes para Emprestar Livro**

---

## Devolver Livro

Classe: `EmprestimoService.devolverLivro(String codigoPatrimonio, LocalDate dataDevolucao)`  
Arquivo de teste: `src/test/java/br/uel/biblioteca/service/EmprestimoServiceTest.java`

### Fluxo principal

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `devolverLivro_deveDevolverLivro_semAtraso` | Devolução no dia exato do prazo | 0 dias de atraso, multa R$ 0,00, sem débito gerado |
| `devolverLivro_deveDevolverLivro_comAtraso` | Devolução 3 dias após o prazo | 3 dias de atraso, multa R$ 3,00, débito salvo |
| `devolverLivro_deveLiberarLivro_aposDevolver` | Qualquer devolução | Livro volta a `disponivel = true` |
| `devolverLivro_deveEncerrarEmprestimo_quandoUnicoItemDevolvido` | Empréstimo com 1 item | Status muda para "ENCERRADO", data de devolução registrada |
| `devolverLivro_naoDeveEncerrarEmprestimo_quandoHouverOutroItemPendente` | Empréstimo com 2 itens, apenas 1 devolvido | Status permanece "ATIVO", data de devolução não registrada |
| `devolverLivro_naoDeveGerarDebito_quandoDevolucaoNoPrazo` | Devolução 2 dias antes do prazo | `debitoDAO.salvar` nunca chamado |

### Fluxos alternativos

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `devolverLivro_deveLancarExcecao_quandoLivroNaoEncontrado` | Código de patrimônio inexistente | `IllegalArgumentException` com código na mensagem |
| `devolverLivro_deveLancarExcecao_quandoLivroNaoEmprestado` | Livro existe mas não tem empréstimo ativo | `IllegalArgumentException` com "empréstimo ativo" na mensagem |

**Subtotal: 8 testes para Devolver Livro**

---

## Resumo geral

| Arquivo | Classe testada | Testes |
|---|---|---|
| `EmprestimoServiceTest.java` | `EmprestimoService` | 20 |
| `AlunoServiceTest.java` | `AlunoService` | 4 |
| `LivroServiceTest.java` | `LivroService` | 11 |
| `AlunoValidacaoTest.java` | Bean Validation / `Aluno` | 11 |
| `LivroValidacaoTest.java` | Bean Validation / `Livro` + `Titulo` | 21 |
| `BibliotecaApplicationTests.java` | Spring Boot context | 1 |
| **Total** | | **68** |

## Cobertura da issue #15

| Tarefa | Teste(s) |
|---|---|
| Empréstimo bem-sucedido | `comUmLivro`, `comDoisLivros` |
| Aluno com débito | `quandoAlunoTemDebitoAtivo` |
| Livro indisponível | `quandoLivroIndisponivel` |
| Exemplar de biblioteca | `quandoLivroEExemplarBiblioteca` |
| Cálculo de data de devolução | `comTresLivros`, `comQuatroLivros`, `naCalculoPrazoFinal` |
| Devolução bem-sucedida | `semAtraso` |
| Devolução com atraso | `comAtraso` |
| Livro não emprestado | `quandoLivroNaoEmprestado` |
| `mvn test` executa todos os testes | CI configurado em `.github/workflows/ci.yml` |
