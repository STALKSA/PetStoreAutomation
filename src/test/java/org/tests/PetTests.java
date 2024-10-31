package org.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class PetTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    public void testGetExistingPet() {
        given()
                .when()
                .get("/pet/{petId}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    public void testGetNonExistingPet() {
        given()
                .when()
                .get("/pet/{petId}", 99999)
                .then()
                .statusCode(404);
    }

    @Test
    public void testAddNewPet() {
        String newPet = "{ \"id\": 12345, \"name\": \"Buddy\", \"status\": \"available\" }";
        given()
                .header("Content-Type", "application/json")
                .body(newPet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("Buddy"));
    }

    @Test
    public void testUpdatePetStatus() {
        String updatedPet = "{ \"id\": 12345, \"name\": \"Buddy\", \"status\": \"sold\" }";
        given()
                .header("Content-Type", "application/json")
                .body(updatedPet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("status", equalTo("sold"));
    }

    @Test
    public void testDeletePet() {
        given()
                .when()
                .delete("/pet/{petId}", 12345)
                .then()
                .statusCode(200);
    }

    @Test
    public void testFindPetsByStatus() {
        given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("available")));
    }

    @Test
    public void testUploadPetImage() {
        given()
                .multiPart("file", "test.jpg")
                .when()
                .post("/pet/{petId}/uploadImage", 12345)
                .then()
                .statusCode(200);
    }
}
