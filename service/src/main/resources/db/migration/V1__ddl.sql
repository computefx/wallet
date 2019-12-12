create table wallets (
    id int not null AUTO_INCREMENT,
    version int not null,
    user_id int not null,
    amount BIGINT not null default 0,
    currency CHAR(3),
    PRIMARY KEY (id)
);

create unique index user_id_currency_idx
on wallets(user_id, currency);

create index user_id_amount_idx
on wallets(user_id, amount);