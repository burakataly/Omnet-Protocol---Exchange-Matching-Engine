package bist.demo.exchange.matching.engine;

import java.io.IOException;

public class MatchingEngineMain {

    public static void main(String[] args) throws IOException {

        MatchingEngine matchingEngine = new MatchingEngine("127.0.0.1", 10000);

        matchingEngine.start();
        System.in.read();
        matchingEngine.stop();
    }
}
