package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.compilation.Compilation;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> eventShorts = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .events(eventShorts)
                .title(compilation.getTitle())
                .build();
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilationList) {
        return compilationList.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }
}
