package com.example.dine_in_order_api.controller;


import com.example.dine_in_order_api.dto.request.ResetPasswordRequest;
import com.example.dine_in_order_api.dto.request.UserRegistrationRequest;
import com.example.dine_in_order_api.dto.request.UserRequest;
import com.example.dine_in_order_api.dto.responce.UserResponce;
import com.example.dine_in_order_api.service.UserService;
import com.example.dine_in_order_api.utility.ResponseBuilder;
import com.example.dine_in_order_api.utility.ResponseStructure;
import com.example.dine_in_order_api.utility.SimpleResponseStructure;
import com.example.dine_in_order_api.utility.ValidationErrorsStructure;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("${app.base-url}")
public class UserController {

    private UserService userService;


//    @Operation(description = """
//            The API Endpoint is used to register user.
//            The endpoints require the user to select one of the specified role along with the other details
//            """,
//            responses = {
//                    @ApiResponse(responseCode = "201",description = "User Created"),
//                    @ApiResponse(responseCode = "401", description = "Invalid Input", content = {
//                            @Content(schema = @Schema(implementation = ValidationErrorsStructure.class))
//                    })
//            }
//    )
    @PostMapping("/admin-register")
    public ResponseEntity<ResponseStructure<UserResponce>> registration(@Valid @RequestBody UserRegistrationRequest registrationRequest){
        UserResponce registration = userService.registration(registrationRequest);
        return ResponseBuilder.created(registration,"Data Stored !!");
    }

    @PostMapping("/staff-register/restaurants/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseStructure<UserResponce>> staffRegistration(@Valid @RequestBody UserRegistrationRequest registrationRequest,@PathVariable long id){
        UserResponce registration = userService.staffRegistration(registrationRequest,id);
        return ResponseBuilder.created(registration, "Data Stored !!");
    }

    @GetMapping("/account")
    public ResponseEntity<ResponseStructure<UserResponce>> findById() {
        UserResponce user = userService.findById();
        return ResponseBuilder.ok(user,"Data Fetched !!");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<SimpleResponseStructure> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        System.out.println("here");
        userService.resetPassword(resetPasswordRequest);
        return ResponseBuilder.success(HttpStatus.OK,"Password Updated");
    }

    //update by id
    @PutMapping("/User")
    @Operation(description = """
            The API Endpoint is used to update user details.
            The endpoints requires the user to select one of the specified role along with the other details.            
            """,
            responses = {
                    @ApiResponse(responseCode = "200" ,description = "User Updated"),
                    @ApiResponse(responseCode = "401" , description = "Invaild Input" , content = {
                            @Content(schema = @Schema(implementation = ValidationErrorsStructure.class))
                    })
            }
    )
    public ResponseEntity<ResponseStructure<UserResponce>> updateById(@RequestBody @Valid UserRequest user){
        UserResponce userRes = userService.updateById(user);
        return ResponseBuilder.ok(userRes,"Data Updated");
    }
}
