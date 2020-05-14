package com.project.emoney.service.impl;

import com.project.emoney.entity.TopUpOption;
import com.project.emoney.mapper.TopUpOptionMapper;
import com.project.emoney.service.TopUpOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopUpOptionServiceImpl implements TopUpOptionService {

    @Autowired
    private TopUpOptionMapper topUpOptionMapper;

    @Override
    public void createTopUpOption(TopUpOption topUpOption) {
        topUpOptionMapper.createTopUpOption(topUpOption);
    }

    @Override
    public List<TopUpOption> getListTopUpOption() {
        return topUpOptionMapper.getAllTopUpOption();
    }

    @Override
    public TopUpOption getById(long id) {
        return topUpOptionMapper.getById(id);
    }
}
