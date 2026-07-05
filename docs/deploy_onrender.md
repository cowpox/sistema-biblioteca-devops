# Deploy no Render — Sistema Biblioteca

**Projeto:** Sistema Biblioteca — UEL Engenharia de Software  
**Disciplina:** Engenharia de Software · **Professor:** André Menolli  
**Data da análise:** 03/07/2026  
**Entrega:** 04/07/2026

---

## 1. Como funciona o Render para este projeto

### Web Service

Um Web Service no Render é um processo de longa duração que recebe tráfego HTTP. O Render executa o processo em contêiner Linux, expõe uma porta via proxy reverso e disponibiliza a aplicação em uma URL pública `*.onrender.com`. Para Spring Boot, o Render executa o JAR ou a imagem Docker e roteia requisições para a porta que a aplicação escutar.

### Conexão com o GitHub

O Render se conecta ao repositório via OAuth (GitHub App). Você autoriza o acesso durante a criação do serviço e escolhe o repositório e a branch. A partir daí o Render observa essa branch.

### Branch de deploy e auto-deploy

Qualquer push na branch configurada dispara um deploy automático. Para este projeto:
- Configurar `develop` agora (para testes)
- Trocar para `main` após o merge da release final

### Deploy condicionado ao CI

O Render tem a opção **"Wait for CI checks to pass before deploying"** no painel do Web Service. Quando ativada, o Render aguarda os GitHub Checks (incluindo o workflow do GitHub Actions) serem bem-sucedidos antes de iniciar o deploy. Isso evita que um push com testes falhando vá para produção — exatamente o fluxo DevOps que o enunciado pede.

### Detecção de Dockerfile

Se houver um `Dockerfile` na raiz do repositório, o Render usa Docker automaticamente. Sem Dockerfile, ele tenta detectar a linguagem e inferir o build. Para Java/Maven, essa detecção funciona mal para versões recentes do Spring Boot — o Dockerfile é a escolha mais segura.

### Variáveis de ambiente

O Render tem uma seção "Environment" em cada serviço onde você define pares `NOME=VALOR`. Essas variáveis são injetadas como variáveis de ambiente do processo — exatamente o que `${DB_URL:fallback}` no `application.properties` já está preparado para ler. Nenhuma credencial entra no código.

### URL pública

O Render fornece automaticamente uma URL no formato `https://nome-do-servico.onrender.com`. Domínio customizado é possível, mas para uso acadêmico a URL padrão é suficiente.

### Logs, Events, Manual Deploy

- **Logs**: stream em tempo real no dashboard. Úteis para diagnosticar falhas de startup.
- **Events**: histórico de todos os deploys (sucesso, falha, duração).
- **Manual Deploy**: botão "Deploy latest commit" para forçar um redeploy sem novo push.
- **Redeploy**: disponível para qualquer commit do histórico.

### Limitações do plano gratuito

| Item | Limitação |
|---|---|
| Web Service | Hiberna após **15 minutos** sem tráfego; cold start ~50 segundos |
| CPU / RAM | 0,1 CPU · 512 MB RAM |
| Horas | 750 horas/mês por conta (suficiente para 1 serviço 24/7) |
| PostgreSQL gratuito | **90 dias** após criação, o banco é **deletado automaticamente** |
| Storage PostgreSQL | 256 MB |
| Egress | 100 GB/mês |

> **Atenção:** o banco PostgreSQL gratuito expira em 90 dias. Para a entrega em 04/07/2026, criar o banco no início de julho garante margem suficiente, mas é importante não deixar o banco como referência permanente.

### Evidências registráveis para o trabalho

Screenshots capturáveis no Render Dashboard:

1. Tela de criação do Web Service (repository, branch, Dockerfile)
2. Tela de criação do PostgreSQL (nome, região, plano)
3. Variáveis de ambiente configuradas (valores ocultos)
4. Log de build Docker bem-sucedido
5. Log de startup da aplicação Spring Boot
6. Aba "Events" com deploy status "Live"
7. URL pública acessível no navegador (`*.onrender.com`)
8. Aba "Metrics" com uptime
9. Opção "Wait for CI" ativada (evidência do fluxo DevOps)

