import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HubPanelTest {

    @Test
    void easyDiffReturnsSize5() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals(5, logic.setDiff("Easy"));
    }

    @Test
    void normalDiffReturnsSize10() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals(10, logic.setDiff("Normal"));
    }

    @Test
    void hardDiffReturnsSize15() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals(15, logic.setDiff("Hard"));
    }

    @Test
    void healthStartsAt3() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals(3, logic.getHealth());
    }

    @Test
    void revealingWhiteCellDecreasesHealth() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boolean wasBlack = logic.revealCell(i, j);
                if (!wasBlack) {
                    assertEquals(2, logic.getHealth());
                    return;
                }
            }
        }
    }

    @Test
    void cellIsRevealedAfterClick() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        logic.revealCell(0, 0);
        assertTrue(logic.isReavealed(0, 0));
    }

    @Test
    void cellIsNotRevealedBeforeClick() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertFalse(logic.isReavealed(0, 0));
    }

    @Test
    void gameIsNotWonAtStart() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertFalse(logic.isWon());
    }

    @Test
    void gameIsNotDeadAtStart() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertFalse(logic.isDead());
    }

    @Test
    void buildCountAllWhiteReturnsZero() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals("0", logic.buildCount(new boolean[]{false, false, false}));
    }

    @Test
    void buildCountAllBlackReturnsSingleNumber() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals("3", logic.buildCount(new boolean[]{true, true, true}));
    }

    @Test
    void buildCountTwoGroupsReturnsCorrectClue() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals("1 1", logic.buildCount(new boolean[]{true, false, true}));
    }

    @Test
    void fasterTimeMeansHigherScore() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertTrue(logic.calculateScore(10) > logic.calculateScore(100));
    }

    @Test
    void getSizeReturnsCorrectSize() {
        NonogrammLogic logic = new NonogrammLogic(5, 3);
        assertEquals(5, logic.getSize());
    }
}