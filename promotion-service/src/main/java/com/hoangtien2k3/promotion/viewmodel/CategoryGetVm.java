package com.hoangtien2k3.promotion.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryGetVm(long id, String name, String slug, long parentId) {
}
