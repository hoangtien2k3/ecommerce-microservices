package com.hoangtien2k3.orderserivce.repository;

import com.hoangtien2k3.orderserivce.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

}
