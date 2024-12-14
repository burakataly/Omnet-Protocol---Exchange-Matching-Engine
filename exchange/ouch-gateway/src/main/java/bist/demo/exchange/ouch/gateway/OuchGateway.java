package bist.demo.exchange.ouch.gateway;

import bist.demo.exchange.common.Constants;
import bist.demo.exchange.common.message.*;
import bist.demo.exchange.common.TcpClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
* Bu ouch client tarafının application layer ve tcp layer arasında iletişim sağlayan ara
* katmanı. düzenli aralıklarla tcpClient üzerinden heartbeat mesajı göndermek ve app
* layer'dan gelen dataları mesaja dönüştürüp tcpClient üzerinden göndermekten sorumlu.
* app layer, yalnızca datayı ve komut tipini gönderiyor, gateway ise protokol tipine göre
* mesajı oluşturuyor ve bir ByteBuffer nesnesine yazıp tcpClienttaki send metodu üzerinden
* servera gönderilmesini sağlıyor. Yine aynı şekilde serverdan gelen mesajları tcpClient
* yardımıyla dinliyor ve aldığı ByteBuffer içerisindeki mesajı çıkartıp app layer'a veriyor.
* Bu sayede app layer business logicle ilgileniyor ve tcp layerda ne olup bittiğinden haberi
* olmuyor
*/
public class OuchGateway implements ClientMessageHandler {
    private final String host;
    private final int port;
    private static int index = 0;
    private final int gatewayIndex;
    private TcpClient tcpClient;
    private boolean connectionStatus;
    private int heartBeatSeq;
    private ScheduledExecutorService threadPool;
    private final MessageParser messageParser;

    public OuchGateway(String host, int port) {
        this.host = host;
        this.port = port;

        //using method reference to pass the handleDefaultMessage method
        //ouchGateway messageHandler'ı implemente etmese de bu metodun imzası
        //interface'teki metodla aynı olduğu için yapılabiliyor.
        //ama bu yalnızca interface'te tek metod varsa geçerli.
        messageParser = new MessageParser(this::handleDefaultMessage);
        messageParser.addParser(Command.HEARTBEAT, this::handleHeartbeat);

        gatewayIndex = ++index;
    }

    public boolean connect() throws IOException {
        tcpClient = new TcpClient(host, port);

        connectionStatus = tcpClient.connect();

        System.out.printf("Connection status to %s:%d : %s\n", "127.0.0.1", 10000, connectionStatus);
        System.out.println("---------------------------------------------");
        return connectionStatus;
    }

    public void start() {
        if (!connectionStatus) {
            System.out.printf("Not connected to server. %d\n", gatewayIndex);
            return;
        }
        heartBeatSeq = 1;
        threadPool = Executors.newScheduledThreadPool(3, r -> new Thread(r, "Ouch-Gateway-Thread"));
        //3 threade sahip bir havuz oluşturuyo, bu havuza istediğimiz taskleri atayabiliriz.
        //ve bu taskler havuzda processin main threadini bloklamadan çalıştırılır.
        threadPool.scheduleWithFixedDelay(this::sendHeartbeat, 1_000, 5_000, TimeUnit.MILLISECONDS);
        threadPool.scheduleWithFixedDelay(this::handleResponse, 1_000, 500, TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat() {
        //System.out.printf("OuchGateWay %d\n", gatewayIndex);
        ByteBuffer data = ByteBuffer.allocate(Constants.HEARTBEAT_DATA_LENGTH);
        data.putInt(heartBeatSeq);
        sendMessage(Command.HEARTBEAT, data);
    }

    public void sendMessage(Command command, ByteBuffer data) {
        ByteBuffer byteBuffer = CommonUtils.createOuchMessage(command.getValue(), data.array());
        try {
            tcpClient.send(byteBuffer);
            System.out.printf("Sent size: %d\n", byteBuffer.array().length);
            System.out.printf("Sent buffer: %s\n", HexShower.convertToHexString(byteBuffer.array()));
        } catch (IOException e) {
            System.out.println("Send error " + e.getMessage());
            System.out.println("Closing application.");
            System.exit(1);
        }
        System.out.println("---------------------------------------------");
    }

    @Override
    public void handleResponse(){
        ByteBuffer buffer = tcpClient.handleResponses();
        messageParser.parseReceivedMessage(buffer);
    }

    private ByteBuffer handleDefaultMessage(Command command, ByteBuffer data) {
        System.out.printf("Un-registered %s command received. Size is %d\n", command, data.remaining());
        return null;
    }

    private ByteBuffer handleHeartbeat(Command command, ByteBuffer data) {
        int sequenceNumber = data.getInt();
        System.out.printf("Heartbeat command received. sequence number: %d\n", sequenceNumber);

        if(sequenceNumber == heartBeatSeq + 1){
            heartBeatSeq++;
        }
        else {
            System.out.printf("Heartbeat sequence number mismatch. Expected: %d, Received: %d\n", heartBeatSeq + 1, sequenceNumber);
        }

        return null;
    }

    public void stop() throws IOException {
        threadPool.shutdown();
        tcpClient.close();
        threadPool = null;
        tcpClient = null;
    }
}
