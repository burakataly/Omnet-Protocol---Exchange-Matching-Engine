package bist.demo.exchange;

import bist.demo.exchange.common.Constants;
import bist.demo.exchange.common.message.Command;
import bist.demo.exchange.ouch.gateway.OuchGateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        OuchGateway ouchGateway = new OuchGateway("127.0.0.1", 10000);

        boolean connect = ouchGateway.connect();

        if (!connect) {
            return;
        }
        OuchClient ouchClient = new OuchClient(ouchGateway);

        ouchGateway.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        do {

            String command = reader.readLine();

            System.out.println(command);

            if ("x".equals(command)) {
                ouchGateway.stop();
                break;
            }

            if (command.startsWith("o:")) {
                sendOrder(ouchClient, command);
            }

        } while (true);

    }

    /*
     o:b,25@100
     o:s,20@105
     */
    private static void sendOrder(OuchClient ouchClient, String order) {
        String[] split = order.split(":");

        if (split.length != 2) {
            System.err.println("wrong command!");
            return;
        }

        String[] orderTokens = split[1].split(",");

        if (orderTokens.length != 2) {
            System.err.println("wrong order details!");
            return;
        }

        byte side = Constants.SIDE_SELL;
        int quantity;
        int price;

        /*
         todo verify 'S'
         */
        if ("B".equals(orderTokens[0]) || "b".equals(orderTokens[0])) {
            side = Constants.SIDE_BUY;
        }

        String[] prices = orderTokens[1].split("@");

        if (prices.length != 2) {
            System.err.println("wrong price details!");
            return;
        }

        try {
            quantity = Integer.parseInt(prices[0]);
            price = Integer.parseInt(prices[1]);
        } catch (Exception exception) {
            System.err.println("parsing error! " + exception.getMessage());
            return;
        }

        ouchClient.sendOrder("APPLE", side, quantity, price);
    }
//        String host = "127.0.0.1";
//        int port = 10000;
//        try {
//            OuchGateway ouchGateway = new OuchGateway(host, port);
//            if (ouchGateway.connect()) {
//                ouchGateway.start();
//                OuchClient client = new OuchClient(ouchGateway);
//                String commodity = "APPLE";
//
//                client.sendOrder(commodity, Constants.SIDE_SELL, 2, 30);
//                client.sendOrder(commodity, Constants.SIDE_SELL, 5, 30);
//                client.sendOrder(commodity, Constants.SIDE_BUY, 3, 30);
//                client.sendOrder(commodity, Constants.SIDE_BUY, 4, 20);
//                client.sendOrder(commodity, Constants.SIDE_BUY, 7, 30);
//                client.sendOrder(commodity, Constants.SIDE_SELL, 1, 40);
//                client.sendOrder(commodity, Constants.SIDE_BUY, 2, 40);
//            }
//            else {
//                System.out.println("Failed to connect to the server.");
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//
//    }
}
