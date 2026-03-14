CREATE SCHEMA if not exists market;

CREATE TABLE if not exists items (
  id BIGSERIAL NOT NULL,
  title varchar(50) NOT NULL,
  description varchar(255) NULL,
  img_path varchar(255) NULL,
  price double precision NULL,
  CONSTRAINT items_pkey PRIMARY KEY (id),
  CONSTRAINT items_title_unique UNIQUE (title)
);

CREATE TABLE if not exists carts (
  id BIGSERIAL NOT NULL,
  user_name varchar(50) NOT NULL,
  CONSTRAINT carts_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists cart_item (
  id BIGSERIAL NOT NULL,
  item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
  cart_id BIGINT NOT NULL,
  count int8 NULL,
  CONSTRAINT cart_item_pkey PRIMARY KEY (item_id, cart_id)
);

ALTER TABLE cart_item DROP CONSTRAINT IF EXISTS cart_item_cart_id_fkey;
ALTER TABLE cart_item
    ADD CONSTRAINT cart_item_cart_id_fkey
        FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE;

CREATE TABLE if not exists orders (
   id BIGSERIAL NOT NULL,
   total_sum double precision NOT NULL,
   status varchar(10) NOT NULL DEFAULT 'NEW',
   CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists order_item (
   id BIGSERIAL NOT NULL,
   order_id BIGINT NOT NULL,
   item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
   count INTEGER NOT NULL,
   UNIQUE(order_id, item_id)
);

ALTER TABLE order_item DROP CONSTRAINT IF EXISTS order_item_order_id_fkey;
ALTER TABLE order_item
    ADD CONSTRAINT order_item_order_id_fkey
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

CREATE TABLE if not exists users (
   id BIGSERIAL NOT NULL,
   user_name varchar(50) NOT NULL,
   status varchar(10),
   password varchar(255) NULL
);

CREATE TABLE if not exists authorities (
   user_name varchar(50) NOT NULL,
   authority varchar(255)
);