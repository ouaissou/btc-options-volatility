package com.wei.binance.binance_options_volatility.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wei.binance.binance_options_volatility.service.OptionService;
import com.wei.binance.binance_options_volatility.model.OptionData;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    @Autowired
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/calls")
    public List<OptionData> getCallOptions() {
        return optionService.getCallOptions();
    }

    @GetMapping("/puts")
    public List<OptionData> getPutOptions() {
        return optionService.getPutOptions();
    }
}
