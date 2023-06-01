package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.service.compilation.CompilationService;
import ru.practicum.ewm.main.service.stat.StatService;

import javax.validation.constraints.Min;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationsPubController {
    private final CompilationService compilationService;
    private final StatService statService;


    @GetMapping
    public List<CompilationDto> findAllCompilations(
            @RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        statService.saveHit("/compilations");
        return compilationService.getAllCompilations(from, size, pinned);
    }

    @GetMapping("/{compId}")
    public CompilationDto findCompilationById(
            @PathVariable Long compId
    ) {
        statService.saveHit("/compilations/" + compId);
        return compilationService.getCompilationById(compId);
    }
}
