package com.lewickiy.coffeeboardapp.entities.productcategory;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductCategory {
    @NonNull
    private int productCategoryId;
    @NonNull
    private String productCategory;
    private int amountProducts;
}