---

## 2. Estratégia recomendada

**Dockerfile + perfil `prod` + Web Service + PostgreSQL no Render**

### Por que Dockerfile

A detecção automática do Render funciona mal para Java 17 com Spring Boot 3.5. Com um Dockerfile multi-stage (build Maven + runtime JRE), o processo é determinístico e idêntico ao que roda em qualquer outro ambiente.

### Por que não `render.yaml`

Esse arquivo serve para Infrastructure-as-Code (criar múltiplos serviços de uma vez via PR). Para um projeto acadêmico com configuração manual via Dashboard, é desnecessário e adiciona complexidade sem benefício.

### Por que perfil `prod`

Criar `application-prod.properties` separado permite definir `show-sql=false`, isolar configurações de produção sem tocar nas configurações de dev/test. Ativado via variável de ambiente `SPRING_PROFILES_ACTIVE=prod`.

### Fluxo de branches

- Deploy inicial configurado na branch `develop` (para validação)
- Após validação, PR `develop → main`, merge e troca da branch no Render para `main`
- Tag `v1.0.0` e publicação da Release com a URL pública documentada

---

## 3. Diagnóstico técnico do projeto

| Pergunta | Resposta |
|---|---|
| Como o banco está configurado? | `application.properties` usa `${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}` com fallback para `localhost:5432/biblioteca` |
| Existe `application-test.properties`? | **Sim** — em `src/test/resources/`, usa H2 com `create-drop`. CI nunca toca o PostgreSQL |
| Existe perfil `prod`? | **Não** — precisa ser criado |
| `application.properties` usa variáveis de ambiente? | **Sim** — já preparado corretamente |
| Aceita `server.port=${PORT:8080}`? | **Não** — essa linha está faltando |
| Maven gera JAR executável? | **Sim** — `spring-boot-maven-plugin` presente no `pom.xml` |
| Depende do PostgreSQL? | **Sim** — `postgresql` como dependência `runtime`; H2 apenas em `test` |
| Tem seed/dados iniciais? | **Sim** — `scripts/seed-dev.sql` (50 alunos, 100 títulos, 300 livros, 20 empréstimos, 5 débitos) |
| Depende de dados preexistentes? | **Sim** — o seed deve ser aplicado após o primeiro boot |
| CI pode quebrar com a configuração de produção? | **Não** — testes usam H2 via `application-test.properties`, completamente isolado |
| `spring-boot-devtools` entra no JAR de produção? | **Não** — declarado como `optional` e `runtime`, não entra no fat JAR |

### Problema crítico identificado

`server.port` não está configurado. O Render injeta uma variável `PORT` (ex: `10000`) e se a aplicação subir em `8080` fixo, o proxy do Render não consegue rotear o tráfego — o health check falha e o deploy fica em loop de restart.

**Correção necessária em `application.properties`:**
```properties
server.port=${PORT:8080}
```

---

## 4. PostgreSQL no Render — criação pelo Dashboard

### Passo a passo

1. Dashboard → **New** → **PostgreSQL**
2. **Name**: `biblioteca-db` (nome amigável, visível no dashboard)
3. **Database**: `biblioteca` (nome real do banco PostgreSQL)
4. **User**: `biblioteca_user`
5. **Region**: `Ohio (US East)` — ou qualquer região disponível no plano gratuito
6. **Plan**: `Free`
7. Clicar em **Create Database**

### Credenciais geradas

Após a criação, o Render exibe na aba "Info":

| Campo | Uso |
|---|---|
| **Internal Database URL** | Configurar como `DB_URL` nas variáveis do Web Service |
| **External Database URL** | Usar localmente para importar dados via `psql` |
| **PSQL Command** | Comando pronto para conectar via terminal |

