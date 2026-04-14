package Algorithm;

public final class HashFunction {
    private HashFunction() {
    }

    public static int hash(String key, int primaryBlockCount) {
        int hash = 0;

        for (int i = 0; i < key.length(); i++) {
            hash = 31 * hash + key.charAt(i);
        }

        return Math.floorMod(hash, primaryBlockCount);
    }
}