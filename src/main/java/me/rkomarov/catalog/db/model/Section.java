package me.rkomarov.catalog.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "section")
@Where(clause = "deleted = false")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "deleted")
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section parent;

    @OneToMany(mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Section> subsections = new HashSet<>();

    @OneToMany(mappedBy = "section", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Product> products = new HashSet<>();

    public void addSubsection(Section subsection) {
        this.subsections.add(subsection);
        subsection.setParent(this);
    }

    public void addProduct(Product product) {
        this.products.add(product);
        product.setSection(this);
    }
}
