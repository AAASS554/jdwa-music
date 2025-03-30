package com.example.jdwa.service;

import com.example.jdwa.model.domain.Order;
public interface OrderManager {
    void sendPassword(Order order,String reciveAddress);
    void sendCode(String code,String reciveAddress);
}