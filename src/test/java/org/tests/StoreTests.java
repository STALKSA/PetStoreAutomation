package org.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class StoreTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    public void testGetInventory() {
        given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .body("available", greaterThanOrEqualTo(0));
    }

    @Test
    public void testPlaceOrder() {
        String newOrder = "{ \"id\": 1, \"petId\": 12345, \"quantity\": 2, \"shipDate\": \"2024-11-01T15:52:01.552Z\"," +
                " \"status\": \"placed\", \"complete\": true }";
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .body("status", equalTo("placed"));
    }

    //сервер обрабатывает заказ с petId = -1 и возвращает код 200
    @Test
    public void testPlaceInvalidPetIdOrder() {
        String newOrder = "{ \"id\": 2, \"petId\": -1, \"quantity\": 2, \"shipDate\": \"2024-11-01T15:52:01.552Z\", " +
                "\"status\": \"placed\", \"complete\": true }";
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(400);
    }

    @Test
    public void testPlaceInvalidOrder() {
        String newOrder = "{ \"id\": 2, \"petId\": 12345, \"quantity\": 2, \"shipDate\": , \"status\": \"placed\", \"complete\": true }"; // Некорректный целочисленный ID
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(400);
    }


    @Test
    public void testGetOrderById() {
        given()
                .when()
                .get("/store/order/{orderId}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    public void testGetNonExistingOrder() {
        given()
                .when()
                .get("/store/order/{orderId}", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetNonCorrectInputOrder() {
        given()
                .when()
                .get("/store/order/{orderId}", "abc")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404)));
    }
    @Test
    public void testDeleteOrder() {
        given()
                .when()
                .delete("/store/order/{orderId}", 1)
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteNotFoundOrder() {
        given()
                .when()
                .delete("/store/order/{orderId}", 99999999)
                .then()
                .statusCode(404);
    }

    //API обрабатывает отрицательные или некорректные значения ID как "не найденный ресурс", а не как "некорректный запрос"
    @Test
    public void testDeleteInvalidOrder() {
        given()
                .when()
                .delete("/store/order/{orderId}", -1)
                .then()
                .statusCode(400);
    }


}
