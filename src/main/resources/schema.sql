create schema if not exists market;

CREATE TABLE if not exists items (
  id BIGSERIAL NOT NULL,
  title varchar(50) NOT NULL,
  description varchar(255) NULL,
  img_path varchar(255) NULL,
  price int8 NULL,
  CONSTRAINT items_pkey PRIMARY KEY (id),
  CONSTRAINT items_title_unique UNIQUE (title)
);

CREATE TABLE if not exists carts (
  id BIGSERIAL NOT NULL,
  item_id BIGINT NOT NULL,
  count int8 NULL,
  CONSTRAINT carts_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists orders (
   id BIGSERIAL NOT NULL REFERENCES order_item(order_id) ON DELETE CASCADE,
   total_sum int8 NOT NULL,
   status varchar(10) NOT NULL DEFAULT 'NEW',
   CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists order_item (
   id BIGSERIAL NOT NULL,
   order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
   item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
   count INTEGER NOT NULL,
   UNIQUE(order_id, item_id)
);