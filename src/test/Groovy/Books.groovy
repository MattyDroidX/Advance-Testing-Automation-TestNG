import io.restassured.response.Response
import org.junit.jupiter.api.Test
import org.testng.Assert
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema
import static io.restassured.RestAssured.*

class Books extends Base {

    @Test
    void GetBooks_ReturnList() {
        Response response = get("/books")

        ArrayList<String> allBooks = response.path("data.title")
        Assert.assertTrue(allBooks.size() > 1, "No books were returned")
    }

    @Test
    void GetBooks_SchemaIsValid() {
        get("/books").then()
                .assertThat().body(matchesJsonSchema("bookSchema.json"))
    }

    @Test
    void CreateAndDeleteBook() {
        File bookFile = new File(getClass().getResource("/book.json").toURI())

        Response createResponse = given()
                .body(bookFile).when()
                .post("/books")
        String responseID = createResponse.jsonPath().getString("post.book_id")

        Assert.assertEquals(createResponse.getStatusCode(), 201)


        Response deleteResponse = given().body("{\n" +
                "    \"book_id\": " + responseID + "\n" +
                "}")
                .when().delete("/books")
        Assert.assertEquals(deleteResponse.getStatusCode(), 200)
        Assert.assertEquals(deleteResponse.jsonPath().getString("message"), "Book successfully deleted")

    }

    @Test
    void deleteNonExistentBook_FailMessage(){
        String nonExistentBookID = "34231"

        Response deleteResponse = given()
        .body("{\n" +
                "    \"book_id\": " + nonExistentBookID + "\n" +
                "}")
        .when().delete("/books")

        Assert.assertEquals(deleteResponse.getStatusCode(),500)
        Assert.assertEquals(deleteResponse.jsonPath().getString("error"),"Unable to find book id: " + nonExistentBookID)

    }

}



