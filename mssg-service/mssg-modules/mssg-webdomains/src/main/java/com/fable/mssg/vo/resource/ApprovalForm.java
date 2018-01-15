package com.fable.mssg.vo.resource;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/12/8
 */
@Data
public class ApprovalForm {
    String id;
    String apprMsg;
    String apprStatus;
    List<SubscribeFormPrv> subscribeFormPrvs;
}
