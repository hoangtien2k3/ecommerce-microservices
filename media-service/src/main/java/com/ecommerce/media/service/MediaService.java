package com.ecommerce.media.service;

import com.ecommerce.media.model.Media;
import com.ecommerce.media.model.dto.MediaDto;
import com.ecommerce.media.viewmodel.MediaPostVm;
import com.ecommerce.media.viewmodel.MediaVm;
import java.util.List;

public interface MediaService {
    Media saveMedia(MediaPostVm mediaPostVm);

    MediaVm getMediaById(Long id);

    void removeMedia(Long id);

    MediaDto getFile(Long id, String fileName);

    List<MediaVm> getMediaByIds(List<Long> ids);
}
