## Список задач

1. Починить тесты
  * `testBudgetPagination` - некорректно работает пагинация + неправильно считается общая статистика записей
  * `testStatsSortOrder` - необходимо реализовать сортировку выдачи в указанном порядке
2. Из модели `BudgetType` через миграцию БД убрать значение `Комиссия`, заменив его на `Расход`
3. Добавить таблицу `Author` - автор внесения записи.
   * 3 колонки - `ID`, `ФИО`, `Дата создания` (дата-время). 
   * Добавить в апи метод создания новой записи в `Author`. На вход передается ФИО, дата создания проставляется сервером автоматически.
   * В `BudgetTable` добавить опциональную привязку по `Author.id`
   * Дополнить `/budget/add` возможностью указать ID автора (опциональное поле)
   * В элементах ответа `/budget/year/{year}/stats` выводить ФИО автора, если он указан для записи, а также время создания записи автора.
   * Добавить в параметры запроса `/budget/year/{year}/stats` опциональный фильтр по ФИО автора и фильтровать по совпадению подстроки игнорируя регистр
