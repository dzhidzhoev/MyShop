BEGIN;

DROP TABLE IF EXISTS OrderItem;
DROP TABLE IF EXISTS OrderTable;
DROP TABLE IF EXISTS Cart;
DROP TABLE IF EXISTS ItemTrait;
DROP TABLE IF EXISTS CategoryTrait;
DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS Trait;
DROP TABLE IF EXISTS UserTable;
DROP TYPE IF EXISTS OrderStatus;
DROP TYPE IF EXISTS TypeEnum;

COMMIT;