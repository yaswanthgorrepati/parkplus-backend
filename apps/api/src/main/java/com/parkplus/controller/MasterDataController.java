package com.parkplus.controller;


import com.parkplus.common.entities.MasterData;
import com.parkplus.common.service.MasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master")
public class MasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    // Public: frontend fetches dropdown values
    @GetMapping("/{key}")
    public List<MasterData> get(@PathVariable(name = "key") String key) {
        return masterDataService.getByKey(key);
    }

    // Admin: add new type
    @PostMapping
    public MasterData add(@RequestBody MasterData masterData) {
        return masterDataService.add(masterData);
    }

    @GetMapping
    public List<MasterData> getAll() {
        return masterDataService.getAll();
    }
}
