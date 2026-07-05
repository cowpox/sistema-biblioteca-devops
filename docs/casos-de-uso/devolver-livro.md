# Descrição dos Casos de Uso — Devolver Livro

**Referência:** Capítulo 2, Tabela 2.16 — Menolli (2025)  
**Implementação:** Issue #12 — `EmprestimoService.devolverLivro()`, `EmprestimoController`

---

## Diagrama de Casos de Uso

```
                ┌──────────────────────────────────────────────────────────────┐
                │                    Sistema de Biblioteca                     │
                │                                                              │
                │   ╭─────────────────╮   <<include>>   ╭──────────────────╮   │
Funcionário ────│───│  Devolver Livro │────────────────>│    Localizar     │   │
                │   ╰─────────────────╯                 │ Empréstimo Ativo │   │
                │            │  <<include>>             ╰──────────────────╯   │
                │            ↓                                                 │
                │   ╭──────────────────────────╮                               │
                │   │  Calcular Atraso e Multa │                               │
                │   ╰──────────────────────────╯                               │
                └──────────────────────────────────────────────────────────────┘
```

---

## Caso de Uso: Devolver Livro

**Nome:** Devolver Livro  
**Ator:** Funcionário  
**Pré-Condição:** O livro deve estar cadastrado no sistema. O livro deve estar emprestado (associado a um `ItemEmprestimo` com `dataDevolucao = null`). O funcionário deve ter acesso ao sistema.

| Fluxo Principal | Fluxos Alternativos |
|---|---|
| **(1)** O aluno apresenta o exemplar ao funcionário. | |
| **(2)** O funcionário informa o código de patrimônio do exemplar. | **(2.a) Livro não cadastrado:** |
| | (2.a.1) O sistema emite mensagem informando que o livro não foi encontrado. |
| | (2.a.2) Abortar o caso de uso. |
| **(3)** O sistema localiza o empréstimo ativo do livro. **(Ponto de Inclusão → Localizar Empréstimo Ativo)** | **(3.a) Livro não está emprestado:** |
| | (3.a.1) O sistema emite mensagem informando que o livro não possui empréstimo ativo. |
| | (3.a.2) Abortar o caso de uso. |
| **(4)** O sistema registra a data de devolução no item de empréstimo. | |
| **(5)** O sistema verifica se houve atraso na devolução. **(Ponto de Inclusão → Calcular Atraso e Multa)** | **(5.a) Devolução com atraso:** |
| | (5.a.1) O sistema calcula os dias de atraso. |
| | (5.a.2) O sistema cria o débito correspondente à multa. |
| | (5.a.3) Continuar no passo 6. |
| **(6)** O sistema marca o livro como disponível para novos empréstimos. | |
| **(7)** O sistema verifica se todos os itens do empréstimo foram devolvidos. | **(7.a) Empréstimo parcialmente devolvido:** |
| | (7.a.1) O empréstimo permanece com status `ATIVO`. |
| | (7.a.2) Continuar no passo 8. |
| **(8)** O sistema persiste as alterações via DAO. | **(8.a) Todos os itens devolvidos:** |
| | (8.a.1) O sistema define `Emprestimo.dataDevolucao` e o status `ENCERRADO`. |
| | (8.a.2) O sistema persiste o encerramento do empréstimo via DAO. |
| **(9)** O sistema exibe o comprovante de devolução ao funcionário. | |

**Pós-Condição:** A devolução do exemplar foi registrada no `ItemEmprestimo.dataDevolucao`. O livro voltou a ficar disponível (`Livro.disponivel = true`). Se houve atraso, um `Debito` foi criado para o aluno. Se todos os itens do empréstimo foram devolvidos, o `Emprestimo` está encerrado (`status = "ENCERRADO"`). O comprovante foi exibido com: nome do aluno, matrícula, título, data prevista, data real, dias de atraso e valor da multa.

**Pontos de Inclusão:**
- Passo 3 → Localizar Empréstimo Ativo
- Passo 5 → Calcular Atraso e Multa

---

## Caso de Uso: Localizar Empréstimo Ativo

**Nome:** Localizar Empréstimo Ativo  
**Ator:** — *(caso de uso de inclusão — sem ator direto)*  
**Pré-Condição:** Um código de patrimônio foi informado pelo funcionário.

| Fluxo Principal | Fluxos Alternativos |
|---|---|
| **(1)** O sistema busca o livro pelo código de patrimônio informado. | **(1.a) Livro não cadastrado:** |
| | (1.a.1) O sistema emite mensagem informando que o livro não existe. |
| | (1.a.2) Retorna ao caso de uso chamador com indicação de falha. |
| **(2)** O sistema busca o `ItemEmprestimo` ativo associado ao livro, onde `dataDevolucao` é nulo. | **(2.a) Nenhum item ativo encontrado:** |
| | (2.a.1) O sistema emite mensagem informando que o livro não possui empréstimo ativo. |
| | (2.a.2) Retorna ao caso de uso chamador com indicação de falha. |
| **(3)** O sistema retorna o item de empréstimo ao caso de uso chamador. | |

**Pós-Condição:** O `ItemEmprestimo` ativo foi localizado. O item contém referências para `Emprestimo`, `Aluno` e `Livro` com todos os dados necessários para a devolução.

---

## Caso de Uso: Calcular Atraso e Multa

**Nome:** Calcular Atraso e Multa  
**Ator:** — *(caso de uso de inclusão — sem ator direto)*  
**Pré-Condição:** O `ItemEmprestimo` ativo foi localizado. A data de devolução foi informada.

| Fluxo Principal | Fluxos Alternativos |
|---|---|
| **(1)** O sistema obtém `ItemEmprestimo.dataPrevistaDevolucao` e a data de devolução atual. | |
| **(2)** O sistema verifica se `dataDevolucao` é posterior a `dataPrevistaDevolucao`. | **(2.a) Devolução no prazo ou antecipada:** |
| | (2.a.1) `diasAtraso = 0`; nenhum débito é gerado. |
| | (2.a.2) Retorna ao caso de uso chamador. |
| **(3)** O sistema calcula o atraso: `diasAtraso = dataDevolucao − dataPrevistaDevolucao` (em dias). | |
| **(4)** O sistema calcula a multa: `valorMulta = diasAtraso × R$ 1,00`. | |
| **(5)** O sistema cria um `Debito` para o aluno do empréstimo com o valor calculado, associado ao `Emprestimo`. | |
| **(6)** O sistema persiste o `Debito` via `DebitoDAO`. | |
| **(7)** O sistema retorna `diasAtraso` e `valorMulta` ao caso de uso chamador. | |

**Pós-Condição:** Se houve atraso, um `Debito` foi criado e persistido com `pago = false`. O valor corresponde a `diasAtraso × R$ 1,00` (constante `MULTA_POR_DIA` em `EmprestimoService`).

**Exemplo de aplicação da regra:**

| Data prevista | Data devolução | Dias de atraso | Multa |
|---|---|---|---|
| 10/06/2026 | 10/06/2026 | 0 | **R$ 0,00** |
| 10/06/2026 | 13/06/2026 | 3 | **R$ 3,00** |
| 10/06/2026 | 20/06/2026 | 10 | **R$ 10,00** |
