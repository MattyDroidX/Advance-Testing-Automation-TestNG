import io.restassured.response.Response
import org.junit.jupiter.api.Test
import org.testng.Assert
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema
import static io.restassured.RestAssured.*

class Article extends Base {

    @Test
    void GetArticles_ReturnList() {
        Response response = get("/articles")

        ArrayList<String> allArticles = response.path("data.title")
        Assert.assertTrue(allArticles.size() > 1, "No articles were returned")
    }

    @Test
    void GetArticles_SchemaIsValid() {
        get("/articles").then()
                .assertThat().body(matchesJsonSchema("bookSchema.json"))
    }

    @Test
    void CreateAndDeleteArticle() {
        File articleFile = new File(getClass().getResource("/article.json").toURI())

        Response createResponse = given()
                .body(articleFile).when()
                .post("/articles")
        String responseID = createResponse.jsonPath().getString("post.book_id")

        Assert.assertEquals(createResponse.getStatusCode(), 201)


        Response deleteResponse = given().body("{\n" +
                "    \"article_id\": " + responseID + "\n" +
                "}")
                .when().delete("/articles")
        Assert.assertEquals(deleteResponse.getStatusCode(), 200)
        Assert.assertEquals(deleteResponse.jsonPath().getString("message"), "Article was successfully deleted")

    }

    @Test
    void deleteNonExistentArticle_FailMessage(){
        String nonExistentArticleID = "34231"

        Response deleteResponse = given()
                .body("{\n" +
                        "    \"article_id\": " + nonExistentArticleID + "\n" +
                        "}")
                .when().delete("/articles")

        Assert.assertEquals(deleteResponse.getStatusCode(),500)
        Assert.assertEquals(deleteResponse.jsonPath().getString("error"),"Unable to find article id: " + nonExistentArticleID)

    }

}
