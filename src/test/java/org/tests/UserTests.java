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
        String newUser = "{ \"id\": 1, \"username\": \"johndoe\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\", \"phone\": \"123-456-7890\", \"userStatus\": 1 }";
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
        String newUser = "{ \"id\": 1, \"username\": \"!@#\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\", \"phone\": \"123-456-7890\", \"userStatus\": 1 }";

        given()
                .header("Content-Type", "application/json")
                .body(newUser)
                .when()
                .post("/user")
                .then()
                .statusCode(400);
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
    //т.к. при создании пользователя считай данный ввод валидным
    @Test
    public void testGetInvalidUsername() {
        given()
                .when()
                .get("/user/{username}", "!@#") // Используем строку с недопустимыми символами
                .then()
                .statusCode(400); // Ожидаем код 400 за некорректный запрос
    }




    @Test
    public void testUpdateUser() {
        String updatedUser = "{ \"id\": 1, \"username\": \"johndoe\", \"firstName\": \"Johnny\", \"lastName\": \"Doe\", \"email\": \"johnny.doe@example.com\", \"password\": \"newpassword\", \"phone\": \"123-456-7890\", \"userStatus\": 1 }";
        given()
                .header("Content-Type", "application/json")
                .body(updatedUser)
                .when()
                .put("/user/{username}", "johndoe")
                .then()
                .statusCode(200);
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
    public void testLoginUser() {
        given()
                .queryParam("username", "johndoe")
                .queryParam("password", "password123")
                .when()
                .get("/user/login")
                .then()
                .statusCode(200);
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
