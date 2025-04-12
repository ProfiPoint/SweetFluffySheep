package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;

public class Unicorn extends Character {
    static int counter = 0;
    static int goalUnicorns = 1;
    static int unicornsInGoal = 0;
    boolean inBound = true;
    int ticksToGetInBound = 0;

    public Unicorn(double[] position, DirectionEnum direction) {
        super("unicorn", position, direction, PhisicsEventsEnum.BEFORE_START);
        this.setEnemy(false);
        counter++;
        gameStatus = GameStatusEnum.PLAYING; // reset game status
        unicornsInGoal = 0; // reset unicorns in goal
    }

    public static void setGoalUnicorns(int goal) {
        goalUnicorns = goal;
    }

    public void kill() {
        super.kill();
        counter--;
        unicornEnteredGoal(false);
        if (counter < goalUnicorns && gameStatus != GameStatusEnum.WIN) {
            System.out.println("Game Over");
            gameStatus = GameStatusEnum.LOSE;
        }
    }

    public static void unicornEnteredGoal(boolean entered) {
        if (entered) {
            unicornsInGoal++;
        }
        // print counter, goalUnicorns and unicornsInGoal and counter - goalUnicorns
        System.out.println("Counter: " + counter);
        System.out.println("Goal unicorns: " + goalUnicorns);
        System.out.println("Unicorns in goal: " + unicornsInGoal);
        System.out.println("Counter - goalUnicorns: " + (counter - unicornsInGoal));
        System.out.println("");
        if (counter - unicornsInGoal == 0 && unicornsInGoal >= goalUnicorns) {
            System.out.println("You win!");
            gameStatus = GameStatusEnum.WIN;
        }
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (inBound == false) {
            // unaffected by arrows and borders
            ticksToGetInBound++;
            if (ticksToGetInBound >= 0) {
                inBound = true;
            }
        } else {
            // affected by arrows and borders
        }
    }

    public static int getGoalUnicorns() {
        return goalUnicorns;
    }

    public static int getUnicornsAlive() {
        return counter;
    }
}
