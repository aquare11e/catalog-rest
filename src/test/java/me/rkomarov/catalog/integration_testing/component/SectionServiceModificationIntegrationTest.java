package me.rkomarov.catalog.integration_testing.component;

import me.rkomarov.catalog.controller.dto.SectionDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.SectionRequestDto;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.service.SectionService;
import me.rkomarov.catalog.testutils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-testing")
public class SectionServiceModificationIntegrationTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public void clearTables() {
        testUtils.clearTables();
    }

    @Test
    @Transactional
    public void creationTest() {
        // Given
        SectionRequestDto sectionRequest = new SectionRequestDto().setTitle("Test");

        // When
        SectionDetailsResponseDto sectionResponse = sectionService.createSection(sectionRequest);

        // Then
        Optional<Section> sectionOptional = sectionRepository.findById(sectionResponse.getId());
        assertTrue(sectionOptional.isPresent());
        Section section = sectionOptional.get();

        assertEquals(sectionRequest.getTitle(), section.getTitle());
        assertEquals(sectionResponse.getId(), section.getId());
        assertEquals(sectionResponse.isDeleted(), section.isDeleted());
    }
    
    @Test
    @Transactional
    public void updateTest() {
        // Given
        Section transientSection = new Section().setTitle("Title");
        Section section = sectionRepository.saveAndFlush(transientSection);

        SectionRequestDto sectionRequest = new SectionRequestDto().setTitle("New_Title");

        // When
        sectionService.updateSection(section.getId(), sectionRequest);

        // Then
        Optional<Section> sectionOptional = sectionRepository.findById(section.getId());
        assertTrue(sectionOptional.isPresent());
        Section actualSection = sectionOptional.get();

        assertEquals(sectionRequest.getTitle(), actualSection.getTitle());
        assertEquals(section.getId(), actualSection.getId());
        assertEquals(section.isDeleted(), actualSection.isDeleted());
        assertEquals(section.getSubsections(), actualSection.getSubsections());
        assertEquals(section.getProducts(), actualSection.getProducts());
        assertEquals(section.getParent(), actualSection.getParent());
    }

    @Test
    @Transactional
    public void deleteSectionTest() {
        // Given
        Section transientSection = new Section().setTitle("Title");
        Section section = sectionRepository.saveAndFlush(transientSection);

        // When
        sectionService.deleteSection(section.getId());
        entityManager.clear();

        // Then
        Optional<Section> sectionFromRepository = sectionRepository.findById(section.getId());
        assertFalse(sectionFromRepository.isPresent());

        Section sectionFromNativeQuery = ((Section) entityManager
                .createNativeQuery("SELECT * FROM section WHERE id = " + section.getId(), Section.class)
                .getSingleResult());
        assertNotNull(sectionFromNativeQuery);
        assertEquals(section.getId(), sectionFromNativeQuery.getId());
        assertTrue(sectionFromNativeQuery.isDeleted());
    }

    @Test
    @Transactional
    public void restoreSectionTest() {
        // Given
        Section transientSection = new Section().setTitle("Title").setDeleted(true);
        Section section = sectionRepository.saveAndFlush(transientSection);
        entityManager.clear();

        Optional<Section> deletedSectionOptional = sectionRepository.findById(section.getId());
        assertFalse(deletedSectionOptional.isPresent());

        // When
        sectionService.restoreSection(section.getId());
        entityManager.clear();

        // Then
        Optional<Section> presentedSectionOptional = sectionRepository.findById(section.getId());
        assertTrue(presentedSectionOptional.isPresent());
        assertEquals(section.getTitle(), presentedSectionOptional.get().getTitle());
        assertFalse(presentedSectionOptional.get().isDeleted());
    }

    @Test
    @Transactional
    public void updateSubsectionsTest() {
        // Given
        Section transientSection = new Section().setTitle("Title");
        Section transientSubsection1 = new Section().setTitle("Sub1");
        Section transientSubsection2 = new Section().setTitle("Sub2");
        Section savedSection = sectionRepository.save(transientSection);
        Section savedSubsection1 = sectionRepository.save(transientSubsection1);
        Section savedSubsection2 = sectionRepository.save(transientSubsection2);

        // When
        sectionService.updateSubsections(savedSection.getId(), Set.of(savedSubsection1.getId(), savedSubsection2.getId()));
        entityManager.clear();

        // Then
        Optional<Section> sectionOptional = sectionRepository.findById(savedSection.getId());
        assertTrue(sectionOptional.isPresent());

        Section section = sectionOptional.get();
        assertEquals(savedSection.getTitle(), section.getTitle());
        assertEquals(2, section.getSubsections().size());

        Set<String> subsectionTitles = Set.of(savedSubsection1.getTitle(), savedSubsection2.getTitle());
        savedSection.getSubsections().forEach(
                subsection -> assertTrue(subsectionTitles.contains(subsection.getTitle()))
        );
    }

    @Test
    @Transactional
    public void removeSubsectionsTest() {
        // Given
        Section transientSection = new Section().setTitle("Title");
        Section transientSubsection1 = new Section().setTitle("Sub1");
        Section transientSubsection2 = new Section().setTitle("Sub2");
        transientSection.addSubsection(transientSubsection1);
        transientSection.addSubsection(transientSubsection2);
        Section savedSection = sectionRepository.save(transientSection);

        Optional<Section> sectionOptional = sectionRepository.findById(savedSection.getId());
        assertTrue(sectionOptional.isPresent());
        assertEquals(2, sectionOptional.get().getSubsections().size());

        // When
        sectionService.removeSubsections(savedSection.getId());
        entityManager.clear();

        // Then
        Optional<Section> actualSectionOptional = sectionRepository.findById(savedSection.getId());
        assertTrue(actualSectionOptional.isPresent());

        Section section = actualSectionOptional.get();
        assertEquals(savedSection.getTitle(), section.getTitle());
        assertTrue(section.getSubsections().isEmpty());
    }
}
