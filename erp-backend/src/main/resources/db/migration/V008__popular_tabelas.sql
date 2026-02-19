-- =====================================================
-- V003__POPULAR_DADOS_INICIAIS.SQL
-- DESCRIÇÃO: PERFIS, PERMISSÕES E USUÁRIO ADMIN
-- =====================================================

-- =====================================================
-- 1. CRIAR PERFIS DO SISTEMA
-- =====================================================
INSERT INTO PERFIS (NOME_PERFIL, DESCRICAO_PERFIL, SISTEMA) VALUES
                                                                ('ADMIN', 'Administrador - Acesso total ao sistema', TRUE),
                                                                ('GESTOR', 'Gestor - Gerencia operações e visualiza relatórios', TRUE),
                                                                ('FINANCEIRO', 'Financeiro - Gerencia cobranças e faturamento', TRUE),
                                                                ('OPERACIONAL', 'Operacional - Executa OS e atende clientes', TRUE);

-- =====================================================
-- 2. CRIAR PERMISSÕES
-- =====================================================

-- MÓDULO: CLIENTES
INSERT INTO PERMISSOES (MODULO, ACAO, DESCRICAO_PERMISSAO) VALUES
                                                               ('CLIENTES', 'VER', 'Visualizar clientes'),
                                                               ('CLIENTES', 'CRIAR', 'Cadastrar novos clientes'),
                                                               ('CLIENTES', 'EDITAR', 'Editar dados de clientes'),
                                                               ('CLIENTES', 'EXCLUIR', 'Excluir clientes');

-- MÓDULO: CONTRATOS
INSERT INTO PERMISSOES (MODULO, ACAO, DESCRICAO_PERMISSAO) VALUES
                                                               ('CONTRATOS', 'VER', 'Visualizar contratos'),
                                                               ('CONTRATOS', 'CRIAR', 'Criar novos contratos'),
                                                               ('CONTRATOS', 'EDITAR', 'Editar contratos existentes'),
                                                               ('CONTRATOS', 'EXCLUIR', 'Cancelar/excluir contratos');

-- MÓDULO: SERVIÇOS
INSERT INTO PERMISSOES (MODULO, ACAO, DESCRICAO_PERMISSAO) VALUES
                                                               ('SERVICOS', 'VER', 'Visualizar catálogo de serviços'),
                                                               ('SERVICOS', 'CRIAR', 'Criar novos serviços'),
                                                               ('SERVICOS', 'EDITAR', 'Editar serviços'),
                                                               ('SERVICOS', 'EXCLUIR', 'Excluir serviços');

-- MÓDULO: ORDENS DE SERVIÇO
INSERT INTO PERMISSOES (MODULO, ACAO, DESCRICAO_PERMISSAO) VALUES
                                                               ('OS', 'VER', 'Visualizar ordens de serviço'),
                                                               ('OS', 'CRIAR', 'Criar novas OS'),
                                                               ('OS', 'EDITAR', 'Editar e atualizar OS'),
                                                               ('OS', 'EXCLUIR', 'Cancelar OS');

-- MÓDULO: FATURAMENTO
INSERT INTO PERMISSOES (MODULO, ACAO, DESCRICAO_PERMISSAO) VALUES
                                                               ('FATURAMENTO', 'VER', 'Visualizar faturas'),
                                                               ('FATURAMENTO', 'CRIAR', 'Gerar novas faturas'),
                                                               ('FATURAMENTO', 'EDITAR', 'Editar e baixar faturas'),
                                                               ('FATURAMENTO', 'EXCLUIR', 'Cancelar faturas');

-- MÓDULO: USUÁRIOS
INSERT INTO PERMISSOES (MODULO, ACAO, DESCRICAO_PERMISSAO) VALUES
                                                               ('USUARIOS', 'VER', 'Visualizar usuários'),
                                                               ('USUARIOS', 'CRIAR', 'Criar novos usuários'),
                                                               ('USUARIOS', 'EDITAR', 'Editar usuários'),
                                                               ('USUARIOS', 'EXCLUIR', 'Desativar usuários');

-- =====================================================
-- 3. ATRIBUIR PERMISSÕES AOS PERFIS
-- =====================================================

-- ADMIN: TODAS AS PERMISSÕES
INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 1, CODIGO_PERMISSAO FROM PERMISSOES;

-- GESTOR: CRUD EM TUDO EXCETO USUÁRIOS
INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 2, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO IN ('CLIENTES', 'CONTRATOS', 'SERVICOS', 'OS');

INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 2, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO = 'FATURAMENTO' AND ACAO IN ('VER', 'EDITAR');

-- FINANCEIRO: APENAS FATURAMENTO
INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 3, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO = 'FATURAMENTO';

INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 3, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO IN ('CLIENTES', 'CONTRATOS') AND ACAO = 'VER';

-- OPERACIONAL: OS E CLIENTES
INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 4, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO = 'OS';

INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 4, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO = 'CLIENTES' AND ACAO IN ('VER', 'EDITAR');

INSERT INTO PERFIL_PERMISSOES (CODIGO_PERFIL, CODIGO_PERMISSAO)
SELECT 4, CODIGO_PERMISSAO FROM PERMISSOES
WHERE MODULO IN ('CONTRATOS', 'SERVICOS') AND ACAO = 'VER';

-- =====================================================
-- 4. CRIAR USUÁRIO ADMIN PADRÃO
-- SENHA: Admin@123 (BCrypt hash)
-- =====================================================
INSERT INTO USUARIOS (
    NOME_COMPLETO,
    EMAIL,
    SENHA_HASH,
    CODIGO_PERFIL,
    ATIVO
) VALUES (
             'Administrador',
             'admin@zyloerp.com',
             '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5jcBqZq0/YXaS',  -- Admin@123
             1,
             TRUE
         );

-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================
COMMENT ON TABLE PERFIS IS 'PERFIS PADRÃO CRIADOS. USUÁRIO: admin@zyloerp.com | SENHA: Admin@123';