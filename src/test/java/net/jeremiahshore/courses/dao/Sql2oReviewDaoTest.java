package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.model.Course;
import net.jeremiahshore.courses.model.Review;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class Sql2oReviewDaoTest {

    private Sql2oCourseDao dao;
    private Connection conn;
    private Course course;
    private Review review;

    @Before
    public void setUp() {
        Sql2o sql2o = new Sql2o(TestUtil.CONNECTION_STRING, "", "");
        dao = new Sql2oCourseDao(sql2o);
        conn = sql2o.open(); //see tearDown for .close()
        course = TestUtil.createTestCourse();
        review = new Review(course.getId(), 3, "It was okay.");
    }

    @Test
    public void addingReviewSetsId() {
        Assert.fail();
    }

    @Test
    public void addedReviewsAreReturnedFromFindAll() {
        Assert.fail();
    }

    @Test
    public void noReviewsReturnsEmptyList() {
        Assert.fail();
    }

    @Test
    public void existingReviewsCanBeFoundByCourseId() {
        Assert.fail();
    }

    @After
    public void tearDown() {
        conn.close();
    }
}