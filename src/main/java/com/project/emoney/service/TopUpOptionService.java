package com.project.emoney.service;

import com.project.emoney.entity.TopUpOption;

import java.util.List;

public interface TopUpOptionService {
    void insert(TopUpOption topUpOption);
    List<TopUpOption> getAll();
    TopUpOption getById(long id);
}
