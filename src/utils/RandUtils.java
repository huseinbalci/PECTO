package utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandUtils {
    static Random rng = new Random();


    public static boolean rollDice(double p) {
        int max = 100000;

        return rng.nextInt(max)< p*max;
    }

    public static int generateRandomInt(int max) {
        return rng.nextInt(max);
    }

    public static double generateRandomDouble(double max) {
        return rng.nextDouble(max);
    }

    public static double prob() {
        return rng.nextDouble();
    }
}
