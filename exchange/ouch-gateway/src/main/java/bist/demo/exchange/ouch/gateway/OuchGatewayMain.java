package bist.demo.exchange.ouch.gateway;

import java.io.IOException;

public class OuchGatewayMain {

    public static void main(String[] args) throws IOException {
        OuchGateway ouchGateway = new OuchGateway("127.0.0.1", 10000);

        boolean connect = ouchGateway.connect();
        if (!connect) return;

        ouchGateway.start();
        System.in.read(); //kullanıcı bişeye basana kadar bekliyo, client çalışmaya devam ediyo
        ouchGateway.stop();
    }
}
