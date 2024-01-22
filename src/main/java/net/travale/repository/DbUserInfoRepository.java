package net.travale.repository;

import net.travale.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DbUserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUserUserName(String userName);
}
