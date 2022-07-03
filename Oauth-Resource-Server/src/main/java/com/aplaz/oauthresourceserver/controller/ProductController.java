package com.aplaz.oauthresourceserver.controller;

import com.aplaz.oauthresourceserver.entity.Product;
import com.aplaz.oauthresourceserver.model.ProductModel;
import com.aplaz.oauthresourceserver.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class ProductController extends ProductModel {

    private ProductService productService;

    //save product by ID
    @PostMapping("/api/product")
    public String postProduct(@RequestBody @Valid ProductModel productModel, Principal principal){

        Product product = new Product();
        product.setProduct_name(productModel.getProduct_name());
        product.setPrice(productModel.getPrice());

        String email = principal.getName();
        return email+productModel.getProduct_name();
        //productService.saveProduct(productModel);
    }

//    @PostMapping("/api/product")
//    public String postProduct(Principal principal){
//        //get Email
//        String email = principal.getName();
//
//        return email;
//        //productService.saveProduct(productModel);
//    }

    //get yours all Products

    //get all Products

    //update Product

    //delete Product
}
