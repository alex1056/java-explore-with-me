package ru.practicum.ewm.main.service.compilation;


import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto saveCompilation(NewCompilationDto newCompilation);

    List<CompilationDto> getAllCompilations(Integer from, Integer size, Boolean pinned);

    CompilationDto getCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto newCompilation);

    void deleteCompilation(Long compId);

}
