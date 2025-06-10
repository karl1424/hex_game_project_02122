package dk.dtu.main;

public class NativeWrapper {
    static {
        System.loadLibrary("mylib");
    }

    public int[] runAlgorithm(int[][] board, int playerNumber, int iterations) {
        return runAlgorithmNative(board, playerNumber, iterations);
    }

    private native int[] runAlgorithmNative(int[][] board, int playerNumber, int iterations);
}
