package com.ubb.event.strategy;

import com.ubb.domain.Duck;
import com.ubb.domain.race.Lane;

public class BinarySearchStrategy extends BaseStrategy implements Strategy {
    private Duck[] sorted;
    private boolean[] used;

    @Override
    public void computeTime(Duck[] ducks, int n, Lane[] lanes, int m) {
        sorted = ducks.clone();
        selectionSortByResAscSpeedDesc(sorted);

        double maxDist = lanes[m - 1].getDistance();
        double minSpeed = ducks[0].getViteza();
        for (Duck d : ducks)
            if (d.getViteza() < minSpeed) minSpeed = d.getViteza();

        double low = 0.0;
        double high = (2.0 * maxDist) / minSpeed;

        int[] lastFeasibleAssign = new int[m];

        for (int step = 0; step < 70; step++) {
            double mid = (low + high) / 2.0;
            if (feasible(mid, lanes, m, lastFeasibleAssign)) {
                bestTime = mid;
                bestAssign = lastFeasibleAssign.clone();
                high = mid;
            } else low = mid;
        }
    }

    private boolean feasible(double T, Lane[] lanes, int m, int[] outAssign) {
        used = new boolean[sorted.length];
        double prevRes = -1;

        for (int laneIdx = 0; laneIdx < m; laneIdx++) {
            double dist = lanes[laneIdx].getDistance();
            int chosenIndex = -1;

            for (int i = 0; i < sorted.length; i++) {
                if (used[i]) continue;
                Duck d = sorted[i];
                if (d.getRezistenta() + 1e-12 < prevRes) continue;

                double time = (2.0 * dist) / d.getViteza();
                if (time <= T + 1e-12) {
                    chosenIndex = i;
                    break;
                }
            }
            if (chosenIndex == -1) return false;

            used[chosenIndex] = true;
            prevRes = sorted[chosenIndex].getRezistenta();
            outAssign[laneIdx] = sorted[chosenIndex].getId().intValue();
        }
        return true;
    }

    private void selectionSortByResAscSpeedDesc(Duck[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j].getRezistenta() < a[best].getRezistenta() ||
                        (a[j].getRezistenta() == a[best].getRezistenta() &&
                                a[j].getViteza() > a[best].getViteza())) {
                    best = j;
                }
            }
            Duck t = a[i];
            a[i] = a[best];
            a[best] = t;
        }
    }
}
