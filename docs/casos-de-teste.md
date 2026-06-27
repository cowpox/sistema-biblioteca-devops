# Casos de Teste — Sistema Biblioteca

**Issue:** #14 — Documentar formalmente os casos de teste do sistema  
**Sprint:** 3  
**Referência:** Casos de uso documentados em `docs/casos-de-uso/emprestar-livro.md` e `docs/casos-de-uso/devolver-livro.md`

---

## Introdução

Este documento registra os casos de teste funcionais associados aos casos de uso **Emprestar Livro** e **Devolver Livro** do Sistema Biblioteca. Os casos de teste cobrem o fluxo principal e os fluxos alternativos de cada caso de uso, conforme especificado na documentação formal e implementados em `EmprestimoService`.

Cada caso de teste está identificado por um ID único, relacionado ao caso de uso correspondente e estruturado com: objetivo, pré-condições, procedimento e resultado esperado.

Os casos de teste de nível de unidade correspondentes estão implementados em `src/test/java/br/uel/biblioteca/service/EmprestimoServiceTest.java`.

---

## 1. Casos de Teste — Emprestar Livro

**Caso de uso de referência:** Emprestar Livro (`docs/casos-de-uso/emprestar-livro.md`)  
**Classe de implementação:** `EmprestimoService.realizarEmprestimo()`

---

### CT-EMP-01 — Empréstimo de um livro com sucesso

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-01 |
| **Caso de uso** | Emprestar Livro — Fluxo Principal |
| **Objetivo** | Verificar que o sistema registra corretamente um empréstimo contendo um único livro disponível para um aluno ativo e sem débitos. |
| **Pré-condições** | Aluno cadastrado, ativo e sem débitos ativos. Livro cadastrado, disponível (`disponivel = true`) e não é exemplar de biblioteca. |
| **Procedimento** | 1. Informar a matrícula do aluno. 2. Informar o código de patrimônio de um livro disponível. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Empréstimo registrado com status `ATIVO`. O livro passa para `disponivel = false`. A data prevista de devolução corresponde à data do empréstimo acrescida do prazo do título. Comprovante exibido com os dados do aluno, do livro e do prazo. |
| **Teste JUnit** | `realizarEmprestimo_deveRealizarEmprestimo_comUmLivro` |

---

### CT-EMP-02 — Cálculo de prazo com dois livros

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-02 |
| **Caso de uso** | Emprestar Livro — Calcular Prazo de Devolução |
| **Objetivo** | Verificar que o prazo final é o maior prazo individual quando o empréstimo contém dois livros. |
| **Pré-condições** | Aluno ativo e sem débitos. Dois livros disponíveis com prazos distintos (ex.: 7 e 10 dias). |
| **Procedimento** | 1. Informar a matrícula do aluno. 2. Informar os códigos de patrimônio dos dois livros. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Prazo final igual ao maior prazo entre os livros (10 dias). Nenhum acréscimo aplicado (regra de acréscimo só se aplica a partir de 3 livros). |
| **Teste JUnit** | `realizarEmprestimo_deveRealizarEmprestimo_comDoisLivros` |

---

### CT-EMP-03 — Cálculo de prazo com acréscimo por quantidade

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-03 |
| **Caso de uso** | Emprestar Livro — Calcular Prazo de Devolução |
| **Objetivo** | Verificar que o sistema aplica o acréscimo de `(n − 2) × 2` dias ao prazo base quando o empréstimo contém mais de dois livros. |
| **Pré-condições** | Aluno ativo e sem débitos. Três ou mais livros disponíveis com prazos individuais conhecidos. |
| **Procedimento** | 1. Informar a matrícula do aluno. 2. Informar os códigos de patrimônio de três livros (prazos: 7, 10 e 5 dias). 3. Confirmar o empréstimo. |
| **Resultado esperado** | Prazo base = 10 dias (maior). Acréscimo = `(3 − 2) × 2 = 2` dias. Prazo final = 12 dias. |
| **Teste JUnit** | `realizarEmprestimo_deveAplicarAcrescimoDeDoisDias_comTresLivros` |

---

