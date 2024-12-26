package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;
import ru.practicum.ErrorResponse;
import ru.practicum.service.StatsService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@RequestBody HitDto hitDto) {

        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<HitStatDto> getHits(@RequestParam String start,
                             @RequestParam String end,
                             @RequestParam(required = false) List<String> uris,
                             @RequestParam (required = false, defaultValue = "false") Boolean unique) {
        return statsService.getHits(start, end, uris, unique);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        errorResponse.setStacktrace(pw.toString());
        return errorResponse;
    }
}
