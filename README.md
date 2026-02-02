# Rate API

Микросервис для управления расходными операциями и лимитами.

## Описание

Микросервис предоставляет API для:
- Приёма транзакций в различных валютах (KZT, RUB и др.).
- Хранения месячных лимитов по категориям расходов (товары и услуги).
- Получения курсов валют (KZT/USD, RUB/USD) и конвертации сумм в USD.
- Пометки транзакций, превысивших лимит.
- Установки новых лимитов и получения списка транзакций, превысивших лимит.

## Требования

- Java 17+
- Maven 3.8+
- Docker (для запуска PostgresSQL и Cassandra)
- Spring Boot 3.x

## Установка и запуск

1. Клонируйте репозиторий:
   ```bash
   git clone <repository-url>
   cd rate-api
   ```

2. Настройте базы данных:
   ```bash
   docker-compose up -d
   docker cp src/main/resources/db/cassandra/V1__create_keyspace.cql cassandra:/tmp/
   docker cp src/main/resources/db/cassandra/V2__create_currency_rate_table.cql cassandra:/tmp/
   docker exec cassandra cqlsh -f /tmp/V1__create_keyspace.cql
   docker exec cassandra cqlsh -f /tmp/V2__create_currency_rate_table.cql
   ``` 

3. Соберите и запустите приложение:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. Приложение будет доступно по адресу: `http://localhost:8080`. Swagger UI по адресу - http://localhost:8080/swagger-ui/index.html

## API

### Endpoints

#### Transaction API
- `POST /api/transactions` - Создание новой транзакции.

#### Client API
- `GET /api/transactions/exceeded` - Получение всех транзакций, превысивших лимит.
- `GET /api/transactions/exceeded/{year}/{month}` - Получение транзакций, превысивших лимит, за указанный месяц.
- `POST /api/limits` - Установка нового лимита.

### Примеры запросов

#### Создание транзакции
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountFrom": "1234567890",
    "accountTo": "0987654321",
    "currencyShortname": "KZT",
    "sum": 10000.00,
    "expenseCategory": "PRODUCT",
    "datetime": "2022-01-02T10:00:00+06:00"
  }'
```

#### Получение транзакций, превысивших лимит
```bash
curl -X GET http://localhost:8080/api/transactions/exceeded
```

#### Установка нового лимита
```bash
curl -X POST http://localhost:8080/api/limits \
  -H "Content-Type: application/json" \
  -d '{
    "expenseCategory": "PRODUCT",
    "limitSum": 1000.00
  }'
```

## Тестирование

Для запуска тестов используйте команду:
```bash
mvn test
```
