package com.ubb.service;

import com.ubb.domain.*;
import com.ubb.event.AbstractObservable;
import com.ubb.event.FriendRequestEvent;
import com.ubb.repository.FriendRequestRepositoryDB;

import java.time.LocalDateTime;
import java.util.List;

public class FriendRequestService extends AbstractObservable<FriendRequestEvent> {

    private final FriendRequestRepositoryDB repo;
    private final FriendshipService friendshipService;

    public FriendRequestService(FriendRequestRepositoryDB repo,
                                FriendshipService friendshipService) {
        this.repo = repo;
        this.friendshipService = friendshipService;
    }

    public void sendRequest(User from, User to) {

        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("You cannot be friends with yourself.");
        }

        if (friendshipService
                .getFriendsOf(from.getId())
                .stream()
                .anyMatch(u -> u.getId().equals(to.getId()))) {
            throw new RuntimeException("You are already friends!");
        }

        if (repo.existsPendingBetween(from.getId(), to.getId())) {
            throw new RuntimeException("A pending request between you already exists.");
        }

        FriendRequest fr = new FriendRequest(
                null,
                from,
                to,
                FriendRequestStatus.PENDING,
                LocalDateTime.now()
        );

        repo.save(fr);
        notifyObservers(new FriendRequestEvent(fr));
    }


    public void acceptRequest(FriendRequest fr) {
        repo.updateStatus(fr.getId(), FriendRequestStatus.APPROVED);
        fr.setStatus(FriendRequestStatus.APPROVED);
        friendshipService.addFriendship(
                fr.getFrom().getId(),
                fr.getTo().getId()
        );
    }

    public void rejectRequest(FriendRequest fr) {
        repo.updateStatus(fr.getId(), FriendRequestStatus.REJECTED);
        fr.setStatus(FriendRequestStatus.REJECTED);
    }

    public List<FriendRequest> getPendingForUser(User user) {
        return repo.getPendingForUser(user.getId());
    }
}
