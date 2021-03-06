package com.rengu.machinereadingcomprehension.Repository;

import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    Optional<List<UserEntity>> findByEnabled(boolean enabled);

    Optional<UserEntity> findByTeamName(String teamName);

    Optional<UserEntity> findByTelephoneNumber(String telephoneNumber);

    List<UserEntity> findByTeamNameNotNull(Sort sort);
}
