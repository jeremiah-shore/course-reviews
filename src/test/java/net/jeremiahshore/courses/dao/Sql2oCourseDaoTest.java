package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.exc.DaoException;
import net.jeremiahshore.courses.model.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oCourseDaoTest {

    private Sql2oCourseDao dao;
    private Connection conn;
    private Course course;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oCourseDao(sql2o);
        conn = sql2o.open(); //see tearDown for .close()
    }

    @Before
    public void createTestCourse() {
        course = new Course("Test", "http://test.com");
    }

    @Test
    public void addingCourseSetsId() throws Exception {
        int originalCourseId = course.getId();

        dao.add(course);

        assertNotEquals(originalCourseId, course.getId());
    }

    @Test
    public void addedCoursesAreReturnedFromFindAll() throws Exception {
        dao.add(course);

        assertEquals(1, dao.findAll().size());
    }

    @Test
    public void noCoursesReturnsEmptyList() throws Exception {
        assertEquals(0, dao.findAll().size());
    }

    @Test
    public void existingCoursesCanBeFoundById() throws Exception {
        dao.add(course);

        Course foundCourse = dao.findById(course.getId());

        assertEquals(course, foundCourse);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }
}