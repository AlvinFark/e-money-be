package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.mybatis.TopUpOptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopUpWorker {

    @Autowired
    private TopUpOptionService topUpOptionService;

    ObjectMapper objectMapper = new ObjectMapper();
    private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

    public String topUpOption() throws JsonProcessingException {
        log.info("[TopUpOption]  Receive get Top Up Option");

        List<TopUpOption> list = topUpOptionService.getListTopUpOption();
        return objectMapper.writeValueAsString(list);
    }
}
