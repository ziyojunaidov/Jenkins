import POJOClasses.Location;
import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ZipcodeAPITest {

    @Test
    public void Test() {
        given() //-> Preparation(Token, Request Body, parameters, cookies)
                .when() //-> To send the request(Request method, request url)
                .then(); //-> Response(Response body, tests, extract data, set local or global variables)
    }

    @Test
    public void statusCodeTest() {
        given()
                .when()

                .get("http://api.zippopotam.us/us/90210") //Set up request method and url
                .then()

                .log().status() // prints the status code
                .log().body()  // prints the response body
                .statusCode(200);
    }

    @Test
    public void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .contentType(ContentType.JSON); //Test if the response is in correct form(JSON)
    }

    @Test
    public void countryInformationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("country", equalTo("United States")); // Tests if the country value is correct
    }

    @Test
    public void stateInformationTest() {
        // Send a request to "http://api.zippopotam.us/us/90210"
        // and check if the state is "California"

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California"));

    }

    // Send a request to "http://api.zippopotam.us/us/90210"
    // and check if the state abbreviation is "CA"
    @Test
    public void stateAbbreviationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].'state abbreviation'", equalTo("CA"));
    }

    @Test
    public void bodyHasItem() {
        // Send a request to "http://api.zippopotam.us/tr/01000"
        // and check if the body has "Büyükdikili Köyü"

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü"));
        //When we don't use index it gets all place names from the response and creates an array  with them.
        //hasItem checks if that array contains "Büyükdikili Köyü" value in it
    }

    @Test
    public void arrayHasSizeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places.'place name'", hasSize(71)); // Test if the size of the list is correct(71)
    }

    @Test
    public void multipleTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("places", hasSize(71)) // Tests if places array's size is 71
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .body("country", equalTo("Turkey"));
        //If one of the methods fails, then the entire @Test will be failed
    }

    //Parameters
    //There are 2 types of parameters
    // 1) Path parameters -> http://api.zippopotam.us/tr/01000 -> They're parts of the url
    // 2) Query parameters -> They are separated by a ? mark

    @Test
    public void pathParametersTest1() {
        String countryCode = "us";
        String zipcode = "90210";

        given()
                .pathParam("country", countryCode)
                .pathParam("zip", zipcode)
                .log().uri()
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .statusCode(200);
    }

    // send a get request for zipcodes between 90210 and 90213 and verify that in all responses the size
    // of the place array is 1
    @Test
    public void pathParametersTest2() {
        for (int i = 90210; i <= 90213; i++) {
            given()
                    .when()
                    .pathParam("zip", i)

                    .get("http://api.zippopotam.us/us/{zip}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1));
        }

    }
    @Test
    public void queryParametersTest1(){
        given()
                .param("page",2)
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/users")
                .then()
                .log().body()
                .statusCode(200);
    }
    // send the same request for the pages between 1-10 and check if
    // the page number we send from request and page number we get from response are the same
    @Test
    public void queryParametersTest2(){
        for (int i = 1; i < 10; i++) {


            given()
                    .param("page", i)
                    .pathParam("apiName","users")
                    .pathParam("version","v1")
                    .log().uri()
                    .when()
                    .get("cusers")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }
    // Write the same test with Data Provider
@Test(dataProvider = "parameters")
public void queryParametersTestWithDataProvider(int pageNumber, String apiName, String version){
    given()
            .param("page", pageNumber)
            .pathParam("apiName",apiName)
            .pathParam("version",version)
            .log().uri()
            .when()
            .get("https://gorest.co.in/public/{version}/{apiName}")
            .then()
            .log().body()
            .statusCode(200)
            .body("meta.pagination.page", equalTo(pageNumber));
}
    @DataProvider
    public Object[][] parameters(){
        Object[][] parametersList ={
                {1,"users", "v1"},
                {2,"users", "v1"},
                {3,"users", "v1"},
                {4,"users", "v1"},
                {5,"users", "v1"},
                {6,"users", "v1"},
                {7,"users", "v1"},
                {8,"users", "v1"},
                {9,"users", "v1"},
                {10,"users", "v1"},
        };
        return parametersList;
    }
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    @BeforeClass
    public void setUp(){
        baseURI = "https://gorest.co.in/public";
        // if the request url in the request method doesn't have http part
        // rest assured puts baseURI to the beginning of the url in the request method

        //if we are using the same things for our requests in our tests we can put them in requestSpecification
        // so we don't have to write them again and again
        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .addPathParam("apiName","users")
                .addPathParam("version","v1")
                .addParam("page", 3)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("meta.pagination.page",equalTo(3))
                .build();
    }
    @Test
    public void baseURITest(){
        given()
                .param("page",3)
                .log().uri()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test
    public void specificationTest(){
        given()

                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification);

    }
    @Test
    public void extractStringTest(){
        String placeName = given()
                .pathParam("country", "us")
                .pathParam("zip", "90210")
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .extract().path("places[0].'place name'");

        //with extract method our result returns a value(not an object)
        //extract returns only one part of the response(the part that we specified in the path method) or list of that value
        // we can assign it to a variable and use it however we want

        System.out.println("placeName = " + placeName);
    }
    @Test
    public void extractIntTest(){
        int pageNumber = given()

                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.page");

        System.out.println("pageNumber = " + pageNumber);

        //We are not allowed to assign an int to a String(cannot assign a type to another type)
    }
    @Test
    public void extractListTest1(){
        List<String> nameList = given()

                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.name");

        System.out.println("nameList = " + nameList);
        System.out.println("nameList.get(4) = " + nameList.get(4));
        System.out.println("nameList.contains(\"Ravi Adiga\") = " + nameList.contains("Ravi Adiga"));

        Assert.assertTrue(nameList.contains("Ravi Adiga"));
    }
    // Send a request to https://gorest.co.in/public/v1/users?page=3
    // and extract email values from the response and check if they contain patel_atreyee_jr@gottlieb.test
    @Test
    public void extractListTest2(){
        List<String> emailList = given()

                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.email");
        System.out.println("emailList = " + emailList);

        Assert.assertTrue(emailList.contains("patel_atreyee_jr@gottlieb.test"));
    }
    // Send a request to https://gorest.co.in/public/v1/users?page=3
    // and check if the next link value contains page=4
    @Test
    public void nextLinkTest(){
        String next = given()

                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.links.next");

        System.out.println("next = " + next);
        Assert.assertTrue(next.contains("page=4"));
    }
    @Test
    public void extractResponse(){
        Response response = given()

                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response();
        //The entire request returns the entire response as a Response object
        //By using this object we are able to extract multiple values with one request
        int page = response.path("meta.pagination.page");
        System.out.println("page = " + page);

        String currentUrl = response.path("meta.pagination.links.current");
        System.out.println("currentUrl = " + currentUrl);

        String name = response.path("data[1].name");
        System.out.println("name = " + name);

        List<String> emailList = response.path("data.email");
        System.out.println("emailList = " + emailList);

        // extract.path           vs                 extract.response
        // extract.path() can only give us one part of the response. If you need different values from different parts of the response (names and page)
        // you need to write two different request.
        // extract.response() gives us the entire response as an object so if you need different values from different parts of the response (names and page)
        // you can get them with only one request
    }
    @Test
    public void extractJSONPOJO(){
        //POJO -> Plain old java object
        Location location = given()
                .pathParam("country", "us")
                .pathParam("zip", "90210")
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .extract().as(Location.class);
        System.out.println("location.getPostcode() = " + location.getPostcode());
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getState() = " + location.getPlaces().get(0).getState());

        // This request extracts the entire response and assigns it to Location class as a Location object
        // We cannot extract the body partially (e.g. cannot extract place object separately)
    }
    //extract path() -> We can extract only one value(String, int...) or list of that value(List<String, List<integer>)
    // String name = extract.path(data[0].name)
    // List<String> nameList = extract.path(data.name)

    //extract response -> We can get the entire body Response as a response object and get whatever we want from it.
    // We don't need a class structure. But if you need to use an object for your next requests it is not useful

    //extract as -> We can extract the entire response body as POJO classes. But We cannot extract one part of the body
    // separately. We need to create a class structure for the entire body
    //extract.as(Location.class)
    //extract.as(User.class)
    //extract.as(Place.class) is not allowed

    //extract.jsonpath() -> We can extract the entire body as POJO classes as well as the only one part of the body
    // .So if you need only one part of the body you don't need to create a class structure for the entire body
    // .You only need class for that part of the body
    @Test
    public void extractWithJsonpath(){
       User user =  given()
                .spec(requestSpecification)
                .when()
                .get("{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getObject("data[0]", User.class);

        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getName() = " + user.getName());
        System.out.println("user.getEmail() = " + user.getEmail());
        System.out.println("user.getGender() = " + user.getGender());
        System.out.println("user.getStatus() = " + user.getStatus());
    }
    @Test
    void extractWithJsonPath3() {

        String name = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getString("data[1].name");

        System.out.println("name = " + name);
    }

    @Test
    void extractWithJsonPath4() {
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response();

        int page = response.jsonPath().getInt("meta.pagination.page");
        System.out.println("page = " + page);

        String currentLink = response.jsonPath().getString("meta.pagination.links.current");
        System.out.println("currentLink = " + currentLink);

        User user = response.jsonPath().getObject("data[2]", User.class);
        System.out.println("user.getName() = " + user.getName());

        List<User> userList = response.jsonPath().getList("data", User.class);
        System.out.println("userList.size() = " + userList.size());
    }
}
