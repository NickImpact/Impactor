package net.impactdev.impactor.sponge.text.placeholders.provided;

public class Memory {

    private static final long MAX = getRuntime().maxMemory() / 1024 / 1024;

    public static long getCurrent() {
        return getRuntime().totalMemory() / 1024 / 1024 - getRuntime().freeMemory() / 1024 / 1024;
    }

    public static long getAllocated() {
        return getRuntime().totalMemory() / 1024 / 1024;
    }

    public static long getMax() {
        return MAX;
    }

    private static Runtime getRuntime() {
        return Runtime.getRuntime();
    }

    public static double toGigs(long value) {
        return (double)(value / 1024.0);
    }

}
