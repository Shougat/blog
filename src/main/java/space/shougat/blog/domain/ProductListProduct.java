package space.shougat.blog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A ProductListProduct.
 */
@Entity
@Table(name = "product_list_product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productlistproduct")
public class ProductListProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = "productListProducts", allowSetters = true)
    private ProductList productList;

    @ManyToOne
    @JsonIgnoreProperties(value = "productListProducts", allowSetters = true)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductList getProductList() {
        return productList;
    }

    public ProductListProduct productList(ProductList productList) {
        this.productList = productList;
        return this;
    }

    public void setProductList(ProductList productList) {
        this.productList = productList;
    }

    public Product getProduct() {
        return product;
    }

    public ProductListProduct product(Product product) {
        this.product = product;
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductListProduct)) {
            return false;
        }
        return id != null && id.equals(((ProductListProduct) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductListProduct{" +
            "id=" + getId() +
            "}";
    }
}
