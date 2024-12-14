package bist.demo.exchange.common.message;

import java.nio.ByteBuffer;

public interface MessageHandler {
    ByteBuffer handle(Command command, ByteBuffer data);
}
