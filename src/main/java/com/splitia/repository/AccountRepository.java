package com.splitia.repository;

import com.splitia.model.enums.AccountType;
import com.splitia.model.finance.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a WHERE a.code = :code AND a.deletedAt IS NULL")
    Optional<Account> findByCode(@Param("code") String code);
    
    @Query("SELECT a FROM Account a WHERE a.type = :type AND a.deletedAt IS NULL ORDER BY a.code")
    List<Account> findByType(@Param("type") AccountType type);
    
    @Query("SELECT a FROM Account a WHERE a.parentAccount IS NULL AND a.deletedAt IS NULL ORDER BY a.code")
    List<Account> findRootAccounts();
    
    @Query("SELECT a FROM Account a WHERE a.parentAccount.id = :parentId AND a.deletedAt IS NULL ORDER BY a.code")
    List<Account> findByParentAccountId(@Param("parentId") UUID parentId);
    
    @Query("SELECT a FROM Account a WHERE a.isActive = true AND a.deletedAt IS NULL ORDER BY a.code")
    List<Account> findAllActive();
}

