package net.jeremiahshore.courses.dao;

public class TestConfig {

    public static final String CONNECTION_STRING = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
}
