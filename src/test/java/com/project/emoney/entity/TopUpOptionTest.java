package com.project.emoney.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TopUpOptionTest {

    private static final long ID = 1;
    private static final long VALUE = 50000;
    private static final long FEE = 1500;


    @Test
    public void constructor() {
        TopUpOption topUpOption = new TopUpOption(ID, VALUE, FEE);
        assertNotNull(topUpOption);
        assertNotNull(topUpOption.getId());
        assertNotNull(topUpOption.getValue());
        assertEquals(topUpOption.getValue(), VALUE);
        assertNotNull(topUpOption.getFee());
        assertEquals(topUpOption.getFee(), FEE);
    }

    @Test
    public void setterValue() {
        TopUpOption topUpOption = new TopUpOption(ID, VALUE, FEE);
        assertNotNull(topUpOption);
        assertNotNull(topUpOption.getId());
        assertNotNull(topUpOption.getValue());
        assertEquals(topUpOption.getValue(), VALUE);
        assertNotNull(topUpOption.getFee());
        assertEquals(topUpOption.getFee(), FEE);

        topUpOption.setValue(60000);
        assertEquals(topUpOption.getValue(), 60000);
    }

    @Test
    public void setterFee() {
        TopUpOption topUpOption = new TopUpOption(ID, VALUE, FEE);
        assertNotNull(topUpOption);
        assertNotNull(topUpOption.getId());
        assertNotNull(topUpOption.getValue());
        assertEquals(topUpOption.getValue(), VALUE);
        assertNotNull(topUpOption.getFee());
        assertEquals(topUpOption.getFee(), FEE);

        topUpOption.setFee(2000);
        assertEquals(topUpOption.getFee(), 2000);
    }


}