### Diferença entre Internal e External URL

- **Internal URL**: `postgresql://user:pass@dpg-xxxxxxx-a/biblioteca`  
  Acessível apenas dentro da rede privada do Render. Mais rápido, sem latência de internet. **Usar no Web Service.**

- **External URL**: `postgresql://user:pass@dpg-xxxxxxx-a.oregon-postgres.render.com/biblioteca`  
  Acessível de qualquer máquina. **Usar para importar o seed localmente.**

### Variáveis de ambiente no Web Service

No Web Service → **Environment**, adicionar:

```
DB_URL                 = [Internal Database URL]
DB_USER                = biblioteca_user
DB_PASSWORD            = [senha gerada pelo Render]
SPRING_PROFILES_ACTIVE = prod
```

> Nunca colocar a External Database URL no repositório ou em arquivos versionados.

---

## 5. Estratégia para subir banco populado

### Recomendação: importar `scripts/seed-dev.sql` via External URL

O `scripts/seed-dev.sql` já existe, está bem estruturado e contém tudo que o sistema precisa para uma demonstração completa. Não faz sentido criar outro mecanismo.

### Por que não usar `data.sql` ou `CommandLineRunner`

- `data.sql` executado a cada boot (com `ddl-auto=update`) tentaria reinserir os dados, causando violações de chave primária na segunda inicialização.
- `CommandLineRunner` com `@Profile("prod")` tornaria o código de produção dependente de dados fictícios — risco de acidente em qualquer refatoração.
- O `seed-dev.sql` como arquivo separado, importado manualmente uma única vez, é mais seguro e rastreável.

### Sequência correta de operações

```
1. Criar banco no Render (Dashboard)
2. Criar Web Service no Render (Dashboard)
3. Configurar variáveis de ambiente
4. Fazer o primeiro deploy
   → Spring Boot (ddl-auto=update) cria as tabelas vazias automaticamente
5. Aguardar startup com sucesso nos logs
6. Importar os dados com psql usando a External URL
7. Validar no navegador
```

### Comandos de importação

```bash
# Limpar tabelas caso o boot tenha inserido algo (seguro executar mesmo se vazias)
psql "EXTERNAL_DATABASE_URL_DO_RENDER" -f scripts/truncate.sql

# Importar o seed completo
psql "EXTERNAL_DATABASE_URL_DO_RENDER" -f scripts/seed-dev.sql
```

### Referência — flags para pg_dump (uso futuro)

Se em algum momento for necessário exportar o banco local e importar no Render:

```bash
# Gerar dump sem owner e sem privileges (evita erros de roles inexistentes)
pg_dump --no-owner --no-privileges --no-comments -f biblioteca.sql biblioteca

# Restaurar no Render via External URL
psql "EXTERNAL_DATABASE_URL_DO_RENDER" -f biblioteca.sql
```

```bash
# Alternativa com formato custom (mais eficiente para bancos grandes)
pg_dump -Fc --no-owner --no-privileges -f biblioteca.dump biblioteca
pg_restore --no-owner --no-privileges -d "EXTERNAL_DATABASE_URL_DO_RENDER" biblioteca.dump
```

As flags `--no-owner` e `--no-privileges` evitam que o dump tente recriar roles e permissões que não existem no banco gerenciado do Render, causando erros no restore.

---

## 6. Segurança e boas práticas

| Item | Situação | Ação |
|---|---|---|
| Senhas no código | Fallback `postgres` no `application.properties` — nunca sobrescreve vars de env | Manter como está |
| `.env` versionado | `.gitignore` já exclui `.env` e `.env.*` | OK |
| External URL no repositório | Não está em nenhum arquivo | Nunca colocar |
| Variáveis de ambiente | Já preparadas com `${VAR:fallback}` | OK |
| Perfil `prod` | Não existe | Criar `application-prod.properties` |
| `ddl-auto` em produção | `update` — aceitável para projeto acadêmico | Documentar a decisão; em prod real, usar `validate` + Flyway |
| `show-sql` em produção | `true` no profile principal | Desligar no perfil `prod` |
| `server.port` | Não configurado | Adicionar `server.port=${PORT:8080}` |
| Dados pessoais no seed | Nomes, CPFs e e-mails fictícios | OK — sem dados reais |

