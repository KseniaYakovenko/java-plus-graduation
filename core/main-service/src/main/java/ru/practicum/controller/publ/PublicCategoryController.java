package ru.practicum.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(@RequestParam(value = "from", defaultValue = "0") int from,
                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable("catId") long catId) {
        return categoryService.getById(catId);
    }
}
