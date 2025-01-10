package com.hoangtien2k3.media.mapper;

import com.hoangtien2k3.media.model.Media;
import com.hoangtien2k3.media.viewmodel.MediaVm;
import com.hoangtien2k3.commonlib.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaVmMapper extends BaseMapper<Media, MediaVm> {
}
