package space.shougat.blog.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "brand_id")
    private Long brandId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Product categoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Product authorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Product companyId(Long companyId) {
        this.companyId = companyId;
        return this;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Product brandId(Long brandId) {
        this.brandId = brandId;
        return this;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", categoryId=" + getCategoryId() +
            ", authorId=" + getAuthorId() +
            ", companyId=" + getCompanyId() +
            ", brandId=" + getBrandId() +
            "}";
    }
}
