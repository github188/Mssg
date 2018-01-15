package com.fable.mssg.datasource.converter;

import com.fable.mssg.vo.datasource.VDataSource;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @author stormning 2017/8/21
 * @since 1.3.0
 */
@Service
public class DataSourceConverter extends RepoBasedConverter<DataSource, VDataSource, String> {
    @Override
    protected VDataSource internalConvert(DataSource source) {
        //例子，可以用BeanUtils copy
        return VDataSource.builder()
        		.id(source.getId())
        		.ds_name(source.getDsName())
        		.media_id(source.getMediaId()==null?"":source.getMediaId().getDeviceId())
        		.ds_code(source.getDsCode())
        		.ds_type(source.getDsType().toString())
        		.parent_id(source.getParentId())
				.dsLevel(source.getDsLevel().toString())
        		.build();
    }
    

}
