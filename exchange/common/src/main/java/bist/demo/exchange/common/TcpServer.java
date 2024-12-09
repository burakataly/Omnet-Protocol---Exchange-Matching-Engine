package bist.demo.exchange.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/*
* threadle bağlantı isteklerini dinleyip kabul ettiği her bağlantı için clientHandle nesnesi
* oluşturuyo. threadle bu yaratılan nesnelerin tutulduğu listeyi gezip mesajları
* handle ediyo.
*
* clientLock kullanımı ve synchronized mantığı: bir thread kabul edilmiş bağlantıları handle
* ederken diğer bir thread yeni bağlantıları dinliycek. ikisinin de kullandığı metotlar
* connectedClients listesini değiştiriyo. bu sebeple bu işin sync olması lazım. Ama metotları
* sync tanımlarsak metotlar farklı olsa da bir nesnenin bir sync metodunu bir thread
* kullanıyoken başka bir thread diğer sync metodu kullanamaz. Ancak clientLock ile yapıldığı
* gibi nesne üzerinden sync tanımlarsak, bu istenilen nesnenin kullanımının sync olması
* anlamına geliyo. Bu sayede bir threadin o nesneyle işi bittikten sonra diğer thread onu
* kullanabilir hale geliyo yani methodu bitirmesini beklemiyo. Yani başka bir nesne için
* böyle bir ihtiyaç olsa onun için de böyle birşey implemente ederdik.
*
*/
public class TcpServer {

    private static final int BACKLOG_SIZE = 1000; //gelen bağlantı istekleri değerlendirilmeden
    //önce kuyruğa alınıyo, bu kuyruğun kapasitesini belirtiyo

    private final String serverName;

    private final String host;

    private final int port; //serverın çalışacağı port

    private final BufferHandler bufferHandler; //bu aslında server tarafının application layer
    //ının referansını tutacak

    private ScheduledExecutorService threadPool = null;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private ServerSocket serverSocket = null; //bağlantı isteklerini dinleyecek socket
    private boolean terminateAcceptor = false; //server kapatılırken bağlantı isteği dinleme
    //loopunu sonlandırmaya yarıyo

    private final List<ClientHandle> connectedClients = new ArrayList<>(); //herbir client için
    //yaratılan handle nesneleri
    private final Object clientLock = new Object(); //connectedClients listesini synchronized
    //olarak kullanmak için

    public TcpServer(String serverName, String host, int port, BufferHandler bufferHandler) {
        this.serverName = serverName;
        this.host = host;
        this.port = port;
        this.bufferHandler = bufferHandler;
    }

    public void stop() throws IOException {
        if (threadPool == null) {
            return;
        }

        terminateAcceptor = true;
        serverSocket.close();

        try {
            threadPool.shutdown();
        } catch (Exception exception) {
            System.err.println("Error while stopping thread. " + exception);
        } finally {
            threadPool = null;
        }
    }

    public void start() throws IOException {
        if (threadPool != null) {
            System.err.println("Already open!");
            return;
        }
        //3 task var her task için bir thread
        threadPool = Executors.newScheduledThreadPool(3,
                r -> new Thread(r, serverName + "-" + threadNumber.getAndIncrement()));

        serverSocket = new ServerSocket(port, BACKLOG_SIZE, InetAddress.getByName(host));

        System.out.printf("%s Server is opened at %s:%d.\n", serverName, host, port);
        
        threadPool.scheduleWithFixedDelay(this::handleConnections, 1000, 500, TimeUnit.MILLISECONDS);
        threadPool.scheduleWithFixedDelay(this::printStats, 5_000, 5_000, TimeUnit.MILLISECONDS);

        threadPool.schedule(this::acceptConnections, 1000, TimeUnit.MILLISECONDS);
    }

    private void printStats() {
        System.out.printf("Connected client count: %d\n", connectedClients.size());
        System.out.println("---------------------------------------------");
    }

    //tcpserver açıkkken bağlantı isteklerini dinleyip kabul
    //ettiği bağlantı için addClient ile yeni handle nesnesini listeye ekliyo
    private void acceptConnections() {
        System.out.println("Waiting clients.");

        while (true) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept(); //bu döndürülen socket'in kernel'de kendine ait bufferı var
                System.out.println("New client connected. " + clientSocket.toString());
                addClient(clientSocket);
            } catch (IOException e) {
                System.out.println("Client connection problem. " + e.getMessage());
            }

            if (terminateAcceptor) { //server kapanıyosa döngüden çıkıyo
                System.out.println("Stopping acceptor.");
                break;
            }
        }
    }

    //listeyi gezerek her bir bağlantı için requestleri handle ediyo
    private void handleConnections() {

        synchronized (clientLock) {
            Iterator<ClientHandle> iterator = connectedClients.iterator();

            while (iterator.hasNext()) {
                ClientHandle next = iterator.next();

                boolean clientHandleStatus = next.handle();

                if (!clientHandleStatus) { //eğer bu clientın handleından false dönmüşse client
                    //droplanacak
                    System.out.println("Client disconnected. " + next);
                    iterator.remove(); //listeden siliyo
                    next.closeClient(); //bağlantıyı dropluyo
                }
            }
        }
    }

    private void addClient(Socket clientSocket) {
        synchronized (clientLock) { //aynı anda bir threadin listeyi update etmesini sağlıyo
            connectedClients.add(new ClientHandle(clientSocket, bufferHandler));
            //tcp kullanıldığından herbir clientın kendi socketi, sonuç olarak kendi istek kuyruğu var
        }
    }
}
