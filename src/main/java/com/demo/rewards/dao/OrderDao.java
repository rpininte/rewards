package com.demo.rewards.dao;

import com.demo.rewards.entity.Customer;
import com.demo.rewards.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {
    List<Order> findAllByCustomerIdAndCreatedOnGreaterThanEqual(Customer customer, Date createdOn);
    List<Order> findAllByCustomerId(Customer customer);
}
