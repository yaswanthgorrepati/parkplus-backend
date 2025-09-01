package com.parkplus.common.service;

import com.parkplus.common.entities.MasterData;
import com.parkplus.common.repository.MasterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterDataService {

    @Autowired
    private MasterDataRepository masterDataRepository;

    public List<MasterData> getByKey(String key) {
        return masterDataRepository.findByKeyAndEnabledTrue(key);
    }

    public MasterData add(MasterData masterData) {
        return masterDataRepository.save(masterData);
    }

    public List<MasterData> getAll() {
        return masterDataRepository.findAll();
    }
}
