CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS public.questions (
    id VARCHAR(6) PRIMARY KEY DEFAULT substr(encode(gen_random_bytes(3), 'hex'), 1, 6),
    question TEXT NOT NULL,
    username VARCHAR(100) NOT NULL
);
