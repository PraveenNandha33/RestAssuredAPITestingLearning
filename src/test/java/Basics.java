import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static  org.hamcrest.Matchers.*;


public class Basics {

    public  static  void main(String[] args)
    {
        //Setting the base URI
        RestAssured.baseURI="https://rahulshettyacademy.com/";
        //Addding the query param , header and body
        //log().all() used to print the logs .This will log the req
        //Body accepts only json
        given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body("{\n" +
                        "  \"location\": {\n" +
                        "    \"lat\": -38.383494,\n" +
                        "    \"lng\": 33.427362\n" +
                        "  },\n" +
                        "  \"accuracy\": 50,\n" +
                        "  \"name\": \"Frontline house\",\n" +
                        "  \"phone_number\": \"(+91) 983 893 3937\",\n" +
                        "  \"address\": \"29, side layout, cohen 09\",\n" +
                        "  \"types\": [\n" +
                        "    \"shoe park\",\n" +
                        "    \"shop\"\n" +
                        "  ],\n" +
                        "  \"website\": \"https://rahulshettyacademy.com\",\n" +
                        "  \"language\": \"French-IN\"\n" +
                        "}\n")
                //in post parameter we should  give the resource
                .when().post("maps/api/place/add/json")
                //Adding assertion for status code
                .then().log().all().assertThat().statusCode(200)
                //Validating the scope in response body is app
                .body("scope",equalTo("APP"))
                //Validating the server name in response header
                .header("server","Apache/2.4.52 (Ubuntu)");
    }
}
