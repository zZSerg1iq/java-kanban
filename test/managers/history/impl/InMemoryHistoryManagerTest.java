package managers.history.impl;

import managers.Managers;
import managers.history.HistoryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager manager;

    @BeforeAll
    public static void getManager(){
        manager = Managers.getDefaultHistory();
    }

    @BeforeEach


    @Test
    void getHistoryShouldShowNothing() {

    }

    @Test
    void add() {
    }

    @Test
    void remove() {
    }
}