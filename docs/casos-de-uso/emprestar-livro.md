# Descrição dos Casos de Uso — Emprestar Livro

**Referência:** Capítulo 2, Tabela 2.16 — Menolli (2025)  
**Exercícios:** 2–6, p. 197, Cap. 9 — Menolli (2025)  
**Diagrama de Sequência:** Fig. 11.11, p. 226, Cap. 11 — Menolli (2025)

---

## Diagrama de Casos de Uso

```
                ┌────────────────────────────────────────────────────────────┐
                │                  Sistema de Biblioteca                     │
                │                                                            │
                │   ╭──────────────────╮   <<include>>   ╭────────────────╮  │
Funcionário ────│───│  Emprestar Livro │────────────────>│    Verificar   │  │
                │   ╰──────────────────╯                 │ Disponibilidade│  │
                │            │  <<include>>              ╰────────────────╯  │
                │            ↓                                               │
                │   ╭──────────────────────────╮                             │
                │   │ Calcular Prazo Devolução │                             │
                │   ╰──────────────────────────╯                             │
                └────────────────────────────────────────────────────────────┘
```

---

## Caso de Uso: Emprestar Livro

**Nome:** Emprestar Livro  
**Ator:** Funcionário  
**Pré-Condição:** O aluno deve possuir cadastro ativo no sistema. Os livros devem estar cadastrados. O funcionário deve ter acesso ao sistema.

| Fluxo Principal | Fluxos Alternativos |
|---|---|
| **(1)** O aluno apresenta os livros e sua identificação ao funcionário. | |
| **(2)** O funcionário informa a matrícula do aluno no sistema. | **(2.a) Aluno não cadastrado:** |
| | (2.a.1) O sistema emite mensagem informando que o aluno não está cadastrado. |
| | (2.a.2) Abortar o caso de uso. |
| **(3)** O sistema verifica se o aluno está ativo. | **(3.a) Aluno inativo:** |
| | (3.a.1) O sistema emite mensagem informando que o aluno está inativo. |
| | (3.a.2) Abortar o caso de uso. |
| **(4)** O sistema verifica se o aluno possui débito ativo. | **(4.a) Aluno com débito ativo:** |
| | (4.a.1) O sistema emite mensagem informando que o aluno possui débito ou pendência ativa. |
| | (4.a.2) Abortar o caso de uso. |
| **(5)** O funcionário informa os códigos de patrimônio dos livros a serem emprestados. | **(5.a) Código de patrimônio duplicado na solicitação:** |
| | (5.a.1) O sistema detecta que o mesmo código foi informado mais de uma vez. |
| | (5.a.2) O sistema emite mensagem indicando o código duplicado. |
| | (5.a.3) Abortar o caso de uso. |
| **(6)** Enquanto houver livro a verificar, faça: | |
| (6.1) O sistema verifica a disponibilidade do livro. **(Ponto de Inclusão → Verificar Disponibilidade)** | **(6.1.a) Livro não encontrado:** |
| | (6.1.a.1) O sistema emite mensagem informando que o livro não está cadastrado. |
| | (6.1.a.2) Abortar o caso de uso. |
| | **(6.1.b) Livro é exemplar de biblioteca:** |
| | (6.1.b.1) O sistema emite mensagem informando que o exemplar não pode ser emprestado. |
| | (6.1.b.2) Abortar o caso de uso. |
| | **(6.1.c) Livro indisponível:** |
| | (6.1.c.1) O sistema emite mensagem informando que o livro está indisponível. |
| | (6.1.c.2) Abortar o caso de uso. |
| (6.2) O sistema cria um item de empréstimo associado ao livro. | |
| **(7)** O sistema calcula o prazo final de devolução. **(Ponto de Inclusão → Calcular Prazo de Devolução)** | |
| **(8)** O sistema registra o empréstimo e todos os seus itens. | |
| **(9)** O sistema marca todos os livros emprestados como indisponíveis. | |
| **(10)** O sistema exibe o comprovante do empréstimo ao funcionário. | |

