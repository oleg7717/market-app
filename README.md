# market-app
Серверная часть веб-приложения «Витрина интернет-магазина»
Для запуска необходимо выполнить:
1. Команду сборки исполняемого jar файла приложения

   $ mvnw clean package
2. Заменить <user_placeholder> и <password_placeholder> на логин и пароль от базы данных в файле docker-compose.yaml в корне проекта
3. Выполнить команду сборки и запуска docker контейнеров 

   $ docker compose up --build