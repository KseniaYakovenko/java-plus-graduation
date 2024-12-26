package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long categoryId);

    void delete(long compilationId);

    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Long id, UpdateCompilationRequestDto dto);
}