package ru.practicum.ewm.main.dto.compilation;

import ru.practicum.ewm.main.dto.event.EventMapper;
import ru.practicum.ewm.main.model.compilation.Compilation;

import java.util.ArrayList;
import java.util.List;


public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                EventMapper.toEventShortDto(compilation.getEvents()),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }

    public static List<CompilationDto> toCompilationDto(Iterable<Compilation> compilations) {
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation));
        }
        return result;
    }
}
