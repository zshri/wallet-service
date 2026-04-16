--liquibase formatted sql

--changeset wallet:003
--comment: Create 1000 test wallets with random UUIDs and initial balance
INSERT INTO wallet (id, balance)
SELECT gen_random_uuid(), 10000.00
FROM generate_series(1, 1000)
ON CONFLICT (id) DO NOTHING;