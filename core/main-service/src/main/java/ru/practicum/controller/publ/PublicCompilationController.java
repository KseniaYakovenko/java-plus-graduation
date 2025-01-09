package ru.practicum.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAll(@RequestParam(value = "pinned", defaultValue = "false") Boolean pinned,
                                       @RequestParam(value = "from", defaultValue = "0") int from,
                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getById(@PathVariable("id") long id) {
        return compilationService.getById(id);
    }
}
