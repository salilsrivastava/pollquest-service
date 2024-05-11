CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS public.questions
(
    id character varying(6) COLLATE pg_catalog."default" NOT NULL DEFAULT substr(encode(gen_random_bytes(3), 'hex'::text), 1, 6),
    description text COLLATE pg_catalog."default" NOT NULL,
    question jsonb,
    CONSTRAINT questions_pkey PRIMARY KEY (id)
)
