-- liquibase formatted sql

-- changeset fabiosiqueira:1755174226507-1
CREATE TABLE transaction
(
    id UUID DEFAULT gen_random_uuid(),
    description      VARCHAR(50),
    transaction_date date         NOT NULL,
    amount           DECIMAL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

