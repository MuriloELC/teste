CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    timezone VARCHAR(60),
    papel VARCHAR(20) NOT NULL
);

CREATE TABLE meetings (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    organizador_id BIGINT REFERENCES users(id),
    data_hora_inicio TIMESTAMP WITH TIME ZONE,
    data_hora_fim TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20),
    link_videoconferencia VARCHAR(255),
    localizacao VARCHAR(255)
);

CREATE TABLE meeting_participants (
    meeting_id BIGINT REFERENCES meetings(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (meeting_id, user_id)
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    status VARCHAR(20) NOT NULL,
    data_hora_inicio_planejada TIMESTAMP WITH TIME ZONE,
    data_hora_fim_planejada TIMESTAMP WITH TIME ZONE,
    data_hora_conclusao TIMESTAMP WITH TIME ZONE,
    prioridade VARCHAR(20),
    responsavel_id BIGINT REFERENCES users(id),
    criador_id BIGINT REFERENCES users(id),
    meeting_id BIGINT REFERENCES meetings(id)
);

CREATE TABLE availability_slots (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    dia_da_semana VARCHAR(20),
    data_especifica DATE,
    hora_inicio TIME,
    hora_fim TIME,
    tipo VARCHAR(20)
);

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    tipo VARCHAR(30),
    mensagem TEXT,
    lida BOOLEAN,
    data_hora_criacao TIMESTAMP WITH TIME ZONE
);

CREATE TABLE chat_messages (
    id SERIAL PRIMARY KEY,
    sender_id BIGINT REFERENCES users(id),
    meeting_id BIGINT REFERENCES meetings(id),
    canal VARCHAR(120) NOT NULL,
    conteudo TEXT,
    data_hora_envio TIMESTAMP WITH TIME ZONE
);
