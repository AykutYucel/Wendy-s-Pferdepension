package at.ac.tuwien.sepm.assignment.individual.fleet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static Connection con = null;

    public static Connection getConnection() throws SQLException{

        if(con == null){
            con = openConnection();
        }
        return con;
    }

    private static Connection openConnection() throws SQLException{
        Connection connection;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database connection failed!");
        }

        try {
            connection = DriverManager.getConnection("jdbc:h2:~/fleet;INIT=runscript from 'classpath:create.sql'", "sa", "");
        } catch (SQLException e) {
            throw new SQLException("Database connection failed!");
        }
        return connection;
    }

    private static void closeConnection() throws SQLException{
        try {
            con.close();
        } catch (SQLException e) {
            throw new SQLException("Database connection failed!");
        }
    }


}
