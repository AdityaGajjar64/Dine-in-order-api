package com.example.dine_in_order_api.mapper;

import com.example.dine_in_order_api.dto.responce.BillResponse;
import com.example.dine_in_order_api.model.Bill;
import com.example.dine_in_order_api.model.Category;
import com.example.dine_in_order_api.model.CuisineType;
import com.example.dine_in_order_api.model.Image;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface BillMapper {
    public BillResponse mapToBillResponse(Bill bill);
    List<String> mapToListString(List<Category> value);
    List<String> map(List<Image> value);
    default String map(Image value){
        if(value != null){
            return value.getImageURL();
        }
        else return null;
    }
    default String mapToString(Category value) {
        if(value == null) {
            return null;
        }
        else return value.getCategory().toLowerCase();
    }
    default String mapToString(CuisineType value) {
        if(value == null) {
            return null;
        }
        else return value.getCuisine().toLowerCase();
    }
}
