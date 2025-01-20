-- Обновляем записи в таблице Budget, заменяя тип Комиссия на Расход
UPDATE Budget
SET type = 'Расход'
WHERE type = 'Комиссия';