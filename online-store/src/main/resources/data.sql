truncate items restart identity cascade;
insert into items (title, description, img_path, price) values
('Мяч футбольный Adidas', 'Официальный мяч для профессиональных матчей, размер 5', '/images/soccer_ball.jpg', 4600),
('Наушники беспроводные Sony', 'Беспроводные наушники с шумоподавлением, время работы до 30 часов', '/images/sony_headphones.jpg', 12500),
('Книга "Мастер и Маргарита"', 'Роман Михаила Булгакова в твердом переплете', '/images/master_margarita.jpg', 900),
('Кофемашина DeLonghi', 'Автоматическая кофемашина для приготовления эспрессо и капучино', '/images/coffee_machine.jpg', 35000),
('Фитнес-браслет Xiaomi', 'Умный браслет с отслеживанием активности и пульса', '/images/fitness_band.jpg', 2300),
('Гитара акустическая Yamaha', 'Акустическая гитара 6 струн, корпус из красного дерева', '/images/acoustic_guitar.jpg', 16000),
('Монитор игровой 27"', 'Игровой монитор с частотой 144 Гц и разрешением 2K', '/images/gaming_monitor.jpg', 29000),
('Куртка зимняя The North Face', 'Теплая зимняя куртка с пуховым наполнителем', '/images/winter_jacket.jpg', 18999),
('Смартфон Samsung Galaxy S23', 'Флагманский смартфон с камерой 200 МП и процессором Snapdragon', '/images/samsung_phone.jpg', 90000),
('Набор кухонных ножей', 'Набор из 6 ножей из нержавеющей стали с деревянной подставкой', '/images/kitchen_knives.jpg', 6500)
on conflict do nothing;

truncate carts restart identity cascade;
insert into carts (user_name) values
('oleg'),
('Hugh.Jackman')
on conflict do nothing;

truncate cart_item restart identity cascade;
insert into cart_item (cart_id, item_id, count) values
(1, 4, 1),
(1, 10, 3),
(1, 3, 1),
(1, 1, 2),
(2, 1, 3),
(2, 3, 1)
on conflict do nothing;


truncate orders restart identity cascade;
insert into orders (user_name, total_sum, status) values
('oleg', 4600, 'ORDERED'),
('oleg', 115000, 'ORDERED'),
('oleg', 4600, 'ORDERED'),
('oleg', 64700, 'ORDERED'),
('Hugh.Jackman', 46900, 'ORDERED')
on conflict do nothing;


insert into order_item (order_id, item_id, count) values
(1, 1, 1),
(2, 2, 1),
(2, 9, 1),
(3, 5, 2),
(4, 4, 1),
(4, 10, 3),
(4, 3, 1),
(4, 1, 2),
(5, 1, 10),
(5, 3, 1)
on conflict do nothing;

truncate users restart identity cascade;
insert into users (user_name, status, password) values
('oleg', 'ACTIVE', '$argon2id$v=19$m=16384,t=2,p=1$R+oQhdqj/PDqDFSiKmvJJw$BK6Y3MuiyT4vUsnFEqTklAE3+83i7iUGGxkBxvmrNWM'), --Ch43wTRY
('Hugh.Jackman', 'ACTIVE', '$argon2id$v=19$m=16384,t=2,p=1$tvHWuq6M+W3i7Dnl4TLCvA$YG7TQ943vg9BYAgCSI1IBQiHgVR//lhDF/aKmTtkg3w'), --Lipton
('Evangeline.Lilly', 'ACTIVE', '$argon2id$v=19$m=16384,t=2,p=1$YffVrajwLh0RDQf2YUUrpg$deAQ4Apm70EiMcbjL4GrcmW6UZTJD3YU3PHC36H/YHU'); --RealSteel