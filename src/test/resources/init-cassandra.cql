CREATE KEYSPACE IF NOT EXISTS exchange_rates
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS exchange_rates.currency_rate (
    currency_from TEXT,
    currency_to TEXT,
    rate_date DATE,
    close_rate DECIMAL,
    created_at TIMESTAMP,

    PRIMARY KEY ((currency_from, currency_to), rate_date)
)
WITH CLUSTERING ORDER BY (rate_date DESC);

INSERT INTO exchange_rates.currency_rate (currency_from, currency_to, rate_date, close_rate, created_at)
VALUES ('RUB', 'USD', '2026-02-02', 0.01321, '2026-02-02T00:00:00Z');