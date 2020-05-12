package com.project.emoney.mybatis;


import com.project.emoney.entity.TopUpOption;

import java.util.List;

public interface TopUpOptionService {

    void createTopUpOption(TopUpOption topUpOption);
    List<TopUpOption> getListTopUpOption();
}
