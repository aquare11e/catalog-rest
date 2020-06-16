package me.rkomarov.catalog.controller;

import lombok.RequiredArgsConstructor;
import me.rkomarov.catalog.controller.dto.SectionRequestDto;
import me.rkomarov.catalog.controller.dto.SectionDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.SectionResponseDto;
import me.rkomarov.catalog.service.SectionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/section")
@RequiredArgsConstructor
public class SectionController implements SectionResource {

    private final SectionService sectionService;

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') || (hasRole('USER') && #showDeleted == false)")
    public List<SectionResponseDto> getAll(@RequestParam(defaultValue = "false") boolean showDeleted) {
        return sectionService.getAllSections(showDeleted);
    }

    @Override
    @GetMapping("/tree")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<SectionDetailsResponseDto> getAllTree() {
        return sectionService.getAllSectionTree();
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public SectionResponseDto get(@PathVariable long id) {
        return sectionService.getSection(id);
    }

    @Override
    @GetMapping("/{id}/tree")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public SectionDetailsResponseDto getTree(@PathVariable long id) {
        return sectionService.getSectionTree(id);
    }


    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SectionDetailsResponseDto create(@RequestBody @Valid SectionRequestDto sectionDto) {
        return sectionService.createSection(sectionDto);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void update(@PathVariable long id, @RequestBody @Valid SectionRequestDto sectionDto) {
        sectionService.updateSection(id, sectionDto);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable long id) {
        sectionService.deleteSection(id);
    }

    @Override
    @PutMapping("/{id}/subsections")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSubsections(@PathVariable long id, @RequestBody Set<Long> subsectionIds) {
        if (subsectionIds.isEmpty()) {
            sectionService.removeSubsections(id);
        } else {
            sectionService.updateSubsections(id, subsectionIds);
        }
    }

    @Override
    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public void restore(@PathVariable long id) {
        sectionService.restoreSection(id);
    }
}
