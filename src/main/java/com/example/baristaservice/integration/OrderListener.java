package com.example.baristaservice.integration;

import com.example.baristaservice.model.CoffeeOrder;
import com.example.baristaservice.model.OrderState;
import com.example.baristaservice.repository.CoffeeOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@Transactional
public class OrderListener {

  @Autowired
  private CoffeeOrderRepository orderRepository;
  @Autowired
  private Waiter waiter;
  @Value("${order.barista-prefix}${random.uuid}")
  private String barista;

  @StreamListener(Waiter.NEW_ORDERS)
  @SendTo(Waiter.FINISHED_ORDERS)
  public Long processNewOrder(Long id) {
    CoffeeOrder order = orderRepository.getOne(id);
    if (order == null) {
      log.warn("Order id {} is NOT valid.", id);
      throw new IllegalArgumentException("Order id is NOT valid.");
    }

    log.info("Receive new order {}. Waiter: {}. Customer: {}",
        id, order.getWaiter(), order.getCustomer());
    order.setState(OrderState.BREWED);
    order.setBarista(barista);
    orderRepository.save(order);
    log.info("Order {} is READY.", order);
    return order.getId();
  }

}