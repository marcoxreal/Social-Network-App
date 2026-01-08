package com.ubb.service;

public class AppContext {
    public final UserService userService;
    public final AuthService authService;
    public final FriendshipService friendshipService;
    public final MessageService messageService;
    public final FriendRequestService friendRequestService;

    public AppContext(UserService userService,
                      AuthService authService,
                      FriendshipService friendshipService,
                      MessageService messageService,
                      FriendRequestService friendRequestService) {
        this.userService = userService;
        this.authService = authService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
    }
}
