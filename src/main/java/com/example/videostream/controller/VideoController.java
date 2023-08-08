package com.example.videostream.controller;

import com.example.videostream.model.Video;
import com.example.videostream.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/film")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping(value = "/", produces = "video/mp4")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Resource> getVideo(@RequestParam String videoName) throws IOException {
        return videoService.getVideo(videoName);
    }

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public List<Video> getVideoList() {
        return videoService.getVideoList();
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String postVideo(@RequestPart MultipartFile video, @RequestParam String description) throws IOException, InterruptedException {
        videoService.postVideo(video, description);
        return "Successful posted video " + video.getName();
    }

}
