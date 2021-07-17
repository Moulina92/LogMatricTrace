package com.upgrad.orderservice.rest;

import com.upgrad.commons.model.response.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
@Slf4j
public final class InventoryServiceClient {

  private static final String SERVICE_URL = "http://inventory-service:8070/inventory";

  private final RestTemplate restTemplate;

  public ItemResponse getItem(final int itemId) {
    log.info("Item id in client:{}",itemId);
    final ResponseEntity<ItemResponse> responseEntity = restTemplate
        .getForEntity(SERVICE_URL + "/get-item/" + itemId, ItemResponse.class);
    return responseEntity.getBody();
  }
}
