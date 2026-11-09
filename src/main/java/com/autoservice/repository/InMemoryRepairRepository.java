package com.autoservice.repository;

import com.autoservice.model.RepairOrder;

import java.util.ArrayList;
import java.util.List;


public class InMemoryRepairRepository implements RepairRepository {

    private final List<RepairOrder> orders = new ArrayList<>();

    @Override
    public void save(RepairOrder order) {
        if (findById(order.getId()) == null) {
            orders.add(order);
        }
    }

    @Override
    public RepairOrder findById(String id) {
        for (RepairOrder order : orders) {
            if (order.getId().equals(id)) {
                return order;
            }
        }
        return null;
    }
}
