package com.gvn.springtutor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applications_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "note")
    private String note;

}
