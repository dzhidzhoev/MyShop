BEGIN;
-- salt myshp82
-- pwd 1
INSERT INTO UserTable (UserID, Email, PwdHash, IsAdmin, LastName, FirstName, MiddleName, Address, Phone) VALUES 
(1, '1@1.com', '444d01eb0131025c0f712674662ecd25', true, 'Иванов', 'Админ', 'Иванович', 'Москва', '+000'),
(2, '2@1.com', '444d01eb0131025c0f712674662ecd25', false, 'Петров', 'Петр', 'Петрович', 'Москва', '+000'),
(3, '3@1.com', '444d01eb0131025c0f712674662ecd25', false, 'Сидоров', 'Семён', 'Семёнович', 'Иваново', '+000');

INSERT INTO Category (CategoryID, Name, IsActive) VALUES
(1, 'Телевизоры', true),
(2, 'Проигрыватели', true),
(3, 'Холодильники', true),
(4, 'Стиральные машины', true);
;

INSERT INTO Trait (TraitID, Name, IsSearchable, Type) VALUES
(1, 'Производитель', true, 'StringType'),
(2, 'Страна сборки', true, 'StringType');

INSERT INTO Trait (TraitID, Name, IsSearchable, Type, MinValue, MaxValue, Unit) VALUES
(3, 'Ширина', true, 'IntType', 10, 250, 'см'),
(4, 'Высота', true, 'IntType', 10, 250, 'см'),
(7, 'Длина', true, 'IntType', 10, 250, 'см'),
(5, 'Толщина', true, 'IntType', 1, 250, 'см');

INSERT INTO Trait (TraitID, Name, IsSearchable, Type, Values) VALUES
(6, 'Стандарт дисков', true, 'EnumType', ARRAY['DVD', 'Bluray']::text[]);

INSERT INTO CategoryTrait (CategoryID, TraitID) VALUES
(1, 1),
(3, 1),
(4, 1),

(1, 2),
(3, 2),
(4, 2),

(1, 3),
(2, 3),
(3, 3),
(4, 3),

(1, 4),
(3, 4),
(4, 4),

(1, 5),
(3, 5),
(4, 5),

(2, 7),
(2, 6);

INSERT INTO Item (ItemID, CategoryID, Name, Price, Count, Active, Description) VALUES
(1, 3, 'Холодильник Me-3000', 100000, 10, true, 'Хороший холодильник для дома'),
(2, 1, 'Телевизор Power9', 20000, 200, true, 'Дешевый телевизор'),
(3, 2, 'Bluray player SONTY', 9000, 0, false, 'Снят с продажи'),
(4, 4, 'Стиральная машина БОШ', 50000, 100, true, 'Бери и используй');

INSERT INTO ItemTrait (ItemID, TraitID, Value, ValueInt) VALUES
(1, 3, NULL, 50),
(1, 4, NULL, 150),
(1, 5, NULL, 70),
(3, 6, 'Bluray', NULL);

INSERT INTO Cart (UserID, ItemID, Count) VALUES
(1, 1, 1),
(1, 2, 10),
(1, 3, 200);

INSERT INTO OrderTable (OrderID, UserID, OrderTime, Name, Status, Total) VALUES
(1, 1, CURRENT_TIMESTAMP, 'Получатель Х', 'Processing', 1000),
(2, 2, CURRENT_TIMESTAMP, 'Ваня', 'Canceled', 1000),
(3, 2, CURRENT_TIMESTAMP, 'Петя', 'Done', 1000);

INSERT INTO OrderItem (OrderID, ItemID, Price, Count) VALUES
(1, 1, 1000, 1),
(2, 2, 1000, 1),
(2, 3, 3000, 20),
(3, 3, 4000, 2);

COMMIT;