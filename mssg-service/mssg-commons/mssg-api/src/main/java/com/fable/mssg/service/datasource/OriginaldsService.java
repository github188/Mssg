package com.fable.mssg.service.datasource;


import com.fable.mssg.domain.dsmanager.Originalds;

import java.util.List;

public interface OriginaldsService {

    public List<Originalds> findAllBymediadeviceid(String mediadeviceid);

    Originalds findOne(String id);

}
