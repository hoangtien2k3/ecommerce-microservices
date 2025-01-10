package com.hoangtien2k3.media.service;

import com.hoangtien2k3.media.model.Media;
import com.hoangtien2k3.media.model.dto.MediaDto;
import com.hoangtien2k3.media.viewmodel.MediaPostVm;
import com.hoangtien2k3.media.viewmodel.MediaVm;
import java.util.List;

public interface MediaService {
    Media saveMedia(MediaPostVm mediaPostVm);

    MediaVm getMediaById(Long id);

    void removeMedia(Long id);

    MediaDto getFile(Long id, String fileName);

    List<MediaVm> getMediaByIds(List<Long> ids);
}
