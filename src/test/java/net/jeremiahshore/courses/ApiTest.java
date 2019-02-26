package net.jeremiahshore.courses;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.jeremiahshore.courses.dao.*;
import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Course;
import net.jeremiahshore.courses.model.Review;
import net.jeremiahshore.testing.ApiClient;
import net.jeremiahshore.testing.ApiResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ApiTest {

    private static final String TEST_PORT = "4568";
    private static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection connection;
    private ApiClient client;
    private Gson gson;

    private CourseDao courseDao;
    private Course course;
    private ReviewDao reviewDao;


    @BeforeClass
    public static void startServer() {
        String[] args = {TEST_PORT, TEST_DATASOURCE};
        Api.main(args);
    }

    @Before
    public void setUp() throws DaoException {
        setupConnections();
        configureTestData();
    }

    private void setupConnections() {
        Sql2o sql2o = Api.getConfiguredSql2o(TEST_DATASOURCE);
        courseDao = new Sql2oCourseDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        connection = sql2o.open();

        client = new ApiClient("http://localhost:" + TEST_PORT);
        gson = new Gson();
    }

    private void configureTestData() throws DaoException {
        course = TestUtil.createTestCourse();
        courseDao.add(course);
    }

    @Test
    public void addingCoursesReturnsCreatedStatus() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        values.put("url", "http://test.com");

        ApiResponse response = client.request("POST", "/courses", gson.toJson(values));

        assertEquals(201, response.getStatus());
    }

    @Test
    public void coursesCanBeAccessedById() {
        ApiResponse response = client.request("GET", "/courses/" + course.getId());
        Course retrievedCourse = gson.fromJson(response.getBody(), Course.class);

        assertEquals(course, retrievedCourse);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() {
        ApiResponse response = client.request("GET", "/courses/42");

        assertEquals(404, response.getStatus());
    }

    @Test
    public void addingReviewGivesCreatedStatus() {
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Test comment");

        String url = String.format("/courses/%d/reviews", course.getId());
        ApiResponse response = client.request("POST", url, gson.toJson(values));

        assertEquals(201, response.getStatus());
    }

    @Test
    public void addingReviewToUnknownReturns500ErrorStatus() {
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Test comment");

        String url = "/courses/42/reviews";
        ApiResponse response = client.request("POST", url, gson.toJson(values));

        assertEquals(500, response.getStatus());
    }

    @Test
    public void getReviewsByCourseIdReturnsListOfMultipleReviewsWhenTheyExist() throws DaoException {
        Review review = new Review(course.getId(), 3, "it was okay");
        Review secondReview = new Review(course.getId(), 5, "it was great");
        reviewDao.add(review);
        reviewDao.add(secondReview);

        String url = String.format("/courses/%d/reviews", course.getId());
        ApiResponse response = client.request("GET", url);
        Type listType = new TypeToken<ArrayList<Review>>(){}.getType();
        List<Review> reviewList = gson.fromJson(response.getBody(), listType);

        assertEquals(2, reviewList.size());
    }

    @Test
    public void getReviewsByCourseIdReturnsNotFoundStatus() {
        String url = String.format("/courses/%d/reviews", course.getId());
        ApiResponse response = client.request("GET", url);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void getReviewsByUnknownCourseIdReturnsNotFoundStatus() {
        String url = "/courses/42/reviews";
        ApiResponse response = client.request("GET", url);

        assertEquals(404, response.getStatus());
    }

    @After
    public void tearDown() {
        connection.close();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }
}