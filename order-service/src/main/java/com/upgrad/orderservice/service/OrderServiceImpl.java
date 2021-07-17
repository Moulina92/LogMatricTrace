package com.upgrad.orderservice.service;

import com.upgrad.commons.model.Message;
import com.upgrad.commons.model.Status;
import com.upgrad.commons.model.User;
import com.upgrad.commons.model.request.NotificationRequest;
import com.upgrad.commons.model.request.PaymentRequest;
import com.upgrad.commons.model.response.ItemResponse;
import com.upgrad.commons.model.response.PaymentResponse;
import com.upgrad.orderservice.model.OrderException;
import com.upgrad.orderservice.rest.InventoryServiceClient;
import com.upgrad.orderservice.rest.NotificationServiceClient;
import com.upgrad.orderservice.rest.PaymentServiceClient;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public final class OrderServiceImpl implements OrderService {

  private final InventoryServiceClient inventoryServiceClient;


  private final PaymentServiceClient paymentServiceClient;

  private final NotificationServiceClient notificationServiceClient;

  private final MeterRegistry meterRegistry;

  @Override
  public String placeOrder(final User user, final int itemId) throws OrderException {

    meterRegistry.counter("placeOrder").increment();
    log.info("In PlaceOrder Method with itemId={}",itemId);
    final double price = getItemPrice(itemId);
    log.info("Price coming as p :{}",price);
    final Status status = makePayment(user.getId(), price);
    if (Status.SUCCESSFUL.equals(status)) {
      sendNotification(user.getEmail(), Message.SUCCESS);
      return UUID.randomUUID().toString();
    }
    sendNotification(user.getEmail(), Message.FAILURE);
    throw new OrderException("Exception in placing order");
  }

  private double getItemPrice(final int itemId) throws OrderException {
    final ItemResponse itemResponse = inventoryServiceClient.getItem(itemId);
    if (itemResponse.isAvailable()) {
      return itemResponse.getItemPrice();
    }
    throw new OrderException(itemResponse.getError().getMessage());
  }

  private Status makePayment(final String userId, final double price) {
    final PaymentRequest paymentRequest = PaymentRequest.builder()
        .userId(userId)
        .amount(price)
        .build();
    final PaymentResponse paymentResponse = paymentServiceClient.makePayment(paymentRequest);
    return paymentResponse.getPaymentStatus();
  }

  private void sendNotification(final String recipient, final Message message) {
    final NotificationRequest notificationRequest = NotificationRequest.builder()
        .recipient(recipient)
        .subject(message.getSubject())
        .body(message.getBody())
        .build();
    notificationServiceClient.sendNotification(notificationRequest);
  }
}
