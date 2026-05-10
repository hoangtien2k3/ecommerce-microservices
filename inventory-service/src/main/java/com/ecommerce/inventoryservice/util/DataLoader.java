package com.ecommerce.inventoryservice.util;

import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        Inventory inventory = new Inventory();
        inventory.setProductName("Iphone 13");
        inventory.setQuantity(12);

        Inventory inventory1 = new Inventory();
        inventory1.setProductName("Macbook Pro 13.3");
        inventory1.setQuantity(4);

        inventoryRepository.save(inventory);
        inventoryRepository.save(inventory1);
    }

}
