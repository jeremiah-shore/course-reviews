package net.jeremiahshore.courses.dao;

import net.jeremiahshore.courses.model.Course;

public class TestConfig {

    public static final String CONNECTION_STRING = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";

    public static Course createTestCourse() {
        return new Course("Test", "http://test.com");
    }
}
