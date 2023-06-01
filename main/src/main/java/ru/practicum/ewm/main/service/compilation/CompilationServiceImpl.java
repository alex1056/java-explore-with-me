package ru.practicum.ewm.main.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.CompilationMapper;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;

import ru.practicum.ewm.main.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.helper.Helpers;

import ru.practicum.ewm.main.model.compilation.Compilation;

import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.model.event.EventCounter;

import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.EventSpecRepository;
import ru.practicum.ewm.main.service.event.EventService;
import ru.practicum.ewm.main.service.stat.StatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository repositoryEvent;
    private final EventService eventService;
    private final EventSpecRepository repositoryEventSpec;
    private final StatService statService;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilation) {
        if (newCompilation.getEvents() == null) {
            newCompilation.setEvents(new ArrayList<Long>());
        }

        List<Long> ids = newCompilation.getEvents();

        EventCounter ec = repositoryEventSpec.countEventsByIds(ids);
        if (ec.getCounter() != ids.size()) {
            throw new NotFoundException("какое-то из событий " + ids);
        }

        List<Event> events = repositoryEvent.findAllByIdsOrderByIdAsc(ids);

        if (newCompilation.getPinned() == null) {
            newCompilation.setPinned(false);
        }

        Compilation comp = new Compilation(
                null,
                newCompilation.getTitle(),
                newCompilation.getPinned(),
                events
        );
        Compilation savedComp = repository.save(comp);

        Optional<Compilation> savedGotComp = repository.findById(savedComp.getId());
        if (savedGotComp.isPresent()) {
            return CompilationMapper.toCompilationDto(savedGotComp.get());
        }
        return null;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto newCompilation) {
        Compilation compForUpdate = findCompilation(compId);

        List<Event> events = null;

        if (newCompilation.getEvents() != null) {
            List<Long> ids = newCompilation.getEvents();
            EventCounter ec = repositoryEventSpec.countEventsByIds(ids);
            if (ec.getCounter() != ids.size()) {
                throw new NotFoundException("какое-то из событий " + ids);
            }
            events = repositoryEvent.findAllByIdsOrderByIdAsc(ids);
        }

        compForUpdate.setTitle(newCompilation.getTitle() != null ? newCompilation.getTitle() : compForUpdate.getTitle());
        compForUpdate.setPinned(newCompilation.getPinned() != null ? newCompilation.getPinned() : compForUpdate.getPinned());
        compForUpdate.setEvents(Helpers.isEventsEqual(compForUpdate.getEvents(), events) ? compForUpdate.getEvents() : events);
        Compilation savedComp = repository.save(compForUpdate);

        Optional<Compilation> savedGotComp = repository.findById(savedComp.getId());
        if (savedGotComp.isPresent()) {
            Compilation compilation = savedGotComp.get();
            List<Event> eventsGot = compilation.getEvents();
            List<Event> eventsAdded = eventService.getEventsWithConfirmedReqCounter(eventsGot);
            compilation.setEvents(eventsAdded);
            return CompilationMapper.toCompilationDto(compilation);
        }
        return null;
    }


    @Override
    public void deleteCompilation(Long compId) {
        Compilation compForDelete = findCompilation(compId);
        repository.deleteById(compForDelete.getId());
    }

    private Compilation findCompilation(Long compId) {
        Optional<Compilation> compilationOpt = repository.findById(compId);
        compilationOpt.orElseThrow(() -> new NotFoundException("компиляция с id=" + compId));
        Compilation compilation = compilationOpt.get();
        List<Event> events = compilation.getEvents();
        List<Event> eventsAdded = eventService.getEventsWithConfirmedReqCounter(events);
        List<Event> eventsAddedViews = statService.getAddEventsWithViews(eventsAdded);
        compilation.setEvents(eventsAddedViews);
        return compilation;
    }

    @Override
    public List<CompilationDto> getAllCompilations(Integer from, Integer size, Boolean pinned) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        if (pinned == null) {
            List<Compilation> compilationList = repository.findAll(page).getContent();
            List<Compilation> compilationListAdded1 =
                    compilationWithConfirmedRequests(compilationList);
            return CompilationMapper.toCompilationDto(compilationListAdded1);
        }

        List<Compilation> compilationList2 = repository.findAllPinnedUnPinned(pinned, page).getContent();
        List<Compilation> compilationListAdded =
                compilationWithConfirmedRequests(compilationList2);

        return CompilationMapper.toCompilationDto(compilationListAdded);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = findCompilation(compId);
        List<Event> events = compilation.getEvents();
        List<Event> eventsUpdated = eventService.getEventsWithConfirmedReqCounter(events);
        compilation.setEvents(eventsUpdated);
        return CompilationMapper.toCompilationDto(compilation);
    }

    private List<Compilation> compilationWithConfirmedRequests(List<Compilation> compilationList) {
        return compilationList.stream()
                .map(compilation -> {
                    List<Event> events = compilation.getEvents();
                    List<Event> eventsUpdated = eventService.getEventsWithConfirmedReqCounter(events);
                    List<Event> eventsAddedViews = statService.getAddEventsWithViews(eventsUpdated);
                    compilation.setEvents(eventsAddedViews);
                    return compilation;
                })
                .collect(Collectors.toList());
    }
}
