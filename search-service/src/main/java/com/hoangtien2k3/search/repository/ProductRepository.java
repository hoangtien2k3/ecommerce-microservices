package com.hoangtien2k3.search.repository;

import com.hoangtien2k3.search.document.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, Long> {
}
