package web.dao;

import org.springframework.stereotype.Component;
import web.model.User;

import java.lang.ref.SoftReference;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAO {

    private static int PEOPLE_COUNT;

    private static final String URL = "jdbc:postgresql://localhost:5432/UserDB";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Lol1987021";

    private static Connection con;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> index() {
        List<User> users = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String SQL = "SELECT * FROM users";
            ResultSet resultSet = stmt.executeQuery(SQL);

            while (resultSet.next()) {
                User user = new User();

                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setAge(resultSet.getInt("age"));

                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public User show(int id) {
        User user = null;
        String SQL = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setInt(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setAge(resultSet.getInt("age"));
                    user.setEmail(resultSet.getString("email"));
                } else {
                    throw new RuntimeException("Пользователь с id " + id + " не найден.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пользователя с id " + id, e);
        }

        return user;
    }

    public void save(User user) {
        try {
            // Убираем ручное задание id, чтобы база данных автоматически генерировала id
            PreparedStatement stmt = con.prepareStatement("INSERT INTO users (name, age, email) VALUES (?, ?, ?)");

            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int id, User editUser) {
        String SQL = "UPDATE users SET name = ?, age = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, editUser.getName());
            ps.setInt(2, editUser.getAge());
            ps.setString(3, editUser.getEmail());
            ps.setInt(4, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя с id " + id, e);
        }
    }

    public void delete(int id) {
        String SQL = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя с id " + id, e);
        }
    }
}
