package dev.rafaelj13.shortify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LinkDAO {
    
    @Autowired
    private DatabaseConnection databaseConnection;
    
    public ResultSet test() throws SQLException {
        Connection conn = databaseConnection.getConnection();

        String sql = "SELECT * FROM links";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        return rs;
    }

    public int addLinkDB(Link link) throws SQLException {
        Connection conn = databaseConnection.getConnection();
        String sql = """
            INSERT INTO links (original_link, clicks, created_at, isDeleted)
            VALUES (?, ?, ?, ?)
            """;
        PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, link.getOriginal_Link());
        stmt.setInt(2, link.getClicks());
        stmt.setTimestamp(3, new java.sql.Timestamp(link.getCreated_at().getTime()));
        stmt.setBoolean(4, link.isDeleted());
        stmt.executeUpdate();
        
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        }
        throw new SQLException("Failed to get generated ID");
    }
    
    public void incrementClicks(int id) throws SQLException {
        Connection conn = databaseConnection.getConnection();
        String sql = "UPDATE links SET clicks = clicks + 1, last_access = NOW() WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
    public Link getLinkById(int decodedLink) throws SQLException {
        Connection conn = databaseConnection.getConnection();
        String sql = "SELECT * FROM links WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, decodedLink);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Link(
                rs.getInt("id"),
                rs.getString("original_link"),
                rs.getInt("clicks"),
                rs.getDate("created_at"),
                rs.getBoolean("isDeleted")
            );
        } else {
            return null;
        }
    }
    public void deleteLink(int id) throws SQLException {
        Connection conn = databaseConnection.getConnection();
        String sql = "UPDATE links SET isDeleted = TRUE WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
}