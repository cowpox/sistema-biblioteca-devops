# Scripts de desenvolvimento

Esta pasta contém scripts auxiliares para resetar e popular o banco local de desenvolvimento.

## Atenção

O script `truncate-dev.sql` apaga os dados das tabelas principais e reinicia as sequências de ID.

Use apenas em ambiente local/de desenvolvimento.

Não execute em produção.

## Ordem de execução

1. `truncate-dev.sql`
2. `seed-dev.sql`

## Conteúdo da massa de dados

O seed cria dados fictícios para testes manuais do sistema, incluindo:

- alunos ativos;
- títulos bibliográficos;
- múltiplos exemplares por título;
- exemplares disponíveis;
- exemplares indisponíveis;
- exemplares de biblioteca;
- empréstimos ativos;
- empréstimos encerrados;
- débitos pagos e não pagos.

## Uso sugerido

Executar os scripts no banco local antes dos testes manuais da aplicação.