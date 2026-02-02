package com.estivate.test.query.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.estivate.query.Projection;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unit tests for {@link Projection#mapTo(Object, Object)} and {@link Projection#mapTo(Object, Class)}.
 */
public class ProjectionMapToTest {

    // ==================== TEST ENTITY CLASSES ====================

    /**
     * Simple entity class for testing source → target mapping.
     */
    @Data
    @NoArgsConstructor
    public static class PersonEntity {
        private Long id;
        private String name;
        private String email;
        private Integer age;
    }

    /**
     * Another entity for testing multi-entity scenarios.
     */
    @Data
    @NoArgsConstructor
    public static class AddressEntity {
        private Long id;
        private String street;
        private String city;
    }

    // ==================== TEST PROJECTION CLASSES ====================

    /**
     * Projection with @Attribute annotations pointing to PersonEntity.
     */
    @Data
    @NoArgsConstructor
    public static class PersonProjection {
        @Projection.Attribute(entity = PersonEntity.class, attribute = "id")
        private Long personId;

        @Projection.Attribute(entity = PersonEntity.class, attribute = "name")
        private String personName;

        @Projection.Attribute(entity = PersonEntity.class, attribute = "email")
        private String personEmail;
    }

    /**
     * Projection with fields pointing to multiple entities.
     */
    @Data
    @NoArgsConstructor
    public static class MultiEntityProjection {
        @Projection.Attribute(entity = PersonEntity.class, attribute = "id")
        private Long personId;

        @Projection.Attribute(entity = PersonEntity.class, attribute = "name")
        private String personName;

        @Projection.Attribute(entity = AddressEntity.class, attribute = "street")
        private String addressStreet;

        @Projection.Attribute(entity = AddressEntity.class, attribute = "city")
        private String addressCity;
    }

    /**
     * Projection with some fields without annotations.
     */
    @Data
    @NoArgsConstructor
    public static class MixedProjection {
        @Projection.Attribute(entity = PersonEntity.class, attribute = "name")
        private String name;

        // No annotation - should be ignored
        private String unmappedField;

        @Projection.Attribute(entity = PersonEntity.class, attribute = "age")
        private Integer age;
    }

    /**
     * Target class that pulls values from source using @Attribute annotations.
     */
    @Data
    @NoArgsConstructor
    public static class TargetWithAnnotations {
        @Projection.Attribute(entity = PersonEntity.class, attribute = "name")
        private String pulledName;

        @Projection.Attribute(entity = PersonEntity.class, attribute = "email")
        private String pulledEmail;
    }

    /**
     * Projection with alias in annotation.
     */
    @Data
    @NoArgsConstructor
    public static class ProjectionWithAlias {
        @Projection.Attribute(entity = PersonEntity.class, attribute = "name", alias = "personAlias")
        private String aliasedName;
    }

    /**
     * Entity without no-arg constructor for testing error case.
     */
    public static class EntityWithoutNoArgConstructor {
        private String value;

        public EntityWithoutNoArgConstructor(String value) {
            this.value = value;
        }
    }

    /**
     * Empty projection with no @Attribute fields.
     */
    @Data
    @NoArgsConstructor
    public static class EmptyProjection {
        private String unmapped;
    }

    /**
     * Projection with attribute pointing to non-existent field.
     */
    @Data
    @NoArgsConstructor
    public static class ProjectionWithNonExistingAttribute {
        @Projection.Attribute(entity = PersonEntity.class, attribute = "nonExistentField")
        private String someValue;
    }

    // ==================== SOURCE → TARGET TESTS ====================

    @Test
    public void testMapTo_SourceToTarget_BasicMapping() {
        // Arrange
        PersonProjection source = new PersonProjection();
        source.setPersonId(1L);
        source.setPersonName("John Doe");
        source.setPersonEmail("john@example.com");

        PersonEntity target = new PersonEntity();

        // Act
        PersonEntity result = Projection.mapTo(source, target);

        // Assert
        assertNotNull(result);
        assertEquals(target, result, "Should return the same target instance");
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void testMapTo_SourceToTarget_PartialMapping() {
        // Arrange - projection only has name, not all fields
        MixedProjection source = new MixedProjection();
        source.setName("Jane Doe");
        source.setAge(30);
        source.setUnmappedField("this should not be mapped");

        PersonEntity target = new PersonEntity();
        target.setId(99L); // Pre-existing value should remain
        target.setEmail("pre-existing@example.com"); // Pre-existing value should remain

        // Act
        Projection.mapTo(source, target);

        // Assert
        assertEquals(99L, target.getId(), "Pre-existing id should remain");
        assertEquals("Jane Doe", target.getName(), "Name should be mapped");
        assertEquals("pre-existing@example.com", target.getEmail(), "Pre-existing email should remain");
        assertEquals(30, target.getAge(), "Age should be mapped");
    }

    @Test
    public void testMapTo_SourceToTarget_OnlyMatchingEntityMapped() {
        // Arrange - projection has fields for both PersonEntity and AddressEntity
        MultiEntityProjection source = new MultiEntityProjection();
        source.setPersonId(1L);
        source.setPersonName("John Doe");
        source.setAddressStreet("123 Main St");
        source.setAddressCity("Springfield");

        PersonEntity personTarget = new PersonEntity();

        // Act - map to PersonEntity
        Projection.mapTo(source, personTarget);

        // Assert - only PersonEntity fields should be mapped
        assertEquals(1L, personTarget.getId());
        assertEquals("John Doe", personTarget.getName());
        assertNull(personTarget.getEmail(), "Email should remain null (not in projection)");
    }

    @Test
    public void testMapTo_SourceToTarget_AddressEntityMapping() {
        // Arrange
        MultiEntityProjection source = new MultiEntityProjection();
        source.setPersonId(1L);
        source.setPersonName("John Doe");
        source.setAddressStreet("123 Main St");
        source.setAddressCity("Springfield");

        AddressEntity addressTarget = new AddressEntity();

        // Act - map to AddressEntity
        Projection.mapTo(source, addressTarget);

        // Assert - only AddressEntity fields should be mapped
        assertNull(addressTarget.getId(), "Id should remain null (not in projection for AddressEntity)");
        assertEquals("123 Main St", addressTarget.getStreet());
        assertEquals("Springfield", addressTarget.getCity());
    }

    // ==================== TARGET PULLS FROM SOURCE TESTS ====================

    @Test
    public void testMapTo_TargetPullsFromSource() {
        // Arrange - source has @Attribute annotations for PersonEntity
        PersonProjection source = new PersonProjection();
        source.setPersonId(1L);
        source.setPersonName("Alice");
        source.setPersonEmail("alice@example.com");

        // Target also has @Attribute annotations that match
        TargetWithAnnotations target = new TargetWithAnnotations();

        // Act
        Projection.mapTo(source, target);

        // Assert - target should pull values from source
        assertEquals("Alice", target.getPulledName());
        assertEquals("alice@example.com", target.getPulledEmail());
    }

    // ==================== NULL HANDLING TESTS ====================

    @Test
    public void testMapTo_NullSource_ReturnsNull() {
        PersonEntity target = new PersonEntity();

        PersonEntity result = Projection.mapTo(null, target);

        assertNull(result);
    }

    @Test
    public void testMapTo_NullTarget_ReturnsNull() {
        PersonProjection source = new PersonProjection();
        source.setPersonName("Test");

        PersonEntity result = Projection.mapTo(source, (PersonEntity) null);

        assertNull(result);
    }

    @Test
    public void testMapTo_BothNull_ReturnsNull() {
        PersonEntity result = Projection.mapTo(null, (PersonEntity) null);

        assertNull(result);
    }

    @Test
    public void testMapTo_NullValuesInSource_MapsNullValues() {
        // Arrange
        PersonProjection source = new PersonProjection();
        source.setPersonId(1L);
        source.setPersonName(null); // Explicitly null
        source.setPersonEmail("test@example.com");

        PersonEntity target = new PersonEntity();
        target.setName("Pre-existing Name");

        // Act
        Projection.mapTo(source, target);

        // Assert
        assertEquals(1L, target.getId());
        assertNull(target.getName(), "Null value from source should overwrite target");
        assertEquals("test@example.com", target.getEmail());
    }

    // ==================== CLASS OVERLOAD TESTS ====================

    @Test
    public void testMapTo_WithClass_CreatesNewInstance() {
        // Arrange
        PersonProjection source = new PersonProjection();
        source.setPersonId(42L);
        source.setPersonName("Bob");
        source.setPersonEmail("bob@example.com");

        // Act
        PersonEntity result = Projection.mapTo(source, PersonEntity.class);

        // Assert
        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals("Bob", result.getName());
        assertEquals("bob@example.com", result.getEmail());
    }

    @Test
    public void testMapTo_WithClass_NullSource_ReturnsNull() {
        PersonEntity result = Projection.mapTo(null, PersonEntity.class);

        assertNull(result);
    }

    @Test
    public void testMapTo_WithClass_NullClass_ReturnsNull() {
        PersonProjection source = new PersonProjection();

        Object result = Projection.mapTo(source, (Class<?>) null);

        assertNull(result);
    }

    @Test
    public void testMapTo_WithClass_NoArgConstructorRequired() {
        // Arrange
        PersonProjection source = new PersonProjection();
        source.setPersonName("Test");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            Projection.mapTo(source, EntityWithoutNoArgConstructor.class);
        });
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    public void testMapTo_EmptyProjection_NoChanges() {
        // Arrange
        EmptyProjection source = new EmptyProjection();
        source.setUnmapped("value");

        PersonEntity target = new PersonEntity();
        target.setId(100L);
        target.setName("Original");

        // Act
        Projection.mapTo(source, target);

        // Assert - target should remain unchanged
        assertEquals(100L, target.getId());
        assertEquals("Original", target.getName());
    }

    @Test
    public void testMapTo_NonMatchingAttribute_Ignored() {
        // Arrange - projection points to attribute that doesn't exist in target
        ProjectionWithNonExistingAttribute source = new ProjectionWithNonExistingAttribute();
        source.setSomeValue("test");

        PersonEntity target = new PersonEntity();

        // Act - should not throw, just ignore the non-matching field
        Projection.mapTo(source, target);

        // Assert - target should remain unchanged
        assertNull(target.getId());
        assertNull(target.getName());
    }

    @Test
    public void testMapTo_SameObjectTypes_BothDirections() {
        // Test case where source and target are the same type with @Attribute annotations
        // This exercises both mapping directions

        PersonProjection source = new PersonProjection();
        source.setPersonId(1L);
        source.setPersonName("Source Name");
        source.setPersonEmail("source@example.com");

        PersonProjection target = new PersonProjection();
        target.setPersonId(2L);

        // Act
        Projection.mapTo(source, target);

        // Assert - both directions should work, target pulls from source
        assertEquals("Source Name", target.getPersonName());
        assertEquals("source@example.com", target.getPersonEmail());
    }

    @Test
    public void testMapTo_PreservesUnmappedTargetFields() {
        // Arrange
        PersonProjection source = new PersonProjection();
        source.setPersonId(1L);
        source.setPersonName("New Name");
        // Note: no email set

        PersonEntity target = new PersonEntity();
        target.setAge(25); // Field not in projection

        // Act
        Projection.mapTo(source, target);

        // Assert
        assertEquals(1L, target.getId());
        assertEquals("New Name", target.getName());
        assertEquals(25, target.getAge(), "Unmapped target field should be preserved");
    }
}
