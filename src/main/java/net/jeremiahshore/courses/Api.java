package net.jeremiahshore.courses;

import com.google.gson.Gson;
import net.jeremiahshore.courses.dao.CourseDao;
import net.jeremiahshore.courses.dao.Sql2oCourseDao;
import net.jeremiahshore.courses.model.Course;
import org.sql2o.Sql2o;

import static spark.Spark.after;
import static spark.Spark.post;
import static spark.Spark.get;

public class Api {

    private static final String JSON_CONTENT_TYPE = "application/json";

    public static void main(String[] args) {
        Sql2o sql2o = new Sql2o("jdbc:h2:~/reviews.db;INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        CourseDao courseDao = new Sql2oCourseDao(sql2o);
        Gson gson = new Gson();

        post("/courses", JSON_CONTENT_TYPE, (request, response) -> {
            Course course = gson.fromJson(request.body(), Course.class);
            courseDao.add(course);
            response.status(201);
            return course;
        }, gson::toJson); //response transform

        get("/courses", JSON_CONTENT_TYPE,
                (request, response) -> courseDao.findAll(), gson::toJson);

        get("/courses/:id", JSON_CONTENT_TYPE, (request, response) -> {
            int id = Integer.parseInt(request.params("id"));
            //todo: what if this is not found?
            return courseDao.findById(id);
        }, gson::toJson);

        after((request, response) -> response.type(JSON_CONTENT_TYPE));
    }
}
