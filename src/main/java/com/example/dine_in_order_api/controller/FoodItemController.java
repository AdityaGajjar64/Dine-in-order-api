package com.example.dine_in_order_api.controller;

import com.example.dine_in_order_api.dto.request.FoodItemRequest;
import com.example.dine_in_order_api.dto.responce.FoodItemResponse;
import com.example.dine_in_order_api.model.Category;
import com.example.dine_in_order_api.service.FoodItemService;
import com.example.dine_in_order_api.utility.ListResponseStructure;
import com.example.dine_in_order_api.utility.ResponseBuilder;
import com.example.dine_in_order_api.utility.ResponseStructure;
import com.example.dine_in_order_api.utility.SimpleResponseStructure;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.base-url}")
@AllArgsConstructor
public class FoodItemController {

    private final FoodItemService foodItemService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/fooditems/restaurants/{id}")
    public ResponseEntity<ResponseStructure<FoodItemResponse>> createFoodItem(@PathVariable long id, @RequestBody FoodItemRequest foodItemRequest){
        FoodItemResponse foodItemResponse = foodItemService.createFoodItem(id,foodItemRequest);
        return ResponseBuilder.created(foodItemResponse,"FoodItem Added successfully");
    }

    @GetMapping("/fooditems/categories")
    public ResponseEntity<ListResponseStructure<FoodItemResponse>> findByCategories(@RequestParam List<String> categories){
            return  ResponseBuilder.ok(foodItemService.findByTwoCategories(categories),"Food item List found according categories");
    }

    @GetMapping("/restaurants/{restaurantId}/fooditems")
    public ResponseEntity<ListResponseStructure<FoodItemResponse>> findByRestaurantId(@PathVariable long restaurantId){
        return  ResponseBuilder.ok(foodItemService.findByRestaurantId(restaurantId),"Food item List found according restaurant");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/fooditems/{fooditemId}")
    public ResponseEntity<ResponseStructure<FoodItemResponse>> updateById(@PathVariable long fooditemId , @RequestBody FoodItemRequest foodItemRequest){
        return ResponseBuilder.ok(foodItemService.updateById(fooditemId,foodItemRequest),"food item updated !!");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/fooditem/{fooditemId}/stock")
    public ResponseEntity<ResponseStructure<FoodItemResponse>> updateStockById(@PathVariable long fooditemId , @RequestParam int stock){
        return ResponseBuilder.ok(foodItemService.updateStockById(fooditemId,stock),"food item stock updated !!");
    }

    @DeleteMapping("/fooditems")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SimpleResponseStructure> updateStockById(@RequestParam long foodItemId){
        foodItemService.deleteById(foodItemId);
        return ResponseEntity.ok(
                 SimpleResponseStructure.builder()
                .httpStatus(201)
                .message("food item deleted !")
                .build());
    }
}
