package net.jeremiahshore.courses;

import com.google.gson.Gson;
import net.jeremiahshore.courses.dao.CourseDao;
import net.jeremiahshore.courses.dao.Sql2oCourseDao;
import net.jeremiahshore.courses.dao.Sql2oCourseDaoTest;
import net.jeremiahshore.courses.dao.TestUtil;
import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Course;
import net.jeremiahshore.testing.ApiClient;
import net.jeremiahshore.testing.ApiResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiTest {

    private static final String TEST_PORT = "4568";
    private static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection connection;
    private CourseDao courseDao;
    private ApiClient client;
    private Gson gson;
    private Course course;


    @BeforeClass
    public static void startServer() {
        String[] args = {TEST_PORT, TEST_DATASOURCE};
        Api.main(args);
    }

    @Before
    public void setUp() throws DaoException {
        Sql2o sql2o = Api.getConfiguredSql2o(TEST_DATASOURCE);
        courseDao = new Sql2oCourseDao(sql2o);
        connection = sql2o.open();
        client = new ApiClient("http://localhost:" + TEST_PORT);
        gson = new Gson();
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
    public void coursesCanBeAccessedById() throws DaoException {


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

    @After
    public void tearDown() {
        connection.close();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }
}