package com.example.project.config;

import com.example.project.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {
    @Bean
    public AnimalMapper animalMapper() {
        return new AnimalMapperImpl();
    }

    @Bean
    public ClientMapper clientMapper() { return new ClientMapperImpl(); }

    @Bean
    public PurchaseMapper purchaseMapper() { return new PurchaseMapperImpl(); }

    @Bean
    public FoodMapper foodMapper() { return new FoodMapperImpl(); }

    @Bean
    public InventoryMapper inventoryMapper() { return new InventoryMapperImpl(); }

    @Bean
    public ToyMapper toyMapper() { return new ToyMapperImpl(); }

    @Bean
    public MedicineMapper medicineMapper() { return new MedicineMapperImpl();}

    @Bean
    public ProductTypeMapper productTypeMapper() { return new ProductTypeMapperImpl(); }

}

// sa fac cate un bean pt fiecare mapper in parte
