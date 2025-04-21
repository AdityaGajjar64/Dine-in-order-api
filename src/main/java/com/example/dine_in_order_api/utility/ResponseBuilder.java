package com.example.dine_in_order_api.utility;

import com.example.dine_in_order_api.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.List;

public class ResponseBuilder {

    /**
     *
     * helps to generate ResponseEntity to sends response
     *
     * @param data is generic object to send in response
     * @param httpStatus is status code of http response
     * @param message message for response
     * @return ResponseEntity to sends response
     */

    public static <T> ResponseEntity<ResponseStructure<T>> success(T data, HttpStatus httpStatus, String message) {
        ResponseStructure<T> res = ResponseStructure.<T>builder()
                .message(message)
                .httpStatus(httpStatus.value())
                .data(data)
                .build();
        return ResponseEntity.status(httpStatus).body(res);
    }

    public static <T> ResponseEntity<ListResponseStructure<T>> success(List<T> data, HttpStatus httpStatus, String message) {
        ListResponseStructure<T> res = ListResponseStructure.<T>builder()
                .message(message)
                .httpStatus(httpStatus.value())
                .httpStatus(httpStatus.value())
                .data(data)
                .build();
        return ResponseEntity.status(httpStatus).body(res);
    }

    /**
     *
     *  helps to generate ResponseEntity to sends response with header details
     *
     * @param data is generic object to send in response
     * @param httpStatus is status code of http response
     * @param message message for response
     * @param headers set headers in response entity
     * @return
     */

    public static <T> ResponseEntity<ResponseStructure<T>> success( HttpStatus httpStatus,HttpHeaders headers, String message,T data) {
        ResponseStructure<T> res =  ResponseStructure.<T>builder()
                .message(message)
                .httpStatus(httpStatus.value())
                .data(data)
                .build();
        return ResponseEntity.status(httpStatus)
                .headers(headers)
                .body(res);

    }

    public static  ResponseEntity<SimpleResponseStructure> success(HttpStatus httpStatus,HttpHeaders httpHeaders,String message) {
        SimpleResponseStructure res =  SimpleResponseStructure.builder()
                .message(message)
                .httpStatus(httpStatus.value())
                .build();

        return ResponseEntity.status(httpStatus)
                .headers(httpHeaders)
                .body(res);
    }

    public static  ResponseEntity<SimpleResponseStructure> success(HttpStatus httpStatus,String message) {
        SimpleResponseStructure res =  SimpleResponseStructure.builder()
                .message(message)
                .httpStatus(httpStatus.value())
                .build();

        return ResponseEntity.status(httpStatus)
                .body(res);
    }

    /**
     * helps creating the ok method for success response with
     * data including the httpstatus code , message and data itself
     *
     * @param data generalize data object to send response with responseEntity
     * @param message success message to send response with responseEntity
     * @return it return the responseEntity of responseStructure of generic object
     *         for proper response with ok httpStatusCode
     */

    public static <T> ResponseEntity<ResponseStructure<T>> ok(T data,String message){
        return success(data,HttpStatus.OK,message);
    }
    public static <T> ResponseEntity<ListResponseStructure<T>> ok(List<T> data,String message){
        return success(data,HttpStatus.OK,message);
    }
    public static <T> ResponseEntity<ResponseStructure<T>> ok(HttpHeaders httpHeaders,T data,String message){
        return success(HttpStatus.OK,httpHeaders,message,data);
    }


    /**
     * helps creating the created method for success response with
     * data including the httpstatus code , message and data itself
     *
     * @param data generalize data object to send response with responseEntity
     * @param message success message to send response with responseEntity
     * @return it return the responseEntity of responseStructure of generic object
     *         for proper response with created httpStatusCode
     */

    public static <T> ResponseEntity<ResponseStructure<T>> created(T data,String message){
        return success(data,HttpStatus.CREATED,message);
    }

    /**
     *
     * user to structure the responseEntity for response with simple error
     *
     * @param message error message for response
     * @param httpStatus is status code of http response
     * @return ResponseEntity of errorStructure
     */

    public static ResponseEntity<SimpleErrorStructure> simpleError(String message, HttpStatus httpStatus) {
        SimpleErrorStructure simpleErrorStructure = SimpleErrorStructure.builder()
                .code(httpStatus.value())
                .message(message)
                .type(httpStatus.name())
                .build();
        return ResponseEntity
                .status(httpStatus)
                .body(simpleErrorStructure);
    }


    /**
     * helps creating the create method for error response with
     * data including the httpstatus code , message and data itself
     *
     * @param message success message to send response with responseEntity
     * @return it return the responseEntity of responseStructure of generic
     *         object for proper response with created httpStatusCode
     */
    public static <T> ResponseEntity<SimpleErrorStructure> notFound(String message){
        return simpleError(message,HttpStatus.NOT_FOUND);
    }

    public static ValidationErrorsStructure ValidationError(HttpStatus httpStatus , String message , List<ValidationErrorsStructure.Errors> errors){
          return ValidationErrorsStructure.builder()
                .code(httpStatus.value())
                .message(message)
                .type(httpStatus.name())
                .errors(errors)
                .build();
    }



}
