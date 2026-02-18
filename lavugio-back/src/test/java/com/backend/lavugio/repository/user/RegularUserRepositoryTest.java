package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.RegularUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RegularUserRepositoryTest {

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Test findByEmail - returns user when email exists")
    void testFindByEmail_ReturnsUserWhenExists() {
        RegularUser user = new RegularUser();
        user.setName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("+381641234567");
        user.setCanOrder(true);
        user.setBlocked(false);

        testEntityManager.persistAndFlush(user);
        testEntityManager.clear();

        Optional<RegularUser> result = regularUserRepository.findByEmail("john.doe@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@test.com");
        assertThat(result.get().isCanOrder()).isTrue();
    }

    @Test
    @DisplayName("Test findByEmail - returns empty when email does not exist")
    void testFindByEmail_ReturnsEmptyWhenNotExists() {
        Optional<RegularUser> result = regularUserRepository.findByEmail("nonexistent@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test findById - returns user when id exists")
    void testFindById_ReturnsUserWhenExists() {
        RegularUser user = new RegularUser();
        user.setName("Mike");
        user.setLastName("Johnson");
        user.setEmail("mike.johnson@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("+381649876543");
        user.setCanOrder(false);
        user.setBlocked(false);

        RegularUser savedUser = testEntityManager.persistAndFlush(user);
        testEntityManager.clear();

        Optional<RegularUser> result = regularUserRepository.findById(savedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedUser.getId());
        assertThat(result.get().getName()).isEqualTo("Mike");
        assertThat(result.get().getEmail()).isEqualTo("mike.johnson@test.com");
        assertThat(result.get().isCanOrder()).isFalse();
    }

    @Test
    @DisplayName("Test findById - returns empty when id does not exist")
    void testFindById_ReturnsEmptyWhenNotExists() {
        Optional<RegularUser> result = regularUserRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test existsByEmail - returns true when email exists")
    void testExistsByEmail_ReturnsTrueWhenExists() {
        RegularUser user = new RegularUser();
        user.setName("Anna");
        user.setLastName("Brown");
        user.setEmail("anna.brown@test.com");
        user.setPassword("password123");
        user.setCanOrder(true);
        user.setBlocked(false);

        testEntityManager.persistAndFlush(user);
        testEntityManager.clear();

        boolean exists = regularUserRepository.existsByEmail("anna.brown@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Test existsByEmail - returns false when email does not exist")
    void testExistsByEmail_ReturnsFalseWhenNotExists() {
        boolean exists = regularUserRepository.existsByEmail("nobody@test.com");

        assertThat(exists).isFalse();
    }
}
