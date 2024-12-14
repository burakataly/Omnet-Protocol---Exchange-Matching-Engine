package bist.demo.exchange.common;

public class Constants {
    public static final int BUFFER_CAPACITY = 4096;
    public static final int KEEP_ALIVE_INTERVAL_MS = 30_000;
    public static final int MESSAGE_LENGTH_EXCEPT_DATA_AND_COMMAND = 1 + 2 + 1; // start + length + crc
    public static final int MAX_COMMODITY_LENGTH = 5;
    public static final int ORDER_DATA_LENGTH = 1 + MAX_COMMODITY_LENGTH + 4 + 4; // side + commodity + quantity + price
    public static final int HEARTBEAT_DATA_LENGTH = 4; // sequence number
    public static final byte SIDE_BUY = 'B';
    public static final byte SIDE_SELL = 'S';
    public static final byte START_BYTE = (byte) 0xAA;
}
