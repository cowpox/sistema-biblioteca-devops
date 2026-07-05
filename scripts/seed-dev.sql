-- =============================================================
-- SEED DE DADOS DE DESENVOLVIMENTO
-- Sistema Biblioteca DevOps — UEL Engenharia de Software
-- Codificação: UTF-8
-- =============================================================
-- Execute truncate.sql ANTES deste arquivo.
--
-- Volumes:  50 alunos · 100 títulos · 300 livros
--           20 empréstimos · 28 itens · 5 débitos
--
-- Cenários disponíveis:
--   • Alunos 1-15  → empréstimos ATIVOS (livros indisponíveis)
--   • Aluno 7      → empréstimo mais antigo (VENCIDO 19 dias)
--   • Alunos 3,5   → empréstimos vencidos (prazo expirado)
--   • Aluno 8      → empréstimo vence hoje (2026-07-01)
--   • Alunos 10-15 → empréstimos dentro do prazo
--   • Aluno 20, 30, 40 → débito pago=false (bloqueados)
--   • Aluno 25, 35     → débito pago=true  (desbloqueados)
--   • Livros pares 2-46 → disponivel=false (emprestados)
--   • Empréstimos 16-20 → ENCERRADOS com histórico de multa
-- =============================================================

-- -------------------------------------------------------
-- 1. ALUNOS (50 registros)
-- -------------------------------------------------------
INSERT INTO aluno (id, matricula, nome, cpf, endereco, email, ativo) VALUES
(1,  '20190001', 'Ana Beatriz Silva',          '00100000001', 'Rua das Flores, 10',     'ana.silva@uel.edu.br',       true),
(2,  '20190002', 'Carlos Eduardo Souza',        '00200000002', 'Av. Paraná, 200',        'carlos.souza@uel.edu.br',    true),
(3,  '20190003', 'Daniela Ferreira Lima',       '00300000003', 'Rua Goiás, 30',          'daniela.lima@uel.edu.br',    true),
(4,  '20190004', 'Eduardo Moreira Costa',       '00400000004', 'Av. Minas Gerais, 400',  'eduardo.costa@uel.edu.br',   true),
(5,  '20190005', 'Fernanda Ribeiro Alves',      '00500000005', 'Rua Bahia, 50',          'fernanda.alves@uel.edu.br',  true),
(6,  '20190006', 'Gabriel Santos Oliveira',     '00600000006', 'Av. Rio de Janeiro, 600','gabriel.oli@uel.edu.br',     true),
(7,  '20190007', 'Helena Carvalho Pereira',     '00700000007', 'Rua Ceará, 70',          'helena.pereira@uel.edu.br',  true),
(8,  '20190008', 'Igor Pimentel Rodrigues',     '00800000008', 'Av. São Paulo, 800',     'igor.rod@uel.edu.br',        true),
(9,  '20190009', 'Juliana Martins Barbosa',     '00900000009', 'Rua Pará, 90',           'juliana.bar@uel.edu.br',     true),
(10, '20190010', 'Kevin Lopes Gomes',           '01000000010', 'Av. Maranhão, 100',      'kevin.gomes@uel.edu.br',     true),
(11, '20190011', 'Laura Nascimento Freitas',    '01100000011', 'Rua Tocantins, 110',     'laura.freitas@uel.edu.br',   true),
(12, '20190012', 'Marcos Vinicius Teixeira',    '01200000012', 'Av. Acre, 120',          'marcos.tei@uel.edu.br',      true),
(13, '20190013', 'Natalia Araujo Correia',      '01300000013', 'Rua Piauí, 130',         'natalia.cor@uel.edu.br',     true),
(14, '20190014', 'Otavio Mendes Cunha',         '01400000014', 'Av. Espírito Santo, 140','otavio.cunha@uel.edu.br',    true),
(15, '20190015', 'Patricia Duarte Moraes',      '01500000015', 'Rua Roraima, 150',       'patricia.mor@uel.edu.br',    true),
(16, '20200001', 'Rafael Costa Machado',        '01600000016', 'Av. Amapá, 160',         'rafael.mac@uel.edu.br',      true),
(17, '20200002', 'Sabrina Nunes Monteiro',      '01700000017', 'Rua Sergipe, 170',       'sabrina.mon@uel.edu.br',     true),
(18, '20200003', 'Tiago Figueiredo Rocha',      '01800000018', 'Av. Alagoas, 180',       'tiago.rocha@uel.edu.br',     true),
(19, '20200004', 'Ubiratan Gomes Neto',         '01900000019', 'Rua Rondônia, 190',      'ubiratan.n@uel.edu.br',      true),
(20, '20200005', 'Vanessa Carvalho Lima',       '02000000020', 'Av. Mato Grosso, 200',   'vanessa.lim@uel.edu.br',     true),
(21, '20200006', 'Wellington Andrade Borges',   '02100000021', 'Rua Amazonas, 210',      'wellington.b@uel.edu.br',    true),
(22, '20200007', 'Ximena Batista Ramos',        '02200000022', 'Av. Pernambuco, 220',    'ximena.ram@uel.edu.br',      true),
(23, '20200008', 'Yasmin Azevedo Pires',        '02300000023', 'Rua Paraíba, 230',       'yasmin.pir@uel.edu.br',      true),
(24, '20200009', 'Zenildo Sousa Rodrigues',     '02400000024', 'Av. Mato Grosso do Sul', 'zenildo.r@uel.edu.br',       true),
(25, '20200010', 'Amanda Cristina Ferraz',      '02500000025', 'Rua Rio Grande Norte, 2','amanda.fer@uel.edu.br',      true),
(26, '20200011', 'Bruno Henrique Cavalcanti',   '02600000026', 'Av. Santa Catarina, 260','bruno.cav@uel.edu.br',       true),
(27, '20200012', 'Camila Aparecida Vieira',     '02700000027', 'Rua Rio Grande Sul, 270','camila.vie@uel.edu.br',      true),
(28, '20200013', 'Diego Renato Campos',         '02800000028', 'Av. Paraíba, 280',       'diego.cam@uel.edu.br',       true),
(29, '20200014', 'Eliane Cristina Salgado',     '02900000029', 'Rua Goiás, 290',         'eliane.sal@uel.edu.br',      true),
(30, '20200015', 'Fabio Augusto Miranda',       '03000000030', 'Av. Minas Gerais, 300',  'fabio.mir@uel.edu.br',       true),
(31, '20210001', 'Giovana Luiza Guimarães',     '03100000031', 'Rua São Paulo, 310',     'giovana.gui@uel.edu.br',     true),
(32, '20210002', 'Henrique Augusto Souza',      '03200000032', 'Av. Rio de Janeiro, 320','henrique.s@uel.edu.br',      true),
(33, '20210003', 'Isabela Leticia Pinto',       '03300000033', 'Rua Bahia, 330',         'isabela.pin@uel.edu.br',     true),
(34, '20210004', 'Joao Victor Salave',          '03400000034', 'Av. Ceará, 340',         'joao.sal@uel.edu.br',        true),
(35, '20210005', 'Kelly Priscila Almeida',      '03500000035', 'Rua Pará, 350',          'kelly.alm@uel.edu.br',       true),
(36, '20210006', 'Lucas Gustavo Ribeiro',       '03600000036', 'Av. Paraná, 360',        'lucas.rib@uel.edu.br',       true),
(37, '20210007', 'Mariana Brito Carvalho',      '03700000037', 'Rua Tocantins, 370',     'mariana.car@uel.edu.br',     true),
(38, '20210008', 'Nelson Augusto Bezerra',      '03800000038', 'Av. Maranhão, 380',      'nelson.bez@uel.edu.br',      true),
(39, '20210009', 'Olivia Fernanda Tavares',     '03900000039', 'Rua Acre, 390',          'olivia.tav@uel.edu.br',      true),
(40, '20210010', 'Paulo Cesar Medeiros',        '04000000040', 'Av. Piauí, 400',         'paulo.med@uel.edu.br',       true),
(41, '20220001', 'Quezia Renata Santana',       '04100000041', 'Rua Sergipe, 410',       'quezia.san@uel.edu.br',      true),
(42, '20220002', 'Roberto Carlos Fonseca',      '04200000042', 'Av. Alagoas, 420',       'roberto.fon@uel.edu.br',     true),
(43, '20220003', 'Simone Aparecida Queiroz',    '04300000043', 'Rua Rondônia, 430',      'simone.que@uel.edu.br',      true),
(44, '20220004', 'Thiago de Oliveira Cruz',     '04400000044', 'Av. Amazonas, 440',      'thiago.cruz@uel.edu.br',     true),
(45, '20220005', 'Ursula Marina Valente',       '04500000045', 'Rua Amapá, 450',         'ursula.val@uel.edu.br',      true),
(46, '20220006', 'Victor Hugo Lacerda',         '04600000046', 'Av. Roraima, 460',       'victor.lac@uel.edu.br',      true),
(47, '20220007', 'Walkiria Sousa Torres',       '04700000047', 'Rua Mato Grosso, 470',   'walkiria.tor@uel.edu.br',    true),
(48, '20230001', 'Xisto Leal Muniz',            '04800000048', 'Av. Espirito Santo, 480','xisto.mun@uel.edu.br',       true),
(49, '20230002', 'Yasmin Cordeiro Melo',        '04900000049', 'Rua Santa Catarina, 490','yasmin.mel@uel.edu.br',      true),
(50, '20230003', 'Zenaide Costa Paiva',         '05000000050', 'Av. Rio Grande Norte, 5','zenaide.pai@uel.edu.br',     true);

