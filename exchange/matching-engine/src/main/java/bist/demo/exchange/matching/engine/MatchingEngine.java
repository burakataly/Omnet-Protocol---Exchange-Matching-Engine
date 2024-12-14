package bist.demo.exchange.matching.engine;

import bist.demo.exchange.common.BufferHandler;
import bist.demo.exchange.common.ClientHandle;
import bist.demo.exchange.common.message.Command;
import bist.demo.exchange.common.message.CommonUtils;
import bist.demo.exchange.common.TcpServer;
import bist.demo.exchange.common.message.HexShower;
import bist.demo.exchange.common.message.MessageParser;

import java.io.IOException;
import java.nio.ByteBuffer;

/*
* serverın application layer tarafı. tcp server ile bağlantı kuruyo. tcpServer'ın
* constructorına bufferhandler olarak kendisini veriyo ki tcpServer içindeki bir
* clientHandler nesnesi bir clientın mesajını handle etmesi için bunu çağırsın
*/
public class MatchingEngine implements BufferHandler {
    private final String host;
    private final int port;
    private final TcpServer tcpServer;
    private final MessageParser messageParser;

    public MatchingEngine(String host, int port) {
        this.host = host;
        this.port = port;

        tcpServer = new TcpServer("MatchingEngine", host, port, this);

        messageParser = new MessageParser(this::handleDefaultMessage);
        messageParser.addParser(Command.HEARTBEAT, this::handleHeartbeat);
    }

    @Override
    public void handleMessage(ByteBuffer buffer, ClientHandle clientHandle) {
        ByteBuffer responseBuffer = messageParser.parseReceivedMessage(buffer);
        try{
            clientHandle.send(responseBuffer);
            System.out.printf("Sent size: %d\n", responseBuffer.array().length);
            System.out.printf("Sent buffer: %s\n", HexShower.convertToHexString(responseBuffer.array()));
        }catch(IOException e){
            System.out.println("Send error " + e.getMessage());
            System.out.println("Closing application.");
            System.exit(1);
        }
        System.out.println("---------------------------------------------");
    }

    private ByteBuffer handleDefaultMessage(Command command, ByteBuffer data) {
        System.out.printf("Un-registered %s command received. Size is %d\n", command, data.remaining());
        return null;
    }

    private ByteBuffer handleHeartbeat(Command command, ByteBuffer data) {
        int sequenceNumber = data.getInt();
        System.out.printf("Heartbeat command received. sequence number: %d\n", sequenceNumber);
        ByteBuffer dataBuffer = ByteBuffer.allocate(4);
        dataBuffer.putInt(sequenceNumber + 1);
        return CommonUtils.createOuchMessage(command.getValue(), dataBuffer.array());
    }

    public void start() throws IOException {
        tcpServer.start();
    }

    public void stop() throws IOException {
        tcpServer.stop();
    }
}
