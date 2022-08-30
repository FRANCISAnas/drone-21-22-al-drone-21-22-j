package com.aldrone2122.project.jolydrone.repositories;

import com.aldrone2122.project.jolydrone.entity.SavedPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PackageDeliveryRepository extends JpaRepository<SavedPackage, Long> {

    Optional<SavedPackage> findByPackageId(long id);

    @Transactional
    void deleteByPackageId(long id);


    Optional<SavedPackage> findTopByOrderByTimeStamp();
}

