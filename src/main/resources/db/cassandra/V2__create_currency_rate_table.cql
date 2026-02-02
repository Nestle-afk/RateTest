CREATE TABLE IF NOT EXISTS exchange_rates.currency_rate (
    currency_from TEXT,
    currency_to TEXT,
    rate_date DATE,
    close_rate DECIMAL,
    created_at TIMESTAMP,

    PRIMARY KEY ((currency_from, currency_to), rate_date)
)
WITH CLUSTERING ORDER BY (rate_date DESC);
