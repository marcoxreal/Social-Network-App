package com.ubb.service;

import com.ubb.domain.Duck;
import com.ubb.domain.Person;
import com.ubb.domain.TipRata;
import com.ubb.domain.User;
import com.ubb.dto.UserData;
import com.ubb.factory.DuckFactory;
import com.ubb.factory.PersonFactory;
import com.ubb.factory.UserFactory;
import com.ubb.repository.FileUserRepository;
import com.ubb.repository.PagingRepository;
import com.ubb.repository.Repository;
import com.ubb.util.paging.Page;
import com.ubb.util.paging.Pageable;
import com.ubb.validator.DuckValidator;
import com.ubb.validator.PersonValidator;
import com.ubb.validator.ValidationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Clasa service care gestioneaza operatiile asupra utilizatorilor.
 * <p>
 * Ofera functionalitati de adaugare, stergere si filtrare a utilizatorilor.
 * </p>
 */
public class UserService {
    private final PagingRepository<User> repo;    private final ValidationContext<User> validationContext;
    private final UserFactory personFactory = new PersonFactory();
    private final UserFactory duckFactory = new DuckFactory();
    public UserService(PagingRepository<User> repo, ValidationContext<User> validationContext) {
        this.repo = repo;
        this.validationContext = validationContext;
    }

    public void addPerson(Long id, String username, String email, String passwordHash) {
        UserData data = new UserData();
        data.id = id;
        data.username = username;
        data.email = email;
        data.passwordHash = passwordHash;

        User person = personFactory.create(data);

        validationContext.setStrategy(new PersonValidator());
        validationContext.executeValidation(person);

        repo.add(person);
    }


    public void addDuck(Long id, String username, String email, String passwordHash,
                        TipRata tip, double viteza, double rezistenta) {

        UserData data = new UserData();
        data.id = id;
        data.username = username;
        data.email = email;
        data.passwordHash = passwordHash;
        data.tip = tip;
        data.viteza = viteza;
        data.rezistenta = rezistenta;

        User duck = duckFactory.create(data);

        validationContext.setStrategy(new DuckValidator());
        validationContext.executeValidation(duck);

        repo.add(duck);
    }


    public void removeUser(Long id) {
        var userOpt = repo.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User cu ID " + id + " nu exista!");
        }

        if (repo instanceof FileUserRepository) {
            for (User u : repo.getAll()) {
                u.removeFriend(id);
            }
            repo.remove(id);
            ((FileUserRepository) repo).saveToFile();
        }
        else {
            repo.remove(id);
        }
    }

    public List<User> getAllUsers() {
        return repo.getAll();
    }

    public List<User> getAllPersons() {
        return repo.getAll()
                .stream()
                .filter(u -> u instanceof Person)
                .toList();
    }

    public List<User> getAllDucks() {
        return repo.getAll()
                .stream()
                .filter(u -> u instanceof Duck)
                .toList();
    }

    public Page<User> getDucksOnPage(int pageNumber, int pageSize, TipRata filter) {
        Pageable pageable = new Pageable(pageNumber, pageSize);
        return repo.findAllOnPage(pageable, filter);
    }

    public Page<User> getUsersOnPage(int pageNumber, int pageSize, TipRata filter) {
        Pageable pageable = new Pageable(pageNumber, pageSize);
        if (filter == null) {
            return repo.findAllOnPage(pageable, null);
        }
        return repo.findAllOnPage(pageable, filter);
    }

    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    public void updateUser(User user) {
        repo.update(user);
    }

    public List<User> getFriendsOfUser(Long userId) {
        Optional<User> userOpt = repo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User cu ID " + userId + " nu exista!");
        }

        User user = userOpt.get();

        return user.getFriends()
                .stream()
                .map(repo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public List<User> searchUsersByUsername(String query, int limit) {
        if (query == null) return List.of();
        String needle = query.trim().toLowerCase();
        if (needle.isEmpty()) return List.of();

        return repo.getAll().stream()
                .filter(u -> u.getUsername() != null)
                .filter(u -> u.getUsername().toLowerCase().contains(needle))
                .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                .limit(limit)
                .toList();
    }



}

