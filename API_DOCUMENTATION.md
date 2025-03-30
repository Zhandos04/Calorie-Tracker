# Документация API для сервиса Calorie Tracker

## Общая информация

Базовый URL: `http://localhost:8080/api`

Формат запросов и ответов: JSON

Все запросы, требующие тело (POST, PUT), должны иметь Content-Type: `application/json`

## Пользователи (Users)

### Создание нового пользователя

**Запрос:**
```
POST /users
```

**Тело запроса:**
```json
{
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "age": 30,
  "weight": 80.5,
  "height": 180,
  "goal": "WEIGHT_LOSS" 
}
```

Допустимые значения для `goal`: `WEIGHT_LOSS`, `MAINTENANCE`, `WEIGHT_GAIN`

**Ответ:** HTTP 201 Created
```json
{
  "id": 1,
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "age": 30,
  "weight": 80.5,
  "height": 180,
  "goal": "WEIGHT_LOSS",
  "dailyCalorieTarget": 2100
}
```

### Получение списка всех пользователей

**Запрос:**
```
GET /users
```

**Ответ:** HTTP 200 OK
```json
[
  {
    "id": 1,
    "name": "Иван Иванов",
    "email": "ivan@example.com",
    "age": 30,
    "weight": 80.5,
    "height": 180,
    "goal": "WEIGHT_LOSS",
    "dailyCalorieTarget": 2100
  },
  {
    "id": 2,
    "name": "Мария Петрова",
    "email": "maria@example.com",
    "age": 28,
    "weight": 62.0,
    "height": 165,
    "goal": "MAINTENANCE",
    "dailyCalorieTarget": 1850
  }
]
```

### Получение пользователя по ID

**Запрос:**
```
GET /users/{id}
```

**Ответ:** HTTP 200 OK
```json
{
  "id": 1,
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "age": 30,
  "weight": 80.5,
  "height": 180,
  "goal": "WEIGHT_LOSS",
  "dailyCalorieTarget": 2100
}
```

### Обновление данных пользователя

**Запрос:**
```
PUT /users/{id}
```

**Тело запроса:**
```json
{
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "age": 30,
  "weight": 78.5,
  "height": 180,
  "goal": "WEIGHT_LOSS"
}
```

**Ответ:** HTTP 200 OK
```json
{
  "id": 1,
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "age": 30,
  "weight": 78.5,
  "height": 180,
  "goal": "WEIGHT_LOSS",
  "dailyCalorieTarget": 2050
}
```

### Удаление пользователя

**Запрос:**
```
DELETE /users/{id}
```

**Ответ:** HTTP 204 No Content

## Блюда (Foods)

### Создание нового блюда

**Запрос:**
```
POST /foods
```

**Тело запроса:**
```json
{
  "name": "Куриная грудка",
  "caloriesPerServing": 165,
  "proteins": 31.0,
  "fats": 3.6,
  "carbohydrates": 0.0
}
```

**Ответ:** HTTP 201 Created
```json
{
  "id": 1,
  "name": "Куриная грудка",
  "caloriesPerServing": 165,
  "proteins": 31.0,
  "fats": 3.6,
  "carbohydrates": 0.0
}
```

### Получение списка всех блюд

**Запрос:**
```
GET /foods
```

**Ответ:** HTTP 200 OK
```json
[
  {
    "id": 1,
    "name": "Куриная грудка",
    "caloriesPerServing": 165,
    "proteins": 31.0,
    "fats": 3.6,
    "carbohydrates": 0.0
  },
  {
    "id": 2,
    "name": "Гречневая каша",
    "caloriesPerServing": 132,
    "proteins": 4.5,
    "fats": 0.9,
    "carbohydrates": 25.0
  }
]
```

### Получение блюда по ID

**Запрос:**
```
GET /foods/{id}
```

**Ответ:** HTTP 200 OK
```json
{
  "id": 1,
  "name": "Куриная грудка",
  "caloriesPerServing": 165,
  "proteins": 31.0,
  "fats": 3.6,
  "carbohydrates": 0.0
}
```

### Поиск блюд по названию

**Запрос:**
```
GET /foods/search?name=курин
```

**Ответ:** HTTP 200 OK
```json
[
  {
    "id": 1,
    "name": "Куриная грудка",
    "caloriesPerServing": 165,
    "proteins": 31.0,
    "fats": 3.6,
    "carbohydrates": 0.0
  },
  {
    "id": 5,
    "name": "Куриный суп",
    "caloriesPerServing": 120,
    "proteins": 8.0,
    "fats": 5.0,
    "carbohydrates": 10.0
  }
]
```

### Обновление блюда

**Запрос:**
```
PUT /foods/{id}
```

