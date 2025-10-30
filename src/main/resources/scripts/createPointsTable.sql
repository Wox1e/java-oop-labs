CREATE TABLE points(
    id bigint primary key,
    function_id int,
    x_value decimal,
    y_value decimal,
    FOREIGN KEY (function_id) REFERENCES functions(id)
)