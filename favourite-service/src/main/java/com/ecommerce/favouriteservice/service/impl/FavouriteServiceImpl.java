package com.ecommerce.favouriteservice.service.impl;

import com.ecommerce.favouriteservice.constant.ConfigConstant;
import com.ecommerce.favouriteservice.dto.FavouriteDto;
import com.ecommerce.favouriteservice.dto.ProductDto;
import com.ecommerce.favouriteservice.dto.UserDto;
import com.ecommerce.favouriteservice.entity.id.FavouriteId;
import com.ecommerce.favouriteservice.exception.wrapper.FavouriteNotFoundException;
import com.ecommerce.favouriteservice.helper.FavouriteMappingHelper;
import com.ecommerce.favouriteservice.repository.FavouriteRepository;
import com.ecommerce.favouriteservice.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private static final Logger log = LoggerFactory.getLogger(FavouriteServiceImpl.class);

    private final FavouriteRepository favouriteRepository;
    private final RestClient.Builder restClientBuilder;

    @Override
    public List<FavouriteDto> findAll() {
        RestClient restClient = restClientBuilder.build();
        return favouriteRepository.findAll()
                .stream()
                .map(FavouriteMappingHelper::map)
                .peek(f -> {
                    try {
                        f.setUserDto(restClient.get()
                                .uri(ConfigConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/{id}", f.getUserId())
                                .retrieve()
                                .body(UserDto.class));
                        f.setProductDto(restClient.get()
                                .uri(ConfigConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/{id}", f.getProductId())
                                .retrieve()
                                .body(ProductDto.class));
                    } catch (Exception e) {
                        log.error("Error fetching user/product for favourite: {}", e.getMessage());
                    }
                })
                .distinct()
                .toList();
    }

    @Override
    public FavouriteDto findById(final FavouriteId favouriteId) {
        RestClient restClient = restClientBuilder.build();
        return favouriteRepository.findById(favouriteId)
                .map(FavouriteMappingHelper::map)
                .map(f -> {
                    try {
                        f.setUserDto(restClient.get()
                                .uri(ConfigConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/{id}", f.getUserId())
                                .retrieve()
                                .body(UserDto.class));
                        f.setProductDto(restClient.get()
                                .uri(ConfigConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/{id}", f.getProductId())
                                .retrieve()
                                .body(ProductDto.class));
                    } catch (Exception e) {
                        log.error("Error fetching user/product for favourite: {}", e.getMessage());
                    }
                    return f;
                })
                .orElseThrow(() -> new FavouriteNotFoundException(
                        String.format("Favourite with id: [%s] not found!", favouriteId)));
    }

    @Override
    public FavouriteDto save(final FavouriteDto favouriteDto) {
        return FavouriteMappingHelper.map(favouriteRepository.save(FavouriteMappingHelper.map(favouriteDto)));
    }

    @Override
    public FavouriteDto update(final FavouriteDto favouriteDto) {
        return FavouriteMappingHelper.map(favouriteRepository.save(FavouriteMappingHelper.map(favouriteDto)));
    }

    @Override
    public void deleteById(final FavouriteId favouriteId) {
        favouriteRepository.deleteById(favouriteId);
    }
}
