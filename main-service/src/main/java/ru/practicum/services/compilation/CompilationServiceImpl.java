package ru.practicum.services.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.CompilationEvents;
import ru.practicum.entity.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundUserException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.repo.CompilationEventsRepo;
import ru.practicum.repo.CompilationRepository;
import ru.practicum.repo.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationEventsRepo compilationEventsRepo;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        if (dto.getTitle() != null && compilationRepository.existsByTitle(dto.getTitle())) {
            throw new ConflictException("Подборка с названием '" + dto.getTitle() + "' уже существует");
        }

        Compilation compilation = CompilationMapper.mapToCompilationFromNewRequest(dto);

        compilation = compilationRepository.save(compilation);

        List<EventShortDto> eventDtos = processCompilationEvents(compilation, dto.getEvents());

        CompilationDto result = new CompilationDto();
        result.setId(compilation.getId());
        result.setTitle(compilation.getTitle());
        result.setPinned(compilation.isPinned());
        result.setEvents(eventDtos);

        return result;
    }

    private List<EventShortDto> processCompilationEvents(Compilation compilation, List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Event> events = eventRepository.findAllById(eventIds);

        if (events.size() != eventIds.size()) {
            Set<Long> foundIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = eventIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new NotFoundUserException("События с id=" + missingIds + " не найдены");
        }

        List<CompilationEvents> compilationEvents = events.stream()
                .map(event -> {
                    CompilationEvents ce = new CompilationEvents();
                    ce.setCompilation(compilation);
                    ce.setEvent(event);
                    return ce;
                })
                .collect(Collectors.toList());

        compilationEventsRepo.saveAll(compilationEvents);

        return events.stream()
                .map(EventMapper::mapToEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCompilation(Long compId) {
        Optional<Compilation> findCompilation = compilationRepository.findById(compId);

        if (findCompilation.isEmpty()) {
            throw new NotFoundUserException("Подборки с id " + compId + "нет");
        } else {
            compilationEventsRepo.deleteByCompilation_Id(compId);
            compilationRepository.delete(findCompilation.get());
        }
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto request) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);

        if (compilation.isEmpty()) {
            throw new NotFoundUserException("Подборки с id " + compId + "нет");
        }

        Compilation findcompilation = CompilationMapper.mapToCompFroUpd(compilation.get(), request);
        List<EventShortDto> listShortEventDto = compilationEventsRepo.findById(compId).stream()
                .map(event -> EventMapper.mapToEventShortDtoFromEvent(event.getEvent())).toList();
        if (request.getEvents() != null) {
            for (Long id : request.getEvents()) {
                Optional<Event> findEvent = eventRepository.findById(id);
                if (findEvent.isPresent()) {
                    compilationEventsRepo.save(new CompilationEvents(null, findcompilation, findEvent.get()));
                } else {
                    throw new NotFoundUserException("События с id " + id + " нет");
                }

                listShortEventDto.add(EventMapper.mapToEventShortDtoFromEvent(findEvent.get()));
            }
        }

        CompilationDto dto = CompilationMapper.mapToCompDto(findcompilation);
        dto.setEvents(listShortEventDto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        if (pinned != null) {
            List<Compilation> findCats = compilationRepository.findByPinned(pinned, from, size);
            if (findCats.isEmpty()) {
                return Collections.emptyList();
            } else {
                return findCats.stream().map(c -> CompilationMapper.mapToCompDto(c)).toList();
            }
        } else {
            List<Compilation> findCats = compilationRepository.findByParam(from, size);
            if (findCats.isEmpty()) {
                return Collections.emptyList();
            } else {
                return findCats.stream().map(c -> CompilationMapper.mapToCompDto(c)).toList();
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        Optional<Compilation> findComp = compilationRepository.findById(compId);

        if (findComp.isEmpty()) {
            throw new NotFoundUserException("The required object was not found.");
        } else {
            CompilationDto compilation = CompilationMapper.mapToCompDto(findComp.get());

            List<EventShortDto> listShortDto = compilationEventsRepo.findByCompilation_Id(compId).stream()
                    .map(comp -> EventMapper.mapToEventShortDtoFromEvent(comp.getEvent())).toList();

            compilation.setEvents(listShortDto);
            return compilation;
        }
    }
}