---

## 7. Arquivos a criar ou modificar

| Arquivo | Ação | Justificativa |
|---|---|---|
| `Dockerfile` | **Criar** | Obrigatório para deploy confiável no Render com Java 17 |
| `.dockerignore` | **Criar** | Reduz contexto Docker e evita enviar `target/`, `.git/`, `references/` |
| `src/main/resources/application-prod.properties` | **Criar** | Configurações específicas de produção |
| `src/main/resources/application.properties` | **Modificar** | Adicionar `server.port=${PORT:8080}` |
| `docs/deploy_onrender.md` | **Este arquivo** | Documentação do processo |
| `README.md` | **Modificar** | Adicionar URL pública e instrução de acesso |
| `scripts/seed-dev.sql` | Nenhuma | Já existe e está correto |
| `.gitignore` | Nenhuma | Já cobre `.env` e credenciais |
| `render.yaml` | Não criar | Desnecessário para configuração via Dashboard |

### Conteúdo esperado do `Dockerfile`

```dockerfile
# Estágio 1: build Maven
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# Estágio 2: runtime JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/biblioteca-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Conteúdo esperado do `.dockerignore`

```
target/
.git/
references/
.mvn/wrapper/maven-wrapper.jar
.vscode/
.idea/
*.md
docs/
scripts/
```

### Conteúdo esperado do `application-prod.properties`

```properties
# Perfil de producao — ativado via SPRING_PROFILES_ACTIVE=prod
# Nao versionar credenciais. Todas as variaveis abaixo sao injetadas
# como variaveis de ambiente pelo Render.

spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.hibernate.ddl-auto=update
```

---

## 8. Issue proposta

**Título:** `feat(devops): configurar deploy da aplicação no Render`  
**Labels:** `devops`, `feature`  
**Branch de trabalho:** `feature/deploy-render`  
**PR para:** `develop`

### Tarefas

**Configuração mínima do projeto:**
- [ ] Adicionar `server.port=${PORT:8080}` em `application.properties`
- [ ] Criar `src/main/resources/application-prod.properties`
- [ ] Criar `Dockerfile` multi-stage
- [ ] Criar `.dockerignore`

**Render — banco de dados:**
- [ ] Criar serviço PostgreSQL no Dashboard do Render
- [ ] Anotar Internal URL e External URL (fora do repositório)

**Render — Web Service:**
- [ ] Criar Web Service conectado ao repositório, branch `develop`
- [ ] Configurar variáveis de ambiente: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `SPRING_PROFILES_ACTIVE=prod`
- [ ] Ativar "Wait for CI checks to pass"
- [ ] Executar deploy inicial e validar logs de startup

**Seed dos dados:**
- [ ] Após primeiro boot bem-sucedido, importar:
  ```bash
  psql "EXTERNAL_URL" -f scripts/seed-dev.sql
  ```
- [ ] Validar dados no navegador

**Validação e documentação:**
- [ ] Acessar URL pública e testar fluxo completo (cadastro → empréstimo → devolução)
- [ ] Atualizar `README.md` com URL pública
- [ ] Registrar screenshots como evidências em `docs/evidence/`

### Critérios de aceite

- [ ] Aplicação acessível via URL pública `*.onrender.com`
- [ ] Fluxo principal funcionando sem erros 500
- [ ] Banco PostgreSQL gerenciado no Render com dados de demonstração
- [ ] Nenhuma credencial no código ou em arquivos versionados
- [ ] Logs de startup sem erros
- [ ] Deploy disparado automaticamente por push na branch configurada
- [ ] `mvn test` (CI) continua passando após as alterações

### Relação com a release `v1.0.0`

A URL pública deve constar na descrição da Release `v1.0.0` como entregável. A branch do Web Service no Render deve ser trocada de `develop` para `main` após o merge final.

### Fases DevOps contempladas

| Fase | Como é demonstrada |
|---|---|
| **Deploy** | Dockerfile + Render Web Service com auto-deploy |
| **Release** | URL pública documentada na Release `v1.0.0` |
| **Operate** | Logs, Events e Metrics no Dashboard do Render |
| **Monitor** | Health check automático do Render + logs de startup do Spring Boot |

---

## 9. Plano de execução seguro

### Etapa 1 — Diagnóstico ✅
Concluído. Ver seção 3.

### Etapa 2 — Ajustes mínimos de configuração
- Adicionar `server.port=${PORT:8080}` em `application.properties`
- Criar `application-prod.properties`

### Etapa 3 — Dockerfile e .dockerignore
- Criar `Dockerfile` multi-stage na raiz do projeto
- Criar `.dockerignore`

### Etapa 4 — Criar banco no Render (manual, via Dashboard)
- New → PostgreSQL → Free → nome `biblioteca-db`
- Anotar Internal URL e External URL em lugar seguro fora do repositório

### Etapa 5 — Criar Web Service no Render (manual, via Dashboard)
- New → Web Service → conectar GitHub → selecionar repo → branch `develop`
- Runtime: Docker (detecta o `Dockerfile` automaticamente)
- Ativar "Wait for CI checks to pass"

### Etapa 6 — Configurar variáveis de ambiente
- `DB_URL` = Internal Database URL
- `DB_USER` = usuário gerado pelo Render
- `DB_PASSWORD` = senha gerada pelo Render
- `SPRING_PROFILES_ACTIVE` = `prod`

### Etapa 7 — Deploy inicial
- Acompanhar logs no Dashboard
- Verificar linha `Started BibliotecaApplication in X.XXX seconds`

### Etapa 8 — Importar seed
```bash
psql "EXTERNAL_DATABASE_URL" -f scripts/seed-dev.sql
```

### Etapa 9 — Validar URL pública
- Acessar `https://[nome].onrender.com`
- Testar cadastro de aluno, listagem de livros, empréstimo e devolução

### Etapa 10 — Registrar evidências
- 9 screenshots (ver seção 1 — Evidências registráveis)
- Salvar em `docs/evidence/` numerando a partir do próximo disponível

### Etapa 11 — Atualizar README.md
- Adicionar seção com URL pública e instrução de acesso

### Etapa 12 — Commit e PR
- Branch: `feature/deploy-render` → PR para `develop`
- CI deve passar antes do merge

### Etapa 13 — Merge `develop → main`
- Após validação em `develop`, abrir PR `develop → main`
- Trocar branch do Web Service no Render de `develop` para `main`

### Etapa 14 — Tag e Release `v1.0.0`
- `git tag v1.0.0` em `main`
- Publicar Release no GitHub com URL pública documentada
- Fechar todas as issues abertas

---

## 10. Resumo das decisões

| Decisão | Escolha | Motivo |
|---|---|---|
| Estratégia de deploy | Dockerfile | Mais confiável para Java 17 + Spring Boot 3.5 no Render |
| `render.yaml` | Não usar | Dashboard é suficiente; YAML adiciona complexidade sem ganho acadêmico |
| `ddl-auto` em produção | `update` | Aceitável para contexto acadêmico; em prod real usar `validate` + Flyway |
| Seed | `scripts/seed-dev.sql` via `psql` | Já existe, é completo e tem reset de sequências |
| Branch inicial | `develop` | Validar deploy antes do merge final em `main` |
| `SPRING_PROFILES_ACTIVE` | Via env var no Render, não no código | Segurança + separação de configurações |
| `server.port` | `${PORT:8080}` | Obrigatório — Render injeta `PORT` dinamicamente |
