package com.ubb.service;

import com.ubb.domain.User;
import com.ubb.repository.Repository;

import java.util.*;

/**
 * Clasa service care gestioneaza comunitatile de utilizatori din retea.
 * <p>
 * Ofera functionalitati pentru:
 *   determinarea numarului de comunitati (componente conexe)
 *   gasirea celei mai sociabile comunitati (cu diametru maxim)
 * </p>
 */
public class CommunityService {
    private final Repository<User> repo;

    /**
     * Creeaza un serviciu pentru analiza comunitatilor.
     * @param repo repository-ul care contine utilizatorii si legaturile dintre ei
     */
    public CommunityService(Repository<User> repo) {
        this.repo = repo;
    }


    /**
     * Calculeaza numarul total de comunitati (componente conexe) din retea.
     * @return numarul de comunitati
     */
    public int getNumberOfCommunities() {
        return getAllCommunitiesIds().size();
    }

    /**
     * Returneaza comunitatea cea mai sociabila (cu diametrul maxim).
     * <p>
     * O comunitate este reprezentata ca lista de obiecte {@link User}.
     * </p>
     * @return lista utilizatorilor din cea mai sociabila comunitate
     */
    public List<User> getMostSociableCommunity() {
        List<Set<Long>> comps = getAllCommunitiesIds();
        Map<Long, User> idx = index();

        int bestDiam = -1;
        Set<Long> bestComp = Collections.emptySet();

        for (Set<Long> comp : comps) {
            int diam = diameter(comp, idx);
            if (diam > bestDiam) {
                bestDiam = diam;
                bestComp = comp;
            }
        }

        return bestComp.stream()
                .sorted()
                .map(idx::get)
                .toList();
    }


    /**
     * Creeaza un index ID → User pentru acces rapid.
     * @return un map cu perechi (id, user)
     */
    private Map<Long, User> index() {
        Map<Long, User> map = new HashMap<>();
        for (User u : repo.getAll()) {
            map.put(u.getId(), u);
        }
        return map;
    }

    /**
     * Returneaza toate comunitatile sub forma de multimi de ID-uri.
     * @return lista componentelor conexe
     */
    private List<Set<Long>> getAllCommunitiesIds() {
        Map<Long, User> idx = index();
        Set<Long> visited = new HashSet<>();
        List<Set<Long>> comps = new ArrayList<>();

        for (Long startId : idx.keySet()) {
            if (visited.contains(startId)) continue;
            Set<Long> comp = bfsCollectComponent(startId, idx, visited);
            comps.add(comp);
        }
        return comps;
    }

    /**
     * Colecteaza o componenta conexa pornind de la un nod dat.
     * <p>Ignora prietenii care nu exista in repository.</p>
     * @param startId ID-ul nodului de start
     * @param idx indexul ID → User
     * @param visited multimea nodurilor deja vizitate
     * @return multimea ID-urilor din componenta
     */
    private Set<Long> bfsCollectComponent(Long startId, Map<Long, User> idx, Set<Long> visited) {
        Set<Long> comp = new HashSet<>();
        Queue<Long> q = new ArrayDeque<>();
        q.add(startId);
        visited.add(startId);

        while (!q.isEmpty()) {
            Long id = q.poll();
            comp.add(id);

            User u = idx.get(id);
            if (u == null) continue;

            for (Long f : u.getFriends()) {
                if (!idx.containsKey(f)) continue;
                if (visited.add(f)) {
                    q.add(f);
                }
            }
        }
        return comp;
    }

    /**
     * Calculeaza diametrul unei componente conexe.
     * <p>
     * Diametrul este lungimea maxima a unui drum intre doua noduri din componenta.
     * </p>
     * @param comp multimea ID-urilor din componenta
     * @param idx indexul ID → User
     * @return valoarea diametrului
     */
    private int diameter(Set<Long> comp, Map<Long, User> idx) {
        if (comp.size() <= 1) return 0;
        int diam = 0;
        for (Long start : comp) {
            int far = bfsMaxDistanceWithin(start, comp, idx);
            diam = Math.max(diam, far);
        }
        return diam;
    }

    /**
     * Calculeaza distanta maxima (in muchii) dintr-un nod dat,
     * limitata la nodurile din componenta curenta.
     * @param start ID-ul nodului de start
     * @param allowed multimea ID-urilor din componenta
     * @param idx indexul ID → User
     * @return distanta maxima pana la un alt nod
     */
    private int bfsMaxDistanceWithin(Long start, Set<Long> allowed, Map<Long, User> idx) {
        Map<Long, Integer> dist = new HashMap<>();
        for (Long id : allowed) dist.put(id, -1);

        Queue<Long> q = new ArrayDeque<>();
        dist.put(start, 0);
        q.add(start);

        int max = 0;
        while (!q.isEmpty()) {
            Long id = q.poll();
            User u = idx.get(id);
            if (u == null) continue;

            for (Long f : u.getFriends()) {
                if (!allowed.contains(f)) continue;
                if (dist.get(f) == -1) {
                    dist.put(f, dist.get(id) + 1);
                    max = Math.max(max, dist.get(f));
                    q.add(f);
                }
            }
        }
        return max;
    }
}
