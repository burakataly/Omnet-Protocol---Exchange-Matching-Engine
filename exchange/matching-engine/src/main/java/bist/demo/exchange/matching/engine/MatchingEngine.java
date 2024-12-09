package bist.demo.exchange.matching.engine;

import bist.demo.exchange.common.BufferHandler;
import bist.demo.exchange.common.ClientHandle;
import bist.demo.exchange.common.message.COMMAND;
import bist.demo.exchange.common.message.CommonUtils;
import bist.demo.exchange.common.TcpServer;
import bist.demo.exchange.common.message.HexShower;

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

    public MatchingEngine(String host, int port) {
        this.host = host;
        this.port = port;

        tcpServer = new TcpServer("MatchingEngine", host, port, this);
    }

    @Override
    public void handleMessage(ByteBuffer buffer, ClientHandle clientHandle) {
        if(buffer == null || clientHandle == null) return;
        if(CommonUtils.isInvalidOuchMessage(buffer)) return;

        int size = buffer.remaining();
        System.out.printf("Received size: %d\n", size);

        byte[] receivedBytes = new byte[size];
        buffer.get(receivedBytes);

        boolean crcResult = CommonUtils.CRCChecker(receivedBytes);

        if(crcResult){
            System.out.printf("CRC is true. Received buffer: %s\n", HexShower.convertToHexString(receivedBytes));

            if(buffer.get(3) == (byte) COMMAND.HEARTBEAT.getValue()){
                int sequenceNumber = buffer.getInt(4);
                ByteBuffer dataBuffer = ByteBuffer.allocate(4);
                dataBuffer.putInt(sequenceNumber + 1);
                ByteBuffer responseBuffer = CommonUtils.createOuchMessage(0x48, dataBuffer.array());

                try{
                    clientHandle.send(responseBuffer);
                    System.out.printf("Sent size: %d\n", responseBuffer.array().length);
                    System.out.printf("Sent buffer: %s\n", HexShower.convertToHexString(responseBuffer.array()));
                }catch(IOException e){
                    System.out.println("Send error " + e.getMessage());
                    System.out.println("Closing application.");
                    System.exit(1);
                }
            }
        }
        else{
            System.out.println("CRC is false");
        }
        System.out.println("---------------------------------------------");
    }

    public void start() throws IOException {
        tcpServer.start();
    }

    public void stop() throws IOException {
        tcpServer.stop();
    }
}
