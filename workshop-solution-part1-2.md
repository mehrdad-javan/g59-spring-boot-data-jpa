# Entities and Repositories

This document provides a summary of all the JPA entities and their corresponding repository interfaces used in the E-commerce Workshop project.

## 1. Customer Management

### Customer
- **Entity**: `Customer`
- **Repository**: `CustomerRepository`
- **Description**: Represents a registered customer in the system. It stores basic information such as first name, last name, and email. Each customer is linked to an `Address` and a `UserProfile`.
- **Key Features**: 
  - Unique email constraint.
  - Automatically tracks creation time using `@PrePersist`.
  - Cascades lifecycle operations to `Address` and `UserProfile`.

#### Entity Code: `Customer.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity // JPA entity
@Table(
        name = "customers" // Table name
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString (no relationships)
public class Customer {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100) // Required, max length 100
    @ToString.Include
    private String firstName;

    @Column(nullable = false, length = 100) // Required, max length 100
    @ToString.Include
    private String lastName;

    @Column(nullable = false, length = 150, unique = true) // Required & unique
    @ToString.Include
    private String email;

    @ToString.Include
    private Instant createdAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // Customer owns Address lifecycle
    @JoinColumn(name = "address_id", nullable = false) // FK column customers.address_id
    private Address address; // Not included in toString/equals

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // Customer owns Profile lifecycle
    @JoinColumn(name = "profile_id") // FK column customers.profile_id (optional)
    private UserProfile profile; // Not included in toString/equals

    // Keeps the bidirectional relation consistent (recommended)
    public void setProfile(UserProfile profile) {
        // Break old link
        if (this.profile != null) {
            this.profile.setCustomer(null);
        }

        // Set new link
        this.profile = profile;

        // Maintain inverse side
        if (profile != null) {
            profile.setCustomer(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
```

#### Repository Code: `CustomerRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.ecommerceworkshop.entity.Customer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Required
    Optional<Customer> findByEmailIgnoreCase(String email);

    List<Customer> findByLastNameIgnoreCase(String lastName);

    // "customers living in a specific city" via address
    List<Customer> findByAddress_CityIgnoreCase(String city);

    // Optional / Advanced
    List<Customer> findByEmailContainingIgnoreCase(String keyword);

    List<Customer> findByCreatedAtAfter(Instant dateTime);

    List<Customer> findByCreatedAtBetween(Instant start, Instant end);

    long countByAddress_CityIgnoreCase(String city);

    boolean existsByEmailIgnoreCase(String email);
}
```

### UserProfile
- **Entity**: `UserProfile`
- **Repository**: `UserProfileRepository`
- **Description**: Stores extended information about a customer, such as a nickname, bio, and phone number. It has a one-to-one relationship with the `Customer`.

#### Entity Code: `UserProfile.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // JPA entity
@Table(name = "user_profiles") // Maps entity to "user_profiles" table

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString
public class UserProfile {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100) // Required with max length
    @ToString.Include
    private String nickname;

    @Column(nullable = false, length = 100) // Required with max length
    @ToString.Include
    private String phoneNumber;

    @Column(length = 500) // Optional, max length 500
    @ToString.Include
    private String bio;

    @OneToOne(mappedBy = "profile") // Inverse side (FK lives on Customer.profile_id)
    private Customer customer; // Not in toString/equals to avoid recursion
}
```

#### Repository Code: `UserProfileRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.ecommerceworkshop.entity.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Required
    Optional<UserProfile> findByNicknameIgnoreCase(String nickname);

    List<UserProfile> findByPhoneNumberContaining(String partialPhone);

    // Optional / Advanced
    List<UserProfile> findByBioIsNotNull();

    List<UserProfile> findByNicknameStartingWithIgnoreCase(String prefix);
}
```

### Address
- **Entity**: `Address`
- **Repository**: `AddressRepository`
- **Description**: Stores physical address details like street, city, and zip code. It is associated with a `Customer`.

#### Entity Code: `Address.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // Marks this class as a JPA entity
@Table(name = "addresses") // Maps entity to "addresses" table

@Getter // Generates getters for all fields
@Setter // Generates setters for all fields
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only included fields participate in equals/hashCode (safe for JPA)
@ToString(onlyExplicitlyIncluded = true) // Only included fields appear in toString (prevents recursion)
public class Address {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated by DB
    @EqualsAndHashCode.Include // Use id for equality when available
    @ToString.Include // Show id in toString
    private Long id;

    @Column(nullable = false) // Required column
    @ToString.Include // Safe scalar field in toString
    private String street;

    @Column(nullable = false) // Required column
    @ToString.Include
    private String city;

    @Column(nullable = false) // Required column
    @ToString.Include
    private String zipCode;
}
```

#### Repository Code: `AddressRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.ecommerceworkshop.entity.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Required
    List<Address> findByZipCode(String zipCode);

    // Optional / Advanced
    List<Address> findByCityIgnoreCase(String city);

    List<Address> findByStreetContainingIgnoreCase(String streetKeyword);

    List<Address> findByZipCodeStartingWith(String prefix);
}
```

## 2. Catalog Management

### Product
- **Entity**: `Product`
- **Repository**: `ProductRepository`
- **Description**: Represents an item available for sale. It includes details like name, price, and a list of image URLs.
- **Relationships**:
  - Belongs to a `Category`.
  - Can be associated with multiple `Promotion` objects.

#### Entity Code: `Product.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity // JPA entity
@Table(name = "products") // Maps to "products" table

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString (no relationships)
public class Product {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 200) // Required
    @ToString.Include
    private String name;

    @Column(nullable = false, precision = 19, scale = 2) // Recommended precision for money
    @ToString.Include
    private BigDecimal price;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 500)
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) // Many products belong to one category (avoid default EAGER)
    @JoinColumn(name = "category_id", nullable = false) // FK products.category_id
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY) // Many-to-many between products and promotions
    @JoinTable(
            name = "products_promotions", // Join table name
            joinColumns = @JoinColumn(name = "product_id"), // FK to product
            inverseJoinColumns = @JoinColumn(name = "promotion_id") // FK to promotion
    )
    private Set<Promotion> promotions = new HashSet<>();

    // Convenience helpers to keep both sides in sync (recommended if bidirectional)
    public void addPromotion(Promotion promotion) {
        promotions.add(promotion);
        //promotion.getProducts().add(this);
    }

    public void removePromotion(Promotion promotion) {
        promotions.remove(promotion);
        //promotion.getProducts().remove(this);
    }
}
```

#### Repository Code: `ProductRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.ecommerceworkshop.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Required
    List<Product> findByCategory_NameIgnoreCase(String categoryName);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    // Optional / Advanced
    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByPriceLessThan(BigDecimal max);

    List<Product> findByCategory_Id(Long categoryId);

    long countByCategory_NameIgnoreCase(String categoryName);
}
```

### Category
- **Entity**: `Category`
- **Repository**: `CategoryRepository`
- **Description**: Used to group products into logical sections (e.g., "Electronics", "Books").

#### Entity Code: `Category.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity // JPA entity
@Table(name = "categories") // Maps to "categories" table

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString
public class Category {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100) // Required
    @ToString.Include
    private String name;
}
```

#### Repository Code: `CategoryRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.ecommerceworkshop.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Required
    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    // Optional / Advanced
    List<Category> findByNameContainingIgnoreCase(String keyword);

    long countBy();
}
```

### Promotion
- **Entity**: `Promotion`
- **Repository**: `PromotionRepository`
- **Description**: Represents discounts or special offers. A promotion can have a name, description, and an active date range. It can be applied to multiple products.

#### Entity Code: `Promotion.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity // JPA entity
@Table(name = "promotions") // Maps to "promotions" table

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString
public class Promotion {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // Promotion code
    @ToString.Include
    private String code;

