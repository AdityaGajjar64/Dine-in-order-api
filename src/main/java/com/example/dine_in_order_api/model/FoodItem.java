package com.example.dine_in_order_api.model;

import com.example.dine_in_order_api.enums.DietType;
import com.example.dine_in_order_api.enums.StockStatus;
import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "FoodItem",indexes = {@Index(name = "idx_name", columnList = "name")})
@EntityListeners(AuditingEntityListener.class)
public class FoodItem {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(name ="name")
    private String name;

    @Column(name ="price")
    private double price;

    @Column(name = "description")
    private String Description;

    @Column(name ="stock")
    private int stock;

    @Column(name = "availability")
    private StockStatus availability;

    @Column(name = "diet_type")
    @Enumerated
    private DietType dietType;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDate createdat;

    @LastModifiedDate
    @Column(name = "last_modified_at")
    private LocalDateTime lastmodifiedat;

    @ManyToOne
    private CuisineType cuisineType;

    @ManyToOne
    private Restaurent restaurent;

    @ManyToMany
    private List<Category> categories;

}
