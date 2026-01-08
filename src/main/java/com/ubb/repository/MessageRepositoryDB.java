package com.ubb.repository;

import com.ubb.domain.Message;
import com.ubb.domain.ReplyMessage;
import com.ubb.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryDB {
    private final String url;
    private final String username;
    private final String password;

    public MessageRepositoryDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private PagingRepository<User> userRepo;
    public void setUserRepo(PagingRepository<User> userRepo) {
        this.userRepo = userRepo;
    }

    public Message save(Message msg) {
        String sql = "INSERT INTO messages (from_user, to_user, message, date, reply_to) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, msg.getFrom().getId());
            stmt.setLong(2, msg.getTo().getId());
            stmt.setString(3, msg.getMessage());
            stmt.setTimestamp(4, Timestamp.valueOf(msg.getDate()));

            if (msg instanceof ReplyMessage reply) {
                stmt.setLong(5, reply.getOriginalMessage().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                msg.setId(id);
            }

            return msg;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getConversation(Long user1, Long user2) {
        String sql = """
            SELECT * FROM messages
            WHERE (from_user = ? AND to_user = ?)
               OR (from_user = ? AND to_user = ?)
            ORDER BY date ASC
        """;

        List<Message> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user1);
            stmt.setLong(2, user2);
            stmt.setLong(3, user2);
            stmt.setLong(4, user1);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractMessage(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Message extractMessage(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long from = rs.getLong("from_user");
        Long to = rs.getLong("to_user");
        String text = rs.getString("message");
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
        Long replyTo = rs.getLong("reply_to");
        Message original = null;

        User fromUser = userRepo.findById(from).orElse(null);
        User toUser = userRepo.findById(to).orElse(null);

        if (replyTo != 0) { // is NOT null
            original = findOne(replyTo);
        }

        if (replyTo == 0)
            return new Message(id, fromUser, toUser, text, date);
        else
            return new ReplyMessage(id, fromUser, toUser, text, date, original);
    }

    public Message findOne(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return extractMessage(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
