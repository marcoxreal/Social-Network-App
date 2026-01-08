package com.ubb.event;

import com.ubb.domain.Duck;
import com.ubb.domain.race.Lane;
import com.ubb.event.strategy.BaseStrategy;
import com.ubb.event.strategy.Strategy;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RaceEvent extends AbstractObservable<RaceEventEvent> {

    private final Long id;
    private final String name;

    private final List<Duck> participants = new CopyOnWriteArrayList<>();
    private final List<Lane> lanes = new ArrayList<>();

    private Strategy strategy;

    private final ExecutorService executor;
    private final ExecutorService notifyExecutor;

    public RaceEvent(Long id, String name, Strategy strategy, List<Lane> lanes) {
        this.id = id;
        this.name = name;
        this.strategy = strategy;
        this.lanes.addAll(lanes);

        this.executor = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors())
        );
        this.notifyExecutor = Executors.newSingleThreadExecutor();
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    public void setStrategy(Strategy strategy) { this.strategy = strategy; }

    public void addParticipant(Duck duck) {
        if (duck == null) return;
        if (!participants.contains(duck)) {
            participants.add(duck);
            asyncNotify("✅ " + duck.getUsername() + " s-a înscris.");
        }
    }

    public List<Duck> getParticipants() {
        return List.copyOf(participants);
    }

    public List<Lane> getLanes() {
        return List.copyOf(lanes);
    }

    /**
     * Start asincron:
     * 1) calculează assignment optim cu Strategy (async)
     * 2) rulează “cursa” pe lane-uri în paralel (async) și notifică live
     */
    public CompletableFuture<RaceResult> startAsync() {
        if (participants.isEmpty()) {
            String msg = "Nu există participanți în " + name + "!";
            asyncNotify("⚠️ " + msg);
            return CompletableFuture.completedFuture(new RaceResult(name, 0, List.of(), msg));
        }
        if (lanes.isEmpty()) {
            String msg = "Nu există lane-uri definite pentru " + name + "!";
            asyncNotify("⚠️ " + msg);
            return CompletableFuture.completedFuture(new RaceResult(name, 0, List.of(), msg));
        }
        int m = lanes.size();
        if (participants.size() < m) {
            String msg = "Prea puțini participanți: ai " + participants.size() + " dar ai " + m + " lane-uri.";
            asyncNotify("⚠️ " + msg);
            return CompletableFuture.completedFuture(new RaceResult(name, 0, List.of(), msg));
        }

        asyncNotify("🏁 Cursa " + name + " începe. Lane-uri: " + m + ", ducks: " + participants.size());
        Duck[] ducksArr = participants.toArray(new Duck[0]);
        Lane[] lanesArr = lanes.toArray(new Lane[0]);

        CompletableFuture<Plan> planFuture = CompletableFuture.supplyAsync(() -> {
            strategy.computeTime(ducksArr, ducksArr.length, lanesArr, lanesArr.length);

            if (strategy instanceof BaseStrategy bs) {
                double bestT = bs.getBestTime();
                int[] assign = bs.getBestAssign();
                if (assign == null) {
                    throw new RuntimeException("Strategy nu a găsit asignare fezabilă.");
                }
                return buildPlan(bestT, assign, ducksArr, lanesArr);
            }

            throw new RuntimeException("Strategy trebuie să fie BaseStrategy ca să citim bestTime/bestAssign.");
        }, executor);

        return planFuture.thenCompose(plan -> runRaceSimulation(plan))
                .exceptionally(ex -> {
                    String msg = "Eroare la cursa " + name + ": " + ex.getMessage();
                    asyncNotify("💥 " + msg);
                    return new RaceResult(name, 0, List.of(), msg);
                });
    }

    /**
     * Construiește maparea lane -> duck și verifică existența id-urilor.
     * assign[] conține duckId (int) pentru fiecare laneIdx.
     */
    private Plan buildPlan(double bestTime, int[] assign, Duck[] ducksArr, Lane[] lanesArr) {
        Map<Long, Duck> byId = Arrays.stream(ducksArr)
                .collect(Collectors.toMap(d -> d.getId(), d -> d));

        List<LaneRun> runs = new ArrayList<>();
        for (int laneIdx = 0; laneIdx < lanesArr.length; laneIdx++) {
            long duckId = assign[laneIdx];
            Duck duck = byId.get(duckId);
            if (duck == null) {
                throw new RuntimeException("Assign invalid: duckId=" + duckId + " nu există în participanți.");
            }
            Lane lane = lanesArr[laneIdx];

            double time = (2.0 * lane.getDistance()) / duck.getViteza();
            runs.add(new LaneRun(lane, duck, time));
        }

        asyncNotify("🧠 Plan calculat: bestTime≈" + String.format("%.3f", bestTime) + "s");
        for (LaneRun r : runs) {
            asyncNotify("Lane " + r.lane().getId() + " (" + r.lane().getDistance() + "m) -> " +
                    r.duck().getUsername() + " (t=" + String.format("%.3f", r.timeSeconds()) + "s)");
        }

        return new Plan(bestTime, runs);
    }

    /**
     * Simulează efectiv “derularea”:
     * - pornește fiecare LaneRun în paralel (CompletableFuture)
     * - trimite notificări start/finish
     * - returnează RaceResult cu ranking după timpul lane-ului
     */
    private CompletableFuture<RaceResult> runRaceSimulation(Plan plan) {
        asyncNotify("🏎️ Derulare cursă...");

        List<CompletableFuture<LaneRun>> futures = plan.runs().stream()
                .map(run -> CompletableFuture.supplyAsync(() -> {
                    asyncNotify("➡️ Lane " + run.lane().getId() + ": " + run.duck().getUsername() + " a pornit");
                    sleepScaled(run.timeSeconds());
                    asyncNotify("✅ Lane " + run.lane().getId() + ": " + run.duck().getUsername() +
                            " a terminat (" + String.format("%.3f", run.timeSeconds()) + "s)");
                    return run;
                }, executor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                .thenApply(doneRuns -> {
                    doneRuns.sort(Comparator.comparingDouble(LaneRun::timeSeconds));

                    List<Duck> ranking = doneRuns.stream().map(LaneRun::duck).toList();
                    Duck winner = ranking.get(0);
                    double best = doneRuns.get(0).timeSeconds();

                    String msg = "🏆 Cursa " + name + " s-a terminat! Winner: " +
                            winner.getUsername() + " (" + String.format("%.3f", best) + "s)";

                    asyncNotify(msg);

                    return new RaceResult(name, best, ranking, msg);
                });
    }

    private void sleepScaled(double seconds) {
        // scale ca să fie “vizibil” în UI fără să sa astepte prea mult
        long ms = Math.max(120, (long) (seconds * 250));
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void asyncNotify(String msg) {
        notifyExecutor.execute(() -> notifyObservers(new RaceEventEvent(name, msg)));
    }

    public void shutdown() {
        executor.shutdownNow();
        notifyExecutor.shutdownNow();
    }

    private record Plan(double bestTime, List<LaneRun> runs) {}
    private record LaneRun(Lane lane, Duck duck, double timeSeconds) {}
}
