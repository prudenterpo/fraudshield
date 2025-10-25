package pdo.fraudshield.repository;

import pdo.fraudshield.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByDocument(String document);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.riskProfile = :riskProfile")
    List<Customer> findByRiskProfile(Customer.CustomerRiskProfile riskProfile);
}