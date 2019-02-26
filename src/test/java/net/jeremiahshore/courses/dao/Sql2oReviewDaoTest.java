package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Course;
import net.jeremiahshore.courses.model.Review;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oReviewDaoTest {

    private Sql2oReviewDao reviewDao;
    private Connection conn;
    private Course course;
    private Review review;
    private Sql2oCourseDao courseDao;

    @Before
    public void setUp() throws DaoException {
        setupConnections();
        configureTestData();
    }

    private void setupConnections() {
        Sql2o sql2o = new Sql2o(TestUtil.CONNECTION_STRING, "", "");
        courseDao = new Sql2oCourseDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open(); //see tearDown for .close()
    }

    private void configureTestData() throws DaoException {
        course = TestUtil.createTestCourse();
        courseDao.add(course);
        review = new Review(course.getId(), 3, "It was okay.");
    }


    @Test
    public void addingReviewSetsId() throws DaoException {
        int originalReviewId = review.getId();

        reviewDao.add(review);

        assertNotEquals(originalReviewId, review.getId());
    }

    @Test
    public void addedReviewsAreReturnedFromFindAll() throws DaoException {
        Review secondReview = new Review(course.getId(), 4, "Pretty good.");
        reviewDao.add(review);
        reviewDao.add(secondReview);

        List<Review> reviewList = reviewDao.findAll();

        assertEquals(2, reviewList.size());
    }

    @Test
    public void noReviewsReturnsEmptyList() {
        List<Review> reviewList = reviewDao.findAll();

        assertEquals(0, reviewList.size());
    }

    @Test
    public void existingReviewsCanBeFoundByCourseId() throws DaoException {
        reviewDao.add(review);

        List<Review> reviewList = reviewDao.findByCourseId(review.getCourse_id());
        Review retrievedReview = reviewList.get(0);

        assert(review != retrievedReview); //make sure it's a different instance
        assertEquals(review, retrievedReview);
    }

    @After
    public void tearDown() {
        conn.close();
    }
}