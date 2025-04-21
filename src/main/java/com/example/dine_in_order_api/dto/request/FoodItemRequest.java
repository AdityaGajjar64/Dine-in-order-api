package com.example.dine_in_order_api.dto.request;

import com.example.dine_in_order_api.dto.constraints.MinValue;
import com.example.dine_in_order_api.enums.DietType;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodItemRequest {
    @MinValue
    private String name;

    private double price;

    @MinValue
    private String description;

    private int stock;

//  @Pattern(regexp = "^(VEG|NON_VEG|VEGAN)$", message = "Diet type must be either 'VEG' or 'NON_VEG' or 'VEGAN'")
    @NotNull(message = "diet type can not be null")
    private DietType dietType;

    @NotBlank(message = "cuisine type can't be blank ")
    @NotEmpty(message = "cuisine type can't be empty ")
    private String cuisineType;

    @NotEmpty(message = "cuisine type can't be empty ")
    @Valid
    private List<@NotBlank(message = "Cuisine type cannot be blank !!") String> categories;


}
