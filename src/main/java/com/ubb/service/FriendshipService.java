package com.ubb.service;

import com.ubb.domain.User;
import com.ubb.exceptions.RepositoryException;
import com.ubb.repository.FileUserRepository;
import com.ubb.repository.PagingRepository;
import com.ubb.repository.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa service pentru prietenii
 */
public class FriendshipService {
    private final Repository<User> repo;
    public FriendshipService(Repository<User> repo) {
        this.repo = repo;
    }
    public void addFriendship(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new IllegalArgumentException("Un utilizator nu poate fi prieten cu el însuși!");
        }

        User u1 = repo.findById(id1)
                .orElseThrow(() -> new RepositoryException("Utilizatorul cu ID " + id1 + " nu există!"));
        User u2 = repo.findById(id2)
                .orElseThrow(() -> new RepositoryException("Utilizatorul cu ID " + id2 + " nu există!"));

        if (u1.getFriends().contains(id2)) {
            throw new RepositoryException("Cei doi utilizatori sunt deja prieteni!");
        }

        u1.addFriend(id2);
        u2.addFriend(id1);


        repo.update(u1);
        repo.update(u2);

        saveIfFileRepo();
    }
    public void removeFriendship(Long id1, Long id2) {
        User u1 = repo.findById(id1)
                .orElseThrow(() -> new RepositoryException("Utilizatorul cu ID " + id1 + " nu există!"));
        User u2 = repo.findById(id2)
                .orElseThrow(() -> new RepositoryException("Utilizatorul cu ID " + id2 + " nu există!"));

        if (!u1.getFriends().contains(id2)) {
            throw new RepositoryException("Utilizatorii nu sunt prieteni!");
        }

        u1.removeFriend(id2);
        u2.removeFriend(id1);


        repo.update(u1);
        repo.update(u2);
        saveIfFileRepo();
    }
    public List<String> getAllFriendships() {
        List<String> result = new ArrayList<>();
        for (User u : repo.getAll()) {
            for (Long fid : u.getFriends()) {
                if (u.getId() < fid) {
                    result.add(u.getUsername() + " <-> " +
                            repo.findById(fid).map(User::getUsername).orElse("necunoscut"));
                }
            }
        }
        return result;
    }
    public List<User> getFriendsOf(Long userId) {
        User u = repo.findById(userId)
                .orElseThrow(() -> new RepositoryException("Utilizatorul cu ID " + userId + " nu există!"));
        List<User> result = new ArrayList<>();
        for (Long fid : u.getFriends()) {
            repo.findById(fid).ifPresent(result::add);
        }
        return result;
    }
    private void saveIfFileRepo() {
        if (repo instanceof FileUserRepository fileRepo) {
            fileRepo.saveToFile();
        }
    }

    public Repository<User> getUserRepo() {
        return repo;
    }
}
