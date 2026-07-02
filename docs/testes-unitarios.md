# Testes Unitários Automatizados

Inventário dos testes unitários implementados com JUnit 5 e Mockito para as funcionalidades de Emprestar Livro, Devolver Livro e Cadastro/Busca de Livros.

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

---

## Busca de Exemplares por Termo

Classe: `LivroService.buscarPorTermo(String termo)`  
Arquivo de teste: `src/test/java/br/uel/biblioteca/service/LivroServiceTest.java`

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `buscarPorTermo_delegaAoDAO` | Termo válido com resultados | Delega ao DAO e retorna a lista |
| `buscarPorTermo_retornaListaVazia_quandoSemResultado` | Termo sem correspondência | Lista vazia retornada |
| `buscarPorTermo_retornaExemplaresIndisponiveis` | Exemplar `disponivel=false` no resultado do DAO | Service não filtra — retorna o exemplar |
| `buscarPorTermo_retornaExemplaresDeBiblioteca` | Exemplar `exemplarBiblioteca=true` no resultado do DAO | Service não filtra — retorna o exemplar |
| `buscarPorTermo_retornaExemplares_porNomeSemAcento` | DAO retorna "Cálculo Vol. 1" para busca "calculo" | Service repassa resultado sem modificar; delegação verificada |
| `buscarPorTermo_retornaExemplares_porAutorSemAcento` | DAO retorna "Machado de Assis" para busca "assis" | Service repassa resultado sem modificar; delegação verificada |

**Subtotal: 6 testes para buscarPorTermo**

> **Nota:** a lógica de filtragem (exibir "Usar este exemplar" somente para disponível e não-biblioteca) está no template Thymeleaf, não no service. Os testes acima verificam que o service retorna todos os exemplares sem filtro, conforme esperado.

> **Normalização accent-insensitive:** a remoção de acentos é feita em dois pontos simultâneos: (1) no Java via `java.text.Normalizer` (NFD + remoção de combining marks) sobre o termo buscado; (2) no SQL via `FUNCTION('translate', LOWER(campo), ...)` sobre as colunas `titulo.nome` e `titulo.autor`. Os testes de service com mock de DAO verificam delegação e contrato comportamental; a eficácia da função `translate()` no JPQL é validada pelo teste manual e pela compatibilidade declarada de H2 e PostgreSQL com essa função.

---

## Busca de Aluno por Termo

Classe: `AlunoService.buscarPorTermo(String termo)`  
Arquivo de teste: `src/test/java/br/uel/biblioteca/service/AlunoServiceTest.java`

| Teste | Cenário | Resultado esperado |
|---|---|---|
| `buscarPorTermo_delegaAoDAO` | Termo válido com resultados | Delega ao DAO e retorna a lista |
| `buscarPorTermo_retornaListaVazia_quandoSemResultado` | Termo sem correspondência | Lista vazia retornada |
| `buscarPorTermo_retornaAlunosInativos` | Aluno `ativo=false` no resultado do DAO | Service não filtra — retorna o aluno; filtro de uso está na view |
| `buscarPorTermo_retornaAlunos_porNomeParcial` | Busca por trecho do nome | Delega ao DAO e retorna resultados |
| `buscarPorTermo_retornaAlunos_porNomeSemAcento` | DAO retorna "João Silva" para busca "joao" | Service repassa resultado sem modificar; delegação verificada |
| `buscarPorTermo_retornaAluno_porCpfExatoSemPontuacao` | Busca por CPF "12345678900" (11 dígitos) | Aluno com CPF correspondente retornado |
| `buscarPorTermo_retornaAluno_porCpfExatoComPontuacao` | Busca por "123.456.789-00" | DAO recebe o termo com pontuação e faz o strip internamente |
| `buscarPorTermo_naoBuscaCpfParcial` | Busca por "456789" (< 11 dígitos) | Lista vazia; CPF parcial não dispara match de CPF no DAO |

**Subtotal: 8 testes para buscarPorTermo**

> **Nota:** a lógica "Usar este aluno" (apenas para `ativo=true`) está no template Thymeleaf, não no service. O service retorna ativos e inativos sem filtro, conforme esperado.

> **Normalização:** nome — `translate(lower(...))` no SQL nativo + `Normalizer.NFD` no Java. CPF — `replaceAll("[^0-9]", "")` no Java, comparação exata; busca por CPF só é ativada quando o termo (após strip) tiver exatamente 11 dígitos.

---

## Resumo geral

| Arquivo | Classe testada | Testes |
|---|---|---|
| `EmprestimoServiceTest.java` | `EmprestimoService` | 20 |
| `AlunoServiceTest.java` | `AlunoService` | 12 |
| `LivroServiceTest.java` | `LivroService` | 19 |
| `AlunoValidacaoTest.java` | Bean Validation / `Aluno` | 11 |
| `LivroValidacaoTest.java` | Bean Validation / `Livro` + `Titulo` | 21 |
| `BibliotecaApplicationTests.java` | Spring Boot context | 1 |
| **Total** | | **84** |

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
