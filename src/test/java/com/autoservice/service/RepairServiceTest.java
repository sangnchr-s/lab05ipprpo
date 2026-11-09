package com.autoservice.service;

import com.autoservice.model.Car;
import com.autoservice.model.Customer;
import com.autoservice.model.RepairOrder;
import com.autoservice.model.RepairStatus;
import com.autoservice.repository.InMemoryRepairRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepairServiceTest {

    private RepairService newService() {
        return new RepairService(new InMemoryRepairRepository());
    }

    private RepairOrder newOrder() {
        Car car = new Car("c1", "Toyota", "A123BC");
        Customer customer = new Customer("u1", "Иванов", "89001112233");
        return new RepairOrder("o1", car, customer, "Замена масла");
    }

    @Test
    void completeOrderWhenRepairIsDone() {
        RepairService service = newService();
        service.createOrder(newOrder());
        service.changeRepairStatus("o1", RepairStatus.DONE);

        service.completeOrder("o1");

        assertTrue(service.getOrder("o1").isCompleted(), "Заказ должен быть завершён");
    }

    @Test
    void completeAlreadyCompletedOrderThrows() {
        RepairService service = newService();
        service.createOrder(newOrder());
        service.changeRepairStatus("o1", RepairStatus.DONE);
        service.completeOrder("o1");

        assertThrows(IllegalStateException.class, () -> service.completeOrder("o1"));
    }

    @Test
    void completeOrderWhenRepairIsNotDoneThrows() {
        RepairService service = newService();
        service.createOrder(newOrder());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.completeOrder("o1")
        );

        assertTrue(exception.getMessage().contains("ремонт ещё не выполнен"));
        assertFalse(service.getOrder("o1").isCompleted(), "Заказ не должен быть завершён");
    }

    @Test
    void acceptCarMovesNewOrderToInProgress() {
        RepairService service = newService();
        service.createOrder(newOrder());

        service.acceptCar("o1");

        assertEquals(RepairStatus.IN_PROGRESS, service.getOrder("o1").getRepairStatus());
        assertFalse(service.getOrder("o1").isCompleted(), "Приём автомобиля не закрывает заказ");
    }

    @Test
    void acceptCarWhenAlreadyInProgressThrows() {
        RepairService service = newService();
        service.createOrder(newOrder());
        service.acceptCar("o1");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.acceptCar("o1")
        );

        assertTrue(exception.getMessage().contains("уже в работе"));
        assertEquals(RepairStatus.IN_PROGRESS, service.getOrder("o1").getRepairStatus());
    }

    @Test
    void issueCarWhenOrderIsCompleted() {
        RepairService service = newService();
        service.createOrder(newOrder());
        service.changeRepairStatus("o1", RepairStatus.DONE);
        service.completeOrder("o1");

        service.issueCar("o1");

        assertTrue(service.getOrder("o1").isIssued(), "Автомобиль должен быть выдан клиенту");
    }

    @Test
    void createDuplicateOrderThrows() {
        RepairService service = newService();
        service.createOrder(newOrder());

        assertThrows(IllegalStateException.class, () -> service.createOrder(newOrder()));
    }

    @Test
    void createNullOrderThrows() {
        RepairService service = newService();

        assertThrows(IllegalArgumentException.class, () -> service.createOrder(null));
    }
}
