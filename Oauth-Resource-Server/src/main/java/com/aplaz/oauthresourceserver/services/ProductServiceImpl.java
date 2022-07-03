package com.aplaz.oauthresourceserver.services;

import com.aplaz.oauthresourceserver.entity.Product;
import com.aplaz.oauthresourceserver.model.ProductModel;
import com.aplaz.oauthresourceserver.repository.ProductRepository;

public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Override
    public void saveProduct(ProductModel productModel) {

        //Create new Entity and insert values
        Product product = new Product();

        product.setProduct_name(productModel.getProduct_name());
        product.setPrice(product.getPrice());

    }
}
