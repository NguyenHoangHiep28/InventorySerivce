package com.example.inventoryservice.entity.seeder;

import com.example.inventoryservice.entity.Category;
import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.entity.enums.ProductSimpleStatus;
import com.example.inventoryservice.repository.CategoryRepository;
import com.example.inventoryservice.repository.ProductRepository;
import com.example.inventoryservice.util.StringHelper;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductSeeder implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductSeeder(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    Faker faker = new Faker();

    @Override
    public void run(String... args) throws Exception {
        createCategory();
        createProducts();
    }
    private void createCategory() {
        List<Category> categories = new ArrayList<>();
        Category category1 = Category.builder().name("Noodle").build();
        Category category2 = Category.builder().name("Tea").build();
        Category category3 = Category.builder().name("Roasted Food").build();
        Category category4 = Category.builder().name("Bread").build();
        categories.add(category1);
        categories.add(category2);
        categories.add(category3);
        categories.add(category4);
        categoryRepository.saveAll(categories);
    }
    private void createProducts() {
        boolean nameExisting = false;
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String productName = faker.food().dish();

            for (Product product :
                    products) {
                if (product.getName().equals(productName)) {
                    nameExisting = true;
                    break;
                }
            }
            if (nameExisting) {
                i--;
                nameExisting = false;
                continue;
            }
            Integer cateId = faker.number().numberBetween(1,4);
            Category category = categoryRepository.findById(cateId).get();
            String slug = StringHelper.toSlug(productName);
            String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
            String detail = "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
            BigDecimal price = new BigDecimal(faker.number().randomNumber(5, true));
            ProductSimpleStatus status = ProductSimpleStatus.ACTIVE;
            Product product = new Product();
            product.setName(productName);
            product.setSlug(slug);
            product.setDescription(description);
            product.setThumbnails("demo-img.jpg");
            product.setPrice(price);
            product.setStatus(status);
            product.setInStockQty(faker.number().numberBetween(10, 20));
            product.setDetail(detail);
            product.setCategory(category);
            products.add(product);
        }
        productRepository.saveAll(products);
    }
}
