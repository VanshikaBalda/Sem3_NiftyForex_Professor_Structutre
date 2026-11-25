
package forex.dao;

import forex.exception.DatabaseException;
import java.sql.*;

public class UserDAO {

    public int create(String name, String email, String hash, String role) {

        String sql = "INSERT INTO users (username, email, password_hash) "
                   + "VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            p.setString(1, name);
            p.setString(2, email);
            p.setString(3, hash);

            p.executeUpdate();

            try (ResultSet r = p.getGeneratedKeys()) {
                if (r.next()) {
                    return r.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Could not create user: " + e.getMessage(), e);
        }

        return -1;
    }

    public UserRecord findByEmail(String email) {

        String sql = "SELECT user_id, username, email, password_hash "
                   + "FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, email);

            try (ResultSet r = p.executeQuery()) {

                if (r.next()) {
                    return new UserRecord(
                        r.getInt("user_id"),
                        r.getString("username"),
                        r.getString("email"),
                        r.getString("password_hash"),
                        "USER"
                    );
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Could not find user: " + e.getMessage(), e);
        }

        return null;
    }

    public record UserRecord(
        int id,
        String name,
        String email,
        String passwordHash,
        String role
    ) {}
}
