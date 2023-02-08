package com.demo.rewards.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reward_point_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardPointsConfig implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double baseAmount;
    private Integer point;
}
