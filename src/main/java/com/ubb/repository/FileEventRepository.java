//package com.ubb.repository;
//
//import com.ubb.domain.Duck;
//import com.ubb.domain.User;
//import com.ubb.event.Event;
////import com.ubb.event.RaceEvent;
//import com.ubb.exceptions.RepositoryException;
//
//import java.io.*;
//import java.util.*;
//
//public class FileEventRepository implements Repository<Event> {
//
//    private final String fileName;
//
//    private final Map<Long, List<Long>> participantMap = new HashMap<>();
//    private final Map<Long, List<Long>> observerMap = new HashMap<>();
//
//    public FileEventRepository(String fileName) {
//        this.fileName = fileName;
//        loadFromFile();
//    }
//
//    @Override
//    public void add(Event event) {
//        if (findById(event.getId()).isPresent()) {
//            throw new RepositoryException("Evenimentul cu ID " + event.getId() + " exista deja!");
//        }
//        events.add(event);
//        saveToFile();
//    }
//
//    @Override
//    public void remove(Long id) {
//        boolean removed = events.removeIf(e -> e.getId().equals(id));
//
//        if (!removed) {
//            throw new RepositoryException("Nu exista niciun eveniment cu ID " + id + "!");
//        }
//        events.removeIf(e -> e.getId().equals(id));
//        participantMap.remove(id);
//        observerMap.remove(id);
//        saveToFile();
//    }
//
//    @Override
//    public Optional<Event> findById(Long id) {
//        return events.stream().filter(e -> e.getId().equals(id)).findFirst();
//    }
//
//    @Override
//    public List<Event> getAll() {
//        return new ArrayList<>(events);
//    }
//
//    @Override
//    public void update(Event event) {
//        for (int i = 0; i < events.size(); i++) {
//            if (events.get(i).getId().equals(event.getId())) {
//                events.set(i, event);
//                break;
//            }
//        }
//        saveToFile();
//    }
//
//
//    private final List<Event> events = new ArrayList<>();
//
//    public Map<Long, List<Long>> getParticipantMap() { return participantMap; }
//    public Map<Long, List<Long>> getObserverMap() { return observerMap; }
//
//
//
//    private void loadFromFile() {
//        events.clear();
//        participantMap.clear();
//        observerMap.clear();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                if (line.startsWith("EVENT,")) {
//                    String[] p = line.split(",");
//                    long id = Long.parseLong(p[1]);
//                    String name = p[2];
//
//                    events.add(new RaceEvent(id, name));
//                }
//                else if (line.startsWith("PARTICIPANTS,")) {
//                    String[] p = line.split(",", 3);
//                    long id = Long.parseLong(p[1]);
//                    participantMap.put(id, parseList(p[2]));
//                }
//                else if (line.startsWith("OBSERVERS,")) {
//                    String[] p = line.split(",", 3);
//                    long id = Long.parseLong(p[1]);
//                    observerMap.put(id, parseList(p[2]));
//                }
//            }
//
//        } catch (IOException e) {
//            System.err.println("Eroare la citirea fisierului events: " + e.getMessage());
//        }
//    }
//
//    private List<Long> parseList(String s) {
//        s = s.replace("[", "").replace("]", "").trim();
//        if (s.isEmpty()) return new ArrayList<>();
//
//        List<Long> list = new ArrayList<>();
//        for (String x : s.split(",")) {
//            try {
//                list.add(Long.parseLong(x.trim()));
//            } catch (NumberFormatException ignored) {}
//        }
//        return list;
//    }
//
//
//    public void saveToFile() {
//        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
//
//            for (Event e : events) {
//                pw.printf("EVENT,%d,%s\n", e.getId(), e.getName());
//
//                List<Long> plist = participantMap.getOrDefault(e.getId(), new ArrayList<>());
//                pw.printf("PARTICIPANTS,%d,%s\n", e.getId(), plist.toString());
//
//                List<Long> olist = observerMap.getOrDefault(e.getId(), new ArrayList<>());
//                pw.printf("OBSERVERS,%d,%s\n", e.getId(), olist.toString());
//            }
//
//        } catch (IOException e) {
//            System.err.println("Eroare la scrierea events.txt: " + e.getMessage());
//        }
//    }
//}