### CT-EMP-04 — Tentativa de empréstimo para aluno não cadastrado

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-04 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 2.a |
| **Objetivo** | Verificar que o sistema rejeita o empréstimo quando a matrícula informada não corresponde a nenhum aluno cadastrado. |
| **Pré-condições** | Nenhuma. A matrícula informada não existe no banco de dados. |
| **Procedimento** | 1. Informar uma matrícula inexistente (ex.: `99999999`). 2. Informar qualquer código de patrimônio. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem de erro indicando que o aluno não foi encontrado. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoAlunoNaoCadastrado` |

---

### CT-EMP-05 — Tentativa de empréstimo para aluno inativo

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-05 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 3.a |
| **Objetivo** | Verificar que o sistema bloqueia o empréstimo quando o aluno está com o status `ativo = false`. |
| **Pré-condições** | Aluno cadastrado com `ativo = false`. |
| **Procedimento** | 1. Informar a matrícula de um aluno inativo. 2. Informar um código de patrimônio válido. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem informando que o aluno está inativo. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoAlunoInativo` |

---

### CT-EMP-06 — Tentativa de empréstimo com débito ativo

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-06 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 4.a |
| **Objetivo** | Verificar que o sistema bloqueia o empréstimo quando o aluno possui ao menos um débito ativo (`pago = false`). |
| **Pré-condições** | Aluno cadastrado e ativo. Aluno possui débito com `pago = false` ou `null`. |
| **Procedimento** | 1. Informar a matrícula do aluno com débito. 2. Informar um código de patrimônio válido. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem informando que o aluno possui débito ativo. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoAlunoTemDebitoAtivo` |

---

### CT-EMP-07 — Tentativa de empréstimo de livro não cadastrado

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-07 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 6.1.a |
| **Objetivo** | Verificar que o sistema rejeita o empréstimo quando um dos códigos de patrimônio informados não existe no banco de dados. |
| **Pré-condições** | Aluno ativo e sem débitos. O código de patrimônio informado não está cadastrado. |
| **Procedimento** | 1. Informar a matrícula de um aluno válido. 2. Informar um código de patrimônio inexistente. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem indicando o código que não foi encontrado. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoLivroNaoEncontrado` |

---

### CT-EMP-08 — Tentativa de empréstimo de exemplar de biblioteca

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-08 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 6.1.b |
| **Objetivo** | Verificar que o sistema rejeita o empréstimo de livro marcado como exemplar de biblioteca (`exemplarBiblioteca = true`). |
| **Pré-condições** | Aluno ativo e sem débitos. Livro cadastrado com `exemplarBiblioteca = true`. |
| **Procedimento** | 1. Informar a matrícula de um aluno válido. 2. Informar o código de patrimônio de um exemplar de biblioteca. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem indicando que o exemplar não pode ser emprestado. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoLivroEExemplarBiblioteca` |

---

### CT-EMP-09 — Tentativa de empréstimo de livro indisponível

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-09 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 6.1.c |
| **Objetivo** | Verificar que o sistema rejeita o empréstimo de livro com `disponivel = false`. |
| **Pré-condições** | Aluno ativo e sem débitos. Livro cadastrado com `disponivel = false` (já emprestado). |
| **Procedimento** | 1. Informar a matrícula de um aluno válido. 2. Informar o código de patrimônio de um livro indisponível. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem indicando que o livro está indisponível. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoLivroIndisponivel` |

---

### CT-EMP-10 — Tentativa de empréstimo com código de patrimônio duplicado

| Campo | Conteúdo |
|---|---|
| **ID** | CT-EMP-10 |
| **Caso de uso** | Emprestar Livro — Fluxo Alternativo 5.a |
| **Objetivo** | Verificar que o sistema detecta e rejeita a solicitação quando o mesmo código de patrimônio é informado mais de uma vez. |
| **Pré-condições** | Aluno ativo e sem débitos. |
| **Procedimento** | 1. Informar a matrícula de um aluno válido. 2. Informar o mesmo código de patrimônio duas vezes na lista. 3. Confirmar o empréstimo. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem indicando código duplicado. A validação ocorre antes de qualquer consulta aos livros. Nenhum empréstimo é registrado. |
| **Teste JUnit** | `realizarEmprestimo_deveLancarExcecao_quandoCodigoPatrimonioDuplicado` |

---

## 2. Casos de Teste — Devolver Livro

**Caso de uso de referência:** Devolver Livro (`docs/casos-de-uso/devolver-livro.md`)  
**Classe de implementação:** `EmprestimoService.devolverLivro()`

---

