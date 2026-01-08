package com.ubb.repository;

import com.ubb.domain.*;
import com.ubb.dto.UserData;
import com.ubb.factory.DuckFactory;
import com.ubb.factory.PersonFactory;
import com.ubb.factory.UserFactory;
import com.ubb.util.paging.Pageable;
import com.ubb.util.paging.Page;

import java.sql.*;
import java.util.*;

public class UserRepositoryDB implements PagingRepository<User> {

    private final String url;
    private final String username;
    private final String password;

    private final UserFactory personFactory = new PersonFactory();
    private final UserFactory duckFactory = new DuckFactory();

    public UserRepositoryDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findById(Long id) {
        try (var conn = DriverManager.getConnection(url, username, password)) {
            var ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            if (!rs.next()) return Optional.empty();

            User u = mapRow(rs);
            loadFriendships(conn, u);
            return Optional.of(u);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        try (var conn = DriverManager.getConnection(url, username, password)) {
            var ps = conn.prepareStatement("SELECT * FROM users");
            var rs = ps.executeQuery();

            while (rs.next()) list.add(mapRow(rs));
            for (User u : list) loadFriendships(conn, u);

            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(User user) {
        try (var conn = DriverManager.getConnection(url, username, password)) {
            // changed to include email and password_hash
            var ps = conn.prepareStatement(
                    "INSERT INTO users(id, username, email, password_hash, user_type, tip_rata, viteza, rezistenta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );

            ps.setLong(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());

            ps.setString(5, user instanceof Duck ? "DUCK" : "PERSON");

            if (user instanceof Duck d) {
                // assume Duck has getTip(), getViteza(), getRezistenta() methods
                ps.setString(6, d.getTip() == null ? null : d.getTip().name());
                ps.setDouble(7, d.getViteza());
                ps.setDouble(8, d.getRezistenta());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.DOUBLE);
                ps.setNull(8, Types.DOUBLE);
            }

            ps.executeUpdate();
            saveFriendships(conn, user);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        try (var conn = DriverManager.getConnection(url, username, password)) {

            // update username, email, password_hash, user_type and duck specific fields
            var ps = conn.prepareStatement("""
                    UPDATE users
                    SET username = ?, email = ?, password_hash = ?, user_type = ?, tip_rata = ?, viteza = ?, rezistenta = ?
                    WHERE id = ?
                    """);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user instanceof Duck ? "DUCK" : "PERSON");

            if (user instanceof Duck d) {
                ps.setString(5, d.getTip() == null ? null : d.getTip().name());
                ps.setDouble(6, d.getViteza());
                ps.setDouble(7, d.getRezistenta());
            } else {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.DOUBLE);
                ps.setNull(7, Types.DOUBLE);
            }

            ps.setLong(8, user.getId());
            ps.executeUpdate();

            var del = conn.prepareStatement(
                    "DELETE FROM friendships WHERE user1_id = ? OR user2_id = ?"
            );
            del.setLong(1, user.getId());
            del.setLong(2, user.getId());
            del.executeUpdate();

            saveFriendships(conn, user);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Long id) {
        try (var conn = DriverManager.getConnection(url, username, password)) {
            var delF = conn.prepareStatement(
                    "DELETE FROM friendships WHERE user1_id = ? OR user2_id = ?"
            );
            delF.setLong(1, id);
            delF.setLong(2, id);
            delF.executeUpdate();

            var ps = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private User mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("username");
        String email = rs.getString("email");
        String pwdHash = rs.getString("password_hash");
        String tipStr = rs.getString("tip_rata");

        if (tipStr == null) {
            // Person
            UserData d = new UserData();
            d.id = id;
            d.username = name;
            d.email = email;
            d.passwordHash = pwdHash;
            return personFactory.create(d);
        }

        // Duck
        UserData d = new UserData();
        d.id = id;
        d.username = name;
        d.email = email;
        d.passwordHash = pwdHash;
        d.tip = TipRata.valueOf(tipStr);
        d.viteza = rs.getDouble("viteza");
        d.rezistenta = rs.getDouble("rezistenta");

        return duckFactory.create(d);
    }

    private void loadFriendships(Connection conn, User u) throws SQLException {
        var ps1 = conn.prepareStatement(
                "SELECT user2_id FROM friendships WHERE user1_id = ?"
        );
        ps1.setLong(1, u.getId());
        var rs1 = ps1.executeQuery();
        while (rs1.next()) {
            u.addFriend(rs1.getLong("user2_id"));
        }
        var ps2 = conn.prepareStatement(
                "SELECT user1_id FROM friendships WHERE user2_id = ?"
        );
        ps2.setLong(1, u.getId());
        var rs2 = ps2.executeQuery();
        while (rs2.next()) {
            u.addFriend(rs2.getLong("user1_id"));
        }
    }


    private void saveFriendships(Connection conn, User u) throws SQLException {
        for (long f : u.getFriends()) {

            long a = Math.min(u.getId(), f);
            long b = Math.max(u.getId(), f);

            var ps = conn.prepareStatement(
                    "INSERT INTO friendships(user1_id, user2_id) VALUES (?, ?) ON CONFLICT DO NOTHING"
            );
            ps.setLong(1, a);
            ps.setLong(2, b);
            ps.executeUpdate();
        }
    }



    @Override
    public Page<User> findAllOnPage(Pageable pageable, TipRata filter) {

        List<User> result = new ArrayList<>();
        int totalCount;

        String baseQuery = "SELECT * FROM users";

        if (filter != null) {
            baseQuery += " WHERE tip_rata = ?";
        }

        String pageQuery = baseQuery + " ORDER BY id LIMIT ? OFFSET ?";
        String countQuery = "SELECT count(*) FROM (" + baseQuery + ") c";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {

            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            if (filter != null) {
                countStmt.setString(1, filter.name());
            }

            ResultSet rsCount = countStmt.executeQuery();
            rsCount.next();
            totalCount = rsCount.getInt(1);

            PreparedStatement stmt = conn.prepareStatement(pageQuery);
            int idx = 1;
            if (filter != null) {
                stmt.setString(idx++, filter.name());
            }
            stmt.setInt(idx++, pageable.getPageSize());
            stmt.setInt(idx, pageable.getPageNumber() * pageable.getPageSize());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Page<>(result, totalCount);
    }

}
