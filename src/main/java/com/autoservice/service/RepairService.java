package com.autoservice.service;

import com.autoservice.model.RepairOrder;
import com.autoservice.model.RepairStatus;
import com.autoservice.repository.RepairRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepairService {

    private static final Logger log = LoggerFactory.getLogger(RepairService.class);

    private final RepairRepository repairRepository;

    public RepairService(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    public RepairOrder createOrder(RepairOrder order) {
        if (order == null || order.getCar() == null) {
            log.error("Ошибка создания заказа: не указан заказ или автомобиль");
            throw new IllegalArgumentException("Заказ и автомобиль должны быть указаны");
        }
        log.info("Начато создание заказа {} для автомобиля {}", order.getId(), order.getCar());

        if (repairRepository.findById(order.getId()) != null) {
            log.warn("Заказ {} уже существует, повторное создание отклонено", order.getId());
            throw new IllegalStateException("Заказ уже существует: " + order.getId());
        }

        repairRepository.save(order);
        log.info("Заказ {} успешно создан", order.getId());
        return order;
    }

    public void acceptCar(String orderId) {
        RepairOrder order = requireOrder(orderId);
        if (order.isCompleted()) {
            throw new IllegalStateException("Нельзя принять автомобиль: заказ уже завершён");
        }
        if (order.getRepairStatus() != RepairStatus.NEW) {
            throw new IllegalStateException("Нельзя принять автомобиль: заказ уже в работе");
        }
        order.setRepairStatus(RepairStatus.IN_PROGRESS);
        repairRepository.save(order);
    }

    public void changeRepairStatus(String orderId, RepairStatus newStatus) {
        RepairOrder order = requireOrder(orderId);
        if (order.isCompleted()) {
            throw new IllegalStateException("Нельзя менять статус: заказ уже завершён");
        }
        order.setRepairStatus(newStatus);
        repairRepository.save(order);
    }


    public void completeOrder(String orderId) {
        RepairOrder order = requireOrder(orderId);
        if (order.isCompleted()) {
            throw new IllegalStateException("Заказ уже завершён");
        }
        if (order.getRepairStatus() != RepairStatus.DONE) {
            throw new IllegalStateException("Нельзя завершить заказ: ремонт ещё не выполнен");
        }
        order.markCompleted();
        repairRepository.save(order);
    }

    public void issueCar(String orderId) {
        RepairOrder order = requireOrder(orderId);
        if (!order.isCompleted()) {
            throw new IllegalStateException("Нельзя выдать автомобиль: заказ ещё не завершён");
        }
        order.markIssued();
        repairRepository.save(order);
    }

    public RepairOrder getOrder(String orderId) {
        return requireOrder(orderId);
    }

    private RepairOrder requireOrder(String orderId) {
        RepairOrder order = repairRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Заказ не найден: " + orderId);
        }
        return order;
    }
}
