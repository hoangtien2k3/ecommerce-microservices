package com.ecommerce.media.service;

import com.ecommerce.commonlib.storage.ObjectStorageService;
import com.ecommerce.commonlib.storage.StorageObject;
import com.ecommerce.media.mapper.MediaVmMapper;
import com.ecommerce.media.model.Media;
import com.ecommerce.media.model.dto.MediaDto;
import com.ecommerce.media.model.dto.MediaDto.MediaDtoBuilder;
import com.ecommerce.media.repository.MediaRepository;
import com.ecommerce.media.utils.StringUtils;
import com.ecommerce.media.viewmodel.MediaPostVm;
import com.ecommerce.media.viewmodel.MediaVm;
import com.ecommerce.media.viewmodel.NoFileMediaVm;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class MediaServiceImpl implements MediaService {

    private final MediaVmMapper mediaVmMapper;
    private final MediaRepository mediaRepository;
    private final ObjectStorageService objectStorageService;
    @Value("${hoangtien2k3.publicUrl:http://localhost:8083}")
    private String publicUrl;

    @Override
    @SneakyThrows
    public Media saveMedia(MediaPostVm mediaPostVm) {
        Media media = new Media();
        media.setCaption(mediaPostVm.caption());
        media.setMediaType(mediaPostVm.multipartFile().getContentType());

        if (StringUtils.hasText(mediaPostVm.fileNameOverride())) {
            media.setFileName(mediaPostVm.fileNameOverride().trim());
        } else {
            media.setFileName(mediaPostVm.multipartFile().getOriginalFilename());
        }

        String objectKey = buildObjectKey(media.getFileName());
        objectStorageService.upload(
            objectKey,
            mediaPostVm.multipartFile().getBytes(),
            media.getMediaType());
        media.setFilePath(objectKey);

        return mediaRepository.save(media);
    }

    @Override
    public void removeMedia(Long id) {
        Media media = mediaRepository.findById(id).orElse(null);
        if (media == null) {
            return;
        }
        try {
            if (StringUtils.hasText(media.getFilePath())) {
                objectStorageService.delete(media.getFilePath());
            }
            mediaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MediaVm getMediaById(Long id) {
        NoFileMediaVm noFileMediaVm = mediaRepository.findByIdWithoutFileInReturn(id);
        if (noFileMediaVm == null) {
            return null;
        }
        String url = getMediaUrl(noFileMediaVm.id(), noFileMediaVm.fileName());

        return new MediaVm(
            noFileMediaVm.id(),
            noFileMediaVm.caption(),
            noFileMediaVm.fileName(),
            noFileMediaVm.mediaType(),
            url
        );
    }

    @Override
    public MediaDto getFile(Long id, String fileName) {

        MediaDtoBuilder builder = MediaDto.builder();

        Media media = mediaRepository.findById(id).orElse(null);
        if (media == null || !fileName.equalsIgnoreCase(media.getFileName())) {
            return builder.build();
        }
        MediaType mediaType = MediaType.valueOf(media.getMediaType());
        StorageObject storageObject = objectStorageService.download(media.getFilePath());

        return builder
            .content(storageObject.getContent())
            .mediaType(mediaType)
            .build();
    }

    @Override
    public List<MediaVm> getMediaByIds(List<Long> ids) {
        return mediaRepository.findAllById(ids).stream()
                .map(mediaVmMapper::toVm)
                .peek(media -> {
                    String url = getMediaUrl(media.getId(), media.getFileName());
                    media.setUrl(url);
                }).toList();
    }

    private String buildObjectKey(String fileName) {
        String safeName = fileName == null ? "file" : fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "-" + safeName;
    }

    private String getMediaUrl(Long mediaId, String fileName) {
        return UriComponentsBuilder.fromUriString(publicUrl)
                .path(String.format("/medias/%1$s/file/%2$s", mediaId, fileName))
                .build().toUriString();
    }
}