**Тело запроса:**
```json
{
  "name": "Куриная грудка запеченная",
  "caloriesPerServing": 160,
  "proteins": 30.0,
  "fats": 3.2,
  "carbohydrates": 0.0
}
```

**Ответ:** HTTP 200 OK
```json
{
  "id": 1,
  "name": "Куриная грудка запеченная",
  "caloriesPerServing": 160,
  "proteins": 30.0,
  "fats": 3.2,
  "carbohydrates": 0.0
}
```

### Удаление блюда

**Запрос:**
```
DELETE /foods/{id}
```

**Ответ:** HTTP 204 No Content

## Приемы пищи (Meals)

### Создание нового приема пищи

**Запрос:**
```
POST /meals
```

**Тело запроса:**
```json
{
  "userId": 1,
  "mealDate": "2025-03-30",
  "mealTime": "12:00:00",
  "mealType": "Обед",
  "mealFoods": [
    {
      "foodId": 1,
      "servings": 1.0
    },
    {
      "foodId": 2,
      "servings": 1.5
    }
  ]
}
```

**Ответ:** HTTP 201 Created
```json
{
  "id": 1,
  "userId": 1,
  "mealDate": "2025-03-30",
  "mealTime": "12:00:00",
  "mealType": "Обед",
  "mealFoods": [
    {
      "id": 1,
      "foodId": 1,
      "foodName": "Куриная грудка",
      "servings": 1.0,
      "calories": 165,
      "proteins": 31.0,
      "fats": 3.6,
      "carbohydrates": 0.0
    },
    {
      "id": 2,
      "foodId": 2,
      "foodName": "Гречневая каша",
      "servings": 1.5,
      "calories": 198,
      "proteins": 6.75,
      "fats": 1.35,
      "carbohydrates": 37.5
    }
  ],
  "totalCalories": 363,
  "totalProteins": 37.75,
  "totalFats": 4.95,
  "totalCarbohydrates": 37.5
}
```

### Получение приема пищи по ID

**Запрос:**
```
GET /meals/{id}
```

**Ответ:** HTTP 200 OK
```json
{
  "id": 1,
  "userId": 1,
  "mealDate": "2025-03-30",
  "mealTime": "12:00:00",
  "mealType": "Обед",
  "mealFoods": [
    {
      "id": 1,
      "foodId": 1,
      "foodName": "Куриная грудка",
      "servings": 1.0,
      "calories": 165,
      "proteins": 31.0,
      "fats": 3.6,
      "carbohydrates": 0.0
    },
    {
      "id": 2,
      "foodId": 2,
      "foodName": "Гречневая каша",
      "servings": 1.5,
      "calories": 198,
      "proteins": 6.75,
      "fats": 1.35,
      "carbohydrates": 37.5
    }
  ],
  "totalCalories": 363,
  "totalProteins": 37.75,
  "totalFats": 4.95,
  "totalCarbohydrates": 37.5
}
```

### Получение приемов пищи пользователя за день

**Запрос:**
```
GET /meals/user/{userId}/date/{date}
```

Пример: `GET /meals/user/1/date/2025-03-30`

**Ответ:** HTTP 200 OK
```json
[
  {
    "id": 1,
    "userId": 1,
    "mealDate": "2025-03-30",
    "mealTime": "08:00:00",
    "mealType": "Завтрак",
    "mealFoods": [...],
    "totalCalories": 320,
    "totalProteins": 15.5,
    "totalFats": 10.2,
    "totalCarbohydrates": 42.0
  },
  {
    "id": 2,
    "userId": 1,
    "mealDate": "2025-03-30",
    "mealTime": "12:00:00",
    "mealType": "Обед",
    "mealFoods": [...],
    "totalCalories": 363,
    "totalProteins": 37.75,
    "totalFats": 4.95,
    "totalCarbohydrates": 37.5
  }
]
```

### Получение приемов пищи пользователя за период

**Запрос:**
```
GET /meals/user/{userId}/period?startDate={date}&endDate={date}
```

Пример: `GET /meals/user/1/period?startDate=2025-03-30&endDate=2025-03-31`

**Ответ:** HTTP 200 OK
```json
[
  {
    "id": 1,
    "userId": 1,
    "mealDate": "2025-03-30",
    "mealTime": "08:00:00",
    "mealType": "Завтрак",
    "mealFoods": [...],
    "totalCalories": 320,
    "totalProteins": 15.5,
    "totalFats": 10.2,
    "totalCarbohydrates": 42.0
  },
  ...
]
```

### Обновление приема пищи

**Запрос:**
```
PUT /meals/{id}
```