-- -------------------------------------------------------
-- 2. TÍTULOS (100 registros)
-- Prazos: IDs 1-33 = 7 dias · 34-66 = 14 dias · 67-100 = 21 dias
-- ISBNs: 978 + 10 dígitos sequenciais (fictícios)
-- -------------------------------------------------------
INSERT INTO titulo (id, nome, isbn, prazo, autor, editora, edicao, ano) VALUES
-- Prazo 7 dias (IDs 1-33) — Engenharia de Software e Fundamentos
(1,  'Engenharia de Software Orientada a Objetos', '9780000000001', 7,  'André Menolli',        'Pearson',          '1ª', '2020'),
(2,  'Código Limpo',                               '9780000000002', 7,  'Robert C. Martin',     'Alta Books',       '1ª', '2019'),
(3,  'O Programador Pragmático',                   '9780000000003', 7,  'David Thomas',         'Bookman',          '2ª', '2019'),
(4,  'Refatoração',                                '9780000000004', 7,  'Martin Fowler',        'Novatec',          '2ª', '2020'),
(5,  'Padrões de Projeto',                         '9780000000005', 7,  'Erich Gamma et al.',   'Bookman',          '1ª', '2000'),
(6,  'Arquitetura Limpa',                          '9780000000006', 7,  'Robert C. Martin',     'Alta Books',       '1ª', '2019'),
(7,  'Domain-Driven Design',                       '9780000000007', 7,  'Eric Evans',           'Campus',           '1ª', '2016'),
(8,  'O Mítico Homem-Mês',                         '9780000000008', 7,  'Frederick Brooks',     'Addison-Wesley',   '2ª', '1995'),
(9,  'Effective Java',                             '9780000000009', 7,  'Joshua Bloch',         'Pearson',          '3ª', '2018'),
(10, 'UML Essencial',                              '9780000000010', 7,  'Martin Fowler',        'Bookman',          '3ª', '2005'),
(11, 'Algoritmos: Teoria e Prática',               '9780000000011', 7,  'Thomas H. Cormen',     'Campus',           '3ª', '2012'),
(12, 'Estruturas de Dados em Java',                '9780000000012', 7,  'Michael Goodrich',     'Bookman',          '2ª', '2013'),
(13, 'Algoritmos em Java',                         '9780000000013', 7,  'Robert Sedgewick',     'Bookman',          '3ª', '2011'),
(14, 'Fundamentos de Algoritmos',                  '9780000000014', 7,  'Nivio Ziviani',        'Cengage',          '3ª', '2010'),
(15, 'Matemática Discreta',                        '9780000000015', 7,  'Kenneth Rosen',        'McGraw-Hill',      '7ª', '2009'),
(16, 'Teoria da Computação',                       '9780000000016', 7,  'Michael Sipser',       'Cengage',          '3ª', '2007'),
(17, 'Compiladores: Princípios e Práticas',        '9780000000017', 7,  'Alfred Aho et al.',    'Pearson',          '2ª', '2008'),
(18, 'Sistemas Operacionais Modernos',             '9780000000018', 7,  'Andrew Tanenbaum',     'Pearson',          '4ª', '2016'),
(19, 'Redes de Computadores',                      '9780000000019', 7,  'Andrew Tanenbaum',     'Pearson',          '5ª', '2011'),
(20, 'Banco de Dados: Introdução',                 '9780000000020', 7,  'Ramez Elmasri',        'Pearson',          '6ª', '2011'),
(21, 'Programação em Java',                        '9780000000021', 7,  'Deitel & Deitel',      'Pearson',          '10ª','2017'),
(22, 'Spring Boot na Prática',                     '9780000000022', 7,  'Craig Walls',          'Novatec',          '2ª', '2019'),
(23, 'Microsserviços em Ação',                     '9780000000023', 7,  'Sam Newman',           'Novatec',          '2ª', '2019'),
(24, 'Docker: Contêineres na Prática',             '9780000000024', 7,  'Karl Matthias',        'Novatec',          '2ª', '2018'),
(25, 'Git: Controle de Versão Distribuído',        '9780000000025', 7,  'Scott Chacon',         'Novatec',          '2ª', '2014'),
(26, 'Segurança em Redes',                         '9780000000026', 7,  'Stallings & Brown',    'Pearson',          '2ª', '2014'),
(27, 'DevOps Handbook',                            '9780000000027', 7,  'Gene Kim et al.',      'IT Revolution',    '1ª', '2016'),
(28, 'Scrum: A Arte Ágil de Fazer o Dobro',        '9780000000028', 7,  'Jeff Sutherland',      'Campus',           '1ª', '2016'),
(29, 'Lean Software Development',                  '9780000000029', 7,  'Mary Poppendieck',     'Addison-Wesley',   '1ª', '2003'),
(30, 'Engenharia de Requisitos',                   '9780000000030', 7,  'Ian Sommerville',      'Pearson',          '9ª', '2011'),
(31, 'Cálculo Vol. 1',                             '9780000000031', 7,  'James Stewart',        'Cengage',          '7ª', '2013'),
(32, 'Cálculo Vol. 2',                             '9780000000032', 7,  'James Stewart',        'Cengage',          '7ª', '2013'),
(33, 'Álgebra Linear',                             '9780000000033', 7,  'Gilbert Strang',       'LTC',              '4ª', '2013'),
-- Prazo 14 dias (IDs 34-66) — Ciências Exatas e Literatura
(34, 'Probabilidade e Estatística',                '9780000000034', 14, 'Walpole et al.',       'Pearson',          '9ª', '2012'),
(35, 'Pesquisa Operacional',                       '9780000000035', 14, 'Frederick Hillier',    'McGraw-Hill',      '10ª','2013'),
(36, 'Física para Engenharia e Ciência',           '9780000000036', 14, 'Halliday et al.',      'LTC',              '10ª','2016'),
(37, 'Inteligência Artificial',                    '9780000000037', 14, 'Russell & Norvig',     'Campus',           '3ª', '2013'),
(38, 'Aprendizado de Máquina',                     '9780000000038', 14, 'Tom Mitchell',         'McGraw-Hill',      '1ª', '1997'),
(39, 'Deep Learning',                              '9780000000039', 14, 'Goodfellow et al.',    'MIT Press',        '1ª', '2016'),
(40, 'Visão Computacional',                        '9780000000040', 14, 'Richard Szeliski',     'Springer',         '2ª', '2022'),
(41, 'Memórias Póstumas de Brás Cubas',            '9780000000041', 14, 'Machado de Assis',     'Penguin',          '1ª', '2012'),
(42, 'Dom Casmurro',                               '9780000000042', 14, 'Machado de Assis',     'Martin Claret',    '1ª', '2002'),
(43, 'Quincas Borba',                              '9780000000043', 14, 'Machado de Assis',     'Martin Claret',    '1ª', '2003'),
(44, 'Iracema',                                    '9780000000044', 14, 'José de Alencar',      'Ática',            '1ª', '2004'),
(45, 'O Guarani',                                  '9780000000045', 14, 'José de Alencar',      'Saraiva',          '1ª', '2005'),
(46, 'Vidas Secas',                                '9780000000046', 14, 'Graciliano Ramos',     'Record',           '1ª', '1938'),
(47, 'São Bernardo',                               '9780000000047', 14, 'Graciliano Ramos',     'Record',           '1ª', '1934'),
(48, 'Grande Sertão: Veredas',                     '9780000000048', 14, 'Guimarães Rosa',       'Nova Fronteira',   '1ª', '1956'),
(49, 'Sagarana',                                   '9780000000049', 14, 'Guimarães Rosa',       'Nova Fronteira',   '1ª', '1946'),
(50, 'Capitães da Areia',                          '9780000000050', 14, 'Jorge Amado',          'Record',           '1ª', '1937'),
(51, 'A Hora da Estrela',                          '9780000000051', 14, 'Clarice Lispector',    'Rocco',            '1ª', '1977'),
(52, 'Perto do Coração Selvagem',                  '9780000000052', 14, 'Clarice Lispector',    'Rocco',            '1ª', '1944'),
(53, 'O Cortiço',                                  '9780000000053', 14, 'Aluísio Azevedo',      'Ática',            '1ª', '1890'),
(54, 'Macunaíma',                                  '9780000000054', 14, 'Mário de Andrade',     'Garnier',          '1ª', '1928'),
(55, 'Morte e Vida Severina',                      '9780000000055', 14, 'João Cabral de Melo',  'José Olympio',     '1ª', '1956'),
(56, 'Fogo Morto',                                 '9780000000056', 14, 'José Lins do Rego',    'José Olympio',     '1ª', '1943'),
(57, 'Menino de Engenho',                          '9780000000057', 14, 'José Lins do Rego',    'José Olympio',     '1ª', '1932'),
(58, 'O Ateneu',                                   '9780000000058', 14, 'Raul Pompéia',         'Ática',            '1ª', '1888'),
(59, 'Iaiá Garcia',                                '9780000000059', 14, 'Machado de Assis',     'Martin Claret',    '1ª', '2003'),
(60, 'Senhora',                                    '9780000000060', 14, 'José de Alencar',      'Saraiva',          '1ª', '2006'),
(61, 'Banco de Dados Avançado',                    '9780000000061', 14, 'Ramez Elmasri',        'Pearson',          '7ª', '2019'),
(62, 'PostgreSQL: Administração',                  '9780000000062', 14, 'Ants Aasma',           'Packt',            '1ª', '2021'),
(63, 'MySQL: O Guia Essencial',                    '9780000000063', 14, 'Paul Dubois',          'Novatec',          '5ª', '2014'),
(64, 'MongoDB: Aplicações NoSQL',                  '9780000000064', 14, 'Kyle Banker',          'Novatec',          '2ª', '2016'),
(65, 'Redis em Ação',                              '9780000000065', 14, 'Josiah Carlson',       'Manning',          '1ª', '2013'),
(66, 'Elasticsearch: Busca Avançada',              '9780000000066', 14, 'Clinton Gormley',      'Novatec',          '1ª', '2015'),
-- Prazo 21 dias (IDs 67-100) — Cloud, Web, Data Science
(67, 'Hadoop: O Guia Definitivo',                  '9780000000067', 21, 'Tom White',            'Novatec',          '4ª', '2015'),
(68, 'Apache Kafka: O Guia',                       '9780000000068', 21, 'Neha Narkhede',        'Novatec',          '2ª', '2018'),
(69, 'Big Data: Como Usar a Análise',              '9780000000069', 21, 'Viktor Mayer',         'Campus',           '1ª', '2013'),
(70, 'ETL e Data Warehouse',                       '9780000000070', 21, 'Ralph Kimball',        'Campus',           '4ª', '2013'),
(71, 'Kubernetes em Ação',                         '9780000000071', 21, 'Marko Luksa',          'Manning',          '1ª', '2018'),
(72, 'AWS: Guia do Desenvolvedor',                 '9780000000072', 21, 'Ben Whaley',           'Novatec',          '2ª', '2019'),
(73, 'Terraform: Infrastructure as Code',          '9780000000073', 21, 'Yevgeniy Brikman',     'Novatec',          '2ª', '2019'),
(74, 'CI/CD na Prática',                           '9780000000074', 21, 'Michiel Rook',         'Manning',          '1ª', '2020'),
(75, 'Ansible: Automação de TI',                   '9780000000075', 21, 'Lorin Hochstein',      'Novatec',          '2ª', '2018'),
(76, 'Linux: Guia do Usuário',                     '9780000000076', 21, 'Rubem Quint',          'Alta Books',       '5ª', '2020'),
(77, 'Shell Script Pro',                           '9780000000077', 21, 'Julio Cezar Neves',    'Novatec',          '3ª', '2017'),
(78, 'Python: Automatize Tarefas',                 '9780000000078', 21, 'Al Sweigart',          'Novatec',          '2ª', '2019'),
(79, 'Go em Ação',                                 '9780000000079', 21, 'William Kennedy',      'Manning',          '1ª', '2016'),
(80, 'Rust: Linguagem de Sistemas',                '9780000000080', 21, 'Steve Klabnik',        'No Starch',        '1ª', '2019'),
(81, 'React: Up and Running',                      '9780000000081', 21, 'Stoyan Stefanov',      'Novatec',          '2ª', '2022'),
(82, 'Angular: Guia Completo',                     '9780000000082', 21, 'Yakov Fain',           'Manning',          '2ª', '2021'),
(83, 'Vue.js: Construindo UIs',                    '9780000000083', 21, 'Callum Macrae',        'Packt',            '2ª', '2020'),
(84, 'Node.js: Desenvolvimento Web',               '9780000000084', 21, 'Shelley Powers',       'Novatec',          '2ª', '2018'),
(85, 'HTML e CSS: Design e Construção',            '9780000000085', 21, 'Jon Duckett',          'Alta Books',       '1ª', '2014'),
(86, 'JavaScript Definitivo',                      '9780000000086', 21, 'David Flanagan',       'Novatec',          '7ª', '2021'),
(87, 'TypeScript: Guia Avançado',                  '9780000000087', 21, 'Nathan Rozentals',     'Packt',            '4ª', '2021'),
(88, 'Next.js em Produção',                        '9780000000088', 21, 'Balthazar Neveux',     'Novatec',          '1ª', '2023'),
(89, 'GraphQL: Guia Completo',                     '9780000000089', 21, 'Eve Porcello',         'Novatec',          '1ª', '2020'),
(90, 'REST APIs com Spring',                       '9780000000090', 21, 'Leonard Richardson',   'Novatec',          '2ª', '2013'),
(91, 'Python para Análise de Dados',               '9780000000091', 21, 'Wes McKinney',         'Novatec',          '3ª', '2019'),
(92, 'Aprendizado Profundo',                       '9780000000092', 21, 'Ian Goodfellow',       'Bookman',          '1ª', '2016'),
(93, 'Machine Learning com Python',                '9780000000093', 21, 'Jake VanderPlas',      'Novatec',          '2ª', '2017'),
(94, 'TensorFlow 2 em Ação',                       '9780000000094', 21, 'Aurélien Géron',       'Novatec',          '3ª', '2020'),
(95, 'PyTorch: Redes Neurais na Prática',          '9780000000095', 21, 'Eli Stevens',          'Manning',          '1ª', '2020'),
(96, 'Análise de Dados com R',                     '9780000000096', 21, 'Hadley Wickham',       'Novatec',          '2ª', '2016'),
(97, 'Estatística Computacional',                  '9780000000097', 21, 'Larry Wasserman',      'Springer',         '1ª', '2004'),
(98, 'Mineração de Dados',                         '9780000000098', 21, 'Jiawei Han',           'Morgan Kaufmann',  '3ª', '2011'),
(99, 'Business Intelligence na Prática',           '9780000000099', 21, 'Maurício Rissetti',    'Bookman',          '2ª', '2016'),
(100,'Governança de Dados',                        '9780000000100', 21, 'John Ladley',          'Morgan Kaufmann',  '2ª', '2019');

