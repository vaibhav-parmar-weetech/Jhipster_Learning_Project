package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OrderProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OrderProduct getOrderProductSample1() {
        return new OrderProduct().id(1L).orderQty(1);
    }

    public static OrderProduct getOrderProductSample2() {
        return new OrderProduct().id(2L).orderQty(2);
    }

    public static OrderProduct getOrderProductRandomSampleGenerator() {
        return new OrderProduct().id(longCount.incrementAndGet()).orderQty(intCount.incrementAndGet());
    }
}
