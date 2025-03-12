package com.example.dine_in_order_api.service;

import com.example.dine_in_order_api.dto.request.FoodItemRequest;
import com.example.dine_in_order_api.dto.responce.FoodItemResponse;
import com.example.dine_in_order_api.model.Category;

import java.util.List;


public interface FoodItemService {
    public FoodItemResponse createFoodItem(long id , FoodItemRequest foodItemRequest);
    public List<FoodItemResponse> findByTwoCategories(List<String> categories);
    public List<FoodItemResponse> findByRestaurantId(long restaurantId);
}
