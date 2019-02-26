package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Course;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oCourseDao implements CourseDao{

    private final Sql2o sql2o;

    public Sql2oCourseDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Course course) throws DaoException {
        String sql = "INSERT INTO courses (name, url) VALUES (:name, :url)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql)
                    .bind(course)
                    .executeUpdate()
                    .getKey();
            course.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException(ex, "Problem adding course");
        }
    }

    @Override
    public List<Course> findAll() {
        return null;
    }
}