### CT-DEV-01 — Devolução no prazo, sem multa

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-01 |
| **Caso de uso** | Devolver Livro — Fluxo Principal |
| **Objetivo** | Verificar que o sistema registra corretamente a devolução de um livro entregue até a data prevista, sem geração de débito. |
| **Pré-condições** | Livro cadastrado e com empréstimo ativo (`ItemEmprestimo.dataDevolucao = null`). Data de devolução igual ou anterior à `dataPrevistaDevolucao`. |
| **Procedimento** | 1. Informar o código de patrimônio do exemplar. 2. Confirmar a devolução. |
| **Resultado esperado** | `ItemEmprestimo.dataDevolucao` preenchida com a data atual. Livro volta a `disponivel = true`. Nenhum débito gerado. Comprovante exibido com dias de atraso = 0 e multa = R$ 0,00. |
| **Teste JUnit** | `devolverLivro_deveDevolverLivro_semAtraso` |

---

### CT-DEV-02 — Devolução com atraso e geração de débito

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-02 |
| **Caso de uso** | Devolver Livro — Fluxo Alternativo 5.a |
| **Objetivo** | Verificar que o sistema calcula o atraso e gera um débito de R$ 1,00 por dia quando o livro é devolvido após a data prevista. |
| **Pré-condições** | Livro com empréstimo ativo. Data de devolução posterior à `dataPrevistaDevolucao` (ex.: 3 dias de atraso). |
| **Procedimento** | 1. Informar o código de patrimônio do exemplar. 2. Confirmar a devolução em data posterior ao prazo. |
| **Resultado esperado** | `diasAtraso = 3`. Débito criado com `valor = R$ 3,00` (3 × R$ 1,00), associado ao aluno e ao empréstimo, com `pago = false`. Comprovante exibe o atraso e o valor da multa. |
| **Teste JUnit** | `devolverLivro_deveDevolverLivro_comAtraso` |

---

### CT-DEV-03 — Livro fica disponível após devolução

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-03 |
| **Caso de uso** | Devolver Livro — Fluxo Principal, passo 6 |
| **Objetivo** | Verificar que o sistema marca o livro como disponível após o registro da devolução. |
| **Pré-condições** | Livro com empréstimo ativo e `disponivel = false`. |
| **Procedimento** | 1. Informar o código de patrimônio do exemplar. 2. Confirmar a devolução. |
| **Resultado esperado** | `Livro.disponivel = true` após a devolução. `LivroDAO.atualizar()` é chamado para persistir a alteração. |
| **Teste JUnit** | `devolverLivro_deveLiberarLivro_aposDevolver` |

---

### CT-DEV-04 — Empréstimo encerrado quando único item é devolvido

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-04 |
| **Caso de uso** | Devolver Livro — Fluxo Principal, passo 8.a |
| **Objetivo** | Verificar que o sistema encerra o empréstimo quando todos os itens são devolvidos. |
| **Pré-condições** | Empréstimo com exatamente um `ItemEmprestimo` ativo. |
| **Procedimento** | 1. Informar o código de patrimônio do único exemplar do empréstimo. 2. Confirmar a devolução. |
| **Resultado esperado** | `Emprestimo.status = "ENCERRADO"`. `Emprestimo.dataDevolucao` preenchida com a data da devolução. `EmprestimoDAO.atualizar()` chamado para persistir o encerramento. |
| **Teste JUnit** | `devolverLivro_deveEncerrarEmprestimo_quandoUnicoItemDevolvido` |

---

### CT-DEV-05 — Empréstimo permanece ativo quando há outro item pendente

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-05 |
| **Caso de uso** | Devolver Livro — Fluxo Alternativo 7.a |
| **Objetivo** | Verificar que o sistema não encerra o empréstimo quando ainda existe ao menos um item com `dataDevolucao = null`. |
| **Pré-condições** | Empréstimo com dois `ItemEmprestimo`. Apenas um dos itens está sendo devolvido; o outro permanece sem data de devolução. |
| **Procedimento** | 1. Informar o código de patrimônio de apenas um dos exemplares. 2. Confirmar a devolução. |
| **Resultado esperado** | `Emprestimo.status` permanece `"ATIVO"`. `Emprestimo.dataDevolucao` permanece `null`. `EmprestimoDAO.atualizar()` não é chamado. |
| **Teste JUnit** | `devolverLivro_naoDeveEncerrarEmprestimo_quandoHouverOutroItemPendente` |

---