**Тело запроса:**
```json
{
  "mealDate": "2025-03-30",
  "mealTime": "12:30:00",
  "mealType": "Обед",
  "mealFoods": [
    {
      "foodId": 1,
      "servings": 1.5
    },
    {
      "foodId": 3,
      "servings": 1.0
    }
  ]
}
```

**Ответ:** HTTP 200 OK
```json
{
  "id": 1,
  "userId": 1,
  "mealDate": "2025-03-30",
  "mealTime": "12:30:00",
  "mealType": "Обед",
  "mealFoods": [...],
  "totalCalories": 420,
  "totalProteins": 45.5,
  "totalFats": 10.2,
  "totalCarbohydrates": 35.0
}
```

### Удаление приема пищи

**Запрос:**
```
DELETE /meals/{id}
```

**Ответ:** HTTP 204 No Content

## Отчеты (Reports)

### Получение отчета о питании за день

**Запрос:**
```
GET /reports/daily/{userId}/{date}
```

Пример: `GET /reports/daily/1/2025-03-30`

**Ответ:** HTTP 200 OK
```json
{
  "userId": 1,
  "userName": "Иван Иванов",
  "date": "2025-03-30",
  "dailyCalorieTarget": 2100,
  "totalCaloriesConsumed": 1850,
  "totalProteinsConsumed": 96.5,
  "totalFatsConsumed": 45.2,
  "totalCarbohydratesConsumed": 180.0,
  "withinCalorieTarget": true,
  "calorieDeficit": 250,
  "meals": [
    {
      "id": 1,
      "mealType": "Завтрак",
      "mealTime": "08:00:00",
      "totalCalories": 320,
      "mealFoods": [...]
    },
    {
      "id": 2,
      "mealType": "Обед",
      "mealTime": "12:00:00",
      "totalCalories": 750,
      "mealFoods": [...]
    },
    {
      "id": 3,
      "mealType": "Ужин",
      "mealTime": "19:00:00",
      "totalCalories": 780,
      "mealFoods": [...]
    }
  ]
}
```

### Проверка соответствия дневной нормы калорий

**Запрос:**
```
GET /reports/calorie-check/{userId}/{date}
```

Пример: `GET /reports/calorie-check/1/2025-03-30`

**Ответ:** HTTP 200 OK
```json
{
  "userId": 1,
  "date": "2025-03-30",
  "dailyCalorieTarget": 2100,
  "caloriesConsumed": 1850,
  "withinTarget": true,
  "calorieDeficit": 250
}
```

### История питания по дням

**Запрос:**
```
GET /reports/history/{userId}?startDate={date}&endDate={date}
```

Пример: `GET /reports/history/1?startDate=2025-03-30&endDate=2025-03-31`

**Ответ:** HTTP 200 OK
```json
[
  {
    "userId": 1,
    "userName": "Иван Иванов",
    "date": "2025-03-30",
    "dailyCalorieTarget": 2100,
    "totalCaloriesConsumed": 1850,
    "totalProteinsConsumed": 96.5,
    "totalFatsConsumed": 45.2,
    "totalCarbohydratesConsumed": 180.0,
    "withinCalorieTarget": true,
    "calorieDeficit": 250,
    "meals": [...]
  },
  {
    "userId": 1,
    "userName": "Иван Иванов",
    "date": "2025-03-31",
    "dailyCalorieTarget": 2100,
    "totalCaloriesConsumed": 2200,
    "totalProteinsConsumed": 105.0,
    "totalFatsConsumed": 50.5,
    "totalCarbohydratesConsumed": 190.0,
    "withinCalorieTarget": false,
    "calorieDeficit": -100,
    "meals": [...]
  }
]
```

### Получение дат, в которые пользователь принимал пищу

**Запрос:**
```
GET /reports/meal-dates/{userId}
```

Пример: `GET /reports/meal-dates/1`

**Ответ:** HTTP 200 OK
```json
[
  "2025-03-28",
  "2025-03-29",
  "2025-03-30",
  "2025-03-31"
]
```

## Обработка ошибок

### Ошибка валидации данных

**Ответ:** HTTP 400 Bad Request
```json
{
  "name": "Имя не может быть пустым",
  "age": "Возраст должен быть положительным числом"
}
```

### Ресурс не найден

**Ответ:** HTTP 404 Not Found
```json
{
  "timestamp": "2025-03-30T14:15:28.235+00:00",
  "message": "Пользователь не найден с id : '999'",
  "details": "uri=/api/users/999"
}
```

### Внутренняя ошибка сервера

**Ответ:** HTTP 500 Internal Server Error
```json
{
  "timestamp": "2025-03-30T14:20:15.235+00:00",
  "message": "Произошла внутренняя ошибка сервера",
  "details": "uri=/api/reports/daily/1/2025-03-30"
}
```