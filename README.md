# MyShop
CMS for online shop

## Сборка
Для сборки можно выполнить Ant-задачи файла `build.xml`:

`compile` - для сборки кода проекта

`compile-tests` - для сборки кода тестов

`test` - для запуска тестов

Либо Maven-цели `build` , `test` (по умолчанию проект Spring создался с поддержкой maven, скрипт сборки для Ant был сгенерирован на основе `pom.xml` автоматически).

Предварительно необходимо прописать параметры подключения к БД в файлах `build.properties`, `src/test/resources/test.properties`, `src/test/resources/test_validate.properties`, `src/main/resources/application.properties`.
В параемтрах тестов нужно прописывать название БД для запуска функциональных тестов, в параметрах приложения - название 
основной БД для запуска. Для запуска тестов нужно создать схему и заполнить БД с помощью Ant-задач `create`, `fill` (либо выполнив файлы `src/test/resources/schema.sql`, `src/test/resources/data.sql`).

Для работы используется СУБД PostgreSQL.
