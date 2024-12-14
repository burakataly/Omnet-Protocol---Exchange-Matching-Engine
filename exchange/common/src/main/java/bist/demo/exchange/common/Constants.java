package bist.demo.exchange.common;

public class Constants {
    public static final int BUFFER_CAPACITY = 4096;
    public static final int KEEP_ALIVE_INTERVAL_MS = 30_000;
    public static final int MESSAGE_LENGTH_EXCEPT_DATA = 1 + 2 + 1;
    public static final byte SIDE_BUY = 'B';
    public static final byte SIDE_SELL = 'S';
}
