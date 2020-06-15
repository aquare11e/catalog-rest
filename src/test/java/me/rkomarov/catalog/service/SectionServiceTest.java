package me.rkomarov.catalog.service;

import ma.glasnost.orika.MapperFacade;
import me.rkomarov.catalog.contoller.dto.SectionRequestDto;
import me.rkomarov.catalog.db.ProductRepository;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.exception.BusinessLogicException;
import me.rkomarov.catalog.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private SectionService sectionService;

    @Test
    public void updateExistedSection() {
        // Given
        long sectionId = 1L;
        SectionRequestDto sectionRequestDto = new SectionRequestDto();
        Section fetchedSection = new Section();

        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.of(fetchedSection));

        // When
        sectionService.updateSection(sectionId, sectionRequestDto);

        // Then
        verify(sectionRepository, times(1)).findById(eq(sectionId));
        verify(mapperFacade, only()).map(eq(sectionRequestDto), eq(fetchedSection));
        verify(sectionRepository, times(1)).save(fetchedSection);
        verifyNoMoreInteractions(sectionRepository);
    }

    @Test
    public void updateNotFoundSection() {
        // Given
        long sectionId = 1L;
        SectionRequestDto sectionRequestDto = new SectionRequestDto();

        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> sectionService.updateSection(sectionId, sectionRequestDto));
        verify(sectionRepository, only()).findById(eq(sectionId));
    }

    @Test
    public void deleteExistedSectionSubtree() {
        // Given
        long sectionId = 1L;
        Set<Section> fetchedSections = Set.of(new Section(), new Section());

        when(sectionRepository.fetchSectionsTreeNodes(eq(Set.of(sectionId)))).thenReturn(fetchedSections);

        // When
        sectionService.deleteSection(sectionId);

        // Then
        verify(sectionRepository, times(1)).fetchSectionsTreeNodes(eq(Set.of(sectionId)));
        verify(sectionRepository, times(1)).softDelete(eq(fetchedSections));
        verifyNoMoreInteractions(sectionRepository);
        verify(productRepository, only()).softDeleteBySections(eq(fetchedSections));
    }

    @Test
    public void deleteNotFoundSectionSubtree() {
        // Given
        long sectionId = 1L;
        when(sectionRepository.fetchSectionsTreeNodes(eq(Set.of(sectionId)))).thenReturn(Set.of());

        // Expect
        assertThrows(NotFoundException.class, () -> sectionService.deleteSection(sectionId));
        verify(sectionRepository, only()).fetchSectionsTreeNodes(eq(Set.of(sectionId)));
        verifyNoInteractions(productRepository);
    }

    @Test
    public void restoreDeletedSectionSubtreeWithActiveParent() {
        // Given
        long sectionId = 1L;
        Set<Section> fetchedSections = Set.of(new Section(), new Section());

        when(sectionRepository.fetchSectionTreeNodesWithDeleted(eq(sectionId))).thenReturn(fetchedSections);
        when(sectionRepository.existDeletedParentBySectionId(eq(sectionId))).thenReturn(0);

        // When
        sectionService.restoreSection(sectionId);

        // Then
        verify(sectionRepository, times(1)).fetchSectionTreeNodesWithDeleted(eq(sectionId));
        verify(sectionRepository, times(1)).existDeletedParentBySectionId(eq(sectionId));
        verify(sectionRepository, times(1)).restoreDeleted(eq(fetchedSections));
        verifyNoMoreInteractions(sectionRepository);
        verify(productRepository, only()).restoreBySections(eq(fetchedSections));
    }

    @Test
    public void restoreDeletedSectionSubtreeWithDeletedParent() {
        // Given
        long sectionId = 1L;
        Set<Section> fetchedSections = Set.of(new Section(), new Section());

        when(sectionRepository.fetchSectionTreeNodesWithDeleted(eq(sectionId))).thenReturn(fetchedSections);
        when(sectionRepository.existDeletedParentBySectionId(eq(sectionId))).thenReturn(1);

        // Expect
        assertThrows(BusinessLogicException.class, () -> sectionService.restoreSection(sectionId));
        verify(sectionRepository, times(1)).fetchSectionTreeNodesWithDeleted(eq(sectionId));
        verify(sectionRepository, times(1)).existDeletedParentBySectionId(eq(sectionId));
        verifyNoMoreInteractions(sectionRepository);
        verifyNoInteractions(productRepository);
    }

    @Test
    public void restoreNotFoundSection() {
        // Given
        long sectionId = 1L;

        when(sectionRepository.fetchSectionTreeNodesWithDeleted(eq(sectionId))).thenReturn(Set.of());

        // Expect
        assertThrows(NotFoundException.class, () -> sectionService.restoreSection(sectionId));
        verify(sectionRepository, only()).fetchSectionTreeNodesWithDeleted(eq(sectionId));
        verifyNoInteractions(productRepository);
    }

    @Test
    public void updateSubsectionsWithAllExistedSections() {
        // Given
        long sectionId = 1L;
        Set<Long> subsectionIds = Set.of(10L, 11L, 12L);
        Section fetchedSection = makeSection(sectionId);

        Set<Section> desiredSubsections = subsectionIds.stream().map(this::makeSection).collect(Collectors.toSet());
        Set<Section> fetchedSubsections = new HashSet<>(desiredSubsections);
        fetchedSubsections.add(makeSection(13L));

        when(sectionRepository.fetchSectionsTreeNodes(eq(subsectionIds))).thenReturn(fetchedSubsections);
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.of(fetchedSection));

        // When
        sectionService.updateSubsections(sectionId, subsectionIds);

        // Then
        verify(sectionRepository, times(1)).fetchSectionsTreeNodes(eq(subsectionIds));
        verify(sectionRepository, times(1)).findById(eq(sectionId));
        verify(sectionRepository, times(1)).removeSectionsParent(eq(fetchedSection));
        verify(sectionRepository, times(1)).updateSectionsParent(eq(fetchedSection), eq(desiredSubsections));
        verifyNoMoreInteractions(sectionRepository);
    }

    @Test
    public void updateSubsectionsWithPossibleCircularProblem() {
        // Given
        long sectionId = 1L;
        Set<Long> subsectionIds = Set.of(10L, 11L, 12L);
        Section fetchedSection = makeSection(sectionId);

        Set<Section> fetchedSubsections = subsectionIds.stream().map(this::makeSection).collect(Collectors.toSet());
        fetchedSubsections.add(fetchedSection);

        when(sectionRepository.fetchSectionsTreeNodes(eq(subsectionIds))).thenReturn(fetchedSubsections);

        // Expect
        assertThrows(BusinessLogicException.class, () -> sectionService.updateSubsections(sectionId, subsectionIds));
        verify(sectionRepository, only()).fetchSectionsTreeNodes(eq(subsectionIds));
    }

    @Test
    public void updateSubsectionsWithNotFoundSection() {
        // Given
        long sectionId = 1L;
        Set<Long> subsectionIds = Set.of(10L, 11L, 12L);
        Set<Section> fetchedSubsections = subsectionIds.stream().map(this::makeSection).collect(Collectors.toSet());

        when(sectionRepository.fetchSectionsTreeNodes(eq(subsectionIds))).thenReturn(fetchedSubsections);
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.empty());

        // Expect
        NotFoundException expectedException =
                assertThrows(NotFoundException.class, () -> sectionService.updateSubsections(sectionId, subsectionIds));
        assertTrue(expectedException.getMessage().contains(Long.toString(sectionId)));

        verify(sectionRepository, times(1)).fetchSectionsTreeNodes(eq(subsectionIds));
        verify(sectionRepository, times(1)).findById(eq(sectionId));
        verifyNoMoreInteractions(sectionRepository);
    }

    @Test
    public void updateSubsectionsWithNotFoundSubsection() {
        // Given
        long sectionId = 1L;
        Section fetchedSection = makeSection(sectionId);
        Set<Long> subsectionIds = Set.of(10L, 11L, 12L);
        Set<Section> fetchedSubsections = Set.of(makeSection(10L), makeSection(11L));

        when(sectionRepository.fetchSectionsTreeNodes(eq(subsectionIds))).thenReturn(fetchedSubsections);
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.of(fetchedSection));

        // Expect
        NotFoundException expectedException =
                assertThrows(NotFoundException.class, () -> sectionService.updateSubsections(sectionId, subsectionIds));
        assertTrue(expectedException.getMessage().contains(Long.toString(12L)));

        verify(sectionRepository, times(1)).fetchSectionsTreeNodes(eq(subsectionIds));
        verify(sectionRepository, times(1)).findById(eq(sectionId));
        verifyNoMoreInteractions(sectionRepository);
    }

    @Test
    public void updateSubsectionsWithNotFoundSectionAndSubsection() {
        // Given
        long sectionId = 1L;
        Set<Long> subsectionIds = Set.of(10L, 11L, 12L);
        Set<Section> fetchedSubsections = Set.of(makeSection(10L), makeSection(11L));

        when(sectionRepository.fetchSectionsTreeNodes(eq(subsectionIds))).thenReturn(fetchedSubsections);
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.empty());

        // Expect
        NotFoundException expectedException =
                assertThrows(NotFoundException.class, () -> sectionService.updateSubsections(sectionId, subsectionIds));
        assertTrue(expectedException.getMessage().contains(Long.toString(sectionId)));
        assertTrue(expectedException.getMessage().contains(Long.toString(12L)));

        verify(sectionRepository, times(1)).fetchSectionsTreeNodes(eq(subsectionIds));
        verify(sectionRepository, times(1)).findById(eq(sectionId));
        verifyNoMoreInteractions(sectionRepository);
    }

    @Test
    public void removeSubsectionsWithExisted() {
        // Given
        long sectionId = 1L;
        Section fetchedSection = new Section();

        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.of(fetchedSection));

        // When
        sectionService.removeSubsections(sectionId);

        // Then
        verify(sectionRepository, times(1)).findById(eq(sectionId));
        verify(sectionRepository, times(1)).removeSectionsParent(eq(fetchedSection));
        verifyNoMoreInteractions(sectionRepository);
    }

    @Test
    public void removeSubsectionsWithNotFoundSection() {
        // Given
        long sectionId = 1L;
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> sectionService.removeSubsections(sectionId));
        verify(sectionRepository, only()).findById(eq(sectionId));
    }

    private Section makeSection(long id) {
        Section section = new Section();
        section.setId(id);
        return section;
    }
}