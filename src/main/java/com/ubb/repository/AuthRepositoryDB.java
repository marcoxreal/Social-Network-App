package com.ubb.repository;

import com.ubb.domain.ConcreteDuck;
import com.ubb.domain.Person;
import com.ubb.domain.TipRata;
import com.ubb.domain.User;
import com.ubb.dto.UserData;

import java.sql.*;
import java.util.Optional;

public class AuthRepositoryDB {

    private final String url;
    private final String username;
    private final String password;

    public AuthRepositoryDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Optional<User> findByEmailOrUsername(String identifier) {
        try (var conn = DriverManager.getConnection(url, username, password)) {
            var ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE email = ? OR username = ?"
            );
            ps.setString(1, identifier);
            ps.setString(2, identifier);

            var rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();

            String type = rs.getString("user_type");
            Long id = rs.getLong("id");
            String uname = rs.getString("username");
            String email = rs.getString("email");
            String passwordHash = rs.getString("password_hash");

            if ("PERSON".equalsIgnoreCase(type)) {
                return Optional.of(new Person(id, uname, email, passwordHash));
            } else if ("DUCK".equalsIgnoreCase(type)) {
                TipRata tip = TipRata.valueOf(rs.getString("tip_rata"));
                double viteza = rs.getDouble("viteza");
                double rezistenta = rs.getDouble("rezistenta");
                return Optional.of(new ConcreteDuck(id, uname, email, passwordHash, tip, viteza, rezistenta));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertUser(String username, String email, String passwordHash, String type) {
        try (var conn = DriverManager.getConnection(url, this.username, this.password)) {
            String sql = "INSERT INTO users (username, email, password_hash, type) VALUES (?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, passwordHash);
                ps.setString(4, type.toUpperCase());
                return ps.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