**Pós-Condição:** O empréstimo foi registrado no sistema com status ativo. Todos os livros emprestados estão marcados como indisponíveis. O comprovante foi exibido com: nome do aluno, matrícula, livros, prazo final em dias e data prevista de devolução.

**Pontos de Inclusão:**
- Passo 6.1 → Verificar Disponibilidade
- Passo 7 → Calcular Prazo de Devolução

---

## Caso de Uso: Verificar Disponibilidade

**Nome:** Verificar Disponibilidade  
**Ator:** — *(caso de uso de inclusão — sem ator direto)*  
**Pré-Condição:** Um código de patrimônio foi informado pelo funcionário.

| Fluxo Principal | Fluxos Alternativos |
|---|---|
| **(1)** O sistema busca o livro pelo código de patrimônio informado. | **(1.a) Livro não encontrado:** |
| | (1.a.1) O sistema emite mensagem informando que o livro não está cadastrado. |
| | (1.a.2) Retorna ao caso de uso chamador com indicação de falha. |
| **(2)** O sistema verifica se o livro é exemplar de biblioteca. | **(2.a) Livro é exemplar de biblioteca:** |
| | (2.a.1) O sistema emite mensagem informando que exemplares de biblioteca não podem ser emprestados. |
| | (2.a.2) Retorna ao caso de uso chamador com indicação de falha. |
| **(3)** O sistema verifica se o livro está disponível para empréstimo. | **(3.a) Livro indisponível:** |
| | (3.a.1) O sistema emite mensagem informando que o livro está indisponível. |
| | (3.a.2) Retorna ao caso de uso chamador com indicação de falha. |
| **(4)** O sistema retorna ao caso de uso chamador confirmando a disponibilidade. | |

**Pós-Condição:** A disponibilidade do livro foi confirmada. O livro está cadastrado, não é exemplar de biblioteca e está marcado como disponível.

---

## Caso de Uso: Calcular Prazo de Devolução

**Nome:** Calcular Prazo de Devolução  
**Ator:** — *(caso de uso de inclusão — sem ator direto)*  
**Pré-Condição:** Ao menos um livro foi validado e está associado ao empréstimo em andamento.

| Fluxo Principal | Fluxos Alternativos |
|---|---|
| **(1)** O sistema obtém a lista de livros do empréstimo. | |
| **(2)** Enquanto houver livro na lista, faça: | |
| (2.1) O sistema obtém o prazo individual do livro por meio do método `verPrazo()`, que delega para o prazo cadastrado no título. | |
| (2.2) O sistema registra o prazo individual do livro. | |
| **(3)** O sistema determina o maior prazo entre todos os livros do empréstimo. | |
| **(4)** O sistema verifica se o número de livros é maior que dois. | **(4.a) Número de livros maior que dois:** |
| | (4.a.1) O sistema calcula o acréscimo: `(n − 2) × 2` dias, onde `n` é a quantidade de livros. |
| | (4.a.2) O sistema soma o acréscimo ao maior prazo. |
| | (4.a.3) Retorna ao passo 5. |
| **(5)** O sistema atribui o prazo final calculado ao empréstimo e a todos os seus itens. | |
| **(6)** O sistema retorna o prazo final em dias ao caso de uso chamador. | |

**Pós-Condição:** O prazo final de devolução em dias foi calculado e atribuído ao empréstimo. A data prevista de devolução corresponde à data do empréstimo acrescida do prazo final calculado.

**Exemplos de aplicação da regra:**

| Nº de livros | Prazos individuais | Maior prazo | Acréscimo | Prazo final |
|---|---|---|---|---|
| 1 | 7 | 7 | 0 | **7 dias** |
| 2 | 7, 10 | 10 | 0 | **10 dias** |
| 3 | 7, 10, 5 | 10 | +2 | **12 dias** |
| 4 | 7, 10, 5, 8 | 10 | +4 | **14 dias** |
| 5 | 7, 10, 5, 8, 14 | 14 | +6 | **20 dias** |

**Referência:** Exercício 1, p. 197, Cap. 9 — Menolli (2025).
