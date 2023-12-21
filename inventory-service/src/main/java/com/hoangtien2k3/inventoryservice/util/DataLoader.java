package com.hoangtien2k3.inventoryservice.util;

import com.hoangtien2k3.inventoryservice.model.Inventory;
import com.hoangtien2k3.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Autowired
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