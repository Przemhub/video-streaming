package com.example.videostream.service;

import com.example.videostream.VideoRepository;
import com.example.videostream.dto.VideoPostDto;
import com.example.videostream.model.Video;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private static final String PATH_TO_VIDEOS = "classpath:videos/";
    private static final String GCLOUD_BUCKET = "video-streaming-bucket2";
    private static final String MP4_SUFFIX = ".mp4";

    private final Storage storage;
    private final ResourceLoader resourceLoader;
    private final VideoRepository videoRepository;


    public Mono<Resource> getVideo(String videoName) {
        return Mono.fromSupplier(() -> resourceLoader.getResource(PATH_TO_VIDEOS + videoName + MP4_SUFFIX));
    }

    public void postVideo(MultipartFile video, VideoPostDto videoInfo) throws IOException {
        Bucket bucket = storage.get(GCLOUD_BUCKET);
        if (bucket.exists()) {
            String fileName = video.getResource().getFilename();
            bucket.create(fileName + MP4_SUFFIX, video.getInputStream().readAllBytes());
            videoRepository.save(Video.builder()
                    .videoName(videoInfo.getVideoName())
                    .description(videoInfo.getDescription())
                    .build());
        } else {
            throw new IOException("Google Cloud storage bucket doesn't exist");
        }
    }


    public List<Video> getVideoList() {
        return videoRepository.findAll();
    }
}
