package com.demo.rewards.service;

import com.demo.rewards.dao.CustomerDao;
import com.demo.rewards.dao.OrderDao;
import com.demo.rewards.dao.RewardPointsConfigDao;
import com.demo.rewards.dto.RewardDto;
import com.demo.rewards.entity.Customer;
import com.demo.rewards.entity.Order;
import com.demo.rewards.entity.RewardPointsConfig;
import com.demo.rewards.exception.RewardsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.demo.rewards.constants.RewardConstants.*;
import static com.demo.rewards.constants.RewardErrorConstants.INVALID_CUSTOMER_ID;
import static com.demo.rewards.constants.RewardErrorConstants.NO_TRANSACTION_AVAILABLE;
import static com.demo.rewards.constants.RewardErrorConstants.NUMBER_CUSTOMER_ID;

@Service
@Slf4j
public class RewardService {
	@Autowired
	CustomerDao customerDao;
	@Autowired
	OrderDao orderDao;
	@Autowired
	RewardPointsConfigDao rewardPointsConfigDao;
	@Value("${month}")
	Integer month;
	private static final DateFormat df = new SimpleDateFormat(YYYY_MM);
	private static final DecimalFormat df1 = new DecimalFormat(DECIMAL_FRACTION);

	public RewardDto findRewardPoints(Integer customerId) throws RewardsException {
		log.info("start findRewardPoints - {}", customerId);
		String rewardPoints;
		if(customerId < 0){
			throw new RewardsException(NUMBER_CUSTOMER_ID);
		}
		Optional<Customer> customerOptional = customerDao.findById(customerId);
		if (customerOptional.isEmpty())
			throw new RewardsException(INVALID_CUSTOMER_ID);

		Customer customer = new Customer();
		customer.setId(customerId);
		Date fromDate = findBackDate();
		List<Order> orderList = orderDao.findAllByCustomerIdAndCreatedOnGreaterThanEqual(customer, fromDate);

		if (orderList.isEmpty())
			throw new RewardsException(NO_TRANSACTION_AVAILABLE);

		Map<String, List<Order>> dateAmountMap = orderList.stream().collect(
				Collectors.groupingBy(data -> df.format(data.getCreatedOn()), Collectors.toCollection(ArrayList::new)));

		rewardPoints = df1.format(calculateRewards(dateAmountMap));
		log.info("end findRewardPoints - reward points - {} for customer id - {}", rewardPoints, customerId);
		return RewardDto.builder().rewardPoints(rewardPoints).build();
	}

	private Date findBackDate() {
		LocalDateTime localDateTime = LocalDateTime.now();
		localDateTime = localDateTime.minusMonths(month);
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Double calculateRewards(Map<String, List<Order>> dateAmountMap) throws RewardsException {
		double rewardPoints = 0.0;
		int pointsFor100;
		int pointsFor50;
		List<RewardPointsConfig> rewardPointsConfigs = rewardPointsConfigDao.findAll();
		if (rewardPointsConfigs.isEmpty()) {
			log.error("calculateRewards - could not find rewardPointsConfig");
			throw new RewardsException("Could not find rewards points settings!");
		}
		Optional<Integer> optionalPointsFor100 = rewardPointsConfigs.stream()
				.filter(data -> data.getBaseAmount() == AMOUNT_100).map(RewardPointsConfig::getPoint).findAny();
		Optional<Integer> optionalPointsFor50 = rewardPointsConfigs.stream()
				.filter(data -> data.getBaseAmount() == AMOUNT_50).map(RewardPointsConfig::getPoint).findAny();

		if (optionalPointsFor100.isEmpty()) {
			log.error("calculateRewards - could not find rewardPoints for $100");
			throw new RewardsException("Could not find rewards points settings!");
		}
		pointsFor100 = optionalPointsFor100.get();

		if (optionalPointsFor50.isEmpty()) {
			log.error("calculateRewards - could not find rewardPoints for $50");
			throw new RewardsException("Could not find rewards points settings!");
		}
		pointsFor50 = optionalPointsFor50.get();
		for (Map.Entry<String, List<Order>> entry : dateAmountMap.entrySet()) {
			List<Order> orders = entry.getValue();
			Optional<Double> optionalTotalAmount = orders.stream().map(Order::getAmount).reduce(Double::sum);
			if (optionalTotalAmount.isPresent()) {
				double amount100 = optionalTotalAmount.get() - AMOUNT_100;
				amount100 = Math.max(0, amount100);
				rewardPoints += amount100 * pointsFor100; 

				double amount50 = optionalTotalAmount.get() - amount100 - AMOUNT_50;
				amount50 = Math.max(0, amount50);
				rewardPoints += amount50 * pointsFor50;
			}
		}

		return rewardPoints;
	}

}
