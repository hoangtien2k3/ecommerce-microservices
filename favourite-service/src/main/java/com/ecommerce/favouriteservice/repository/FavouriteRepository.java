package com.ecommerce.favouriteservice.repository;

import com.ecommerce.favouriteservice.entity.Favourite;
import com.ecommerce.favouriteservice.entity.id.FavouriteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRepository extends JpaRepository<Favourite, FavouriteId> {

}
