package com.upgrad.orderservice.controller;

import com.upgrad.commons.model.Status;
import com.upgrad.commons.model.request.OrderRequest;
import com.upgrad.commons.model.response.OrderResponse;
import com.upgrad.orderservice.model.OrderException;
import com.upgrad.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public final class OrderController {


  private final OrderService orderService;

  @PostMapping("/place-order")
  public OrderResponse placeOrder(@RequestBody final OrderRequest orderRequest) {

    try {
      log.info("Oder Service processing starts with itemId::{} and UserId::{}",orderRequest.getItemId(),
              orderRequest.getUser().getId());
      return OrderResponse.builder()
          .orderId(orderService.placeOrder(orderRequest.getUser(), orderRequest.getItemId()))
          .itemId(orderRequest.getItemId())
          .orderStatus(Status.SUCCESSFUL)
          .build();
    } catch (final OrderException e) {
      log.error("Exception Occur  in Oder Service with itemId::{} and UserId::{} and exception:{}",
              orderRequest.getItemId(), orderRequest.getUser().getId(),e);
      return OrderResponse.builder()
          .itemId(orderRequest.getItemId())
          .orderStatus(Status.FAILED)
          .build();
    }
  }
}
