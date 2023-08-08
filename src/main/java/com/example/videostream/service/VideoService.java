package com.example.videostream.service;

import com.example.videostream.VideoRepository;
import com.example.videostream.model.Video;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private static final String GCLOUD_BUCKET = "video-streaming-bucket2";
    private static final String MP4_SUFFIX = ".mp4";
    private final Logger logger = LoggerFactory.getLogger(VideoService.class);
    private final Storage storage;
    private final ResourceLoader resourceLoader;
    private final VideoRepository videoRepository;
    private static final String PATH_TO_VIDEOS = "classpath:videos/";

    public Mono<Resource> getVideo(String videoName) throws FileNotFoundException {
        Bucket bucket = storage.get(GCLOUD_BUCKET);
        if (bucket == null) {
            throw new FileNotFoundException("Bucket " + GCLOUD_BUCKET + " has not yet been created, try POSTing a video.");
        }
        Blob blob = bucket.get(videoName + MP4_SUFFIX);

        return Mono.fromSupplier(() -> new InputStreamResource(new ByteArrayInputStream(blob.getContent())));
    }

    public void postVideo(MultipartFile video, String description) throws IOException, InterruptedException {
        Bucket bucket = storage.get(GCLOUD_BUCKET);
        if (bucket == null) {
            logger.warn("Bucket not found creating new bucket: " + GCLOUD_BUCKET);
            storage.create(BucketInfo.newBuilder(GCLOUD_BUCKET).setLocation("europe-central2").build()).wait();
        }
        String fileName = video.getResource().getFilename();
        bucket.create(fileName, video.getInputStream().readAllBytes(), "video/mp4");
        videoRepository.save(Video.builder()
                .videoName(fileName)
                .description(description)
                .build());

    }

    public List<Video> getVideoList() {
        return videoRepository.findAll();
    }
}
