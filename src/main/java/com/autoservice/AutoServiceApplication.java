package com.autoservice;

import com.autoservice.model.Car;
import com.autoservice.model.Customer;
import com.autoservice.model.RepairOrder;
import com.autoservice.model.RepairStatus;
import com.autoservice.repository.InMemoryRepairRepository;
import com.autoservice.repository.RepairRepository;
import com.autoservice.service.RepairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AutoServiceApplication.class);

    public static void main(String[] args) {
        RepairRepository repository = new InMemoryRepairRepository();
        RepairService service = new RepairService(repository);

        Car car = new Car("c1", "Toyota", "A123BC");
        Customer customer = new Customer("u1", "Иванов", "89001112233");
        RepairOrder order = new RepairOrder("o1", car, customer, "Замена масла и фильтра");
        service.createOrder(order);

        log.info("Создан: {}", service.getOrder("o1"));

        try {
            service.completeOrder("o1");
        } catch (IllegalStateException e) {
            log.warn("Завершение запрещено: {}", e.getMessage());
        }

        service.acceptCar("o1");
        log.info("Автомобиль принят: {}", service.getOrder("o1"));

        service.changeRepairStatus("o1", RepairStatus.DONE);
        log.info("Ремонт выполнен: {}", service.getOrder("o1"));

        service.completeOrder("o1");
        log.info("Заказ закрыт: {}", service.getOrder("o1"));

        service.issueCar("o1");
        log.info("Автомобиль выдан: {}", service.getOrder("o1"));

        // Проблемные сценарии: повторный заказ (WARN) и пустой заказ (ERROR)
        try {
            service.createOrder(order);
        } catch (IllegalStateException e) {
            log.info("Повторное создание запрещено: {}", e.getMessage());
        }
        try {
            service.createOrder(null);
        } catch (IllegalArgumentException e) {
            log.info("Некорректный заказ: {}", e.getMessage());
        }
    }
}
