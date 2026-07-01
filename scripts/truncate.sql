-- =============================================================
-- TRUNCATE — Sistema Biblioteca DevOps
-- Remove todos os dados e reinicia as sequências de ID.
-- Execute este script ANTES do seed-dev.sql.
-- =============================================================
-- Ordem: dependentes primeiro; CASCADE garante integridade.
TRUNCATE TABLE debito, item_emprestimo, emprestimo, livro, titulo, aluno
    RESTART IDENTITY CASCADE;
