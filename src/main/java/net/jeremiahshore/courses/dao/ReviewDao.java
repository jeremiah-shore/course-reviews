package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Review;

import java.util.List;

public interface ReviewDao {

    void Add(Review review) throws DaoException;

    List<Review> findAll();

    List<Review> findByCourseId(int courseId);
}
