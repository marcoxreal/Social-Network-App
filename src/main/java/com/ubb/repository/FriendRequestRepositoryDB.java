package com.ubb.repository;

import com.ubb.domain.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestRepositoryDB {

    private final String url;
    private final String username;
    private final String password;

    private PagingRepository<User> userRepo;

    public FriendRequestRepositoryDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void setUserRepo(PagingRepository<User> userRepo) {
        this.userRepo = userRepo;
    }

    public FriendRequest save(FriendRequest fr) {
        String sql = """
                    INSERT INTO friend_requests(from_user, to_user, status, date)
                    VALUES (?, ?, ?, ?) RETURNING id
                """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, fr.getFrom().getId());
            stmt.setLong(2, fr.getTo().getId());
            stmt.setString(3, fr.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(fr.getDate()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fr = new FriendRequest(
                        rs.getLong("id"),
                        fr.getFrom(),
                        fr.getTo(),
                        fr.getStatus(),
                        fr.getDate()
                );
            }
            return fr;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<FriendRequest> getPendingForUser(Long userId) {
        String sql = "SELECT * FROM friend_requests WHERE to_user = ? AND status = 'PENDING'";
        List<FriendRequest> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extract(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private FriendRequest extract(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        User from = userRepo.findById(rs.getLong("from_user")).orElse(null);
        User to = userRepo.findById(rs.getLong("to_user")).orElse(null);
        FriendRequestStatus status =
                FriendRequestStatus.valueOf(rs.getString("status"));
        LocalDateTime date =
                rs.getTimestamp("date").toLocalDateTime();

        return new FriendRequest(id, from, to, status, date);
    }

    public void updateStatus(Long requestId, FriendRequestStatus status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, requestId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsPendingBetween(Long u1, Long u2) {
        String sql = """
        SELECT 1 FROM friend_requests
        WHERE status = 'PENDING'
          AND (
                (from_user = ? AND to_user = ?)
             OR (from_user = ? AND to_user = ?)
          )
        LIMIT 1
    """;

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, u1);
            stmt.setLong(2, u2);
            stmt.setLong(3, u2);
            stmt.setLong(4, u1);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

