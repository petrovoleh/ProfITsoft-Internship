**Запуск програми**

Для використання программи потрібно мати запущений postgresql і створити базу даних profitsoft
CREATE DATABASE profitsoft

Метод `main` основного класу застосунку `com.petrovoleh.Task2Application`.

**Імпорт даних основних сутностей**

Вихідний файл для імпорту даних основних сутностей `Order` та `Client` розташований за посиланням: [data.json](https://github.com/olehpetrov/task2/blob/main/src/main/resources/data/data.json).

**Скріпти Liquibase**

Скріпти Liquibase для створення таблиць та індексів сутностей `Order`, `Client`, та заповнення таблиці допоміжної сутності `Client` розташовані в теці `src/main/resources/db`.

**Збереження даних**

Для збереження та роботи з даними використовується імідж PostgreSQL 16.2, доступний за посиланням: [download/postgresql](https://www.postgresql.org/download/l).

**Виконання запитів для ендпойнтів**

Приклад виконання запиту для ендпойнта `/api/orders/_list`:

Запит для отримання першої сторінки розміром 10 замовлень з іменем клієнта "Client1":

```json
{
  "clientName": "Client1",

  "page": 0,
  "size": 10
}
```

Для пошуку замовлення за ідентифікатором клієнта можна вказати запит:

```json
{
  "name": "Client1"
}
```
