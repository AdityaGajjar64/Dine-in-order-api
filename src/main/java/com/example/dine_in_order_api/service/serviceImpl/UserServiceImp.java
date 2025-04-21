package com.example.dine_in_order_api.service.serviceImpl;

import com.example.dine_in_order_api.dto.request.ResetPasswordRequest;
import com.example.dine_in_order_api.dto.request.UserRegistrationRequest;
import com.example.dine_in_order_api.dto.request.UserRequest;
import com.example.dine_in_order_api.dto.responce.UserResponce;
import com.example.dine_in_order_api.enums.UserRole;
import com.example.dine_in_order_api.exception.IllegalRequestException;
import com.example.dine_in_order_api.exception.InvalidPasswordException;
import com.example.dine_in_order_api.exception.RestaurantNotFoundException;
import com.example.dine_in_order_api.mapper.UserMapper;
import com.example.dine_in_order_api.model.*;
import com.example.dine_in_order_api.repository.RestaurentRepository;
import com.example.dine_in_order_api.repository.TableRepository;
import com.example.dine_in_order_api.repository.UserRepository;
import com.example.dine_in_order_api.security.util.UserIentity;
import com.example.dine_in_order_api.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TableRepository tableRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIentity userIentity;
    private final RestaurentRepository restaurentRepository;

    @Override
    public UserResponce registration(UserRegistrationRequest registrationRequest) {
        Admin user = new Admin();

        userMapper.mapToUserEntity(registrationRequest, user);

        encryptPassword(user);

        user.setUserrole(UserRole.ADMIN);

        return userMapper.mapToUserResponce(userRepository.save(user));
    }

    @Override
    public UserResponce findById() {
        User user = userIentity.getCurrentUser();
        userRepository.findById(Long.valueOf(user.getUserid()));
        return userMapper.mapToUserResponce(user);
    }

    @Override
    public UserResponce updateById(UserRequest userRes) {
         User user = userIentity.getCurrentUser();
         userMapper.mapToUser(userRes,user);
         return userMapper.mapToUserResponce(userRepository.save(user));
    }

    @Override
    public UserResponce staffRegistration(UserRegistrationRequest registrationRequest, long id) {

        Restaurent restaurant = restaurentRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "No restaurant found,Enter valid restaurant id"));

        if(restaurant.getCreatedBy().equals(userIentity.getCurrentUserEmail())){
            Staff user = new Staff();
            userMapper.mapToUserEntity(registrationRequest, user);
            encryptPassword(user);
            user.setUserrole(UserRole.STAFF);
            user.setRestaurent(restaurant);
            user.setRestaurantTables(new ArrayList<>(restaurant.getRestaurantTables()));
            return userMapper.mapToUserResponce(userRepository.save(user));
           }
        else{
            throw new IllegalRequestException(
                    "Access denied: You are not authorized add staff at "+restaurant.getName());
        }
    }


    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userIentity.getCurrentUser();
        if(passwordEncoder.matches(resetPasswordRequest.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            userRepository.save(user);
        }
        else {
            throw new InvalidPasswordException("Enter valid old password !!");
        }
    }

    private void encryptPassword(User user){
        String encodedPassword =  passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

}
