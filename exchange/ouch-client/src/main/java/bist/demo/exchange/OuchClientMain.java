package bist.demo.exchange;

import bist.demo.exchange.common.Constants;
import bist.demo.exchange.ouch.gateway.OuchGateway;

import java.io.IOException;

public class OuchClientMain {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 10000;
        try {
            OuchGateway ouchGateway = new OuchGateway(host, port);
            if (ouchGateway.connect()) {
                ouchGateway.start();
                OuchClient client = new OuchClient(ouchGateway);
                String commodity = "APPLE";

                client.sendOrder(commodity, Constants.SIDE_SELL, 2, 30);
                client.sendOrder(commodity, Constants.SIDE_SELL, 5, 30);
                client.sendOrder(commodity, Constants.SIDE_BUY, 3, 30);
                client.sendOrder(commodity, Constants.SIDE_BUY, 4, 20);
                client.sendOrder(commodity, Constants.SIDE_BUY, 7, 30);
                client.sendOrder(commodity, Constants.SIDE_SELL, 1, 40);
                client.sendOrder(commodity, Constants.SIDE_BUY, 2, 10);
                client.sendOrder(commodity, Constants.SIDE_SELL, 2, 50);
            } else {
                System.out.println("Failed to connect to the server.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
