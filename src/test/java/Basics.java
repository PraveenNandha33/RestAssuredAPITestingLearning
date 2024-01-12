import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static  org.hamcrest.Matchers.*;


public class Basics {

    @Test
    public static void BasicsOfRestAssured(){
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

    @Test
    public static void testingWithPayload(){
        RestAssured.baseURI="https://rahulshettyacademy.com/";
        //Addding the query param , header and body
        //log().all() used to print the logs .This will log the req
        //Body accepts only json
        given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                //Fetched the body from payload class
                .body(payload.addPlace())
                //in post parameter we should  give the resource
                .when().post("maps/api/place/add/json")
                //Adding assertion for status code
                .then().log().all().assertThat().statusCode(200)
                //Validating the scope in response body is app
                .body("scope",equalTo("APP"))
                //Validating the server name in response header
                .header("server","Apache/2.4.52 (Ubuntu)");
    }

    public  static void learnExtractionAndJSONPath(){
        RestAssured.baseURI="https://rahulshettyacademy.com/";
        String res=given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                //Fetched the body from payload class
                .body(payload.addPlace())
                //in post parameter we should  give the resource
                .when().post("maps/api/place/add/json")
                //Adding assertion for status code
                .then().assertThat().statusCode(200)
                //Validating the scope in response body is app
                .body("scope",equalTo("APP"))
                //Validating the server name in response header
                .header("server","Apache/2.4.52 (Ubuntu)").extract().response().asString();
        //This method will take string as i/p convert it into json and supports to parse json with diff methods
        JsonPath js=new JsonPath(res);
        //inside the getstring method we need to give the path for the node
        String placeId=js.getString("place_id");
        System.out.println("PlaceId="+placeId);

    }

    @Test
    public static void AddUpdateGetE2ETesting(){
        //Add Place
        RestAssured.baseURI="https://rahulshettyacademy.com/";
        String response=given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body(payload.addPlace())
                .when().post("maps/api/place/add/json")
                .then().assertThat().statusCode(200)
                .extract().response().asString();
        JsonPath js=new JsonPath(response);
        String placeid=js.getString("place_id");
        System.out.println("Added place successfully");
        //Update Address - PUT
        String newAddress="114,Pollachi, Coimbatore";
        given().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body("{\n" +
                        "\"place_id\":\""+placeid+"\",\n" +
                        "\"address\":\""+newAddress+"\",\n" +
                        "\"key\":\"qaclick123\"\n" +
                        "}\n").
                when().put("maps/api/place/update/json").
                then().log().all().assertThat().body("msg",equalTo("Address successfully updated")).statusCode(200);

        //Fetch the latest address and validate the new address is coming in response -Get
        String fetchGetRes=given().log().all().queryParam("key","qaclick123").and().queryParam("place_id",placeid).
                when().get("maps/api/place/get/json").then().log().all().assertThat().statusCode(200).extract().response().asString();
        JsonPath actualGetResponse=new JsonPath(fetchGetRes);
        String actualAddress= actualGetResponse.getString("address");
        System.out.println("Actual Address="+actualAddress);
        Assert.assertEquals(actualAddress,newAddress);
    }

    @Test
    public static  void loadJsonBodyFromExternalFile() throws IOException {

        RestAssured.baseURI="https://rahulshettyacademy.com/";
        given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body(new String(Files.readAllBytes(Paths.get("src/test/Utilities/AddPlacePayload.json"))))
         .when().post("maps/api/place/add/json")
                //Adding assertion for status code
                .then().log().all().assertThat().statusCode(200);

    }
}
