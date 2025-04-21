package com.example.dine_in_order_api.service.serviceImpl;

import com.example.dine_in_order_api.dto.request.FoodItemRequest;
import com.example.dine_in_order_api.dto.responce.FoodItemResponse;
import com.example.dine_in_order_api.enums.StockStatus;
import com.example.dine_in_order_api.exception.*;
import com.example.dine_in_order_api.mapper.FoodItemMapper;
import com.example.dine_in_order_api.model.Category;
import com.example.dine_in_order_api.model.CuisineType;
import com.example.dine_in_order_api.model.FoodItem;
import com.example.dine_in_order_api.model.Restaurent;
import com.example.dine_in_order_api.repository.*;
import com.example.dine_in_order_api.security.util.UserIentity;
import com.example.dine_in_order_api.service.FoodItemService;
import com.example.dine_in_order_api.utility.SimpleResponseStructure;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;


@AllArgsConstructor
@Service
public class FoodItemserviceImpl implements FoodItemService {

    private final RestaurentRepository restaurentRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;
    private final CuisineRepository cuisineRepository;
    private final CategoryRepository categoryRepository;
    private final UserIentity userIentity;

    @Override
    @Transactional
    public FoodItemResponse createFoodItem(long id, FoodItemRequest foodItemRequest) {

        FoodItem foodItem = foodItemMapper.mapToFoodItem(foodItemRequest);


        Restaurent restaurent = restaurentRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found !!"));

        if(!userIentity.getCurrentUserEmail().equals(restaurent.getCreatedBy())){
            throw new IllegalRequestException("invalid authority to add food item for this restaurant ");
        }

        cuisineRepository.findById(foodItem.getCuisineType().getCuisine())
                .orElseGet(() -> {
                    CuisineType cuisineType = cuisineRepository.save(foodItem.getCuisineType());
                    restaurent.getCuisineTypes().add(cuisineType);
                    restaurentRepository.save(restaurent);
                    return cuisineType;
                });

        foodItem.setCategories(this.createNonExistingCategory(foodItem.getCategories()));
        foodItem.setRestaurent(restaurent);
        foodItem.setCuisineType(foodItem.getCuisineType());

        if(foodItem.getStock()>0)
            foodItem.setAvailability(StockStatus.STOCK_IN);
        else foodItem.setAvailability(StockStatus.STOCK_OUT);

        foodItem.setDeleted(false);

        foodItemRepository.save(foodItem);

        return foodItemMapper.mappToFoodItemResponse(foodItem);
    }

    @Override
    public List<FoodItemResponse> findByTwoCategories(List<String> categories) {
        if(categories.isEmpty()){
            throw new FoodNotFoundException("No food with this categories");
        }
        else{
            List<FoodItemResponse> foodItemList = foodItemMapper.mapToListOfFoodItemResponse(
                    foodItemRepository.findByTwoCategories(
                            categories.stream().distinct().toList(),
                            categories.size())
                            .stream().filter(foodItem -> !foodItem.isDeleted()).toList()
            );
            if(foodItemList.isEmpty()){
                throw new FoodNotFoundException("No food with this categories");
            }
            else {
                return foodItemList;
            }
        }
    }

    @Override
    @Cacheable(cacheNames = "food-item-cache",key = "#restaurantId")
    public List<FoodItemResponse> findByRestaurantId(long restaurantId) {

        Restaurent restaurent = restaurentRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found !!"));

        List<FoodItem>  foodItemList = foodItemRepository.findByRestaurent(restaurent).stream()
                        .filter(foodItem -> !foodItem.isDeleted())
                        .toList();

        if(foodItemList.isEmpty()){
            throw new FoodNotFoundException("No food with this Restaurant");
        }
        else {
            return foodItemMapper.mapToListOfFoodItemResponse(foodItemList);
        }
    }

    @Override
    public FoodItemResponse updateById(long fooditemId, FoodItemRequest foodItemRequest) {

        FoodItem foodItem = foodItemRepository.findById(fooditemId)
                .orElseThrow(() -> new FoodNotFoundException("invalid food id !"));

        if(userIentity.getCurrentUserEmail().equals(foodItem.getCreatedBy())){
            FoodItem updatedFoodItem = foodItemMapper.mapToFoodItem(foodItemRequest);
            return foodItemMapper.mappToFoodItemResponse(
                    foodItemRepository.save(updatedFoodItem)
            );
        }
        else{
            throw new IllegalRequestException("invalid authority to update food item");
        }
    }

    @Override
    public FoodItemResponse updateStockById(long fooditemId, int stock) {

        FoodItem foodItem = foodItemRepository.findById(fooditemId)
                .orElseThrow(() -> new FoodNotFoundException("invalid food id !"));

        if(userIentity.getCurrentUserEmail().equals(foodItem.getCreatedBy())){
            foodItem.setStock(stock);
            return foodItemMapper.mappToFoodItemResponse(
                    foodItemRepository.save(foodItem)
            );
        }
        else{
            throw new IllegalRequestException("invalid authority to update food item stock");
        }
    }

    @Override
    public void deleteById(long fooditemId) {

        FoodItem foodItem = foodItemRepository.findById(fooditemId)
                .orElseThrow(() -> new FoodNotFoundException("invalid food id !"));

        if(!foodItem.isDeleted()){
            throw new FoodNotFoundException("food item is already deleted !!");
        }

        if(userIentity.getCurrentUserEmail().equals(foodItem.getCreatedBy())){
            foodItem.setDeleted(true);
            foodItemRepository.save(foodItem);
        }else{
            throw new IllegalRequestException("invalid authority to delete food item");
        }
    }

    private List<Category> createNonExistingCategory(List<Category> categories) {
        return categories.stream()
                .map(type -> categoryRepository.findById(type.getCategory())
                        .orElseGet(() ->categoryRepository.save(type)))
                .toList();
    }
}
