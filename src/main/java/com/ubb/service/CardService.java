package com.ubb.service;

import com.ubb.domain.*;
import com.ubb.domain.card.*;
import com.ubb.exceptions.RepositoryException;
import com.ubb.repository.FileCardRepository;
import com.ubb.repository.Repository;
import com.ubb.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service care gestionează cardurile (SkyFlyers, SwimMasters)
 * și membrii (rațele) asociate acestora.
 */
public class CardService {
    private final Repository<Card<?>> cardRepo;
    private final Repository<User> userRepo;
    private boolean legaturiReconstruite = false;
    private final ValidationContext<Card<?>> validationContext;

    /**
     * Creează un serviciu de carduri care reconstruiește automat legăturile card ↔ rațe.
     *
     * @param cardRepo repository-ul cardurilor
     * @param userRepo repository-ul utilizatorilor (pentru a căuta rațele membre)
     */
    public CardService(Repository<Card<?>> cardRepo, Repository<User> userRepo,ValidationContext<Card<?>> validationContext) {
        this.cardRepo = cardRepo;
        this.userRepo = userRepo;
        this.validationContext = validationContext;
        reconstruiesteLegaturi();
    }

    /**
     * Reconstruiește legăturile card ↔ membri, pe baza map-ului din FileCardRepository.
     * (Se apelează automat la crearea serviciului.)
     */
    private void reconstruiesteLegaturi() {
        if (legaturiReconstruite) return;
        if (!(cardRepo instanceof FileCardRepository repo)) return;

        Map<Long, List<Long>> memberships = repo.getMembershipsMap();
        List<User> allUsers = userRepo.getAll();

        for (var entry : memberships.entrySet()) {
            Long cardId = entry.getKey();
            List<Long> membriIds = entry.getValue();

            Card<?> card = cardRepo.findById(cardId).orElse(null);
            if (card == null) continue;

            for (Long duckId : membriIds) {
                User found = allUsers.stream()
                        .filter(u -> u.getId().equals(duckId))
                        .findFirst()
                        .orElse(null);

                if (found instanceof Duck duck) {
                    ((Card<Duck>) card).adaugaMembru(duck);
                }
            }
        }

        legaturiReconstruite = true;
    }

    /** Adaugă un nou card */
    public void addCard(Long id, String nume, TipCard tip) {
        Card<? extends Duck> card = switch (tip) {
            case FLYING -> new SkyFlyers(id, nume);
            case SWIMMING -> new SwimMasters(id, nume);
        };
        cardRepo.add(card);

    }

    /** Șterge un card */
    public void removeCard(Long id) {
        cardRepo.remove(id);

    }

    /** Adauga o rata intr-un card */
    public void addDuckToCard(Long cardId, Long duckId) {
        ensureLegaturi();

        Card<?> card = cardRepo.findById(cardId)
                .orElseThrow(() -> new RepositoryException("Cardul nu exista!"));

        User found = userRepo.getAll().stream()
                .filter(u -> u.getId().equals(duckId))
                .findFirst()
                .orElseThrow(() -> new RepositoryException("Utilizatorul nu exista!"));

        if (!(found instanceof Duck duck)) {
            throw new RepositoryException("Doar ratele pot fi adaugate in carduri!");
        }

        boolean alreadyInCard = card.getMembri().stream()
                .anyMatch(d -> d.getId().equals(duckId));
        if (alreadyInCard) {
            throw new RepositoryException("Rata cu ID " + duckId + " este deja in acest card!");
        }

        if ((card instanceof SkyFlyers && duck.getTip() != TipRata.FLYING) ||
                (card instanceof SwimMasters && duck.getTip() != TipRata.SWIMMING)) {
            throw new RepositoryException("Tipul raței nu corespunde tipului cardului!");
        }

        ((Card<Duck>) card).adaugaMembru(duck);
        cardRepo.update(card);
    }

    /** Șterge o rață dintr-un card */
    public void removeDuckFromCard(Long cardId, Long duckId) {
        ensureLegaturi();

        Card<?> card = cardRepo.findById(cardId)
                .orElseThrow(() -> new RepositoryException("Cardul nu exista!"));

        boolean existsInCard = card.getMembri().stream()
                .anyMatch(d -> d.getId().equals(duckId));

        if (!existsInCard) {
            throw new RepositoryException("Rata cu ID " + duckId + " nu se afla în acest card!");
        }

        ((Card<Duck>) card).getMembri()
                .removeIf(d -> d.getId().equals(duckId));

        cardRepo.update(card);
    }


    /** Afișează toate cardurile cu membrii */
    public List<String> afiseazaCarduriDetaliat() {
        ensureLegaturi();

        List<String> detalii = new ArrayList<>();
        for (Card<? extends Duck> card : cardRepo.getAll()) {
            String membri = card.getMembri().isEmpty()
                    ? "(fara membri)"
                    : card.getMembri().stream()
                    .map(Duck::getUsername)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            detalii.add(card.getNumeCard() + " -> " + membri);
        }
        return detalii;
    }


    public String afiseazaPerformantaCard(Long cardId) {
        ensureLegaturi();

        Card<?> card = cardRepo.findById(cardId)
                .orElseThrow(() -> new RepositoryException("Cardul nu exista!"));

        PerformantaCard perf = card.getPerformantaMedie();
        return String.format(
                "Card %s -> viteza medie = %.2f, rezistenta medie = %.2f",
                card.getNumeCard(),
                perf.getVitezaMedie(),
                perf.getRezistentaMedie()
        );
    }



    private void ensureLegaturi() {
        if (!legaturiReconstruite) {
            reconstruiesteLegaturi();
        }
    }


    public void stergeUserDinCarduri(Long userId) {
        ensureLegaturi();

        boolean modificat = false;
        for (Card<?> card : cardRepo.getAll()) {
            var membri = new ArrayList<>(card.getMembri());
            for (Duck membru : membri) {
                if (membru.getId().equals(userId)) {
                    ((Card<Duck>) card).eliminaMembru(membru);
                    modificat = true;
                }
            }
            if (modificat) cardRepo.update(card);
        }


    }

}