-- -------------------------------------------------------
-- 3. LIVROS (300 exemplares)
-- Colunas: id, codigo_patrimonio, disponivel, exemplar_biblioteca, titulo_id
-- Distribuição:
--   titulos  1-30:  2 exemplares cada (livros   1-60)
--   titulos 31-70:  3 exemplares cada (livros  61-180)
--   titulos 71-100: 4 exemplares cada (livros 181-300)
-- Regra: 1º exemplar de cada título = exemplar_biblioteca=true
-- disponivel=true para todos; UPDATE ao final marca os emprestados.
-- -------------------------------------------------------
INSERT INTO livro (id, codigo_patrimonio, disponivel, exemplar_biblioteca, titulo_id) VALUES
-- Títulos 1-30 (2 exemplares/título, prazo 7 dias)
(1,'000001',true,true,1),(2,'000002',true,false,1),
(3,'000003',true,true,2),(4,'000004',true,false,2),
(5,'000005',true,true,3),(6,'000006',true,false,3),
(7,'000007',true,true,4),(8,'000008',true,false,4),
(9,'000009',true,true,5),(10,'000010',true,false,5),
(11,'000011',true,true,6),(12,'000012',true,false,6),
(13,'000013',true,true,7),(14,'000014',true,false,7),
(15,'000015',true,true,8),(16,'000016',true,false,8),
(17,'000017',true,true,9),(18,'000018',true,false,9),
(19,'000019',true,true,10),(20,'000020',true,false,10),
(21,'000021',true,true,11),(22,'000022',true,false,11),
(23,'000023',true,true,12),(24,'000024',true,false,12),
(25,'000025',true,true,13),(26,'000026',true,false,13),
(27,'000027',true,true,14),(28,'000028',true,false,14),
(29,'000029',true,true,15),(30,'000030',true,false,15),
(31,'000031',true,true,16),(32,'000032',true,false,16),
(33,'000033',true,true,17),(34,'000034',true,false,17),
(35,'000035',true,true,18),(36,'000036',true,false,18),
(37,'000037',true,true,19),(38,'000038',true,false,19),
(39,'000039',true,true,20),(40,'000040',true,false,20),
(41,'000041',true,true,21),(42,'000042',true,false,21),
(43,'000043',true,true,22),(44,'000044',true,false,22),
(45,'000045',true,true,23),(46,'000046',true,false,23),
(47,'000047',true,true,24),(48,'000048',true,false,24),
(49,'000049',true,true,25),(50,'000050',true,false,25),
(51,'000051',true,true,26),(52,'000052',true,false,26),
(53,'000053',true,true,27),(54,'000054',true,false,27),
(55,'000055',true,true,28),(56,'000056',true,false,28),
(57,'000057',true,true,29),(58,'000058',true,false,29),
(59,'000059',true,true,30),(60,'000060',true,false,30),
-- Títulos 31-70 (3 exemplares/título, prazo 14 dias)
(61,'000061',true,true,31),(62,'000062',true,false,31),(63,'000063',true,false,31),
(64,'000064',true,true,32),(65,'000065',true,false,32),(66,'000066',true,false,32),
(67,'000067',true,true,33),(68,'000068',true,false,33),(69,'000069',true,false,33),
(70,'000070',true,true,34),(71,'000071',true,false,34),(72,'000072',true,false,34),
(73,'000073',true,true,35),(74,'000074',true,false,35),(75,'000075',true,false,35),
(76,'000076',true,true,36),(77,'000077',true,false,36),(78,'000078',true,false,36),
(79,'000079',true,true,37),(80,'000080',true,false,37),(81,'000081',true,false,37),
(82,'000082',true,true,38),(83,'000083',true,false,38),(84,'000084',true,false,38),
(85,'000085',true,true,39),(86,'000086',true,false,39),(87,'000087',true,false,39),
(88,'000088',true,true,40),(89,'000089',true,false,40),(90,'000090',true,false,40),
(91,'000091',true,true,41),(92,'000092',true,false,41),(93,'000093',true,false,41),
(94,'000094',true,true,42),(95,'000095',true,false,42),(96,'000096',true,false,42),
(97,'000097',true,true,43),(98,'000098',true,false,43),(99,'000099',true,false,43),
(100,'000100',true,true,44),(101,'000101',true,false,44),(102,'000102',true,false,44),
(103,'000103',true,true,45),(104,'000104',true,false,45),(105,'000105',true,false,45),
(106,'000106',true,true,46),(107,'000107',true,false,46),(108,'000108',true,false,46),
(109,'000109',true,true,47),(110,'000110',true,false,47),(111,'000111',true,false,47),
(112,'000112',true,true,48),(113,'000113',true,false,48),(114,'000114',true,false,48),
(115,'000115',true,true,49),(116,'000116',true,false,49),(117,'000117',true,false,49),
(118,'000118',true,true,50),(119,'000119',true,false,50),(120,'000120',true,false,50),
(121,'000121',true,true,51),(122,'000122',true,false,51),(123,'000123',true,false,51),
(124,'000124',true,true,52),(125,'000125',true,false,52),(126,'000126',true,false,52),
(127,'000127',true,true,53),(128,'000128',true,false,53),(129,'000129',true,false,53),
(130,'000130',true,true,54),(131,'000131',true,false,54),(132,'000132',true,false,54),
(133,'000133',true,true,55),(134,'000134',true,false,55),(135,'000135',true,false,55),
(136,'000136',true,true,56),(137,'000137',true,false,56),(138,'000138',true,false,56),
(139,'000139',true,true,57),(140,'000140',true,false,57),(141,'000141',true,false,57),
(142,'000142',true,true,58),(143,'000143',true,false,58),(144,'000144',true,false,58),
(145,'000145',true,true,59),(146,'000146',true,false,59),(147,'000147',true,false,59),
(148,'000148',true,true,60),(149,'000149',true,false,60),(150,'000150',true,false,60),
(151,'000151',true,true,61),(152,'000152',true,false,61),(153,'000153',true,false,61),
(154,'000154',true,true,62),(155,'000155',true,false,62),(156,'000156',true,false,62),
(157,'000157',true,true,63),(158,'000158',true,false,63),(159,'000159',true,false,63),
(160,'000160',true,true,64),(161,'000161',true,false,64),(162,'000162',true,false,64),
(163,'000163',true,true,65),(164,'000164',true,false,65),(165,'000165',true,false,65),
(166,'000166',true,true,66),(167,'000167',true,false,66),(168,'000168',true,false,66),
(169,'000169',true,true,67),(170,'000170',true,false,67),(171,'000171',true,false,67),
(172,'000172',true,true,68),(173,'000173',true,false,68),(174,'000174',true,false,68),
(175,'000175',true,true,69),(176,'000176',true,false,69),(177,'000177',true,false,69),
(178,'000178',true,true,70),(179,'000179',true,false,70),(180,'000180',true,false,70),
-- Títulos 71-100 (4 exemplares/título, prazo 21 dias)
(181,'000181',true,true,71),(182,'000182',true,false,71),(183,'000183',true,false,71),(184,'000184',true,false,71),
(185,'000185',true,true,72),(186,'000186',true,false,72),(187,'000187',true,false,72),(188,'000188',true,false,72),
(189,'000189',true,true,73),(190,'000190',true,false,73),(191,'000191',true,false,73),(192,'000192',true,false,73),
(193,'000193',true,true,74),(194,'000194',true,false,74),(195,'000195',true,false,74),(196,'000196',true,false,74),
(197,'000197',true,true,75),(198,'000198',true,false,75),(199,'000199',true,false,75),(200,'000200',true,false,75),
(201,'000201',true,true,76),(202,'000202',true,false,76),(203,'000203',true,false,76),(204,'000204',true,false,76),
(205,'000205',true,true,77),(206,'000206',true,false,77),(207,'000207',true,false,77),(208,'000208',true,false,77),
(209,'000209',true,true,78),(210,'000210',true,false,78),(211,'000211',true,false,78),(212,'000212',true,false,78),
(213,'000213',true,true,79),(214,'000214',true,false,79),(215,'000215',true,false,79),(216,'000216',true,false,79),
(217,'000217',true,true,80),(218,'000218',true,false,80),(219,'000219',true,false,80),(220,'000220',true,false,80),
(221,'000221',true,true,81),(222,'000222',true,false,81),(223,'000223',true,false,81),(224,'000224',true,false,81),
(225,'000225',true,true,82),(226,'000226',true,false,82),(227,'000227',true,false,82),(228,'000228',true,false,82),
(229,'000229',true,true,83),(230,'000230',true,false,83),(231,'000231',true,false,83),(232,'000232',true,false,83),
(233,'000233',true,true,84),(234,'000234',true,false,84),(235,'000235',true,false,84),(236,'000236',true,false,84),
(237,'000237',true,true,85),(238,'000238',true,false,85),(239,'000239',true,false,85),(240,'000240',true,false,85),
(241,'000241',true,true,86),(242,'000242',true,false,86),(243,'000243',true,false,86),(244,'000244',true,false,86),
(245,'000245',true,true,87),(246,'000246',true,false,87),(247,'000247',true,false,87),(248,'000248',true,false,87),
(249,'000249',true,true,88),(250,'000250',true,false,88),(251,'000251',true,false,88),(252,'000252',true,false,88),
(253,'000253',true,true,89),(254,'000254',true,false,89),(255,'000255',true,false,89),(256,'000256',true,false,89),
(257,'000257',true,true,90),(258,'000258',true,false,90),(259,'000259',true,false,90),(260,'000260',true,false,90),
(261,'000261',true,true,91),(262,'000262',true,false,91),(263,'000263',true,false,91),(264,'000264',true,false,91),
(265,'000265',true,true,92),(266,'000266',true,false,92),(267,'000267',true,false,92),(268,'000268',true,false,92),
(269,'000269',true,true,93),(270,'000270',true,false,93),(271,'000271',true,false,93),(272,'000272',true,false,93),
(273,'000273',true,true,94),(274,'000274',true,false,94),(275,'000275',true,false,94),(276,'000276',true,false,94),
(277,'000277',true,true,95),(278,'000278',true,false,95),(279,'000279',true,false,95),(280,'000280',true,false,95),
(281,'000281',true,true,96),(282,'000282',true,false,96),(283,'000283',true,false,96),(284,'000284',true,false,96),
(285,'000285',true,true,97),(286,'000286',true,false,97),(287,'000287',true,false,97),(288,'000288',true,false,97),
(289,'000289',true,true,98),(290,'000290',true,false,98),(291,'000291',true,false,98),(292,'000292',true,false,98),
(293,'000293',true,true,99),(294,'000294',true,false,99),(295,'000295',true,false,99),(296,'000296',true,false,99),
(297,'000297',true,true,100),(298,'000298',true,false,100),(299,'000299',true,false,100),(300,'000300',true,false,100);

