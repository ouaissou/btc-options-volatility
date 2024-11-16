package com.wei.binance.binance_options_volatility.service;

import org.springframework.stereotype.Service;

import com.wei.binance.binance_options_volatility.model.OptionData;
import com.wei.binance.binance_options_volatility.component.WebSocketClientComponent;

import java.util.List;

@Service
public class OptionService {

    private final WebSocketClientComponent webSocketClientComponent;

    // Inject the WebSocketClientComponent to get the data
    public OptionService(WebSocketClientComponent webSocketClientComponent) {
        this.webSocketClientComponent = webSocketClientComponent;
        webSocketClientComponent.startClient(); 
    }

    public List<OptionData> getCallOptions() {
        //List<OptionData> callOptions = webSocketClientComponent.getCallOptions();
        //System.out.println("Service Layer - Call Options: " + callOptions);
        return webSocketClientComponent.getCallOptions();
    }

    public List<OptionData> getPutOptions() {
        return webSocketClientComponent.getPutOptions();
    }
}
