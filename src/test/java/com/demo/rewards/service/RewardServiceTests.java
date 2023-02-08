package com.demo.rewards.service;

import com.demo.rewards.dao.CustomerDao;
import com.demo.rewards.dao.OrderDao;
import com.demo.rewards.dao.RewardPointsConfigDao;
import com.demo.rewards.dto.RewardDto;
import com.demo.rewards.entity.Customer;
import com.demo.rewards.entity.Order;
import com.demo.rewards.entity.Product;
import com.demo.rewards.entity.RewardPointsConfig;
import com.demo.rewards.exception.RewardsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RewardServiceTests {

    @InjectMocks
    RewardService rewardService;
    @Mock
    CustomerDao customerDao;
    @Mock
    OrderDao orderDao;
    @Mock
    RewardPointsConfigDao rewardPointsConfigDao;
    Integer validCustomerID = 1;
    Integer invalidCustomerID = 10;

    Optional<Customer> optionalCustomer;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(rewardService, "month", 3);
        Customer customer = new Customer();
        customer.setFirstName("James");
        customer.setLastName("Bond");
        customer.setEmailId("Jamesbond@gmail.com");
        customer.setId(validCustomerID);
        optionalCustomer = Optional.of(customer);

    }


    @Test
    public void givenCustomerID_whenFound_thenReturnRewardPoints() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(getOrder(1, "ORD001", 1, 14.4, 2, "2022-08-02"));
        orderList.add(getOrder(2, "ORD002", 2, 11.4, 3, "2022-08-07"));
        orderList.add(getOrder(3, "ORD003", 3, 137.0, 10, "2022-09-02"));
        orderList.add(getOrder(4, "ORD004", 2, 19.0, 5, "2022-09-02"));
        orderList.add(getOrder(5, "ORD005", 2, 64.6, 17, "2022-10-02"));
        orderList.add(getOrder(6, "ORD006", 3, 287.7, 21, "2022-10-12"));
        orderList.add(getOrder(7, "ORD007", 2, 64.6, 17, "2022-11-02"));

        List<RewardPointsConfig> rewardPointsConfigList = new ArrayList<>();
        rewardPointsConfigList.add(new RewardPointsConfig(1, 100.0, 2));
        rewardPointsConfigList.add(new RewardPointsConfig(2, 50.0, 1));
        Customer customer = new Customer();
        customer.setId(validCustomerID);
        lenient().when(customerDao.findById(validCustomerID)).thenReturn(optionalCustomer);
        when(orderDao.findAllByCustomerIdAndCreatedOnGreaterThanEqual(any(), any())).thenReturn(orderList);
        when(rewardPointsConfigDao.findAll()).thenReturn(rewardPointsConfigList);
        RewardDto rewardDto = rewardService.findRewardPoints(validCustomerID);
        assertNotNull(rewardDto);
        assertEquals(rewardDto.getRewardPoints(), "731.20");
    }

    @Test
    public void givenCustomerID_whenNotFound_thenReturnErrorMessage() {
        RewardsException thrown = assertThrows(RewardsException.class,
                () -> rewardService.findRewardPoints(invalidCustomerID), "Please provide a valid customer ID!");
        assertTrue(thrown.getErrorMessage().contains("Please provide a valid customer ID!"));
    }

    private Date getDate(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private Order getOrder(int id, String orderId, int productID, double amount, int quantity, String dateStr) {
        Order order = new Order(id, orderId, optionalCustomer.get(), new Product(productID), amount, quantity);
        order.setCreatedOn(getDate(dateStr));
        order.setCreatedBy(1);
        order.setLastModifiedOn(getDate(dateStr));
        order.setLastModifiedBy(1);
        order.setIsActive(true);

        return order;
    }
}