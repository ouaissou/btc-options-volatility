package com.wei.binance.binance_options_volatility.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.binance.binance_options_volatility.model.OptionData;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketClientComponent {

    private static final String ENDPOINT = "wss://nbstream.binance.com/eoptions/ws";
    private static final String EXPIRATION_DATE = "241227";
    private final ObjectMapper mapper;
    private List<OptionData> callOptions = new ArrayList<>();
    private List<OptionData> putOptions = new ArrayList<>();

    @Autowired
    public WebSocketClientComponent(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void startClient() {
        try {
            BinanceWebSocketClient client = new BinanceWebSocketClient(new URI(ENDPOINT));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<OptionData> getCallOptions() {
        return callOptions;
    }

    public List<OptionData> getPutOptions() {
        return putOptions;
    }

    private class BinanceWebSocketClient extends WebSocketClient {

        public BinanceWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            String subscribeMessage = String.format("{\"method\":\"SUBSCRIBE\",\"params\":[\"BTC@ticker@%s\"],\"id\":1}", EXPIRATION_DATE);
            send(subscribeMessage);
        }

        @Override
        public void onMessage(String message) {
            System.out.println("Received message: " + message);
            try {
                processMessage(message);
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
            System.out.println("Processing message..."); 
            List<Map<String, Object>> dataList = mapper.readValue(message, List.class);
            List<OptionData> callsData = new ArrayList<>();
            List<OptionData> putsData = new ArrayList<>();

            for (Map<String, Object> data : dataList) {
                String symbol = (String) data.get("s");
                Double volatility = Double.parseDouble(data.get("vo").toString());

                if (symbol.endsWith("-C")) {
                    String strikePrice = symbol.split("-")[2];
                    callsData.add(new OptionData(Double.parseDouble(strikePrice), volatility));
                } else if (symbol.endsWith("-P")) {
                    String strikePrice = symbol.split("-")[2];
                    putsData.add(new OptionData(Double.parseDouble(strikePrice), volatility));
                }
            }

            System.out.println("Call Options List: " + callOptions);
            System.out.println("Put Options List: " + putOptions);

            // Store the data for later use
            callOptions = callsData;
            putOptions = putsData;
        }
    }
}