-- -------------------------------------------------------
-- 4. EMPRÉSTIMOS (20 registros)
-- IDs 1-15: ATIVO | IDs 16-20: ENCERRADO
-- Colunas: id, aluno_id, data_emprestimo, data_prevista_devolucao, data_devolucao, status
-- Todos os livros são de títulos com prazo=7 dias.
-- dataPrevistaDevolucao = dataEmprestimo + 7 + max(0,(nLivros-2)*2)
-- -------------------------------------------------------
INSERT INTO emprestimo (id, aluno_id, data_emprestimo, data_prevista_devolucao, data_devolucao, status) VALUES
-- Empréstimos ATIVOS (data_devolucao=NULL)
(1,  1,  '2026-06-15', '2026-06-22', NULL, 'ATIVO'),   -- 2 livros (prazo 7d) — VENCIDO há 9 dias
(2,  2,  '2026-06-18', '2026-06-25', NULL, 'ATIVO'),   -- 1 livro             — VENCIDO há 6 dias
(3,  3,  '2026-06-10', '2026-06-17', NULL, 'ATIVO'),   -- 2 livros            — VENCIDO há 14 dias
(4,  4,  '2026-06-20', '2026-06-27', NULL, 'ATIVO'),   -- 1 livro             — VENCIDO há 4 dias
(5,  5,  '2026-06-12', '2026-06-19', NULL, 'ATIVO'),   -- 2 livros            — VENCIDO há 12 dias
(6,  6,  '2026-06-22', '2026-06-29', NULL, 'ATIVO'),   -- 1 livro             — VENCIDO há 2 dias
(7,  7,  '2026-06-05', '2026-06-12', NULL, 'ATIVO'),   -- 2 livros            — VENCIDO há 19 dias
(8,  8,  '2026-06-24', '2026-07-01', NULL, 'ATIVO'),   -- 1 livro             — VENCE HOJE
(9,  9,  '2026-06-17', '2026-06-24', NULL, 'ATIVO'),   -- 2 livros            — VENCIDO há 7 dias
(10, 10, '2026-06-25', '2026-07-02', NULL, 'ATIVO'),   -- 1 livro             — dentro do prazo
(11, 11, '2026-06-28', '2026-07-05', NULL, 'ATIVO'),   -- 2 livros            — dentro do prazo
(12, 12, '2026-06-20', '2026-06-27', NULL, 'ATIVO'),   -- 1 livro             — VENCIDO há 4 dias
(13, 13, '2026-06-29', '2026-07-06', NULL, 'ATIVO'),   -- 2 livros            — dentro do prazo
(14, 14, '2026-06-30', '2026-07-07', NULL, 'ATIVO'),   -- 1 livro             — dentro do prazo
(15, 15, '2026-06-25', '2026-07-02', NULL, 'ATIVO'),   -- 2 livros            — dentro do prazo
-- Empréstimos ENCERRADOS (geraram débito por atraso)
(16, 20, '2026-06-01', '2026-06-08', '2026-06-11', 'ENCERRADO'),  -- 3 dias atraso → débito R$3,00
(17, 25, '2026-05-20', '2026-05-27', '2026-06-03', 'ENCERRADO'),  -- 7 dias atraso → débito R$7,00
(18, 30, '2026-05-10', '2026-05-17', '2026-05-19', 'ENCERRADO'),  -- 2 dias atraso → débito R$2,00
(19, 35, '2026-05-01', '2026-05-08', '2026-05-18', 'ENCERRADO'),  -- 10 dias atraso → débito R$10,00
(20, 40, '2026-04-15', '2026-04-22', '2026-04-25', 'ENCERRADO');  -- 3 dias atraso → débito R$3,00

