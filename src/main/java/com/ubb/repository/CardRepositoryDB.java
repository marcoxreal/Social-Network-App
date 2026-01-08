package com.ubb.repository;

import com.ubb.domain.Duck;
import com.ubb.domain.User;
import com.ubb.domain.card.Card;
import com.ubb.domain.card.SkyFlyers;
import com.ubb.domain.card.SwimMasters;
import com.ubb.domain.card.TipCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardRepositoryDB implements Repository<Card<?>> {

    private final String url;
    private final String user;
    private final String pass;

    private final Repository<User> userRepo;

    public CardRepositoryDB(String url, String user, String pass, Repository<User> userRepo) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.userRepo = userRepo;
    }

    private Connection conn() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    @Override
    public void add(Card<?> card) {
        try (var c = conn()) {

            var check = c.prepareStatement("SELECT 1 FROM cards WHERE id = ?");
            check.setLong(1, card.getId());
            var rs = check.executeQuery();

            if (rs.next()) {
                throw new RuntimeException("Card cu ID " + card.getId() + " deja există!");
            }

            var ps = c.prepareStatement(
                    "INSERT INTO cards(id, nume, tip) VALUES (?, ?, ?)"
            );

            ps.setLong(1, card.getId());
            ps.setString(2, card.getNumeCard());
            ps.setString(3, card instanceof SkyFlyers ? "FLYING" : "SWIMMING");

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void remove(Long id) {
        try (var c = conn()) {
            var ps = c.prepareStatement("DELETE FROM cards WHERE id = ?");
            ps.setLong(1, id);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Cardul cu ID " + id + " nu există!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Card<?>> findById(Long id) {
        try (var c = conn()) {

            var ps = c.prepareStatement("SELECT * FROM cards WHERE id = ?");
            ps.setLong(1, id);

            var rs = ps.executeQuery();
            if (!rs.next())
                return Optional.empty();

            Card<?> card = mapCard(rs);

            loadMembers(c, card);

            return Optional.of(card);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Card<?>> getAll() {
        List<Card<?>> cards = new ArrayList<>();

        try (var c = conn()) {

            var ps = c.prepareStatement("SELECT * FROM cards");
            var rs = ps.executeQuery();

            while (rs.next()) {
                Card<?> card = mapCard(rs);
                loadMembers(c, card);
                cards.add(card);
            }

            return cards;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Card<?> card) {
        try (var c = conn()) {

            var ps = c.prepareStatement("""
                UPDATE cards SET
                    nume = ?, tip = ?
                WHERE id = ?
            """);

            ps.setString(1, card.getNumeCard());
            ps.setString(2, card instanceof SkyFlyers ? "FLYING" : "SWIMMING");
            ps.setLong(3, card.getId());
            ps.executeUpdate();

            var del = c.prepareStatement("DELETE FROM card_members WHERE card_id=?");
            del.setLong(1, card.getId());
            del.executeUpdate();

            var ins = c.prepareStatement(
                    "INSERT INTO card_members(card_id, duck_id) VALUES (?, ?)"
            );

            for (Duck d : card.getMembri()) {
                ins.setLong(1, card.getId());
                ins.setLong(2, d.getId());
                ins.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    private Card<?> mapCard(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String nume = rs.getString("nume");
        String tip = rs.getString("tip");

        return switch (tip) {
            case "FLYING" -> new SkyFlyers(id, nume);
            case "SWIMMING" -> new SwimMasters(id, nume);
            default -> throw new IllegalArgumentException("Tip invalid: " + tip);
        };
    }

    private void loadMembers(Connection c, Card<?> card) throws SQLException {
        var ps = c.prepareStatement(
                "SELECT duck_id FROM card_members WHERE card_id = ?"
        );
        ps.setLong(1, card.getId());

        var rs = ps.executeQuery();
        while (rs.next()) {
            long duckId = rs.getLong("duck_id");

            userRepo.findById(duckId)
                    .filter(u -> u instanceof Duck)
                    .map(u -> (Duck) u)
                    .ifPresent(d -> ((Card<Duck>) card).adaugaMembru(d));
        }
    }

    private void saveMembersIncremental(Connection c, Card<?> card) throws SQLException {

        var ps = c.prepareStatement("""
            INSERT INTO card_members(card_id, duck_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
        """);

        for (Duck d : card.getMembri()) {
            ps.setLong(1, card.getId());
            ps.setLong(2, d.getId());
            ps.executeUpdate();
        }
    }
}
