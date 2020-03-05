package DoD;

import java.util.Random;

public class Utils {

    /**
     * A function to generate a random 2d vector (position) within the ranges given
     * @param minX minimum x bound
     * @param maxX maximum x bound
     * @param minY minimum y bound
     * @param maxY minimum y bound
     * @return A random 2d vector (position)
     */
    public static int[] getRandomPosInRange(int minX, int maxX, int minY, int maxY) {
        int[] pos = {0, 0};
        Random r = new Random();
        pos[0] = r.nextInt(maxX-minX) + minX; // this algebra allow us to set a min and max bound for the random number
        pos[1] = r.nextInt(maxY-minY) + minY;
        return pos;
    }

}
