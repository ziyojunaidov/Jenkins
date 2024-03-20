import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsers {

    public String randomName(){
        return RandomStringUtils.randomAlphabetic(10);
    }
    public String randomEmail(){
        return RandomStringUtils.randomAlphanumeric(7) + "@techno.com";
    }
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    @BeforeClass
    public void setUp(){
        baseURI = "https://gorest.co.in/public/v2/users";

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization","Bearer e19cbe01d184b4785dd437eb6b91b759a4a20f957e6938fa3cac3510ca68759b")
                .setContentType(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectContentType(ContentType.JSON)
                .build();
    }
    @Test
    public void getUserList(){
        given()
                .when()
                .get() //Since the entire url is our base URI, we don't need to use anything in request method
                .then()
                //.log().body()
                .statusCode(200)
                //.contentType(ContentType.JSON)
                .spec(responseSpecification)
                .body("",hasSize(10));

    }
    @Test
    public void createNewUser(){
        given()
                //.header("Authorization","Bearer e19cbe01d184b4785dd437eb6b91b759a4a20f957e6938fa3cac3510ca68759b")
                .body("{\"name\":\""+randomName()+"\",\"gender\":\"male\",\"email\":\""+randomEmail()+"\",\"status\":\"active\"}")
                //.contentType(ContentType.JSON)
                .spec(requestSpecification)
                .when()
                .post()
                .then()
                //.log().body()
                .statusCode(201)
                //.contentType(ContentType.JSON);
                .spec(responseSpecification);
    }
    @Test
    public void createNewUserWithMaps(){
        Map<String, String> user = new HashMap<>();
        user.put("name", randomName());
        user.put("gender","male");
        user.put("email", randomEmail());
        user.put("status","active");
        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email", equalTo(user.get("email")))
                .body("name",equalTo(user.get("name")));
    }
    User user;
    User userFromResponse;
    @Test
    public void createNewUserWithObjects(){
         user = new User(randomName(),randomEmail(),"male","active");
//        user.setName(randomName());
//        user.setEmail(randomEmail());
//        user.setGender("male");
//        user.setStatus("active");

        userFromResponse =  given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email",equalTo(user.getEmail()))
                .body("name",equalTo(user.getName()))
                .extract().as(User.class);
    }
    @Test(dependsOnMethods = "createNewUserWithObjects")
    public void createUserNegativeTest(){
        User userNegative = new User(randomName(),user.getEmail(),"male","active");

        given()
                .spec(requestSpecification)
                .body(userNegative)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(422);
    }
    /**
     * get the user you created in createNewUserWithObject test
     **/
    @Test(dependsOnMethods = "createNewUserWithObjects")
    public void getUserById(){
        given()
                .pathParam("userId",userFromResponse.getId())
                .spec(requestSpecification)
                .when()
                .get("{userId}")
                .then()
                .spec(responseSpecification)
                .body("id",equalTo(userFromResponse.getId()))
                .body("name", equalTo(userFromResponse.getName()))
                .body("email", equalTo(userFromResponse.getEmail()));

    }
    /**
     * Update the user you created in createNewUserWithObjects
     */
    @Test(dependsOnMethods = "createNewUserWithObjects")
    public void updateUser(){

        User updateUser = new User(randomName(),randomEmail(),"male","active");

//        userFromResponse.setName(randomName());
//        userFromResponse.setEmail(randomEmail());

        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .body(updateUser)
                .when()
                .put("{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("id",equalTo(userFromResponse.getId()))
//                .body("email", equalTo(userFromResponse.getEmail()))
//                .body("name", equalTo(userFromResponse.getName()));
                .body("email", equalTo(updateUser.getEmail()))
                .body("name", equalTo(userFromResponse.getName()));

    }
    /**
     * Delete the user you created in createNewUserWithObject
     **/
    @Test(dependsOnMethods = "createNewUserWithObjects")
    public void deleteUser(){
        given()
                .spec(requestSpecification)
                .pathParam("userId",userFromResponse.getId())
                .when()
                .delete("{userId}")
                .then()
                .statusCode(204);
    }
    /**
     * create delete user negative test
     **/
    @Test(dependsOnMethods = {"createNewUserWithObjects","deleteUser"})
    public void deleteUserNegativeTest(){
        given()
                .spec(requestSpecification)
                .pathParam("uerId",userFromResponse.getId())
                .when()
                .delete("{userId}")
                .then()
                .statusCode(404);
    }
    @Test(dependsOnMethods = {"createNewUserWithObjects","deleteUser"})
    public void getUserByIdNegativeTest(){
        given()
                .pathParam("userId",user.getId())
                .spec(requestSpecification)
                .when()
                .get("{userId}")
                .then()
                .statusCode(404);
    }
}
