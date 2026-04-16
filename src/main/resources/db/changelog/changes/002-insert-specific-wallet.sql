--liquibase formatted sql

--changeset wallet:002
--comment: Create specific test wallet with fixed UUID
INSERT INTO wallet (id, balance)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 10000.00)
ON CONFLICT (id) DO NOTHING;