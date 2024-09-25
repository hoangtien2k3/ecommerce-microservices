package com.hoangtien2k3.search.service;

import com.hoangtien2k3.search.config.ServiceUrlConfig;
import com.hoangtien2k3.search.constants.MessageCode;
import com.hoangtien2k3.search.document.Product;
import com.hoangtien2k3.search.exception.NotFoundException;
import com.hoangtien2k3.search.repository.ProductRepository;
import com.hoangtien2k3.search.viewmodel.ProductEsDetailVm;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ProductSyncDataService {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;
    private final ProductRepository productRepository;

    public ProductEsDetailVm getProductEsDetailById(Long id) {
        final URI url = UriComponentsBuilder.fromHttpUrl(
                serviceUrlConfig.product()).path("/storefront/products-es/{id}").buildAndExpand(id).toUri();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(ProductEsDetailVm.class);
    }

    public void updateProduct(Long id) {
        ProductEsDetailVm productEsDetailVm = getProductEsDetailById(id);
        Product product = productRepository.findById(id).orElseThrow(()
                -> new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, id));

        product.setName(productEsDetailVm.name());
        product.setSlug(productEsDetailVm.slug());
        product.setPrice(productEsDetailVm.price());
        product.setIsPublished(productEsDetailVm.isPublished());
        product.setIsVisibleIndividually(productEsDetailVm.isVisibleIndividually());
        product.setIsAllowedToOrder(productEsDetailVm.isAllowedToOrder());
        product.setIsFeatured(productEsDetailVm.isFeatured());
        product.setThumbnailMediaId(productEsDetailVm.thumbnailMediaId());
        product.setBrand(productEsDetailVm.brand());
        product.setCategories(productEsDetailVm.categories());
        product.setAttributes(productEsDetailVm.attributes());
        productRepository.save(product);
    }

    public void createProduct(Long id) {
        ProductEsDetailVm productEsDetailVm = getProductEsDetailById(id);

        Product product = Product.builder()
                .id(id)
                .name(productEsDetailVm.name())
                .slug(productEsDetailVm.slug())
                .price(productEsDetailVm.price())
                .isPublished(productEsDetailVm.isPublished())
                .isVisibleIndividually(productEsDetailVm.isVisibleIndividually())
                .isAllowedToOrder(productEsDetailVm.isAllowedToOrder())
                .isFeatured(productEsDetailVm.isFeatured())
                .thumbnailMediaId(productEsDetailVm.thumbnailMediaId())
                .brand(productEsDetailVm.brand())
                .categories(productEsDetailVm.categories())
                .attributes(productEsDetailVm.attributes())
                .build();

        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        final boolean isProductExisted = productRepository.existsById(id);
        if (!isProductExisted) {
            throw new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, id);
        }

        productRepository.deleteById(id);
    }
}
