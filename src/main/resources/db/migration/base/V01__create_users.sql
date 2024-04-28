CREATE TABLE IF NOT EXISTS task.users
(
    id bigserial not null,
    email character varying not null unique,
    first_name character varying not null,
    last_name character varying not null,
    birth_date date not null,
    address character varying,
    phone character varying unique,
    primary key (id)
);


