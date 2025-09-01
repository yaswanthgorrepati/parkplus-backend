package com.parkplus.common.repository;

import com.parkplus.common.entities.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterDataRepository extends JpaRepository<MasterData, Long> {
    List<MasterData> findByKeyAndEnabledTrue(String key);
}
