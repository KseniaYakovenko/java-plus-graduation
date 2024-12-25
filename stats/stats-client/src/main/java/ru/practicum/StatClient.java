package ru.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "stats-server")
public interface StatClient {

    @PostMapping("/hit")
    void saveHit(@RequestBody HitDto hitDto);

    @GetMapping("stats")
    List<HitStatDto> getStats(@RequestParam(value = "start", required = false, defaultValue = "") String start,
                              @RequestParam(value = "end", required = false, defaultValue = "") String end,
                              @RequestParam(value = "uris", required = false, defaultValue = "") List<String> uris,
                              @RequestParam(value = "unique", defaultValue = "false") Boolean unique);
}
