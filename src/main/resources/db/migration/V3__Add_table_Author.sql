-- Добавляем таблицу Author
create table author
(
    id     serial primary key,
    full_name   text not null,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);