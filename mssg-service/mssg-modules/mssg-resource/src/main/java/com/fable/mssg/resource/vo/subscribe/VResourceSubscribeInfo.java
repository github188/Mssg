package com.fable.mssg.resource.vo.subscribe;

import com.fable.mssg.resource.vo.VResourceInfo;
import com.fable.mssg.vo.subscribe.VSubscribeInfo;
import com.fable.mssg.vo.subscribe.VSubscribePrv;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description 订阅和资源信息
 * @Author wangmeng 2017/12/25
 */
@Data
@Builder
public class VResourceSubscribeInfo {
    VResourceInfo resourceInfo;
    VSubscribeInfo subscribeInfo;

}
