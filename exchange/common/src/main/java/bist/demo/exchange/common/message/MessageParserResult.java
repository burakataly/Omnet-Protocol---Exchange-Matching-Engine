package bist.demo.exchange.common.message;

public enum MessageParserResult {
    SUCCESSFUL,
    TOO_SMALL_MESSAGE,
    SIZE_MISMATCH,
    WRONG_MESSAGE_START,
    UNKNOWN_COMMAND,
    CRC_MISMATCH,
    WRONG_MESSAGE_END
}
