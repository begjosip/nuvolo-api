<p align="center">
<img src="https://github.com/begjosip/nuvolo-mail/blob/master/templates/images/nuvolo.png?raw=true" alt="logo"/>
</p>

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<div align="center">

![Java: Java](https://img.shields.io/badge/spring-3164341?style=for-the-badge&logo=spring&logoColor=fff)
![PostgreSQL: PostgreSQL](https://img.shields.io/badge/-postgresql-%230064a5?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis: Redis](https://img.shields.io/badge/-redis-%23D82C20?style=for-the-badge&logo=redis&logoColor=white)
![RabbitMQ: RabbitMQ](https://img.shields.io/badge/-rabbitmq-%23FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Flyway: Flyway](https://img.shields.io/badge/-redis-%23cc0000?style=for-the-badge&logo=flyway&logoColor=white)
![Hibernate: Hibernate](https://img.shields.io/badge/-hibernate-%235A5539?style=for-the-badge&logo=hibernate&logoColor=white)
![JUnit5: JUnit5](https://img.shields.io/badge/-junit5-%2325a162?style=for-the-badge&logo=junit5&logoColor=white)
![Docker: Docker](https://img.shields.io/badge/-docker-%230db7ed?style=for-the-badge&logo=docker&logoColor=white)
![SonarLint: SonarLint](https://img.shields.io/badge/-SonarLint-%23301934?style=for-the-badge&logo=SonarLint&logoColor=white)

</div>

---

### Description

This project is the backend service for an e-commerce application, providing robust user authentication, product
management, and order processing functionalities. It leverages technologies like Spring Boot, PostgreSQL, Redis,
RabbitMQ and to ensure high performance and scalability. Key features include user registration and login,
product CRUD operations, shopping cart management, and integration with payment gateways.

---

### Requirements

- **Java 21**
- **PostgreSQL 15**
- **RabbitMQ 3.13**
- **Redis**

---

### Instructions

To run application you need to fulfill all requirements from above. In the root of the project there is a `docker`
folder with given structure:

```
docker/
|__ docker-compose.yml
|__ rabbitmq/
    |__ definitions.json
    |__ rabbitmq.conf
```

In `docker-compose.yml` there is a script for downloading required images and setting containers for local development
out of the box.
Positioned in project directory correctly change directory to `docker` with `cd docker` and run following commands:

`docker compose up -d`

This will set up **postgres**, **rabbitmq** and **redis** containers for local development.

At this moment application has two application properties files.
Default one `application.properties` and properties for development environment `application-dev.properties`. For
running application in development environment and using properties from default and development file use
command from project root directory:

```./gradlew bootRun --args='--spring.profiles.active=dev'```

All logs will be written to `logs/nuvolo.log` and when reaching size of 10MB they will be archived.

---

### API Documentation

#### Authentication controller

**POST >** _/api/v1/auth/register_

Description: Registration of new user. Endpoint is not protected and it is publicly available.

```json
{
    "firstName": "Nuvolo",
    "lastName": "Nuvolo",
    "email": "nuvolo@nuvolo.com",
    "password": "nuvolo!?",
    "confirmPassword": "nuvolo!?"
}
```

**201 CREATED**

---

**POST >** _/api/v1/auth/sign-in_

Description: Sign in. Endpoint is not protected and it is publicly available.

```json
{
    "email": "nuvolo@nuvolo.com",
    "password": "nuvolo!?"
}
```

**200 OK**

```json
{
    "id": 59,
    "firstName": "Nuvolo",
    "lastName": "Nuvolo",
    "email": "nuvolo@nuvolo.com",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJudXZvbG8tYXBpIiwic3ViIjoiam9zaXBiZWdpYzU3QGdtYWlsLmNvbSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzE2MzM0MTEwLCJleHAiOjE3MTYzMzUwMTB9.7aWyab10ppubQQ9Ty0zDG994qUWuXpOkCfvBTYRUhjN8kZxI8XchXQK-4iYwZY198pOc8MQhw7knYBTA3yzLIw"
}
```

---

**POST >** _/api/v1/auth/verify/{token}_

Description: Verification of user with token over email. Endpoint is not protected and it is publicly available.

**200 OK**

---

**POST >** _/api/v1/auth/request-password-reset_

Description: User request forgotten password reset. As a result email with password reset link is sent over email
service.

```json
{
    "email": "nuvolo@nuvolo.com"
}
```

**200 OK**

---

**POST >** _/api/v1/auth/reset-password_

Description: User request password reset.

```json
{
    "email": "nuvolo@nuvolo.com",
    "password": "nuvolo!?",
    "confirmPassword": "nuvolo!?",
    "token": "eee2be08-254f-48f7-b63c-5cc55805219f"
}
```

**200 OK**


#### Admin controller

**GET >** _/api/v1/admin/users_

Description: Get list of all users.

**200 OK**

---

**POST >** _/api/v1/admin/discount_

Description: Admin request for discount creation.

```json
{
    "name": "Discount Name",
    "description": "Christmas discount",
    "discountPercentage": 0.50,
    "startDate": "2024-06-01T12:00:00",
    "endDate": "2024-07-30T12:00:00"
}
```

**201 CREATED**

---

**GET >** _/api/v1/admin/discount_

Description: Get all discounts.

**200 OK**

---

**POST >** _/api/v1/admin/category_

Description: Admin request for category creation.

```json
{
    "name": "Category Name",
    "description": "Category description"
}
```

**201 CREATED**

---

### Database

#### PostgreSQL

Database is set up using Flyway schemas. If some schemas are changed or models can not be validated application will not
be able to run.

| Table Name         | Description                                           |
|--------------------|-------------------------------------------------------|
| nuvolo_user        | Stores information about users.                       |
| verification       | Stores verification tokens for user accounts.         |
| role               | Stores user roles.                                    |
| address            | Stores user addresses.                                |
| user_role          | Associates users with their roles.                    |
| category           | Stores product categories.                            |
| discount           | Stores discounts for products.                        |
| type               | Stores product type.                                  |
| product_inventory  | Stores information about products stocks.             |
| product            | Stores information products.                          |
| order_details      | Stores order details and association with user.       |
| order_item         | Stores information about ordered products.            |
| review             | Stores data about product reviews.                    |
| forgotten_password | Stores data about forgotten passwords reset requests. |

--- 

#### Redis

Redis is used for storing shopping sessions for users. It is using generated session ID as key which is provided in HTTP
request if session already exists and returns object of `ShoppingSession` instance.

```java
@Data
public class ShoppingSession {
    private Long userId;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private List<CartItem> cartItems;
}
```

```java
@Data
public class CartItem {
    private Long productId;
    private Integer quantity;
}
```








