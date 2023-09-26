package com.garden.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUserName(String user_name);

    final String queryDeleteUser = "DELETE FROM users WHERE user_name=?";

    @Modifying
    @Transactional
    @Query(value = queryDeleteUser, nativeQuery = true)
    public void deleteByUserName(String userName);

}
