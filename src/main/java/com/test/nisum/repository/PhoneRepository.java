package com.test.nisum.repository;

import com.test.nisum.domain.entity.Phone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends CrudRepository<Phone, Long> {
    Optional<Phone> findByNumber(String number);
}
