package com.fable.mssg.resource.service.datasource.impl;

import com.fable.mssg.domain.dsmanager.Originalds;
import com.fable.mssg.resource.repository.datasource.OriginaldsRepository;
import com.fable.mssg.service.datasource.OriginaldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OriginaldsServiceImpl  implements OriginaldsService {

    @Autowired
    OriginaldsRepository originaldsRepository;
    //查找对应媒体源下的设备
    @Override
    public List<Originalds> findAllBymediadeviceid(String mediadeviceid) {
        return originaldsRepository.findAllByMediaDeviceId(mediadeviceid);
    }

    @Override
    public Originalds findOne(String id) {
        return originaldsRepository.findOne(id);
    }
}
