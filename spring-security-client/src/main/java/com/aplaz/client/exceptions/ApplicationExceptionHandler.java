package com.aplaz.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/****************       @ExceptionHandler   **********************************
 *
 * @ExceptionHandler is an annotation used to handle the
 * specific exceptions and sending the custom responses to the client.
 * Define a class that extends the RuntimeException class
 *
 */

/******************     HashMap     ************************
 *
 *  A HashMap however, store items in "key/value" pairs,
 *  and you can access them by an index of another type.
 */

/************************   @RestControllerAdvice   *****************************
     *The @ControllerAdvice annotation was first introduced in Spring 3.2.
     * It allows you to handle exceptions across the whole application,
     * not just to an individual controller.
     * You can think of it as an interceptor of exceptions
     * thrown by methods annotated with @RequestMapping
 */

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException ex){

        //  Print all errors of this Type -> ``` MethodArgumentNotValidException ```

        Map<String,String> errorMap = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error->{
            errorMap.put(error.getField(),error.getDefaultMessage());
        });

        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public String NullPointerException(NullPointerException ex){

        //``` NULL Value from DB```
        return "Bad Null Values";
    }
}
