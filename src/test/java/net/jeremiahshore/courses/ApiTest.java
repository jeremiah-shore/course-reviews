package net.jeremiahshore.courses;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

public class ApiTest {

    private static final String TEST_PORT = "4568";
    private static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection connection;

    @BeforeClass
    public static void startServer() {
        String[] args = {TEST_PORT, TEST_DATASOURCE};
        Api.main(args);
    }

    @Before
    public void setUp() {
        Sql2o sql2o = Api.getConfiguredSql2o(TEST_DATASOURCE);
        connection = sql2o.open();
    }

    @After
    public void tearDown() {
        connection.close();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }
}