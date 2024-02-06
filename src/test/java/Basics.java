import AddPlaceAPI_POJOClasses.AddPlace;
import AddPlaceAPI_POJOClasses.Location;
import EcommerceAPIPOJOClasses.*;
import GetCourseAPI_POJOclasses.GetCoursePOJO;
import GetCourseAPI_POJOclasses.WebAutomation;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    public static  void learningOauth() {
        //fetching the access token from authorization server
        RestAssured.baseURI="https://rahulshettyacademy.com/oauthapi/";
        String accessTokenResponse=given().log().all().formParam("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParam("client_secret","erZOWM9g3UtwNRj340YYaK_W")
                .formParam("grant_type","client_credentials")
                .formParam("scope","trust").
        when().post("oauth2/resourceOwner/token").then().log().all().extract().response().asString();
        JsonPath js=new JsonPath(accessTokenResponse);
        String accessToken=js.getString("access_token").toString();

        //Making get request
        String bookdetails=given().queryParam("access_token",accessToken).
        when().log().all().get("getCourseDetails").
        then().extract().response().asString();
        System.out.println("Book details Response"+ bookdetails);

    }

    //Using POJO classes for deserialization
    @Test
    public static  void learningPOJOclasses() {
        //fetching the access token from authorization server
        RestAssured.baseURI="https://rahulshettyacademy.com/oauthapi/";
        String accessTokenResponse=given().log().all().formParam("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParam("client_secret","erZOWM9g3UtwNRj340YYaK_W")
                .formParam("grant_type","client_credentials")
                .formParam("scope","trust").
                when().post("oauth2/resourceOwner/token").then().log().all().extract().response().asString();
        JsonPath js=new JsonPath(accessTokenResponse);
        String accessToken=js.getString("access_token").toString();

        //Using GetcoursePOJO class object
        GetCoursePOJO bookdetails=given().queryParam("access_token",accessToken).
                when().log().all().get("getCourseDetails").
                then().extract().response().as(GetCoursePOJO.class);
        //Fetching the linkedin from response using pojo classes
        System.out.println("Pojo output for linkedin "+bookdetails.getLinkedIn());
        //Printing  the title of WebAutomationCourses
        List<WebAutomation> webAutomationCourse=bookdetails.getCourses().getWebAutomation();
        System.out.println("WebAutomation Course Titles and Price");
        for(int i=0;i<webAutomationCourse.size();i++)
        {
            System.out.println(webAutomationCourse.get(i).getCourseTitle()+" Rs "+webAutomationCourse.get(i).getPrice());
        }
    }

    //Serialization
    @Test
    public void AddPlace(){

        RestAssured.baseURI="https://rahulshettyacademy.com/";
        AddPlace addPlace=new AddPlace();
        addPlace.setAccuracy(50);
        addPlace.setAddress("29, side layout, cohen 09");
        addPlace.setLanguage("French-IN");
        addPlace.setName("Frontline house");
        addPlace.setPhone_number("(+91) 983 893 3937");
        addPlace.setWebsite("https://rahulshettyacademy.com");
        addPlace.setTypes(Arrays.asList("shoe park","shop"));
        Location location=new Location();
        location.setLat(-38.383494);
        location.setLng(33.427362);
        addPlace.setLocation(location);

        given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                //Inside the body we are passing the object of the class
                .body(addPlace)
                .when().post("maps/api/place/add/json")
                //Adding assertion for status code
                .then().log().all().assertThat().statusCode(200)
                //Validating the scope in response body is app
                .body("scope",equalTo("APP"))
                //Validating the server name in response header
                .header("server","Apache/2.4.52 (Ubuntu)");
    }


    //Req/Response Spec Builder
    @Test
    public void reqResSpecBuilder(){
        //Giving requirements for Request specific  builder
        RequestSpecification reqSpec=new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com/").setContentType("application/json").addQueryParam("key","qaclick123").build();
        //Giving requirements for Response specific  builder
        ResponseSpecification resSpec=new ResponseSpecBuilder().expectStatusCode(200).expectContentType("application/json").expectBody("scope",equalTo("APP")).expectHeader("server","Apache/2.4.52 (Ubuntu)").build();
        //Separating the given part and storing it  in request specifiation object
        RequestSpecification request=given().spec(reqSpec).body(payload.addPlace());
        //Using the response spec builder to assert the status code and storing it in response object
        Response response=request.when().post("maps/api/place/add/json").then().spec(resSpec).extract().response();
        System.out.println("Response "+response);

    }

    //EcommerceAPITesting
     @Test
    public void ecommerceAPITest(){

        RequestSpecification eCommerceJsonReq= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com").setContentType(ContentType.JSON).build();
        ResponseSpecification loginRes=new ResponseSpecBuilder().expectContentType(ContentType.JSON).expectStatusCode(200).expectBody("message",equalTo("Login Successfully")).build();
         ECommerceLogin eCommerceLogin=new ECommerceLogin();
         eCommerceLogin.setUserEmail("praveen334@gmail.com");
         eCommerceLogin.setUserPassword("Stud@123");
         ECommerceLoginResponse eCommerceLoginResponse =  given().log().all().spec(eCommerceJsonReq).body(eCommerceLogin).when().post("api/ecom/auth/login").then().assertThat().spec(loginRes).log().all().extract().response().as(ECommerceLoginResponse.class);

        String authorizationToken= eCommerceLoginResponse.getToken();
        String userId= eCommerceLoginResponse.getUserId();
         System.out.println("authorizationToken"+authorizationToken+" userId"+userId);

         //createProduct
         RequestSpecification addProductReq= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com").addHeader("authorization",authorizationToken).build();

       RequestSpecification prodSpecifications=  given().log().all().spec(addProductReq).
                 param("productName","Qwerty").
                 param("productAddedBy",userId).
                 param("productCategory","Course").
                 param("productSubCategory","Learning").
                 param("productPrice","10000").
               param("productDescription","Testing").
                 param("productFor","Students").
                 multiPart("productImage",new File("C:\\Users\\Praveennandha\\Pictures\\Screenshots\\Screenshot 2023-06-27 151921.png"));
       ResponseSpecification addProdResponseSpec=new ResponseSpecBuilder().expectStatusCode(201).expectBody("message",equalTo("Product Added Successfully")).build();
         String addProdResponse=prodSpecifications.when().post("api/ecom/product/add-product").then().log().all().spec(addProdResponseSpec).extract().response().asString();
         JsonPath addProdJsonPath=new JsonPath(addProdResponse);
         String productId=addProdJsonPath.getString("productId");
         System.out.println(productId);

         //Creating Orderfor TheProduct

         OrderDetail orderDetail=new OrderDetail();
         Orders orders=new Orders();
         orderDetail.setCountry("India");
         orderDetail.setProductOrderedId(productId);
         List<OrderDetail> orderDetailsList=new ArrayList<>();
         orderDetailsList.add(orderDetail);
         orders.setOrders(orderDetailsList);
         RequestSpecification creadOrderReqSpec= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com").setContentType(ContentType.JSON).addHeader("authorization",authorizationToken).build();
         String orderResponse=given().log().all().spec(creadOrderReqSpec).body(orders)
                 .when().log().all().post("api/ecom/order/create-order").then().assertThat().statusCode(201).extract().response().asString();
         JsonPath  placeOrderJsonResponse=new JsonPath(orderResponse);
          List<String> orderList=placeOrderJsonResponse.getList("orders");
         //ViewOrders
         given().log().all().spec(creadOrderReqSpec).queryParam("id",orderList.get(0)).when().log().all().get("api/ecom/order/get-orders-details").then().assertThat().statusCode(200).body("message",equalTo("Orders fetched for customer Successfully"));

       //Deleting the product

         given().log().all().spec(eCommerceJsonReq).pathParam("productId",productId).when().log().all().delete("api/ecom/product/delete-product/{productId}").then().assertThat().statusCode(200);





     }

}