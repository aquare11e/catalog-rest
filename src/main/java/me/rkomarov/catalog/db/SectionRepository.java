package me.rkomarov.catalog.db;

import me.rkomarov.catalog.db.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query(value = "SELECT * FROM section", nativeQuery = true)
    List<Section> fetchSectionsWithDeleted();

    @Query("SELECT DISTINCT s FROM Section s " +
            "LEFT JOIN FETCH s.subsections " +
            "LEFT JOIN FETCH s.products p ")
    Set<Section> fetchFullSections();

    @Query("SELECT DISTINCT s FROM Section s " +
            "LEFT JOIN FETCH s.subsections " +
            "LEFT JOIN FETCH s.products p " +
            "WHERE s IN ?1 ")
    Set<Section> fetchFullSections(Set<Section> sections);

    @Query(value = "WITH RECURSIVE sections AS (" +
            "SELECT s.* FROM section s " +
            "WHERE id IN ?1 " +
            "UNION " +
            "SELECT sc.* FROM section sc " +
            "INNER JOIN sections ss ON ss.id = sc.section_id " +
            ") " +
            "SELECT * FROM sections s " +
            "WHERE s.deleted = false",
            nativeQuery = true
    )
    Set<Section> fetchSectionsTreeNodes(Set<Long> ids);

    @Query(value = "WITH RECURSIVE sections AS (" +
            "SELECT s.* FROM section s " +
            "WHERE id = ?1 " +
            "UNION " +
            "SELECT sc.* FROM section sc " +
            "INNER JOIN sections ss ON ss.id = sc.section_id " +
            ") " +
            "SELECT * FROM sections",
            nativeQuery = true
    )
    Set<Section> fetchSectionTreeNodesWithDeleted(long id);

    @Query(value = "SELECT COUNT(*) FROM section s " +
            "LEFT JOIN section AS parent ON s.section_id = parent.id " +
            "WHERE parent.deleted = true AND s.id = ?1",
            nativeQuery = true)
    int existDeletedParentBySectionId(long id);

    @Modifying
    @Query("UPDATE Section s SET s.parent = NULL WHERE s.parent = ?1")
    void removeSectionsParent(Section parent);

    @Modifying
    @Query("UPDATE Section s SET s.parent = ?1 WHERE s IN ?2")
    void updateSectionsParent(Section parent, Set<Section> sections);

    @Modifying
    @Query("UPDATE Section s SET s.deleted = true WHERE s IN ?1")
    void softDelete(Set<Section> sections);

    @Modifying
    @Query("UPDATE Section s SET s.deleted = false WHERE s IN ?1")
    void restoreDeleted(Set<Section> sections);
}
