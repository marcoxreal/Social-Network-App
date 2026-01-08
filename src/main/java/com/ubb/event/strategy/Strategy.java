package com.ubb.event.strategy;

import com.ubb.domain.Duck;
import com.ubb.domain.race.Lane;

public interface Strategy {
    void computeTime(Duck[] ducks, int n, Lane[] lanes, int m);
}
