-- ================================================
-- SEED: Filmes, Sessões e Reservas de Teste
-- Execute no SQL Editor do Neon (console.neon.tech)
-- ================================================

-- Inserir filmes
INSERT INTO filmes (titulo, descricao, ano, duracao, classificacao, imagem, preco) VALUES
('Duna: Parte Dois',       'Paul Atreides se une aos Fremen para vingar a destruição de sua família e impedir um futuro que apenas ele pode prever.',                       2024, 166, '14', 'https://image.tmdb.org/t/p/w500/czembW0Rk1Ke7lCJGahbOhdCuhV.jpg', 32.00),
('Oppenheimer',            'A história do físico J. Robert Oppenheimer e seu papel no desenvolvimento da bomba atômica durante a Segunda Guerra Mundial.',                   2023, 180, '14', 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg', 28.00),
('Guardiões da Galáxia Vol. 3', 'Os Guardiões embarcam em uma missão para proteger Rocket e descobrir seu passado misterioso.',                                             2023, 150, '12', 'https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg', 30.00),
('Elementos',              'Em uma cidade onde fogo, água, terra e ar convivem, Faísca e Gota descobrem algo surpreendente em comum.',                                       2023, 101, 'L',  'https://image.tmdb.org/t/p/w500/4Y1WNkd88JXmGfhtWR7dmDAo1T2.jpg', 25.00),
('Alien: Romulus',         'Um grupo de jovens colonizadores do espaço se depara com a forma de vida mais aterrorizante do universo.',                                       2024, 119, '16', 'https://image.tmdb.org/t/p/w500/b33nnKl1GSFbao4l3fZDDqsMx0F.jpg', 32.00),
('Deadpool & Wolverine',   'Deadpool é recrutado pela Autoridade de Variação Temporal e acaba formando uma improvável parceria com Wolverine.',                              2024, 127, '16', 'https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg', 35.00),
('Pobres Criaturas',       'Bella Baxter é trazida de volta à vida por um cientista excêntrico e foge com um advogado libertino para aventuras pelo mundo.',                 2023, 141, '18', 'https://image.tmdb.org/t/p/w500/kCGlIMHnOm8JPXNbM7HL1ZnIQ8s.jpg', 28.00),
('Furiosa: Uma Saga Mad Max','A origem da guerreira Furiosa antes de seu encontro com Max Rockatansky no apocalipse.',                                                       2024, 148, '16', 'https://image.tmdb.org/t/p/w500/iADOJ8Zymht2JPMoy3R7xceZprc.jpg', 32.00)
ON CONFLICT DO NOTHING;

-- Associar gêneros (busca IDs pelo nome para evitar hardcode)
INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Duna: Parte Dois'         AND g.nome IN ('Ação','Aventura','Ficção Científica')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Oppenheimer'              AND g.nome IN ('Drama','Thriller')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Guardiões da Galáxia Vol. 3' AND g.nome IN ('Ação','Aventura','Comédia')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Elementos'                AND g.nome IN ('Animação','Romance','Aventura')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Alien: Romulus'           AND g.nome IN ('Terror','Ficção Científica','Thriller')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Deadpool & Wolverine'     AND g.nome IN ('Ação','Comédia')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Pobres Criaturas'         AND g.nome IN ('Drama','Romance','Comédia')
ON CONFLICT DO NOTHING;

INSERT INTO filmes_generos (filme_id, genero_id)
SELECT f.id, g.id FROM filmes f, generos g
WHERE f.titulo = 'Furiosa: Uma Saga Mad Max' AND g.nome IN ('Ação','Aventura')
ON CONFLICT DO NOTHING;

-- Inserir sessões (próximos dias)
INSERT INTO sessoes (filme_id, data_hora, sala, capacidade, ativa)
SELECT f.id, NOW() + INTERVAL '1 day' + (h * INTERVAL '1 hour'), sala, cap, true
FROM filmes f
JOIN (VALUES
    ('Duna: Parte Dois',            14, 'Sala 1', 120),
    ('Duna: Parte Dois',            18, 'Sala 1', 120),
    ('Duna: Parte Dois',            20, 'Sala 2', 100),
    ('Oppenheimer',                 15, 'Sala 2', 100),
    ('Oppenheimer',                 19, 'Sala 3',  80),
    ('Oppenheimer',                 17, 'Sala 1', 120),
    ('Guardiões da Galáxia Vol. 3', 13, 'Sala 3',  80),
    ('Guardiões da Galáxia Vol. 3', 16, 'Sala 2', 100),
    ('Guardiões da Galáxia Vol. 3', 21, 'Sala 1', 120),
    ('Elementos',                   11, 'Sala 4', 150),
    ('Elementos',                   15, 'Sala 4', 150),
    ('Elementos',                   13, 'Sala 4', 150),
    ('Alien: Romulus',              20, 'Sala 1', 120),
    ('Alien: Romulus',              22, 'Sala 3',  80),
    ('Alien: Romulus',              21, 'Sala 2', 100),
    ('Deadpool & Wolverine',        14, 'Sala 2', 100),
    ('Deadpool & Wolverine',        17, 'Sala 1', 120),
    ('Deadpool & Wolverine',        19, 'Sala 3',  80),
    ('Pobres Criaturas',            16, 'Sala 3',  80),
    ('Pobres Criaturas',            18, 'Sala 2', 100),
    ('Furiosa: Uma Saga Mad Max',   18, 'Sala 1', 120),
    ('Furiosa: Uma Saga Mad Max',   21, 'Sala 2', 100),
    ('Furiosa: Uma Saga Mad Max',   20, 'Sala 3',  80)
) AS dados(titulo, h, sala, cap) ON f.titulo = dados.titulo
ON CONFLICT DO NOTHING;

-- Inserir reservas de teste para o cliente
INSERT INTO reservas (usuario_id, sessao_id, quantidade, assentos, status, valor_total, data_reserva)
SELECT
    u.id,
    s.id,
    2,
    'A1,A2',
    'CONFIRMADA',
    f.preco * 2,
    NOW() - INTERVAL '1 day'
FROM usuarios u, sessoes s
JOIN filmes f ON f.id = s.filme_id
WHERE u.email = 'cliente@cinema.com'
  AND f.titulo = 'Duna: Parte Dois'
LIMIT 1
ON CONFLICT DO NOTHING;

INSERT INTO reservas (usuario_id, sessao_id, quantidade, assentos, status, valor_total, data_reserva)
SELECT
    u.id,
    s.id,
    1,
    'C5',
    'CONFIRMADA',
    f.preco,
    NOW() - INTERVAL '3 hours'
FROM usuarios u, sessoes s
JOIN filmes f ON f.id = s.filme_id
WHERE u.email = 'cliente@cinema.com'
  AND f.titulo = 'Oppenheimer'
LIMIT 1
ON CONFLICT DO NOTHING;

INSERT INTO reservas (usuario_id, sessao_id, quantidade, assentos, status, valor_total, data_reserva)
SELECT
    u.id,
    s.id,
    3,
    'B2,B3,B4',
    'CANCELADA',
    f.preco * 3,
    NOW() - INTERVAL '3 days'
FROM usuarios u, sessoes s
JOIN filmes f ON f.id = s.filme_id
WHERE u.email = 'cliente@cinema.com'
  AND f.titulo = 'Guardiões da Galáxia Vol. 3'
LIMIT 1
ON CONFLICT DO NOTHING;

SELECT 'Seed concluído!' AS resultado;