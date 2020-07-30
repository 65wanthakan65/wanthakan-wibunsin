package com.challenge.healthcheck.rest.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class HealthcheckServiceTest {

    private HealthcheckService healthcheckService;

    @BeforeEach
    public void setUp() throws Exception {
    	healthcheckService = new HealthcheckService();
    }

    @Test
    public void checkServerReachable() throws Exception {
    	healthcheckService.isServerReachable("http://google.com");
    }
    
    @Test
    public void checkDataFromCSV() throws Exception {
    	healthcheckService.getDataFromCSV(null);
    }
}