# market-app
Серверная часть веб-приложения «Витрина интернет-магазина»
Для запуска необходимо выполнить:
1. Сборку приложения online-store и payment-service (выполнять из корня проекта)

   $ mvnw clean package

2. Создать файл .env с настройками для docker-compose

   POSTGRES_DB: <db_name>

   POSTGRES_USER: <user_name>

   POSTGRES_PASSWORD: <password> 

3. Выполнить команду сборки и запуска docker контейнеров 

   $ docker compose up --build -d


Тесты запускать с запущенной в контейнере или установленной локально БД