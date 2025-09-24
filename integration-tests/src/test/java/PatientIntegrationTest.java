import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        String loginPaylod = """
                    {
                        "email":"testuser@test.com",
                        "password":"password123"
                    }
                """;

        String token = given()
                .contentType("application/json")
                .body(loginPaylod)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients");

        System.out.println("Response Body (Pretty Print):");
        response.getBody().prettyPrint();

        response.then()
                .statusCode(200)
                .body("patients", notNullValue());
    }
}
