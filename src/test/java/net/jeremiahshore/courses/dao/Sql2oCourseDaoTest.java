package net.jeremiahshore.courses.dao;

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

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oCourseDao(sql2o);
        //keep conection open through entire test so that it isn't wiped out
        conn = sql2o.open();
    }

    @Test
    public void addingCourseSetsId() throws Exception {
        Course course = new Course("Test", "http://test.com");
        int originalCourseId = course.getId();

        dao.add(course);

        assertNotEquals(originalCourseId, course.getId());
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }
}