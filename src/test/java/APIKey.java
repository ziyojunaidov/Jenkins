import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class APIKey {
    /**
     * Use https://www.weatherapi.com/docs/ as a reference.
     * First, You need to signup to weatherapi.com, and then you can find your API key under your account
     * after that, you can use Java to request: http://api.weatherapi.com/v1/current.json?key=[YOUR-APIKEY]&q=Indianapolis&aqi=no
     * Parse the json and print the current temperature in F and C.
     **/
    @Test
    public void weatherAPI(){
        Response response = given()
                .param("key","f05ce31fee9b4817a8501337241503") // API key
                .param("q", "Buffalo")
                .log().uri()
                .when()
                .get("http://api.weatherapi.com/v1/current.json")
                .then()
                .log().body()
                .extract().response();

        double tempC = response.jsonPath().getDouble("current.temp_c");
        double tempF = response.jsonPath().getDouble("current.temp_f");

        System.out.println("tempF = " + tempF);
        System.out.println("tempC = " + tempC);
    }
}
