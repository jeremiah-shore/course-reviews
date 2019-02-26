package net.jeremiahshore.courses;

import com.google.gson.Gson;
import net.jeremiahshore.courses.dao.CourseDao;
import net.jeremiahshore.courses.dao.ReviewDao;
import net.jeremiahshore.courses.dao.Sql2oCourseDao;
import net.jeremiahshore.courses.dao.Sql2oReviewDao;
import net.jeremiahshore.courses.exc.ApiError;
import net.jeremiahshore.courses.model.Course;
import net.jeremiahshore.courses.model.Review;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class Api {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String DEFAULT_DATASOURCE = "jdbc:h2:~/reviews.db";
    private static final String INIT_SCRIPT_PATH = ";INIT=RUNSCRIPT from 'classpath:db/init.sql'";
    private static final Gson GSON = new Gson();

    private static String datasource;

    public static void main(String[] args) {
        processArgs(args);

        Sql2o sql2o = getConfiguredSql2o(datasource);
        CourseDao courseDao = new Sql2oCourseDao(sql2o);
        ReviewDao reviewDao = new Sql2oReviewDao(sql2o);

        defineCourseHttpMethods(courseDao);
        defineReviewHttpMethods(reviewDao);
        defineExceptionHttpMethods();
        defineAfterHttpMethod();
    }

    private static void processArgs(String[] args) {
        if(args.length == 0) {
            datasource = DEFAULT_DATASOURCE;
        } else {
            if(args.length != 2) {
                System.out.println("java Api <port> <datasource>");
                System.exit(0);
            } else {
                port(Integer.parseInt(args[0]));
                datasource = args[1];
            }
        }
    }

    static Sql2o getConfiguredSql2o(String datasource) { //necessary for use in test methods as well
        return new Sql2o(datasource + INIT_SCRIPT_PATH, "", "");
    }

    private static void defineCourseHttpMethods(CourseDao courseDao) {
        post("/courses", JSON_CONTENT_TYPE, (request, response) -> {
            Course course = GSON.fromJson(request.body(), Course.class);
            courseDao.add(course);
            response.status(201);
            return course;
        }, GSON::toJson); //response transform

        get("/courses", JSON_CONTENT_TYPE,
                (request, response) -> courseDao.findAll(), GSON::toJson);

        get("/courses/:id", JSON_CONTENT_TYPE, (request, response) -> {
            int id = Integer.parseInt(request.params("id"));
            Course course = courseDao.findById(id);
            if (course == null) {
                throw new ApiError(404, "Could not find course with id " + id);
            }
            return course;
        }, GSON::toJson);
    }

    private static void defineReviewHttpMethods(ReviewDao reviewDao) {
        post("/reviews", JSON_CONTENT_TYPE, (request, response) -> {
            Review review = GSON.fromJson(request.body(), Review.class);
            reviewDao.add(review);
            response.status(201);
            return review;
        }, GSON::toJson);

        get("courses/:course_id/reviews", JSON_CONTENT_TYPE, (request, response) -> {
            int id = Integer.parseInt(request.params("course_id"));
            List<Review> reviewList = reviewDao.findByCourseId(id);
            if(reviewList.isEmpty()) {
                throw new ApiError(404, "Could not find any reviews with course_id " + id);
            }
            return reviewList;
        }, GSON::toJson);

        get("/reviews/all", JSON_CONTENT_TYPE,
                (request, response) -> reviewDao.findAll(), GSON::toJson);
    }

    private static void defineExceptionHttpMethods() {
        exception(ApiError.class, (exception, request, response) -> {
            ApiError error = (ApiError) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", error.getStatus());
            jsonMap.put("errorMessage", error.getMessage());

            response.type(JSON_CONTENT_TYPE); //after does not run in .exception()
            response.status(error.getStatus());
            response.body(GSON.toJson(jsonMap));
        });
    }

    private static void defineAfterHttpMethod() {
        after((request, response) -> response.type(JSON_CONTENT_TYPE));
    }


}
