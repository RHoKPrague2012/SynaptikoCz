DROP TABLE category;
DROP TABLE product;
DROP TABLE farm;
DROP TABLE contact;
DROP TABLE farm_category;
DROP TABLE farm_product;

CREATE TABLE category (name TEXT PRIMARY KEY);
CREATE TABLE product (name TEXT PRIMARY KEY, category_id);
CREATE TABLE farm (name TEXT, gps_lat REAL NOT NULL, gps_long REAL NOT NULL, desc TEXT, type TEXT);
CREATE TABLE contact (farm_id INTEGER, type TEXT, contact TEXT, UNIQUE (farm_id, type, contact));
CREATE TABLE farm_category (farm_id INTEGER, category_id INTEGER, PRIMARY KEY (farm_id, category_id));
CREATE TABLE farm_product (farm_id INTEGER, product_id INTEGER, PRIMARY KEY (farm_id, product_id));
