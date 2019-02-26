package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Course;

import java.util.List;

public interface CourseDao {

    void add(Course course) throws DaoException;

    List<Course> findAll();

    Course findById(int id);
}
