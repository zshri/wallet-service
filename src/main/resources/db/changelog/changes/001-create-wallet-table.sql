--liquibase formatted sql

--changeset wallet:001
CREATE TABLE wallet (
    id UUID PRIMARY KEY,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00
);