### CT-DEV-06 — Tentativa de devolução de livro não cadastrado

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-06 |
| **Caso de uso** | Devolver Livro — Fluxo Alternativo 2.a |
| **Objetivo** | Verificar que o sistema rejeita a devolução quando o código de patrimônio informado não corresponde a nenhum livro cadastrado. |
| **Pré-condições** | Nenhuma. O código de patrimônio informado não existe no banco de dados. |
| **Procedimento** | 1. Informar um código de patrimônio inexistente (ex.: `999999`). 2. Confirmar a devolução. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem indicando que o livro não foi encontrado. Nenhuma alteração é persistida. |
| **Teste JUnit** | `devolverLivro_deveLancarExcecao_quandoLivroNaoEncontrado` |

---

### CT-DEV-07 — Tentativa de devolução de livro sem empréstimo ativo

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-07 |
| **Caso de uso** | Devolver Livro — Fluxo Alternativo 3.a |
| **Objetivo** | Verificar que o sistema rejeita a devolução quando o livro não possui `ItemEmprestimo` com `dataDevolucao = null`. |
| **Pré-condições** | Livro cadastrado sem empréstimo ativo (já devolvido anteriormente ou nunca emprestado). |
| **Procedimento** | 1. Informar o código de patrimônio de um livro disponível (sem empréstimo ativo). 2. Confirmar a devolução. |
| **Resultado esperado** | Sistema rejeita a operação com mensagem informando que o livro não possui empréstimo ativo. Nenhum débito é gerado. Livro não é modificado. |
| **Teste JUnit** | `devolverLivro_deveLancarExcecao_quandoLivroNaoEmprestado` |

---

### CT-DEV-08 — Devolução antecipada sem geração de débito

| Campo | Conteúdo |
|---|---|
| **ID** | CT-DEV-08 |
| **Caso de uso** | Devolver Livro — Calcular Atraso e Multa — Fluxo Alternativo 2.a |
| **Objetivo** | Verificar que nenhum débito é gerado quando o livro é devolvido antes da data prevista. |
| **Pré-condições** | Livro com empréstimo ativo. Data de devolução anterior à `dataPrevistaDevolucao` (ex.: 2 dias antes do prazo). |
| **Procedimento** | 1. Informar o código de patrimônio do exemplar. 2. Confirmar a devolução em data anterior ao prazo. |
| **Resultado esperado** | `diasAtraso = 0`. Nenhum débito criado. `DebitoDAO.salvar()` não é chamado. Comprovante exibe devolução no prazo. |
| **Teste JUnit** | `devolverLivro_naoDeveGerarDebito_quandoDevolucaoNoPrazo` |

---

## Resumo da cobertura

### Emprestar Livro

| ID | Cenário | Fluxo |
|---|---|---|
| CT-EMP-01 | Empréstimo de um livro com sucesso | Principal |
| CT-EMP-02 | Prazo final com dois livros (maior prazo) | Calcular Prazo |
| CT-EMP-03 | Prazo final com acréscimo por quantidade (3+ livros) | Calcular Prazo |
| CT-EMP-04 | Aluno não cadastrado | FA 2.a |
| CT-EMP-05 | Aluno inativo | FA 3.a |
| CT-EMP-06 | Aluno com débito ativo | FA 4.a |
| CT-EMP-07 | Livro não cadastrado | FA 6.1.a |
| CT-EMP-08 | Exemplar de biblioteca | FA 6.1.b |
| CT-EMP-09 | Livro indisponível | FA 6.1.c |
| CT-EMP-10 | Código de patrimônio duplicado na solicitação | FA 5.a |

### Devolver Livro

| ID | Cenário | Fluxo |
|---|---|---|
| CT-DEV-01 | Devolução no prazo, sem multa | Principal |
| CT-DEV-02 | Devolução com atraso e débito | FA 5.a |
| CT-DEV-03 | Livro fica disponível após devolução | Principal, passo 6 |
| CT-DEV-04 | Empréstimo encerrado quando único item devolvido | FA 8.a |
| CT-DEV-05 | Empréstimo permanece ativo com item pendente | FA 7.a |
| CT-DEV-06 | Livro não cadastrado | FA 2.a |
| CT-DEV-07 | Livro sem empréstimo ativo | FA 3.a |
| CT-DEV-08 | Devolução antecipada sem débito | Calcular Atraso — FA 2.a |
