# Task Manager API

REST API приложение для управления задачами с JWT-аутентификацией.

## Технологии

- **Java 21** *(Amazon Corretto)*
- **Spring Boot 4.1.0**
- **Spring Security** + **JWT**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL 15**
- **Lombok**
- **Docker** + **Docker Compose**
- **Maven**

## Возможности

- Регистрация и аутентификация пользователей
- JWT токены (Access + Refresh)
- CRUD операции с задачами
- Каждая задача привязана к пользователю
- Проверка прав доступа
- Валидация входных данных
- Глобальная обработка ошибок
- Docker контейнеризация

## Требования

- Java 21
- Docker Desktop
- Maven
- PostgreSQL (если запускаете без Docker)

## Запуск с Docker

```bash
# 1. Клонировать репозиторий
git clone https://github.com/yawwwka/TaskManager.git
cd TaskManager

# 2. Собрать JAR
mvn clean package -DskipTests

# 3. Запустить контейнеры
docker-compose up -d

# 4. Проверить логи
docker logs tasks-app -f