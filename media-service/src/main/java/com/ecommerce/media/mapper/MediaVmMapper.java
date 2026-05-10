package com.ecommerce.media.mapper;

import com.ecommerce.media.model.Media;
import com.ecommerce.media.viewmodel.MediaVm;
import com.ecommerce.commonlib.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaVmMapper extends BaseMapper<Media, MediaVm> {
}
