DROP TABLE category;
DROP TABLE product;
DROP TABLE farm;
DROP TABLE contact;
DROP TABLE farm_category;
DROP TABLE farm_product;

CREATE TABLE category (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);
CREATE TABLE product (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, category_id);
CREATE TABLE farm (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, gps_lat REAL NOT NULL, gps_long REAL NOT NULL, desc TEXT, type TEXT);
CREATE TABLE contact (_id INTEGER PRIMARY KEY AUTOINCREMENT, farm_id INTEGER, type TEXT, contact TEXT, UNIQUE (farm_id, type, contact));
CREATE TABLE farm_category (_id INTEGER PRIMARY KEY AUTOINCREMENT, farm_id INTEGER, category_id INTEGER, UNIQUE (farm_id, category_id));
CREATE TABLE farm_product (_id INTEGER PRIMARY KEY AUTOINCREMENT, farm_id INTEGER, product_id INTEGER, UNIQUE (farm_id, product_id));
