package org.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class UserTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    public void testCreateUser() {
        String newUser = "{ \"id\": 1, \"username\": \"johndoe\", \"firstName\": \"John\", " +
                "\"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\", " +
                "\"phone\": \"123-456-7890\", \"userStatus\": 1 }";
        given()
                .header("Content-Type", "application/json")
                .body(newUser)
                .when()
                .post("/user")
                .then()
                .statusCode(200);
    }

    //сервер обрабатывает невалидный формат имени пользователя !@# как верный с кодом ответа 200
    @Test
    public void testCreateUserWithInvalidUsername() {
        String newUser = "{ \"id\": 1, \"username\": \"!@#\", \"firstName\": \"John\", " +
                "\"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\"," +
                " \"phone\": \"123-456-7890\", \"userStatus\": 1 }";

        given()
                .header("Content-Type", "application/json")
                .body(newUser)
                .when()
                .post("/user")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateUsersWithArray() {
        String usersArray = "["
                + "{ \"id\": 1, \"username\": \"user1\", \"firstName\": \"John\", \"lastName\": \"Doe\", " +
                "\"email\": \"john.doe1@example.com\", \"password\": \"password1\", \"phone\": \"123-456-7890\", " +
                "\"userStatus\": 1 },"
                + "{ \"id\": 2, \"username\": \"user2\", \"firstName\": \"Jane\", \"lastName\": \"Doe\", " +
                "\"email\": \"jane.doe@example.com\", \"password\": \"password2\", \"phone\": \"098-765-4321\", " +
                "\"userStatus\": 2 }"
                + "]";

        given()
                .header("Content-Type", "application/json")
                .body(usersArray)
                .when()
                .post("/user/createWithArray")
                .then()
                .statusCode(200); 
    }


    @Test
    public void testGetUserByUsername() {
        given()
                .when()
                .get("/user/{username}", "johndoe")
                .then()
                .statusCode(200)
                .body("username", equalTo("johndoe"));
    }

    @Test
    public void testGetNonExistingUser() {
        given()
                .when()
                .get("/user/{username}", "nonexistentuser")
                .then()
                .statusCode(404);
    }

    //сервер интерпретирует некорректное имя пользователя как несуществующего пользователя, а не как невалидный запрос,
    //т.к. при создании пользователя считает данный ввод валидным
    @Test
    public void testGetInvalidUsername() {
        given()
                .when()
                .get("/user/{username}", " ")
                .then()
                .statusCode(400);
    }


    @Test
    public void testUpdateUser() {
        String updatedUser = "{ \"id\": 1, \"username\": \"johndoe\", \"firstName\": \"Johnny\", " +
                "\"lastName\": \"Doe\", \"email\": \"johnny.doe@example.com\", \"password\": \"newpassword\", " +
                "\"phone\": \"123-456-7890\", \"userStatus\": 1 }";
        given()
                .header("Content-Type", "application/json")
                .body(updatedUser)
                .when()
                .put("/user/{username}", "johndoe")
                .then()
                .statusCode(200);
    }

    //API создает нового пользователя вместо того, чтобы возвращать ошибку 404
    @Test
    public void testUpdateNonExistingUser() {
        String updatedUser = "{ \"id\": 1, \"username\": \"nonexistentuser\", \"firstName\": \"Johnny\", " +
                "\"lastName\": \"Doe\", \"email\": \"johnny.doe@example.com\", \"password\": \"newpassword\"," +
                " \"phone\": \"123-456-7890\", \"userStatus\": 1 }";

        given()
                .header("Content-Type", "application/json")
                .body(updatedUser)
                .when()
                .put("/user/{username}", "nouser")
                .then()
                .statusCode(404);
    }

    //API возвращает 200 даже при отправке некорректных данных
    @Test
    public void testUpdateUserWithInvalidData() {
        String invalidUser = "{ \"id\": 1, \"username\": 12345, \"firstName\": \"Johnny\", \"lastName\": \"Doe\" }";

        given()
                .header("Content-Type", "application/json")
                .body(invalidUser)
                .when()
                .put("/user/{username}", " ")
                .then()
                .statusCode(400);
    }

    @Test
    public void testDeleteUser() {
        given()
                .when()
                .delete("/user/{username}", "johndoe")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteNonExistingUser() {
        given()
                .when()
                .delete("/user/{username}", "nouserthere")
                .then()
                .statusCode(404);
    }

    //API ищет и не находит пользователя с невалидным именем и возвращает 404
    @Test
    public void testDeleteUserWithInvalidUsername() {
        given()
                .when()
                .delete("/user/{username}", " ")
                .then()
                .statusCode(400);
    }

    @Test
    public void testLoginUser() {
        given()
                .queryParam("username", "johndoe")
                .queryParam("password", "password123")
                .when()
                .get("/user/login")
                .then()
                .statusCode(200)
                .header("X-Expires-After", notNullValue())
                .header("X-Rate-Limit", notNullValue());
    }

    //API не проверяет корректность введенных данных и возвращает успешный статус
    @Test
    public void testLoginUserWithInvalidCredentials() {
        given()
                .queryParam("username", "johndoe")
                .queryParam("password", "   ") // Используем неверный пароль
                .when()
                .get("/user/login")
                .then()
                .statusCode(400); // Ожидаем код 400 для неверных данных
    }

    @Test
    public void testLogoutUser() {
        given()
                .when()
                .get("/user/logout")
                .then()
                .statusCode(200);
    }
}
