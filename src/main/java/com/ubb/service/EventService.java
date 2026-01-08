//package com.ubb.service;
//
//import com.ubb.domain.Duck;
//import com.ubb.domain.User;
//import com.ubb.event.Event;
//import com.ubb.event.RaceEvent;
//import com.ubb.event.RaceResult;
//import com.ubb.repository.FileEventRepository;
//import com.ubb.repository.Repository;
//
//import java.util.*;
//
//public class EventService {
//
//    private final Repository<Event> eventRepo;
//    private final Repository<User> userRepo;
//
//    public EventService(Repository<Event> eventRepo, Repository<User> userRepo) {
//        this.eventRepo = eventRepo;
//        this.userRepo = userRepo;
//
//        if (eventRepo instanceof FileEventRepository f)
//            reconstruiesteLegaturile(f);
//    }
//
//    private void reconstruiesteLegaturile(FileEventRepository fileRepo) {
//
//        for (Event e : eventRepo.getAll()) {
//
//            if (e instanceof RaceEvent race) {
//
//                List<Long> pids = fileRepo.getParticipantMap()
//                        .getOrDefault(e.getId(), List.of());
//
//                for (Long id : pids) {
//                    userRepo.findById(id)
//                            .filter(u -> u instanceof Duck)
//                            .map(u -> (Duck) u)
//                            .ifPresent(race::addParticipant);
//                }
//
//                List<Long> oids = fileRepo.getObserverMap()
//                        .getOrDefault(e.getId(), List.of());
//
//                for (Long id : oids) {
//                    userRepo.findById(id)
//                            .ifPresent(race::addObserver);
//                }
//            }
//        }
//    }
//
//    public void createRaceEvent(Long id, String name) {
//        eventRepo.add(new RaceEvent(id, name));
//    }
//
//
//
//    public void addDuckToRace(Long eventId, Long duckId) {
//
//        Event ev = eventRepo.findById(eventId)
//                .orElseThrow(() -> new IllegalArgumentException("Eveniment inexistent!"));
//
//        if (!(ev instanceof RaceEvent race))
//            throw new IllegalArgumentException("Evenimentul nu este cursa!");
//
//        Duck duck = userRepo.findById(duckId)
//                .filter(u -> u instanceof Duck)
//                .map(u -> (Duck) u)
//                .orElseThrow(() -> new IllegalArgumentException("Rata inexistenta!"));
//
//        if (duck.getTip() != com.ubb.domain.TipRata.SWIMMING)
//            throw new IllegalArgumentException("Doar ratele SWIMMING pot participa!");
//
//
//        boolean exists = race.getParticipants().stream()
//                .anyMatch(d -> d.getId().equals(duckId));
//
//        if (exists)
//            throw new IllegalArgumentException("Rata este deja in cursa!");
//
//        race.addParticipant(duck);
//
//        if (eventRepo instanceof FileEventRepository file)
//            file.getParticipantMap()
//                    .computeIfAbsent(eventId, k -> new ArrayList<>())
//                    .add(duckId);
//
//        eventRepo.update(ev);
//    }
//
//
//
//    public boolean addObserverToRace(Long eventId, Long userId) {
//
//        Event ev = eventRepo.findById(eventId)
//                .orElse(null);
//
//        if (ev == null) return false;
//
//        User u = userRepo.findById(userId)
//                .orElse(null);
//
//        if (u == null) return false;
//
//        ev.addObserver(u);
//
//        if (eventRepo instanceof FileEventRepository file)
//            file.getObserverMap()
//                    .computeIfAbsent(eventId, k -> new ArrayList<>())
//                    .add(userId);
//
//        eventRepo.update(ev);
//        return true;
//    }
//
//
//
//    public RaceResult startEvent(Long eventId) {
//        Event ev = eventRepo.findById(eventId)
//                .orElseThrow(() -> new IllegalArgumentException("Eveniment inexistent!"));
//
//        if (ev instanceof RaceEvent race)
//            return race.start();
//
//        return new RaceResult(ev.getName(), 0, List.of(),
//                "Tip eveniment nesuportat");
//    }
//
//    public List<Event> getAllEvents() {
//        return eventRepo.getAll();
//    }
//
//    public void removeEvent(Long id) {
//        eventRepo.remove(id);
//    }
//
//
//    public void stergeUserDinEvenimente(Long userId) {
//
//        for (Event ev : eventRepo.getAll()) {
//            if (ev instanceof RaceEvent race) {
//                race.getParticipants().removeIf(d -> d.getId().equals(userId));
//            }
//            eventRepo.update(ev);
//        }
//
//        if (eventRepo instanceof FileEventRepository file) {
//            file.getParticipantMap().values().forEach(list -> list.removeIf(id -> id.equals(userId)));
//            file.getObserverMap().values().forEach(list -> list.removeIf(id -> id.equals(userId)));
//            file.saveToFile();
//        }
//    }
//}
