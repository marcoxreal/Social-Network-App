package com.ubb.repository;

import com.ubb.domain.*;
import com.ubb.dto.UserData;
import com.ubb.factory.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Repository care gestioneaza utilizatorii si persista datele in fisier.
 */
public class FileUserRepository extends AbstractRepository<User> {
    private final String fileName;
    private final UserFactory personFactory = new PersonFactory();
    private final UserFactory duckFactory = new DuckFactory();

    /**
     * Creeaza un repository bazat pe fisier.
     * @param fileName calea catre fisierul de date
     */
    public FileUserRepository(String fileName) {
        this.fileName = fileName;
        loadFromFile();
    }

    @Override
    protected Long getId(User elem) {
        return elem.getId();
    }

    @Override
    public void add(User elem) {
        super.add(elem);
        saveToFile();
    }

    @Override
    public void remove(Long id) {
        super.remove(id);
        saveToFile();
    }

    /**
     /**
     * Incarca utilizatorii si prieteniile din fisier.
     * Format exemplu:
     * PERSON,1,Alex,[2,3]
     * DUCK,2,Maca,FLYING,[1]
     */
    /**
     * Încarcă utilizatorii (Person și Duck) din fișier și reconstruiește prieteniile.
     * Formatul suportat:
     *  PERSON,id,username,[id1,id2,...]
     *  DUCK,id,username,tip,[id1,id2,...]
     */
    private void loadFromFile() {
        getElems().clear();
        Map<Long, List<Long>> friendshipsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String type = parts[0].trim();
                Long id = Long.parseLong(parts[1].trim());
                String username = parts[2].trim();

                UserData data = new UserData();
                data.id = id;
                data.username = username;

                if (type.equalsIgnoreCase("PERSON")) {
                    User person = personFactory.create(data);
                    getElems().add(person);

                    if (parts.length >= 4) {
                        friendshipsMap.put(id, parseFriendList(parts[3]));
                    }
                }


                else if (type.equalsIgnoreCase("DUCK")) {
                    String tipStr = "";
                    String friendsStr = null;
                    double viteza = 0.0;
                    double rezistenta = 0.0;

                    // Formatul corect: DUCK,id,username,tip,viteza,rezistenta,[prieteni]
                    if (parts.length >= 6) {
                        tipStr = parts[3].trim();

                        try {
                            viteza = Double.parseDouble(parts[4].trim());
                        } catch (NumberFormatException e) {
                            viteza = 0.0;
                        }

                        try {
                            rezistenta = Double.parseDouble(parts[5].trim());
                        } catch (NumberFormatException e) {
                            rezistenta = 0.0;
                        }

                        if (parts.length >= 7) {
                            friendsStr = parts[6].trim();
                        }
                    }
                    // compatibilitate cu format vechi (fără viteza/rezistenta)
                    else if (parts.length >= 4) {
                        String part4 = parts[3].trim();
                        if (part4.contains("[")) {
                            int idx = part4.indexOf('[');
                            tipStr = part4.substring(0, idx).replace(",", "").trim();
                            friendsStr = part4.substring(idx);
                        } else {
                            tipStr = part4;
                            if (parts.length == 5) {
                                friendsStr = parts[4];
                            }
                        }
                    }

                    // completăm UserData și creăm rața
                    data.tip = TipRata.valueOf(tipStr);
                    data.viteza = viteza;
                    data.rezistenta = rezistenta;

                    User duck = duckFactory.create(data);
                    getElems().add(duck);

                    if (friendsStr != null) {
                        friendshipsMap.put(id, parseFriendList(friendsStr));
                    }

                    // 🟢 debug opțional

                }


            }

            for (var entry : friendshipsMap.entrySet()) {
                Long id = entry.getKey();
                for (Long friendId : entry.getValue()) {
                    User u1 = findById(id).orElse(null);
                    User u2 = findById(friendId).orElse(null);
                    if (u1 != null && u2 != null) {
                        u1.addFriend(friendId);
                        u2.addFriend(id);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Eroare la citirea fisierului: " + e.getMessage());
        }
    }


    /**
     * Functie ajutatoare care transforma stringul de prieteni din fisier intr-o lista de ID-uri.
     * Exemplu: "[1, 2, 3]" → [1, 2, 3]
     */
    private List<Long> parseFriendList(String friendsString) {
        String clean = friendsString
                .replace("[", "")
                .replace("]", "")
                .trim();
        if (clean.isEmpty()) return new ArrayList<>();

        List<Long> friendIds = new ArrayList<>();
        for (String s : clean.split("\\s*,\\s*")) {
            try {
                friendIds.add(Long.parseLong(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return friendIds;
    }



    /**
     * Salveaza toti utilizatorii si prieteniile in fisier.
     */
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (User u : getElems()) {
                String friends = u.getFriends().isEmpty()
                        ? "[]"
                        : u.getFriends().toString(); // ex: [1, 2, 3]

                if (u instanceof Person person) {
                    pw.printf("PERSON,%d,%s,%s\n",
                            person.getId(),
                            person.getUsername(),
                            friends);
                }
                else if (u instanceof Duck duck) {
                    pw.printf("DUCK,%d,%s,%s,%.2f,%.2f,%s\n",
                            duck.getId(),
                            duck.getUsername(),
                            duck.getTip(),
                            duck.getViteza(),
                            duck.getRezistenta(),
                            friends);
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la scrierea fișierului: " + e.getMessage());
        }
    }


    private void addFriendsFromString(User user, String friendsString) {

        String clean = friendsString.replace("[", "").replace("]", "").trim();
        if (clean.isEmpty()) return;

        String[] ids = clean.split("\\s*,\\s*");
        for (String idStr : ids) {
            try {
                Long fid = Long.parseLong(idStr);
                user.addFriend(fid);
            } catch (NumberFormatException ignored) {

            }
        }
    }

}
