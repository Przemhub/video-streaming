package com.example.videostream.service;

import com.example.videostream.VideoRepository;
import com.example.videostream.dto.VideoPostDto;
import com.example.videostream.model.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private static final String PATH_TO_VIDEOS = "classpath:videos/";
    private final String MP4_SUFFIX = ".mp4";

    private final ResourceLoader resourceLoader;
    private final VideoRepository videoRepository;

    public Mono<Resource> getVideo(String videoName) {
        return Mono.fromSupplier(() -> resourceLoader.getResource(PATH_TO_VIDEOS + videoName + MP4_SUFFIX));
    }

    public void postVideo(MultipartFile video, VideoPostDto videoInfo) throws IOException {
        String fileName = video.getResource().getFilename();
        video.transferTo(Files.createFile(Path.of(PATH_TO_VIDEOS + fileName)));
        videoRepository.save(Video.builder()
                .videoName(videoInfo.getVideoName())
                .description(videoInfo.getDescription())
                .build());
    }

    public List<Video> getVideoList() {
        return videoRepository.findAll();
    }
}
