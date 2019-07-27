package com.example.baristaservice.integration;

import com.example.baristaservice.model.CoffeeOrder;
import com.example.baristaservice.model.OrderState;
import com.example.baristaservice.repository.CoffeeOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
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
  @Autowired
  @Qualifier(Waiter.FINISHED_ORDERS)
  private MessageChannel finishedOrderMessageChannel;

  @Value("${order.barista-prefix}${random.uuid}")
  private String barista;

  @StreamListener(Waiter.NEW_ORDERS)
  public void processNewOrder(Long id) {
    CoffeeOrder order = orderRepository.getOne(id);
    if (order == null) {
      log.warn("Order id {} is NOT valid.", id);
      return;
    }

    log.info("Receive new order {}. Waiter: {}. Customer: {}",
        id, order.getWaiter(), order.getCustomer());
    order.setState(OrderState.BREWED);
    order.setBarista(barista);
    orderRepository.save(order);
    log.info("Order {} is READY.", order);

//    waiter.finishedOrders().send(MessageBuilder.withPayload(order.getId()).build());
    finishedOrderMessageChannel.send(MessageBuilder.withPayload(order.getId()).build());
  }

}