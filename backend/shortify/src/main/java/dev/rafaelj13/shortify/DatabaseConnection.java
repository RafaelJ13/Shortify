package dev.rafaelj13.shortify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnection {

    @Value("${spring.datasource.mysql_url}")
    private String mysql_addr;

    @Value("${spring.datasource.username}")
    private String mysql_user;

    @Value("${spring.datasource.password}")
    private String mysql_password;

    private Connection conn;

    public Connection getConnection() throws SQLException{
        if(conn == null || conn.isClosed()){
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(mysql_addr, mysql_user, mysql_password);
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        return conn;
    }
}
