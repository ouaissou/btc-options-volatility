package com.wei.binance.binance_options_volatility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class BinanceOptionsVolatilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinanceOptionsVolatilityApplication.class, args);
    }
}

@Component
class WebSocketClientComponent implements CommandLineRunner {
    private static final String ENDPOINT = "wss://nbstream.binance.com/eoptions/ws";
    private static final String EXPIRATION_DATE = "241227";
    private final ObjectMapper mapper;

    @Autowired
    public WebSocketClientComponent(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void run(String... args) {
        try {
            BinanceWebSocketClient client = new BinanceWebSocketClient(new URI(ENDPOINT));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BinanceWebSocketClient extends WebSocketClient {
        public BinanceWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            String subscribeMessage = String.format(
                "{\"method\":\"SUBSCRIBE\",\"params\":[\"BTC@ticker@%s\"],\"id\":1}",
                EXPIRATION_DATE
            );
            send(subscribeMessage);
        }

        @Override
        public void onMessage(String message) {
            try {
                processMessage(message);
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Connection closed");
        }

        @Override
        public void onError(Exception ex) {
            System.err.println("Error occurred: " + ex.getMessage());
        }

        private void processMessage(String message) throws Exception {
            
            // Parse JSON message
            List<Map<String, Object>> dataList = mapper.readValue(message, List.class);
            
            // Create data structures for calls and puts
            List<OptionData> callsData = new ArrayList<>();
            List<OptionData> putsData = new ArrayList<>();

            // Process each item in the list
            for (Map<String, Object> data : dataList) {
                String symbol = (String) data.get("s");
                Double volatility = Double.parseDouble(data.get("vo").toString());

                // Check if it's a call or put option based on the symbol
                if (symbol.endsWith("-C")) {
                    String strikePrice = symbol.split("-")[2];
                    System.out.println("Call option strike price: " + strikePrice);
                    callsData.add(new OptionData(Double.parseDouble(strikePrice), volatility));
                    System.out.println("Call option volatility: " + volatility);
                } else if (symbol.endsWith("-P")) {
                    String strikePrice = symbol.split("-")[2];
                    System.out.println("Put option strike price: " + strikePrice);
                    putsData.add(new OptionData(Double.parseDouble(strikePrice), volatility));
                    System.out.println("Put option volatility: " + volatility);
                }
            }

            // Sort data
            callsData.sort(Comparator.comparing(OptionData::getStrikePrice));
            putsData.sort(Comparator.comparing(OptionData::getStrikePrice));
        }

    }

    static class OptionData {
        private final double strikePrice;
        private final double volatility;

        public OptionData(double strikePrice, double volatility) {
            this.strikePrice = strikePrice;
            this.volatility = volatility;
        }

        public double getStrikePrice() {
            return strikePrice;
        }

        public double getVolatility() {
            return volatility;
        }
    }
}

