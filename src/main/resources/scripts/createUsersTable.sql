CREATE TABLE users(
    id int primary key,
    username varchar(64) unique not null,
    password_hash varchar(64) not null
)