-- -------------------------------------------------------
-- 5. ITENS DE EMPRÉSTIMO (28 registros)
-- Colunas: id, emprestimo_id, livro_id, data_prevista_devolucao, data_devolucao
-- Itens ativos (emp 1-15): data_devolucao=NULL, livro=exemplar emprestável (par)
-- Itens encerrados (emp 16-20): data_devolucao preenchida
-- -------------------------------------------------------
INSERT INTO item_emprestimo (id, emprestimo_id, livro_id, data_prevista_devolucao, data_devolucao) VALUES
-- Itens dos empréstimos ATIVOS
(1,  1,  2,  '2026-06-22', NULL),   -- emp 1, livro '000002' (Eng. SW OO, 2º ex.)
(2,  1,  4,  '2026-06-22', NULL),   -- emp 1, livro '000004' (Código Limpo, 2º ex.)
(3,  2,  6,  '2026-06-25', NULL),   -- emp 2, livro '000006' (Programador Pragmático)
(4,  3,  8,  '2026-06-17', NULL),   -- emp 3, livro '000008' (Refatoração)
(5,  3,  10, '2026-06-17', NULL),   -- emp 3, livro '000010' (Padrões de Projeto)
(6,  4,  12, '2026-06-27', NULL),   -- emp 4, livro '000012' (Arquitetura Limpa)
(7,  5,  14, '2026-06-19', NULL),   -- emp 5, livro '000014' (DDD)
(8,  5,  16, '2026-06-19', NULL),   -- emp 5, livro '000016' (O Mítico Homem-Mês)
(9,  6,  18, '2026-06-29', NULL),   -- emp 6, livro '000018' (Effective Java)
(10, 7,  20, '2026-06-12', NULL),   -- emp 7, livro '000020' (UML Essencial)
(11, 7,  22, '2026-06-12', NULL),   -- emp 7, livro '000022' (Algoritmos CLRS)
(12, 8,  24, '2026-07-01', NULL),   -- emp 8, livro '000024' (Estruturas de Dados)
(13, 9,  26, '2026-06-24', NULL),   -- emp 9, livro '000026' (Algoritmos em Java)
(14, 9,  28, '2026-06-24', NULL),   -- emp 9, livro '000028' (Fund. de Algoritmos)
(15, 10, 30, '2026-07-02', NULL),   -- emp 10, livro '000030' (Matemática Discreta)
(16, 11, 32, '2026-07-05', NULL),   -- emp 11, livro '000032' (Teoria da Computação)
(17, 11, 34, '2026-07-05', NULL),   -- emp 11, livro '000034' (Compiladores)
(18, 12, 36, '2026-06-27', NULL),   -- emp 12, livro '000036' (Sistemas Operacionais)
(19, 13, 38, '2026-07-06', NULL),   -- emp 13, livro '000038' (Redes de Computadores)
(20, 13, 40, '2026-07-06', NULL),   -- emp 13, livro '000040' (Banco de Dados: Intro)
(21, 14, 42, '2026-07-07', NULL),   -- emp 14, livro '000042' (Programação em Java)
(22, 15, 44, '2026-07-02', NULL),   -- emp 15, livro '000044' (Spring Boot na Prática)
(23, 15, 46, '2026-07-02', NULL),   -- emp 15, livro '000046' (Microsserviços em Ação)
-- Itens dos empréstimos ENCERRADOS (data_devolucao preenchida)
(24, 16, 48, '2026-06-08', '2026-06-11'),  -- Docker (2º ex.) — 3 dias atraso
(25, 17, 50, '2026-05-27', '2026-06-03'),  -- Git (2º ex.) — 7 dias atraso
(26, 18, 52, '2026-05-17', '2026-05-19'),  -- Segurança em Redes (2º ex.) — 2 dias atraso
(27, 19, 54, '2026-05-08', '2026-05-18'),  -- DevOps Handbook (2º ex.) — 10 dias atraso
(28, 20, 56, '2026-04-22', '2026-04-25');  -- Scrum (2º ex.) — 3 dias atraso

