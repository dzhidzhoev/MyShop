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

CREATE TYPE TypeEnum AS ENUM (
  'StringType',
  'IntType',
  'EnumType'
);

CREATE TYPE OrderStatus AS ENUM (
  'Processing',
  'Delivery',
  'Done',
  'Canceled'
);

CREATE TABLE UserTable (
  UserID SERIAL PRIMARY KEY,
  Email text UNIQUE NOT NULL,
  PwdHash text NOT NULL,
  EmailToken text,
  PwdChangeToken text,
  IsAdmin boolean,
  IsDeleted boolean,
  FirstName text,
  LastName text,
  MiddleName text,
  Phone text,
  Address text
);

CREATE TABLE Trait (
  TraitID SERIAL PRIMARY KEY,
  Name text NOT NULL,
  IsSearchable boolean,
  Type TypeEnum NOT NULL,
  MinValue int,
  MaxValue int,
  Values text[],
  Unit text
);

CREATE TABLE Category (
  CategoryID SERIAL PRIMARY KEY,
  Name text NOT NULL,
  IsActive boolean
);

CREATE TABLE CategoryTrait (
  CategoryID int,
  TraitID int,
  PRIMARY KEY (CategoryID, TraitID)
);

CREATE TABLE ItemTrait (
  ItemID int,
  TraitID int,
  Value text,
  ValueInt int,
  PRIMARY KEY (ItemID, TraitID)
);

CREATE TABLE Item (
  ItemID SERIAL PRIMARY KEY,
  CategoryID int NOT NULL,
  Name text NOT NULL,
  Price int NOT NULL,
  Count int,
  Active boolean,
  Description text,
  Image bytea
);

CREATE TABLE Cart (
  UserID int,
  ItemID int,
  Count int DEFAULT 1,
  PRIMARY KEY (UserID, ItemID)
);

CREATE TABLE OrderTable (
  OrderID SERIAL PRIMARY KEY,
  UserID int NOT NULL,
  OrderTime timestamp,
  DeliveryTime text,
  Name text,
  Phone text,
  Email text,
  Address text,
  Comment text,
  Status OrderStatus NOT NULL,
  Total int NOT NULL
);

CREATE TABLE OrderItem (
  OrderID int,
  ItemID int,
  Price int,
  Count int,
  PRIMARY KEY (OrderID, ItemID)
);

ALTER TABLE CategoryTrait ADD FOREIGN KEY (CategoryID) REFERENCES Category (CategoryID);

ALTER TABLE CategoryTrait ADD FOREIGN KEY (TraitID) REFERENCES Trait (TraitID);

ALTER TABLE ItemTrait ADD FOREIGN KEY (TraitID) REFERENCES Trait (TraitID) ON DELETE CASCADE;

ALTER TABLE ItemTrait ADD FOREIGN KEY (ItemID) REFERENCES Item (ItemID) ON DELETE CASCADE;

ALTER TABLE Cart ADD FOREIGN KEY (UserID) REFERENCES UserTable (UserID);

ALTER TABLE Cart ADD FOREIGN KEY (ItemID) REFERENCES Item (ItemID);

ALTER TABLE Item ADD FOREIGN KEY (CategoryID) REFERENCES Category (CategoryID);

ALTER TABLE OrderTable ADD FOREIGN KEY (UserID) REFERENCES UserTable (UserID);

ALTER TABLE OrderItem ADD FOREIGN KEY (OrderID) REFERENCES OrderTable (OrderID);

ALTER TABLE OrderItem ADD FOREIGN KEY (ItemID) REFERENCES Item (ItemID);

COMMIT;