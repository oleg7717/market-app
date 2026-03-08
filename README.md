# market-app
Серверная часть веб-приложения «Витрина интернет-магазина»
Для запуска необходимо выполнить:
1. Сборку приложения online-store (открыть cmd в директории проекта)

   $ mvnw clean package
   $ docker build -t online-store:latest .

2. Сборку приложения online-store

   $ cd ../payment-service
   $ mvnw clean package
3. Заменить <user_placeholder> и <password_placeholder> на логин и пароль от базы данных в файле docker-compose.yaml в корне проекта
4. Выполнить команду сборки и запуска docker контейнеров 

   $ docker compose up --build
   $ docker build -t online-store:latest .


Тесты запускать с запущенной в контейнере или установленной локально БД