    @Column(nullable = false) // Start date required
    @ToString.Include
    private LocalDate startDate;

    @Column // End date optional (can be null for open-ended)
    @ToString.Include
    private LocalDate endDate;
}
```

#### Repository Code: `PromotionRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.lexicon.ecommerceworkshop.entity.Promotion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Optional but useful
    Optional<Promotion> findByCodeIgnoreCase(String code);

    // Required: active on date
    @Query("""
           select p from Promotion p
           where p.startDate <= :date
             and (p.endDate is null or p.endDate >= :date)
           """)
    List<Promotion> findActiveOn(@Param("date") LocalDate date);

    // Optional / Advanced
    List<Promotion> findByStartDateAfter(LocalDate date);

    List<Promotion> findByEndDateBefore(LocalDate date);

    List<Promotion> findByEndDateIsNull();
}
```

## 3. Order & Transactional Management

### Order
- **Entity**: `Order`
- **Repository**: `OrderRepository`
- **Description**: Represents a completed purchase placed by a customer. It records the order date, total amount, and the current `OrderStatus`.
- **Relationships**:
  - Belongs to a single `Customer`.
  - Contains multiple `OrderItem` records.

#### Entity Code: `Order.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA entity
@Table(name = "orders") // Maps to "orders" table

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString
public class Order {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false) // Required order date
    @ToString.Include
    private Instant orderDate;

    @Enumerated(EnumType.STRING) // Store enum as readable string (not ordinal)
    @Column(nullable = false, length = 30)
    @ToString.Include
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY) // Many orders belong to one customer
    @JoinColumn(name = "customer_id", nullable = false) // FK orders.customer_id
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Order owns items lifecycle
    private List<OrderItem> items = new ArrayList<>();

    // Convenience helpers keep bidirectional relation consistent
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    @PrePersist
    public void prePersist() {
        this.orderDate = Instant.now();
    }
}
```

#### Repository Code: `OrderRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.lexicon.ecommerceworkshop.entity.Order;
import se.lexicon.ecommerceworkshop.entity.OrderStatus;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer_Id(Long customerId);

    @Query("select distinct o from Order o left join fetch o.items where o.status = :status")
    List<Order> findByStatusWithItems(@Param("status") OrderStatus status);
}
```

### OrderItem
- **Entity**: `OrderItem`
- **Repository**: `OrderItemRepository`
- **Description**: Represents a specific product line item within an `Order`. It captures the price and quantity of the product at the time of purchase.

#### Entity Code: `OrderItem.java`
```java
package se.lexicon.ecommerceworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity // JPA entity
@Table(name = "order_items") // Maps to "order_items" table

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safe equals/hashCode
@ToString(onlyExplicitlyIncluded = true) // Safe toString
public class OrderItem {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false) // Required quantity
    @ToString.Include
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2) // Snapshot price at time of purchase (recommended BigDecimal)
    @ToString.Include
    private BigDecimal priceAtPurchase;

    @ManyToOne(fetch = FetchType.LAZY) // Many order items belong to one order
    @JoinColumn(name = "order_id", nullable = false) // FK order_items.order_id
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY) // Many order items reference one product
    @JoinColumn(name = "product_id", nullable = false) // FK order_items.product_id
    private Product product;
}
```

#### Repository Code: `OrderItemRepository.java`
```java
package se.lexicon.ecommerceworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.ecommerceworkshop.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Optional / Advanced
    List<OrderItem> findByOrder_Id(Long orderId);

    List<OrderItem> findByProduct_Id(Long productId);

    List<OrderItem> findByQuantityGreaterThan(Integer quantity);
}
```

---

## Order Status (Enum)
While not an entity itself, the **`OrderStatus`** enum defines the possible states of an `Order`:
- `CREATED`: The order has been created.
- `PAID`: The order has been paid for.
- `SHIPPED`: The order has been shipped.
- `CANCELLED`: The order has been cancelled.

#### Enum Code: `OrderStatus.java`
```java
package se.lexicon.ecommerceworkshop.entity;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    CANCELLED
}
```
