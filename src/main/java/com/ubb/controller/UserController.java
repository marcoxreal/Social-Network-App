package com.ubb.controller;

import com.ubb.domain.User;
import com.ubb.domain.FriendRequest;
import com.ubb.event.FriendRequestEvent;
import com.ubb.event.Observer;
import com.ubb.repository.FriendRequestRepositoryDB;
import com.ubb.service.*;
import com.ubb.ui.UserProfilePage;
import com.ubb.event.RaceEvent;
import com.ubb.controller.RaceController;
import com.ubb.domain.race.Lane;
import com.ubb.event.strategy.BinarySearchStrategy;
import com.ubb.event.strategy.Strategy;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Parent;

import com.ubb.domain.Duck;
import com.ubb.domain.TipRata;
import com.ubb.util.paging.Page;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<FriendRequestEvent> {

    private UserService userService;
    private AuthService authService;
    private MessageService messageService;
    private ObservableList<User> model = FXCollections.observableArrayList();
    private User loggedUser;
    private FriendRequestService friendRequestService;
    private final ObservableList<User> searchModel = FXCollections.observableArrayList();

    @FXML private TableView<User> tableView;
    @FXML private TableColumn<User, Long> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colTip;
    @FXML private TableColumn<User, Double> colViteza;
    @FXML private TableColumn<User, Double> colRezistenta;

    @FXML private Button btnNext;
    @FXML private Button btnPrev;
    @FXML private Label labelPage;
    @FXML private Button btnLogin;
    @FXML private Label loginStatusLabel;
    @FXML private ComboBox<TipRata> comboFilterTip;
    @FXML private Button btnSendMessage;
    @FXML private Button btnAdd;
    @FXML private Button btnDelete;
    @FXML private Button btnAddFriends;
    @FXML private Button btnRemoveFriends;
    @FXML private Button btnFriendRequests;
    @FXML private Label lblFilter;
    @FXML private TextField txtSearch;
    @FXML private ListView<User> listSearchResults;
    @FXML private Button btnClearSearch;
    @FXML private Button btnStartRace;


    private int pageSize = 9;
    private int currentPage = 0;
    private int totalElements = 0;

    public void setService(UserService userService,  AuthService authService, MessageService messageService) {
        this.authService = authService;
        this.userService = userService;
        this.messageService = messageService;
        comboFilterTip.getItems().setAll((TipRata[]) TipRata.values());
        comboFilterTip.getItems().add(0, null);

        initModel();
    }

    private FriendshipService friendshipService;

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setFriendRequestService(FriendRequestService service) {
        this.friendRequestService = service;
    }


    @Override
    public void update(FriendRequestEvent event) {
        if (loggedUser == null) return;
        if (!event.getRequest().getTo().getId().equals(loggedUser.getId()))
            return;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Friend request");
            alert.setHeaderText(null);

            alert.setContentText(
                    "New friend request from: " +
                            event.getRequest().getFrom().getUsername()
            );

            alert.show();
        });
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        btnSendMessage.setDisable(true);
//        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        btnAddFriends.setDisable(true);
        btnRemoveFriends.setDisable(true);
        btnFriendRequests.setDisable(true);
        btnStartRace.setDisable(true);

        colTip.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            if (u instanceof Duck d) {
                return new ReadOnlyObjectWrapper<>(d.getTip().toString());
            }
            return new ReadOnlyObjectWrapper<>("PERSOANA");
        });

        colViteza.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            if (u instanceof Duck d) {
                return new ReadOnlyObjectWrapper<>(d.getViteza());
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        colRezistenta.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            if (u instanceof Duck d) {
                return new ReadOnlyObjectWrapper<>(d.getRezistenta());
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        tableView.setItems(model);
        listSearchResults.setItems(searchModel);
        listSearchResults.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String tip = (item instanceof Duck d) ? d.getTip().name() : "PERSOANA";
                    setText(item.getUsername() + " (" + tip + ")");
                }
            }
        });
        listSearchResults.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                User selected = listSearchResults.getSelectionModel().getSelectedItem();
                if (selected != null) openUserProfile(selected.getId());
            }
        });
        txtSearch.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) hideSearchResults();
        });
        txtSearch.textProperty().addListener((obs, old, now) -> refreshSearch(now));

        tableView.setVisible(false);
        tableView.setManaged(false);
        btnPrev.setVisible(false); btnPrev.setManaged(false);
        btnNext.setVisible(false); btnNext.setManaged(false);
        labelPage.setVisible(false); labelPage.setManaged(false);
        lblFilter.setVisible(false);
        lblFilter.setManaged(false);
        comboFilterTip.setVisible(false);
        comboFilterTip.setManaged(false);
        btnAddFriends.setVisible(false);
        btnAddFriends.setManaged(false);
        btnRemoveFriends.setVisible(false);
        btnRemoveFriends.setManaged(false);
        btnDelete.setVisible(false);
        btnDelete.setManaged(false);
    }


    private void initModel() {
        Page<User> page =
                userService.getUsersOnPage(currentPage, pageSize, comboFilterTip.getValue());

        int maxPage = (int) Math.ceil(1.0 * page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage < 0) maxPage = 0;

        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = userService.getUsersOnPage(currentPage, pageSize, comboFilterTip.getValue());
        }

        totalElements = page.getTotalNumberOfElements();

        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable((currentPage + 1) * pageSize >= totalElements);

        var users = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());

        model.setAll(users);

        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
        friendRequestService.addObserver(this);
        showAlert("Login successful", "Welcome, " + user.getUsername(), Alert.AlertType.INFORMATION);
        btnLogin.setDisable(true);
        loginStatusLabel.setText("Logged in as: " + user.getUsername());
        btnSendMessage.setDisable(false);
        btnAdd.setDisable(false);
        btnDelete.setDisable(false);
        btnAddFriends.setDisable(false);
        btnRemoveFriends.setDisable(false);
        btnFriendRequests.setDisable(false);
        btnStartRace.setDisable(false);

        List<FriendRequest> pending =
                friendRequestService.getPendingForUser(user);

        if (!pending.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Friend requests");
            alert.setHeaderText(null);
            alert.setContentText(
                    "You have " + pending.size() + " pending friend request(s)"
            );
            alert.show();
        }

    }

    @FXML
    private void onSearchEnter() {
        // Enter în TextField: dacă există un singur rezultat, îl deschidem,
        // altfel deschidem pe cel selectat
        if (!listSearchResults.isVisible()) return;

        User selected = listSearchResults.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openUserProfile(selected.getId());
            return;
        }

        if (searchModel.size() == 1) {
            openUserProfile(searchModel.get(0).getId());
        }
    }

    @FXML
    private void onClearSearch() {
        txtSearch.clear();
        hideSearchResults();
    }

    private void refreshSearch(String query) {
        if (query == null) query = "";
        query = query.trim();

        if (query.length() < 1) {
            hideSearchResults();
            return;
        }

        // limită mică, ca dropdown
        List<User> hits = userService.searchUsersByUsername(query, 10);

        searchModel.setAll(hits);

        boolean show = !hits.isEmpty();
        listSearchResults.setVisible(show);
        listSearchResults.setManaged(show);

        if (show) {
            listSearchResults.getSelectionModel().selectFirst();
        }
    }

    private void hideSearchResults() {
        listSearchResults.setVisible(false);
        listSearchResults.setManaged(false);
        searchModel.clear();
    }

    /**
     * Deschide profilul într-un Stage nou.
     */
    private void openUserProfile(Long userId) {
        try {
            // luăm userul fresh din DB (cu prieteni încărcați)
            User target = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserProfilePage page = new UserProfilePage(
                    loggedUser,
                    target,
                    userService,
                    friendshipService,
                    friendRequestService,
                    messageService
            );
            page.show();

            hideSearchResults();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }



    public void onNext(ActionEvent e) {
        currentPage++;
        initModel();
    }

    public void onPrev(ActionEvent e) {
        currentPage--;
        initModel();
    }

    public void onFilter(ActionEvent e) {
        currentPage = 0;
        initModel();
    }

    @FXML
    public void onAdd(ActionEvent e) throws IOException {
        openAddUserWindow();
        initModel();
    }

    @FXML
    private void handleOpenLogin() {
        try {
            AppContext context = new AppContext(
                    userService,
                    authService,
                    friendshipService,
                    messageService,
                    friendRequestService
            );

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ubb/views/login-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);

            LoginController controller = loader.getController();
            controller.setContext(context);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open login window", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void handleSendMessage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ubb/views/friend-selector-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Choose a friend");
            stage.setScene(scene);

            FriendSelectorController controller = loader.getController();
            controller.init(loggedUser, userService, messageService, stage);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFriendRequests() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/ubb/views/friend-requests-view.fxml")
        );

        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        FriendRequestsController ctrl = loader.getController();
        ctrl.init(
                friendRequestService,
                loggedUser
        );

        stage.setTitle("Friend Requests");
        stage.setScene(scene);
        stage.show();
    }




    @FXML
    private void onDelete() {
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No user selected", Alert.AlertType.ERROR);
            return;
        }
        userService.removeUser(selected.getId());
        initModel();
    }

    @FXML
    private void onAddFriends() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/ubb/views/add-friends-view.fxml")
        );

        Parent root = loader.load();

        AddFriendsController ctrl = loader.getController();
        ctrl.init(
                loggedUser,
                friendRequestService,
                userService.getAllUsers()
        );

        Stage stage = new Stage();
        stage.setTitle("Send Friend Request");
        stage.setScene(new Scene(root));
        stage.show();
    }


    private void openAddUserWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/ubb/views/add-user-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setTitle("Add User");
        stage.setScene(scene);

        AddUserController controller = loader.getController();
        controller.init(userService, stage);

        stage.show();
    }

    @FXML
    public void onRemoveFriends() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ubb/views/remove-friends-view.fxml")
            );
            Parent root = loader.load();

            RemoveFriendsController ctrl = loader.getController();
            ctrl.setUserService(userService);
            ctrl.setFriendshipService(friendshipService);

            Stage stage = new Stage();
            stage.setTitle("Remove Friends");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onStartRace() {
        try {
            if (loggedUser == null) {
                showAlert("Error", "You must be logged in to start a race.", Alert.AlertType.ERROR);
                return;
            }

            List<Lane> lanes = new ArrayList<>(List.of(
                    new Lane(1, 50),
                    new Lane(2, 70),
                    new Lane(3, 100)
            ));
            lanes.sort(Comparator.comparingDouble(Lane::getDistance));
            Strategy strategy = new BinarySearchStrategy();
            RaceEvent race = new RaceEvent(1L, "Duck Race", strategy, lanes);
            userService.getAllUsers().stream()
                    .filter(u -> u instanceof Duck)
                    .map(u -> (Duck) u)
                    .filter(d -> d.getViteza() > 0) // evita infinit / invalid
                    .forEach(race::addParticipant);

            if (race.getParticipants().isEmpty()) {
                showAlert("Race", "No ducks available for race.", Alert.AlertType.INFORMATION);
                return;
            }

            if (race.getParticipants().size() < lanes.size()) {
                showAlert("Race",
                        "Not enough ducks. Need at least " + lanes.size() +
                                " ducks for " + lanes.size() + " lanes, but have " + race.getParticipants().size() + ".",
                        Alert.AlertType.WARNING);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ubb/views/race-view.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(
                    getClass().getResource("/com/ubb/views/profile-style.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("Race");
            stage.setScene(scene);

            RaceController ctrl = loader.getController();
            ctrl.init(stage, race, messageService, loggedUser);

            stage.setOnCloseRequest(ev -> ctrl.shutdown());
            stage.show();
            ctrl.startRace();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void onShowCommunities() {
        int communities = countRealCommunities();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Communities");
        alert.setHeaderText(null);
        alert.setContentText("Number of communities: " + communities);
        alert.showAndWait();
    }

    @FXML
    public void onMostSociableCommunity() {
        CommunityResult result = findMostSociableCommunity();

        if (result == null) {
            showAlert("Error", "No communities", Alert.AlertType.ERROR);
            return;
        }

        showAlert("Most Sociable Community", "Most sociable community\n" +
                "Diameter: " + result.diameter + "\n" +
                "Users: " + result.members,  Alert.AlertType.INFORMATION);
    }


    private int countRealCommunities() {
        List<User> users = userService.getAllUsers();

        Map<Long, User> map = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));


        Set<Long> visited = new HashSet<>();
        int communities = 0;

        communities += (int) users.stream()
                .filter(u -> !visited.contains(u.getId()))
                .map(u -> dfsCount(u.getId(), map, visited))
                .filter(size -> size >= 2)
                .count();

        return communities;
    }

    private int dfsCount(Long id,
                         Map<Long, User> map,
                         Set<Long> visited) {

        visited.add(id);
        int size = 1;

        size += map.get(id).getFriends().stream()
                .filter(fid -> !visited.contains(fid))
                .mapToInt(fid -> dfsCount(fid, map, visited))
                .sum();

        return size;
    }

    private static class CommunityResult {
        int diameter;
        List<Long> members;

        CommunityResult(int diameter, List<Long> members) {
            this.diameter = diameter;
            this.members = members;
        }
    }

    private CommunityResult findMostSociableCommunity() {

        Map<Long, User> map = new HashMap<>();
        for (User u : userService.getAllUsers()) {
            map.put(u.getId(), u);
        }


        Set<Long> visited = new HashSet<>();
        CommunityResult best = null;

        for (Long id : map.keySet()) {
            if (!visited.contains(id)) {

                List<Long> component = new ArrayList<>();
                collectComponent(id, map, visited, component);

                if (component.size() < 2) continue;

                int diameter = computeDiameter(component, map);

                if (best == null || diameter > best.diameter) {
                    best = new CommunityResult(diameter, component);
                }
            }
        }
        return best;
    }

    private int computeDiameter(List<Long> nodes, Map<Long, User> map) {

        Long start = nodes.get(0);
        BFSResult first = bfs(start, map, nodes);
        BFSResult second = bfs(first.farthestNode, map, nodes);

        return second.maxDistance;
    }

    private static class BFSResult {
        Long farthestNode;
        int maxDistance;

        BFSResult(Long node, int dist) {
            this.farthestNode = node;
            this.maxDistance = dist;
        }
    }

    private BFSResult bfs(Long start,
                          Map<Long, User> map,
                          List<Long> component) {

        Set<Long> allowed = new HashSet<>(component);
        Queue<Long> q = new LinkedList<>();
        Map<Long, Integer> dist = new HashMap<>();

        q.add(start);
        dist.put(start, 0);

        Long farthest = start;
        int maxDist = 0;

        while (!q.isEmpty()) {
            Long u = q.poll();
            for (Long v : map.get(u).getFriends()) {
                if (allowed.contains(v) && !dist.containsKey(v)) {
                    dist.put(v, dist.get(u) + 1);
                    q.add(v);

                    if (dist.get(v) > maxDist) {
                        maxDist = dist.get(v);
                        farthest = v;
                    }
                }
            }
        }
        return new BFSResult(farthest, maxDist);
    }

    private void collectComponent(
            Long id,
            Map<Long, User> map,
            Set<Long> visited,
            List<Long> component) {

        visited.add(id);
        component.add(id);

        for (Long f : map.get(id).getFriends()) {
            if (!visited.contains(f)) {
                collectComponent(f, map, visited, component);
            }
        }
    }

    public void cleanup() {
        if (friendRequestService != null) {
            friendRequestService.removeObserver(this);
        }
    }

}
