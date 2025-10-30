CREATE TABLE functions(
    id int primary key,
    name varchar(32) not null,
    type varchar(32) not null,
    author_id int,
    FOREIGN KEY (author_id) REFERENCES Users(id)
)