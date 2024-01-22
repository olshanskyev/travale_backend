package net.travale.repository;

import net.travale.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DbUserRepository extends JpaRepository<User, Long> {
    List<User> findByUserName(String userName);
    List<User> findByPasswordResetToken(String resetToken);
}
