CREATE KEYSPACE IF NOT EXISTS exchange_rates
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};
