package bist.demo.exchange.ouch.gateway;

import bist.demo.exchange.common.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OuchGatewayMain {

    public static void main(String[] args) throws IOException {
        OuchGateway ouchGateway = new OuchGateway("127.0.0.1", 10000);

        boolean connect = ouchGateway.connect();

        if (!connect) {
            return;
        }

        ouchGateway.start();
        System.in.read();
        ouchGateway.stop();
    }
}
