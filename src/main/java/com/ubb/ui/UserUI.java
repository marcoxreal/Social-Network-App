//package com.ubb.ui;
//
//import com.ubb.domain.TipRata;
//import com.ubb.domain.card.TipCard;
//import com.ubb.service.*;
//
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Consola principala pentru gestionarea utilizatorilor, prieteniilor,
// * comunitatilor, cardurilor si evenimentelor (curse).
// */
//public class UserUI {
//
//    private final UserService userService;
//    private final FriendshipService friendshipService;
//    private final CommunityService communityService;
//    private final CardService cardService;
//    private final EventService eventService;
//    private final Scanner scanner = new Scanner(System.in);
//
//    public UserUI(UserService userService,
//                  FriendshipService friendshipService,
//                  CommunityService communityService,
//                  CardService cardService,
//                  EventService eventService) {
//        this.userService = userService;
//        this.friendshipService = friendshipService;
//        this.communityService = communityService;
//        this.cardService = cardService;
//        this.eventService = eventService;
//    }
//
//    public void run() {
//        while (true) {
//            printMainMenu();
//            System.out.print("Alege o optiune: ");
//            String cmd = scanner.nextLine().trim();
//
//            switch (cmd) {
//                case "1" -> runUserMenu();
//                case "2" -> runFriendshipMenu();
//                case "3" -> runCommunityMenu();
//                case "4" -> runCardMenu();
//                case "5" -> runEventMenu();
//                case "0" -> {
//                    System.out.println("Iesire...");
//                    return;
//                }
//                default -> System.out.println("Comanda invalida!");
//            }
//            System.out.println();
//        }
//    }
//
//    private void printMainMenu() {
//        System.out.println("""
//                === MENIU PRINCIPAL ===
//                1. Gestionare utilizatori
//                2. Gestionare prietenii
//                3. Analiza comunitati
//                4. Gestionare carduri
//                5. Gestionare evenimente (curse)
//                0. Iesire
//                """);
//    }
//
//    // ---------------- UTILIZATORI ----------------
//
//    private void runUserMenu() {
//        while (true) {
//            System.out.println("""
//                    --- Meniu Utilizatori ---
//                    1. Adauga persoana
//                    2. Adauga rata
//                    3. Sterge utilizator
//                    4. Afiseaza toti utilizatorii
//                    5. Afiseaza doar persoane
//                    6. Afiseaza doar rate
//                    0. Inapoi
//                    """);
//            System.out.print("Alege: ");
//            String cmd = scanner.nextLine().trim();
//
//            try {
//                switch (cmd) {
//                    case "1" -> addPersonUI();
//                    case "2" -> addDuckUI();
//                    case "3" -> removeUserUI();
//                    case "4" -> showAllUsers();
//                    case "5" -> showAllPersons();
//                    case "6" -> showAllDucks();
//                    case "0" -> { return; }
//                    default -> System.out.println("Comanda invalida!");
//                }
//            } catch (Exception e) {
//                System.out.println("Eroare: " + e.getMessage());
//            }
//        }
//    }
//
//    private void addPersonUI() {
//        try {
//            System.out.print("ID: ");
//            long id = Long.parseLong(scanner.nextLine());
//            System.out.print("Username: ");
//            String username = scanner.nextLine();
//
//            userService.addPerson(id, username);
//            System.out.println("Persoana adaugata cu succes!");
//        } catch (Exception e) {
//            System.out.println("Eroare: " + e.getMessage());
//        }
//    }
//
//    private void addDuckUI() {
//        try {
//            System.out.print("ID: ");
//            long id = Long.parseLong(scanner.nextLine());
//            System.out.print("Username: ");
//            String username = scanner.nextLine();
//
//            System.out.print("Tip rata (FLYING, SWIMMING, FLYING_AND_SWIMMING): ");
//            TipRata tip = TipRata.valueOf(scanner.nextLine().trim().toUpperCase());
//
//            System.out.print("Viteza: ");
//            double viteza = Double.parseDouble(scanner.nextLine());
//            System.out.print("Rezistenta: ");
//            double rezistenta = Double.parseDouble(scanner.nextLine());
//
//            userService.addDuck(id, username, tip, viteza, rezistenta);
//            System.out.println("Rata adaugata cu succes!");
//        } catch (Exception e) {
//            System.out.println("Eroare: " + e.getMessage());
//        }
//    }
//
//    private void removeUserUI() {
//        try {
//            System.out.print("ID utilizator: ");
//            long id = Long.parseLong(scanner.nextLine());
//
//            cardService.stergeUserDinCarduri(id);
//            eventService.stergeUserDinEvenimente(id);
//            userService.removeUser(id);
//            System.out.println("Utilizator sters!");
//        } catch (Exception e) {
//            System.out.println("Eroare: " + e.getMessage());
//        }
//    }
//
//    private void showAllUsers() {
//        var list = userService.getAllUsers();
//        if (list.isEmpty()) System.out.println("Nu exista utilizatori.");
//        else list.forEach(System.out::println);
//    }
//
//    private void showAllPersons() {
//        var list = userService.getAllPersons();
//        if (list.isEmpty()) System.out.println("Nu exista persoane.");
//        else list.forEach(System.out::println);
//    }
//
//    private void showAllDucks() {
//        var list = userService.getAllDucks();
//        if (list.isEmpty()) System.out.println("Nu exista rate.");
//        else list.forEach(System.out::println);
//    }
//
//    // ---------------- PRIETENII ----------------
//
//    private void runFriendshipMenu() {
//        while (true) {
//            System.out.println("""
//                    --- Meniu Prietenii ---
//                    1. Adauga prietenie
//                    2. Sterge prietenie
//                    3. Afiseaza toate prieteniile
//                    4. Afiseaza prietenii unui utilizator
//                    0. Inapoi
//                    """);
//            System.out.print("Alege: ");
//            String cmd = scanner.nextLine().trim();
//
//            try {
//                switch (cmd) {
//                    case "1" -> addFriendshipUI();
//                    case "2" -> removeFriendshipUI();
//                    case "3" -> showAllFriendships();
//                    case "4" -> showFriendsOfUser();
//                    case "0" -> { return; }
//                    default -> System.out.println("Comanda invalida!");
//                }
//            } catch (Exception e) {
//                System.out.println("Eroare: " + e.getMessage());
//            }
//        }
//    }
//
//    private void addFriendshipUI() {
//        System.out.print("ID primul utilizator: ");
//        Long id1 = Long.parseLong(scanner.nextLine());
//        System.out.print("ID al doilea utilizator: ");
//        Long id2 = Long.parseLong(scanner.nextLine());
//        friendshipService.addFriendship(id1, id2);
//        System.out.println("Prietenie adaugata!");
//    }
//
//    private void removeFriendshipUI() {
//        System.out.print("ID primul utilizator: ");
//        Long id1 = Long.parseLong(scanner.nextLine());
//        System.out.print("ID al doilea utilizator: ");
//        Long id2 = Long.parseLong(scanner.nextLine());
//        friendshipService.removeFriendship(id1, id2);
//        System.out.println("Prietenie stearsa!");
//    }
//
//    private void showAllFriendships() {
//        var list = friendshipService.getAllFriendships();
//        if (list.isEmpty()) System.out.println("Nu exista prietenii.");
//        else list.forEach(System.out::println);
//    }
//
//    private void showFriendsOfUser() {
//        System.out.print("ID utilizator: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        var list = friendshipService.getFriendsOf(id);
//        if (list.isEmpty()) System.out.println("Nu are prieteni.");
//        else list.forEach(System.out::println);
//    }
//
//    // ---------------- COMUNITATI ----------------
//
//    private void runCommunityMenu() {
//        while (true) {
//            System.out.println("""
//                    --- Meniu Comunitati ---
//                    1. Afiseaza numarul de comunitati
//                    2. Afiseaza cea mai sociabila comunitate
//                    0. Inapoi
//                    """);
//            System.out.print("Alege: ");
//            String cmd = scanner.nextLine().trim();
//
//            switch (cmd) {
//                case "1" -> System.out.println("Numar de comunitati: " + communityService.getNumberOfCommunities());
//                case "2" -> showMostSociableCommunity();
//                case "0" -> { return; }
//                default -> System.out.println("Comanda invalida!");
//            }
//        }
//    }
//
//    private void showMostSociableCommunity() {
//        var best = communityService.getMostSociableCommunity();
//        if (best.isEmpty()) System.out.println("Nu exista comunitati.");
//        else best.forEach(System.out::println);
//    }
//
//    // ---------------- CARDURI ----------------
//
//    private void runCardMenu() {
//        while (true) {
//            System.out.println("""
//                    --- Meniu Carduri ---
//                    1. Adauga card
//                    2. Sterge card
//                    3. Adauga rata in card
//                    4. Elimina rata din card
//                    5. Afiseaza toate cardurile
//                    6. Afiseaza performanta unui card
//                    0. Inapoi
//                    """);
//            System.out.print("Alege: ");
//            String cmd = scanner.nextLine().trim();
//
//            try {
//                switch (cmd) {
//                    case "1" -> addCardUI();
//                    case "2" -> removeCardUI();
//                    case "3" -> addDuckToCardUI();
//                    case "4" -> removeDuckFromCardUI();
//                    case "5" -> showAllCards();
//                    case "6" -> showCardPerformanceUI();
//                    case "0" -> { return; }
//                    default -> System.out.println("Comanda invalida!");
//                }
//            } catch (Exception e) {
//                System.out.println("Eroare: " + e.getMessage());
//            }
//        }
//    }
//
//    private void addCardUI() {
//        System.out.print("ID card: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        System.out.print("Nume card: ");
//        String nume = scanner.nextLine();
//        System.out.print("Tip card (FLYING / SWIMMING): ");
//        TipCard tip = TipCard.valueOf(scanner.nextLine().trim().toUpperCase());
//        cardService.addCard(id, nume, tip);
//        System.out.println("Card adaugat!");
//    }
//
//    private void removeCardUI() {
//        try {
//            System.out.print("ID card: ");
//            Long id = Long.parseLong(scanner.nextLine());
//            cardService.removeCard(id);
//            System.out.println("Card sters!");
//        }
//        catch (Exception e) {
//            System.out.println("Eroare: " + e.getMessage());
//        }
//    }
//
//    private void addDuckToCardUI() {
//        System.out.print("ID card: ");
//        long cardId = Long.parseLong(scanner.nextLine());
//        System.out.print("ID rata: ");
//        long duckId = Long.parseLong(scanner.nextLine());
//        cardService.addDuckToCard(cardId, duckId);
//        System.out.println("Rata adaugata in card!");
//    }
//
//    private void removeDuckFromCardUI() {
//        System.out.print("ID card: ");
//        long cardId = Long.parseLong(scanner.nextLine());
//        System.out.print("ID rata: ");
//        long duckId = Long.parseLong(scanner.nextLine());
//        cardService.removeDuckFromCard(cardId, duckId);
//        System.out.println("Rata stearsa din card!");
//    }
//
//    private void showAllCards() {
//        List<String> detalii = cardService.afiseazaCarduriDetaliat();
//        if (detalii.isEmpty()) System.out.println("Nu exista carduri.");
//        else detalii.forEach(System.out::println);
//    }
//
//    private void showCardPerformanceUI() {
//        System.out.print("ID card: ");
//        long id = Long.parseLong(scanner.nextLine());
//
//        try {
//            String info = cardService.afiseazaPerformantaCard(id);
//            System.out.println(info);
//        } catch (Exception e) {
//            System.out.println("Eroare: " + e.getMessage());
//        }
//    }
//
//    // ---------------- EVENIMENTE ----------------
//
//    private void runEventMenu() {
//        while (true) {
//            System.out.println("""
//                    --- Meniu Evenimente (Curse) ---
//                    1. Creeaza o cursa
//                    2. Adauga o rata la o cursa
//                    3. Porneste o cursa
//                    4. Afiseaza toate cursele
//                    5. Sterge o cursa
//                    6. Adauga observator
//                    0. Inapoi
//                    """);
//            System.out.print("Alege: ");
//            String cmd = scanner.nextLine().trim();
//
//            try {
//                switch (cmd) {
//                    case "1" -> createRaceEventUI();
//                    case "2" -> addDuckToRaceUI();
//                    case "3" -> startEventUI();
//                    case "4" -> showAllEventsUI();
//                    case "5" -> removeEventUI();
//                    case "6" -> addObserverToRaceUI();
//                    case "0" -> { return; }
//                    default -> System.out.println("Comanda invalida!");
//                }
//            } catch (Exception e) {
//                System.out.println("Eroare: " + e.getMessage());
//            }
//        }
//    }
//
//    private void createRaceEventUI() {
//        System.out.print("ID eveniment: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        System.out.print("Nume cursa: ");
//        String name = scanner.nextLine();
//        eventService.createRaceEvent(id, name);
//    }
//
//    private void addDuckToRaceUI() {
//        System.out.print("ID eveniment: ");
//        Long eventId = Long.parseLong(scanner.nextLine());
//        System.out.print("ID rata: ");
//        Long duckId = Long.parseLong(scanner.nextLine());
//
//        try {
//            eventService.addDuckToRace(eventId, duckId);
//            System.out.println("Rata a fost adaugata la cursa!");
//        } catch (Exception e) {
//            System.out.println("Eroare: " + e.getMessage());
//        }
//    }
//
//
//    private void addObserverToRaceUI() {
//        System.out.print("ID eveniment: ");
//        Long eventId = Long.parseLong(scanner.nextLine());
//        System.out.print("ID utilizator: ");
//        Long userId = Long.parseLong(scanner.nextLine());
//
//        boolean ok = eventService.addObserverToRace(eventId, userId);
//        if (ok) System.out.println("Utilizatorul urmareste acum cursa!");
//        else System.out.println("Nu s-a putut adauga observatorul.");
//    }
//
//    private void startEventUI() {
//        System.out.print("ID eveniment: ");
//        Long eventId = Long.parseLong(scanner.nextLine());
//
//        var result = eventService.startEvent(eventId);
//
//        System.out.println("\n=== Rezultat cursa: " + result.getEventName() + " ===");
//        System.out.println(result.getMessage());
//
//        if (!result.getRanking().isEmpty()) {
//            System.out.printf("Timp total: %.3f secunde\n", result.getBestTime());
//            System.out.println("\n--- Clasament ---");
//            int poz = 1;
//            for (var duck : result.getRanking()) {
//                System.out.printf("%d. %s (viteza=%.2f, rezistenta=%.2f)\n",
//                        poz++, duck.getUsername(), duck.getViteza(), duck.getRezistenta());
//            }
//        }
//    }
//
//    private void showAllEventsUI() {
//        var list = eventService.getAllEvents();
//        if (list.isEmpty()) System.out.println("Nu exista curse.");
//        else list.forEach(e -> System.out.println("ID=" + e.getId() + " | Nume=" + e.getName()));
//    }
//
//    private void removeEventUI() {
//        System.out.print("ID eveniment: ");
//        Long id = Long.parseLong(scanner.nextLine());
//        eventService.removeEvent(id);
//    }
//}
