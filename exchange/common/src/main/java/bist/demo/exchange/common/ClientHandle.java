package bist.demo.exchange.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/*
* Tcpserver'da her bir client için bir tane bu sınıfın nesnesinden var
* bir tcpClient'dan gelen mesajları socketten alıp, bytebuffer nesnesine yazıp serverın
* application layerına handle etmesi için gönderiyo. ayrıca app layer'dan client'a
* gönderilecek mesaj da bunun üzerinden gidiyor.
*/
public class ClientHandle {
    private final Socket socket; //client'ın socketi

    private final BufferHandler bufferHandler;

    private final ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_CAPACITY);

    private long expireTime;

    public ClientHandle(Socket socket, BufferHandler bufferHandler) {
        this.socket = socket;
        this.bufferHandler = bufferHandler;

        setExpireTime();
    }

    private void setExpireTime() {
        expireTime = System.currentTimeMillis() + Constants.KEEP_ALIVE_INTERVAL_MS;
    }

    private boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }

    public boolean handle() {

        if (isExpired()) {
            System.err.println("Client socket is disconnected. " + socket);
            return false;
        }

        InputStream inputStream;
        try {
            inputStream = socket.getInputStream(); //kernelde bulunan bufferdaki byte şeklindeki verileri alır
        } catch (IOException e) {
            System.err.println("Input stream read error. " + e.getMessage());
            return false;
        }

        boolean readStatus = readIncomingMessage(inputStream); //kernelden alınan byteları tek tek okuyup bytebuffer nesnesine yazacak

        if (!readStatus) {
            System.err.println("Read incoming message error.");
            return false;
        }

        int size = buffer.remaining(); //mesajlar okunup buffera yazıldıktan sonra okunmak
        // için position set ediliyo, eğer remaningden pozitif değer dönmezse bu okunacak
        // değerin olmadığı anlamına geliyo. Bu socketten boş buffer döndüyse veya bufferı
        // okurken exception olduysa falan olabilir.

        if (size <= 0) { //mesaj okunmadığından expire time set edilmeden dönülüyo.
            return true;
        }

        setExpireTime(); //bir dahaki mesaj için 30 sn mühlet

        bufferHandler.handleMessage(buffer, this); //serverın handle etmesi için bytebuffer nesnesi gönderiliyo
        return true;
    }

    private boolean readIncomingMessage(InputStream inputStream) {
        int readByte;

        buffer.clear(); //bytebuffera yeni okunacak mesajı yazmadan önce temizliyo ve position 0'a set ediliyo

        while (true) {

            try {
                if (inputStream.available() <= 0) {
                    break;
                }
            } catch (IOException e) {
                return false;
            }

            try {
                readByte = inputStream.read(); //byte byte okuyo
            } catch (IOException e) {
                return false;
            }

            if (readByte == -1) {
                break;
            }

            buffer.put((byte) readByte); //eğer okunan byteda sorun yoksa buffera yazıyo
            //bu yazmadan sonra position 1 artar
        }
        buffer.flip(); //buffera yazma işi bittikten sonra okunabilmesi için buffer kapasitesini
        //gösteren limit o anki positiona position ise 0'a set ediliyo
        return true;
    }

    public void send(ByteBuffer sendingBuffer) throws IOException{
        if(socket == null || socket.isClosed()){
            return;
        }

        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("Output stream read error. " + e.getMessage());
            return;
        }

        byte[] bytes = new byte[sendingBuffer.remaining()];
        sendingBuffer.get(bytes);
        outputStream.write(bytes);
    }

    public void closeClient() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Socket close error. " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "ClientHandle{" +
                "socket=" + socket +
                '}';
    }
}
