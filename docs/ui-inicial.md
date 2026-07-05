# Interface Inicial e Padronização de Navegação

**Issue:** #37 — Criar página inicial e padronizar navegação entre módulos principais  
**Sprint:** 2 (13/06–19/06/2026)

---

## Objetivo

Criar um ponto de entrada único para a aplicação por meio de uma página inicial acessível pela rota `/` e padronizar os links de navegação entre os módulos principais do sistema, garantindo acesso consistente e sem necessidade de digitação manual de URLs.

A implementação não alterou nenhuma regra de negócio, camada de serviço, DAO ou Model.

---

## Arquivos criados

| Arquivo | Descrição |
|---|---|
| `src/main/java/.../controller/HomeController.java` | Controller que mapeia `GET /` para o template `index` |
| `src/main/resources/templates/index.html` | Página inicial com quatro cards de acesso aos módulos |
| `src/main/resources/templates/fragments/nav.html` | Fragmento Thymeleaf reutilizável com a barra de navegação |
| `src/main/resources/static/css/style.css` | CSS personalizado para ajuste visual dos placeholders |

---

## Arquivos modificados

| Arquivo | Alteração |
|---|---|
| `templates/alunos/lista.html` | Inclusão do fragmento de navbar |
| `templates/alunos/form.html` | Inclusão do fragmento de navbar |
| `templates/livros/lista.html` | Inclusão do fragmento de navbar |
| `templates/livros/form.html` | Inclusão do fragmento de navbar; placeholder do Código de Patrimônio corrigido (`123456` → `000001`); placeholder do ISBN adicionado (`1234567890123`) |
| `templates/emprestimos/form.html` | Inclusão do fragmento de navbar; rodapé de links duplicados removido; botão Cancelar corrigido (`/alunos` → `/`) |
| `templates/emprestimos/devolver.html` | Inclusão do fragmento de navbar; rodapé de links duplicados removido; botão Cancelar corrigido (`/emprestimos/novo` → `/`) |
| `templates/emprestimos/comprovante.html` | Inclusão do fragmento de navbar; botões redundantes `Alunos` e `Livros` removidos (mantido CTA `Novo Empréstimo`) |
| `templates/emprestimos/comprovante-devolucao.html` | Inclusão do fragmento de navbar; botões redundantes `Alunos` e `Livros` removidos (mantidos CTAs `Nova Devolução` e `Novo Empréstimo`) |
| `templates/index.html` | Texto do `<h1>` corrigido de "Sistema de Biblioteca" para "Sistema Biblioteca" |
| Todos os 9 templates | Link `<link rel="stylesheet" th:href="@{/css/style.css}"/>` adicionado ao `<head>` |

---

## Estrutura da navegação

### Barra de navegação (`fragments/nav.html`)

Fragmento Thymeleaf incluído via `th:replace` em todos os templates. Exibe sempre, na mesma ordem:

```
Sistema Biblioteca  |  Alunos  |  Livros  |  Novo Empréstimo  |  Devolver Livro
```

O brand "Sistema Biblioteca" aponta para `/`. Todos os links usam `th:href="@{...}"` (sem caminhos relativos frágeis).

### Rotas principais

| Módulo | Rota |
|---|---|
| Página inicial | `GET /` |
| Lista de alunos | `GET /alunos` |
| Cadastrar aluno | `GET /alunos/novo` |
| Lista de livros | `GET /livros` |
| Cadastrar livro | `GET /livros/novo` |
| Realizar empréstimo | `GET /emprestimos/novo` |
| Devolver livro | `GET /emprestimos/devolver` |
| Comprovante de empréstimo | `GET /emprestimos/{id}` |
| Comprovante de devolução | `GET /emprestimos/devolver/resultado` |

### Botões Cancelar

| Formulário | Destino do Cancelar | Justificativa |
|---|---|---|
| Cadastrar Aluno | `/alunos` | Retorna à lista de origem (padrão CRUD) |
| Cadastrar Livro | `/livros` | Retorna à lista de origem (padrão CRUD) |
| Realizar Empréstimo | `/` | Destino neutro; não há lista de empréstimos |
| Devolver Livro | `/` | Destino neutro; sem relação lógica com outro módulo |

---

## Decisões técnicas

### Fragmento reutilizável de navbar

A criação de `fragments/nav.html` centraliza a definição da navegação principal em um único arquivo. Qualquer alteração futura nos links (nome, rota ou ordem) é feita em um único ponto e refletida automaticamente em todos os templates.

A inclusão é feita com `th:replace`, que substitui o elemento hospedeiro pelo fragmento:

```html
<nav th:replace="~{fragments/nav :: navbar}"></nav>
```

### Remoção de navegação duplicada

Os templates `emprestimos/form.html` e `emprestimos/devolver.html` possuíam um rodapé com links de navegação pré-existentes à issue #37, que se tornaram redundantes após a adição da navbar. Esses blocos foram removidos para evitar duplicação e inconsistência (o rodapé listava apenas um subconjunto dos links da navbar).

Os comprovantes (`comprovante.html` e `comprovante-devolucao.html`) mantiveram apenas os botões de ação contextual (`Novo Empréstimo`, `Nova Devolução`), que expressam a próxima ação natural após cada operação. Os botões `Alunos` e `Livros` foram removidos por serem redundantes com a navbar.

### CSS de placeholder

O arquivo `static/css/style.css` ajusta a aparência dos textos de exemplo nos campos de formulário. O seletor `.form-control::placeholder` foi necessário para sobrepor a regra equivalente do Bootstrap 5.3, que define o mesmo seletor com classe (especificidade maior que `::placeholder` simples):

```css
.form-control::placeholder {
    color: #6c757d;
    opacity: 0.45;
}
```

O link para o arquivo é incluído em todos os templates via `th:href="@{/css/style.css}"`, garantindo resolução correta independentemente do context path da aplicação.

---

## Critérios de aceite atendidos

| Critério | Status |
|---|---|
| Rota `/` acessível, sem erro 404 | ✅ |
| Página inicial com acesso aos quatro módulos | ✅ |
| Navbar padronizada em todas as telas principais | ✅ |
| Nomes e ordem dos links consistentes em todas as telas | ✅ |
| Sem menus duplicados ou incompletos | ✅ |
| Sem links quebrados nas rotas principais | ✅ |
| Botões Cancelar com destinos logicamente coerentes | ✅ |
| Placeholders de formulário consistentes e discretos | ✅ |
| Padrão visual preservado (Bootstrap 5.3.3) | ✅ |
| Nenhuma regra de negócio alterada | ✅ |
| 68 testes automatizados passando | ✅ |
