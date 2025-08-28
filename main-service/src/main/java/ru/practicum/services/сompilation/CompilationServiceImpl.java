package ru.practicum.services.сompilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
public class CompilationServiceImpl implements CompilationService{
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationEventsRepo compilationEventsRepo;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        List<Long> eventIds = dto.getEvents();
        Compilation compilation = CompilationMapper.mapToCompilationFromNewRequest(dto);

        compilation = compilationRepository.save(compilation);
        List<EventShortDto> listShortDto = new ArrayList<>();

        for (Long id : eventIds) {
            Optional<Event> findEvent = eventRepository.findById(id);
            if (findEvent.isPresent()) {
                compilationEventsRepo.save(new CompilationEvents(null, compilation, findEvent.get()));
            } else {
                throw new NotFoundUserException("События с id " + id + " нет");
            }

            listShortDto.add(EventMapper.mapToEventShortDtoFromEvent(findEvent.get()));
        }

        if (!listShortDto.isEmpty()) {
            CompilationDto compilationDto = CompilationMapper.mapToCompDto(compilation);
            compilationDto.setEvents(listShortDto);
            return compilationDto;
        } {
            throw  new ConflictException("Integrity constraint has been violated.");
        }
    }

    @Override
    public void deleteCompilation(Long compId) {
        Optional<Compilation> findCompilation = compilationRepository.findById(compId);

        if (findCompilation.isEmpty()) {
            throw new NotFoundUserException("Подборки с id " + compId + "нет");
        } else {
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
        if (!request.getEvents().isEmpty()) {
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
    public Page<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));

        Page<Compilation> requestsPage = compilationRepository.findByPinned(pinned, pageable);

        if (!requestsPage.getContent().isEmpty()) {
            List<CompilationDto> content = requestsPage.getContent().stream()
                    .map(compilation -> CompilationMapper.mapToCompDto(compilation))
                    .sorted(Comparator.comparing(CompilationDto::getId))
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    content,
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        } else {
            return new PageImpl<>(
                    Collections.emptyList(),
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        }
    }

    @Override
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