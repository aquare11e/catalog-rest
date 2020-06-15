package me.rkomarov.catalog.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import me.rkomarov.catalog.contoller.dto.SectionRequestDto;
import me.rkomarov.catalog.contoller.dto.SectionDetailsResponseDto;
import me.rkomarov.catalog.contoller.dto.SectionResponseDto;
import me.rkomarov.catalog.db.ProductRepository;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.exception.BusinessLogicException;
import me.rkomarov.catalog.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final ProductRepository productRepository;
    private final MapperFacade mapperFacade;

    @Transactional(readOnly = true)
    public List<SectionResponseDto> getAllSections(boolean showDeleted) {
        List<Section> sections = showDeleted ?
                sectionRepository.fetchSectionsWithDeleted() :
                sectionRepository.findAll();
        return mapperFacade.mapAsList(sections, SectionResponseDto.class);
    }

    @Transactional(readOnly = true)
    public List<SectionDetailsResponseDto> getAllSectionTree() {
        Set<Section> rawSections = sectionRepository.fetchFullSections();

        Set<Section> sections = rawSections.stream()
                .filter(section -> section.getParent() == null)
                .collect(Collectors.toSet());

        return mapperFacade.mapAsList(sections, SectionDetailsResponseDto.class);
    }

    @Transactional(readOnly = true)
    public SectionResponseDto getSection(long sectionId) {
        Section section = getSectionOrThrow(sectionId);
        return mapperFacade.map(section, SectionResponseDto.class);
    }

    @Transactional(readOnly = true)
    public SectionDetailsResponseDto getSectionTree(long sectionId) {
        Set<Section> subtreeSections = sectionRepository.fetchSectionsTreeNodes(Set.of(sectionId));
        if (subtreeSections.isEmpty()) {
            throw new NotFoundException("Section with id was not found: " + sectionId);
        }

        Section section = sectionRepository.fetchFullSections(subtreeSections).stream()
                .filter(sc -> sc.getId().equals(sectionId))
                .collect(Collectors.toSet())
                .iterator().next();

        return mapperFacade.map(section, SectionDetailsResponseDto.class);
    }

    @Transactional
    public SectionDetailsResponseDto createSection(SectionRequestDto sectionDto) {
        Section section = mapperFacade.map(sectionDto, Section.class);
        Section savedEntity = sectionRepository.save(section);

        return mapperFacade.map(savedEntity, SectionDetailsResponseDto.class);
    }

    @Transactional
    public void updateSection(long sectionId, SectionRequestDto sectionDto) {
        Section section = getSectionOrThrow(sectionId);
        mapperFacade.map(sectionDto, section);

        sectionRepository.save(section);
    }

    @Transactional
    public void deleteSection(long sectionId) {
        Set<Section> sections = sectionRepository.fetchSectionsTreeNodes(Set.of(sectionId));
        if (sections.isEmpty()) {
            throw new NotFoundException("Section with id was not found: " + sectionId);
        }

        sectionRepository.softDelete(sections);
        productRepository.softDeleteBySections(sections);
    }

    @Transactional
    public void restoreSection(long sectionId) {
        Set<Section> sections = sectionRepository.fetchSectionTreeNodesWithDeleted(sectionId);
        if (sections.isEmpty()) {
            throw new NotFoundException("Section with id was not found: " + sectionId);
        }

        if (sectionRepository.existDeletedParentBySectionId(sectionId) > 0) {
            throw new BusinessLogicException("Impossible to restore section with deleted parent");
        }

        sectionRepository.restoreDeleted(sections);
        productRepository.restoreBySections(sections);
    }

    @Transactional
    public void updateSubsections(long sectionId, Set<Long> subsectionIds) {
        Set<Section> subsectionTreeNodes = sectionRepository.fetchSectionsTreeNodes(subsectionIds);
        if (subsectionTreeNodes.stream().anyMatch(subsection -> subsection.getId().equals(sectionId))) {
            throw new BusinessLogicException("Circular sections problem was detected");
        }

        Set<Section> desiredSubsectionEntities = subsectionTreeNodes.stream()
                .filter(subsection -> subsectionIds.contains(subsection.getId()))
                .collect(Collectors.toSet());

        Optional<Section> optionalSection = sectionRepository.findById(sectionId);
        if (desiredSubsectionEntities.size() != subsectionIds.size() || optionalSection.isEmpty()) {
            Set<Long> missedSectionIds = new HashSet<>(subsectionIds);

            missedSectionIds.removeAll(desiredSubsectionEntities.stream().map(Section::getId).collect(Collectors.toSet()));
            if (optionalSection.isEmpty()) {
                missedSectionIds.add(sectionId);
            }

            throw new NotFoundException("Section(s) with id was not found: " + missedSectionIds);
        }

        Section section = optionalSection.get();
        sectionRepository.removeSectionsParent(section);
        sectionRepository.updateSectionsParent(section, desiredSubsectionEntities);
    }

    @Transactional
    public void removeSubsections(long sectionId) {
        Section section = getSectionOrThrow(sectionId);
        sectionRepository.removeSectionsParent(section);
    }

    private Section getSectionOrThrow(long id) {
        Optional<Section> sectionOptional = sectionRepository.findById(id);
        return sectionOptional.orElseThrow(() -> new NotFoundException("Section with id was not found: " + id));
    }
}