-- -------------------------------------------------------
-- 6. MARCAR LIVROS EMPRESTADOS COMO INDISPONÍVEIS
-- Os 23 livros com item_emprestimo ativo (data_devolucao NULL)
-- são os exemplares pares dos títulos 1-23.
-- -------------------------------------------------------
UPDATE livro
   SET disponivel = false
 WHERE id IN (2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46);

-- -------------------------------------------------------
-- 7. DÉBITOS (5 registros)
-- Colunas: id, aluno_id, emprestimo_id, valor, data_geracao, pago
-- Vinculados aos 5 empréstimos ENCERRADOS com atraso.
-- Multa: R$1,00/dia (conforme EmprestimoService.MULTA_POR_DIA).
-- -------------------------------------------------------
INSERT INTO debito (id, aluno_id, emprestimo_id, valor, data_geracao, pago) VALUES
(1, 20, 16, 3.00,  '2026-06-11', false),  -- aluno 20: pago=false → BLOQUEADO
(2, 25, 17, 7.00,  '2026-06-03', true),   -- aluno 25: pago=true  → pode emprestar
(3, 30, 18, 2.00,  '2026-05-19', false),  -- aluno 30: pago=false → BLOQUEADO
(4, 35, 19, 10.00, '2026-05-18', true),   -- aluno 35: pago=true  → pode emprestar
(5, 40, 20, 3.00,  '2026-04-25', false);  -- aluno 40: pago=false → BLOQUEADO

-- -------------------------------------------------------
-- 8. RESET DAS SEQUÊNCIAS
-- Garante que novos registros inseridos pelo Spring Boot
-- não colidam com os IDs inseridos manualmente.
-- -------------------------------------------------------
ALTER TABLE aluno          ALTER COLUMN id RESTART WITH 51;
ALTER TABLE titulo         ALTER COLUMN id RESTART WITH 101;
ALTER TABLE livro          ALTER COLUMN id RESTART WITH 301;
ALTER TABLE emprestimo     ALTER COLUMN id RESTART WITH 21;
ALTER TABLE item_emprestimo ALTER COLUMN id RESTART WITH 29;
ALTER TABLE debito         ALTER COLUMN id RESTART WITH 6;
