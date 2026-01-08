//package com.ubb.repository;
//
//import com.ubb.domain.Duck;
//import com.ubb.domain.User;
//import com.ubb.event.Event;
//import com.ubb.event.RaceEvent;
//import com.ubb.exceptions.RepositoryException;
//
//import java.sql.*;
//import java.util.*;
//
//public class EventRepositoryDB implements Repository<Event> {
//
//    private final String url;
//    private final String user;
//    private final String pass;
//
//    private final Repository<User> userRepo;
//
//    public EventRepositoryDB(String url, String user, String pass, Repository<User> userRepo) {
//        this.url = url;
//        this.user = user;
//        this.pass = pass;
//        this.userRepo = userRepo;
//    }
//
//    private Connection conn() throws SQLException {
//        return DriverManager.getConnection(url, this.user, this.pass);
//    }
//
//
//    @Override
//    public void add(Event event) {
//        try (var c = conn()) {
//
//            var check = c.prepareStatement(
//                    "SELECT 1 FROM events WHERE id = ?"
//            );
//            check.setLong(1, event.getId());
//
//            if (check.executeQuery().next())
//                throw new RepositoryException("Event cu ID " + event.getId() + " deja există!");
//
//            var ps = c.prepareStatement(
//                    "INSERT INTO events(id, name, type) VALUES (?, ?, ?)"
//            );
//
//            ps.setLong(1, event.getId());
//            ps.setString(2, event.getName());
//            ps.setString(3, event instanceof RaceEvent ? "RACE" : "UNKNOWN");
//
//            ps.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    @Override
//    public void remove(Long id) {
//        try (var c = conn()) {
//
//            var ps = c.prepareStatement("DELETE FROM events WHERE id = ?");
//            ps.setLong(1, id);
//
//            int rows = ps.executeUpdate();
//
//            if (rows == 0)
//                throw new RepositoryException("Nu există evenimentul cu ID " + id);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//
//    @Override
//    public Optional<Event> findById(Long id) {
//        try (var c = conn()) {
//
//            var ps = c.prepareStatement("SELECT * FROM events WHERE id = ?");
//            ps.setLong(1, id);
//
//            var rs = ps.executeQuery();
//            if (!rs.next())
//                return Optional.empty();
//
//            Event event = mapEvent(rs);
//
//            loadParticipants(c, (RaceEvent) event);
//            loadObservers(c, event);
//
//            return Optional.of(event);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    @Override
//    public List<Event> getAll() {
//        List<Event> list = new ArrayList<>();
//
//        try (var c = conn()) {
//
//            var ps = c.prepareStatement("SELECT * FROM events");
//            var rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Event e = mapEvent(rs);
//
//                if (e instanceof RaceEvent race) {
//                    loadParticipants(c, race);
//                    loadObservers(c, race);
//                }
//
//                list.add(e);
//            }
//
//            return list;
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    @Override
//    public void update(Event event) {
//        try (var c = conn()) {
//
//            var ps = c.prepareStatement(
//                    "UPDATE events SET name = ?, type = ? WHERE id = ?"
//            );
//
//            ps.setString(1, event.getName());
//            ps.setString(2, event instanceof RaceEvent ? "RACE" : "UNKNOWN");
//            ps.setLong(3, event.getId());
//            ps.executeUpdate();
//
//            c.prepareStatement("DELETE FROM event_participants WHERE event_id = " + event.getId())
//                    .executeUpdate();
//
//            c.prepareStatement("DELETE FROM event_observers WHERE event_id = " + event.getId())
//                    .executeUpdate();
//
//            if (event instanceof RaceEvent race) {
//                saveParticipants(c, race);
//                saveObservers(c, race);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//
//    private Event mapEvent(ResultSet rs) throws SQLException {
//        long id = rs.getLong("id");
//        String name = rs.getString("name");
//        String type = rs.getString("type");
//
//        return switch (type) {
//            case "RACE" -> new RaceEvent(id, name);
//            default -> throw new IllegalArgumentException("Tip invalid: " + type);
//        };
//    }
//
//
//    private void loadParticipants(Connection c, RaceEvent event) throws SQLException {
//        var ps = c.prepareStatement("SELECT duck_id FROM event_participants WHERE event_id = ?");
//        ps.setLong(1, event.getId());
//
//        var rs = ps.executeQuery();
//        while (rs.next()) {
//            long duckId = rs.getLong("duck_id");
//
//            userRepo.findById(duckId)
//                    .filter(u -> u instanceof Duck)
//                    .map(u -> (Duck) u)
//                    .ifPresent(event::addParticipant);
//        }
//    }
//
//    private void loadObservers(Connection c, Event event) throws SQLException {
//        var ps = c.prepareStatement("SELECT user_id FROM event_observers WHERE event_id = ?");
//        ps.setLong(1, event.getId());
//
//        var rs = ps.executeQuery();
//        while (rs.next()) {
//            long userId = rs.getLong("user_id");
//
//            userRepo.findById(userId)
//                    .ifPresent(event::addObserver);
//        }
//    }
//
//
//    private void saveParticipants(Connection c, RaceEvent event) throws SQLException {
//        var ps = c.prepareStatement("""
//            INSERT INTO event_participants(event_id, duck_id)
//            VALUES (?, ?)
//        """);
//
//        for (Duck d : event.getParticipants()) {
//            ps.setLong(1, event.getId());
//            ps.setLong(2, d.getId());
//            ps.executeUpdate();
//        }
//    }
//
//    private void saveObservers(Connection c, Event event) throws SQLException {
//        var ps = c.prepareStatement("""
//            INSERT INTO event_observers(event_id, user_id)
//            VALUES (?, ?)
//        """);
//
//        for (var obs : event.getSubscribers()) {
//            if (obs instanceof User u) {
//                ps.setLong(1, event.getId());
//                ps.setLong(2, u.getId());
//                ps.executeUpdate();
//            }
//        }
//    }
//}
