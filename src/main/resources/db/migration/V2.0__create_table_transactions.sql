CREATE TABLE IF NOT EXISTS transactions
(
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    account_no VARCHAR(20) NOT NULL,
    trx_amount DECIMAL(21, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    trx_date DATE NOT NULL,
    trx_time TIME NOT NULL,
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);