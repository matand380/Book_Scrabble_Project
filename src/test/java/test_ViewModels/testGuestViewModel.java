package test_ViewModels;

import BookScrabbleApp.Model.BookScrabbleGuestFacade;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testGuestViewModel {


    private BS_Guest_ViewModel guestViewModel;
    private BookScrabbleGuestFacade guestFacade;

    @BeforeEach
    public void setUp() {
        try {
        guestFacade = new BookScrabbleGuestFacade();
        guestViewModel = new BS_Guest_ViewModel();
            Field guestFacadeField = BS_Guest_ViewModel.class.getDeclaredField("guestFacade");
            guestFacadeField.setAccessible(true);
            guestFacadeField.set(guestViewModel, guestFacade);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testSetBoard() {
        // TODO: Implement the test for setBoard method
    }

    @Test
    public void testSetHand() {
        // TODO: Implement the test for setHand method
    }

    @Test
    public void testSetScore() {
        // TODO: Implement the test for setScore method
    }

    @Test
    public void testOpenSocket() {
        // TODO: Implement the test for openSocket method
    }

    @Test
    public void testTryPlaceWord() {
        // TODO: Implement the test for tryPlaceWord method
    }

    @Test
    public void testPassTurn() {
        // TODO: Implement the test for passTurn method
    }

    @Test
    public void testChallengeRequest() {
        // TODO: Implement the test for challengeRequest method
    }

    @Test
    public void testEndGame() {
        // TODO: Implement the test for endGame method
    }

    @Test
    public void testSetPlayerProperties() {
        // TODO: Implement the test for setPlayerProperties method
    }

    @Test
    public void testSetWordsForChallenge() {
        // TODO: Implement the test for setWordsForChallenge method
    }

    @Test
    public void testInitializeProperties() {
        guestViewModel.initializeProperties();

        // Assert that the properties are initialized correctly
        assertEquals("", guestViewModel.hostIp.get());
        assertEquals("", guestViewModel.hostPort.get());
        assertEquals(7, guestViewModel.viewableHandGuest.size());
        assertEquals(15, guestViewModel.viewableBoard.size());
        assertEquals(15, guestViewModel.viewableBoard.get(0).size());
        assertEquals(0, guestViewModel.viewableScores.size());
        assertEquals("", guestViewModel.challengeWord.get());
        assertEquals(0, guestViewModel.viewableWordsForChallenge.size());
        assertNull(guestViewModel.winnerProperty.get());
    }

    @Test
    public void testInitializeUpdateMap() {
        guestViewModel.initializeUpdateMap();

        // Assert that the updatesMap is initialized correctly

    }
}


