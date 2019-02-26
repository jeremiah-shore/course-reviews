package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Review;

import java.util.List;

public interface ReviewDao {

    void Add(Review review) throws DaoException;

    List<Review> findAll();

    List<Review> findByCourseId(int courseId);
}
