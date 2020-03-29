# MyShop
CMS for online shop

## Сборка
Для сборки можно выполнить Ant-задачи файла `build.xml`:

`compile` - для сборки кода проекта

`compile-tests` - для сборки кода тестов

`test` - для запуска тестов

Либо Maven-цели `build` , `test` (по умолчанию проект Spring создался с поддержкой maven, скрипт сборки для Ant был сгенерирован на основе `pom.xml` автоматически).

Предварительно необходимо указать параметры подключения к БД в файлах `build.properties`, `src/test/resources/test.properties`, `src/test/resources/test_validate.properties`, `src/main/resources/application.properties`.
В параметрах тестов нужно указывать название БД для запуска юнит-тестов, в параметрах приложения - название 
основной БД для запуска. Для запуска тестов нужно создать схему и заполнить БД с помощью Ant-задач `create`, `fill` (либо выполнив файлы `src/test/resources/schema.sql`, `src/test/resources/data.sql`).

Для работы используется СУБД PostgreSQL.

## Функционирование
Код использует Spring и Spring Data. Юнит-тесты написаны с помощью фреймворка TestNG. 

Для взаимодействия с БД используется механизм, описываемый спецификацией JPA, при этом 
используется реализация Hibernate.

Классы сущностей находятся в пакете `com.myshop.model`.

DAO-интерфейсы (репозитории) находятся в пакете `com.myshop.repository`. Они унаследованы от 
класса `JpaRepository`, который сам по себе предоставляет пользователю базовые CRUD-методы,
так что вручную реализовывать их не нужно.
Вручную реализованы методы, требующие некоторой бизнес-логики.

Некоторые методы репозиториев реализуются с использованием механизма порождения тела
запроса по его сигнатуре (Derived Query Methods), встроенному в Spring Data. 
Такие методы не имеют явно написанного тела - Spring Data автоматически генерирует для них тело, 
в котором выполняется запрос, соответствующий названию метода (например, как видно
из сигнатуры метода `Set<Item> findItemsByCategoryId(int categoryId)`,
он должен возвращать все товары, идентификатор категории которых совпадает с переданным
в качестве параметра).

Для осуществления фильтрации товаров в выдаче магазина в интерфейсе `ItemRepository` реализован 
метод `findItemsByTerms`. Он использует JPA Criteria API для динамического составления
сложных SQL-запросов, соответствующих условиям фильтра, переданного в качестве параметра.