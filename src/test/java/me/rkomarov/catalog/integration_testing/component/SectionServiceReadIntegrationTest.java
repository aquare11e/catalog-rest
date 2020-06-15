package me.rkomarov.catalog.integration_testing.component;

import me.rkomarov.catalog.contoller.dto.SectionDetailsResponseDto;
import me.rkomarov.catalog.contoller.dto.SectionResponseDto;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.exception.NotFoundException;
import me.rkomarov.catalog.service.SectionService;
import me.rkomarov.catalog.testutils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SectionServiceReadIntegrationTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private TestUtils testUtils;

    private Section activeSection;
    private Section deletedSection;

    @BeforeAll
    @Transactional
    public void initSections() {
        Section section1 = new Section();
        section1.setTitle("1");
        section1.setDeleted(false);

        Section section1_1 = new Section();
        section1_1.setTitle("1.1");
        section1_1.setDeleted(false);
        section1.addSubsection(section1_1);

        Section section2 = new Section();
        section2.setTitle("2");
        section2.setDeleted(false);

        Section section2_1 = new Section();
        section2_1.setTitle("2.1");
        section2_1.setDeleted(false);
        section2.addSubsection(section2_1);

        Section section2_2 = new Section();
        section2_2.setTitle("2.2");
        section2_2.setDeleted(false);
        section2.addSubsection(section2_2);

        Section section3 = new Section();
        section3.setTitle("3");
        section3.setDeleted(true);

        activeSection = sectionRepository.save(section1);
        sectionRepository.save(section2);
        deletedSection = sectionRepository.save(section3);
    }

    @AfterAll
    public void clearTables() {
        testUtils.clearTables();
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllSectionTreeTest() {
        List<SectionDetailsResponseDto> sectionTree = sectionService.getAllSectionTree();

        assertEquals(2, sectionTree.size());
        for (SectionDetailsResponseDto element : sectionTree) {
            assertTrue(Set.of("1", "2").contains(element.getTitle()));
            element.getSubsections().forEach(
                    subsection -> assertTrue(subsection.getTitle().startsWith(element.getTitle() + "."))
            );
        }
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllSectionsTest() {
        List<SectionResponseDto> active = sectionService.getAllSections(false);
        List<SectionResponseDto> deleted = sectionService.getAllSections(true);

        assertEquals(5, active.size());
        assertEquals(6, deleted.size());
    }

    @Test
    @Transactional(readOnly = true)
    public void getSectionTreeTest() {
        SectionDetailsResponseDto receivedDto = sectionService.getSectionTree(activeSection.getId());
        assertNotNull(receivedDto);
        assertEquals(activeSection.getTitle(), receivedDto.getTitle());
        assertEquals(
                activeSection.getSubsections().stream().map(Section::getTitle).collect(Collectors.toList()),
                receivedDto.getSubsections().stream().map(SectionDetailsResponseDto::getTitle).collect(Collectors.toList())
        );
    }

    @Test
    @Transactional(readOnly = true)
    public void getActiveSectionTest() {
        SectionResponseDto receivedDto = sectionService.getSection(activeSection.getId());
        assertNotNull(receivedDto);
        assertEquals(activeSection.getTitle(), receivedDto.getTitle());
    }

    @Test
    @Transactional(readOnly = true)
    public void getDeletedSectionTest() {
        assertThrows(NotFoundException.class, () -> sectionService.getSection(deletedSection.getId()));
    }
}

