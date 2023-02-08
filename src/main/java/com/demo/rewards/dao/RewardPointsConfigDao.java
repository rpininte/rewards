package com.demo.rewards.dao;

import com.demo.rewards.entity.RewardPointsConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardPointsConfigDao extends JpaRepository<RewardPointsConfig, Integer> {
}
