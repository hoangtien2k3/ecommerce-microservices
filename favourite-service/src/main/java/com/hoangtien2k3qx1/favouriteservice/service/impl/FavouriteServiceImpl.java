package com.hoangtien2k3qx1.favouriteservice.service.impl;

import com.hoangtien2k3qx1.favouriteservice.constant.ConfigConstant;
import com.hoangtien2k3qx1.favouriteservice.dto.FavouriteDto;
import com.hoangtien2k3qx1.favouriteservice.dto.ProductDto;
import com.hoangtien2k3qx1.favouriteservice.dto.UserDto;
import com.hoangtien2k3qx1.favouriteservice.entity.id.FavouriteId;
import com.hoangtien2k3qx1.favouriteservice.exception.wrapper.FavouriteNotFoundException;
import com.hoangtien2k3qx1.favouriteservice.helper.FavouriteMappingHelper;
import com.hoangtien2k3qx1.favouriteservice.repository.FavouriteRepository;
import com.hoangtien2k3qx1.favouriteservice.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    @Autowired
    private final FavouriteRepository favouriteRepository;
    @Autowired
    private final RestTemplate restTemplate;

    @Override
    public List<FavouriteDto> findAll() {
        return this.favouriteRepository.findAll()
                .stream()
                .map(FavouriteMappingHelper::map)
                .map(f -> {
                    f.setUserDto(this.restTemplate
                            .getForObject(
                                    ConfigConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/" + f.getUserId(),
                                    UserDto.class)
                    );
                    f.setProductDto(this.restTemplate
                            .getForObject(
                                    ConfigConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/" + f.getProductId(),
                                    ProductDto.class)
                    );
                    return f;
                })
                .distinct()
                .toList();
    }

    @Override
    public FavouriteDto findById(final FavouriteId favouriteId) {
        return this.favouriteRepository.findById(favouriteId)
                .map(FavouriteMappingHelper::map)
                .map(f -> {
                    f.setUserDto(this.restTemplate
                            .getForObject(ConfigConstant.DiscoveredDomainsApi
                                    .USER_SERVICE_API_URL + "/" + f.getUserId(), UserDto.class));
                    f.setProductDto(this.restTemplate
                            .getForObject(ConfigConstant.DiscoveredDomainsApi
                                    .PRODUCT_SERVICE_API_URL + "/" + f.getProductId(), ProductDto.class));
                    return f;
                })
                .orElseThrow(() -> new FavouriteNotFoundException(
                        String.format("Favourite with id: [%s] not found!", favouriteId)));
    }

    @Override
    public FavouriteDto save(final FavouriteDto favouriteDto) {
        return FavouriteMappingHelper.map(this.favouriteRepository
                .save(FavouriteMappingHelper.map(favouriteDto)));
    }

    @Override
    public FavouriteDto update(final FavouriteDto favouriteDto) {
        return FavouriteMappingHelper.map(this.favouriteRepository
                .save(FavouriteMappingHelper.map(favouriteDto)));
    }

    @Override
    public void deleteById(final FavouriteId favouriteId) {
        this.favouriteRepository.deleteById(favouriteId);
    }

}