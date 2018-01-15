package com.fable.mssg.vo.resource;

import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import lombok.Data;
import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/12/5
 */
@Data
public class SubscribeForm {
    List<SubscribeFormPrv> prvs;
    String applyCause;
    String linkMan;
    String phone;
    String resId;
    String fileName;
    String duty;
    String cellPhone;

    @SneakyThrows
    public List<SubscribePrv> getSubscribePrv(){
        List<SubscribePrv> subscribePrvs = new ArrayList<>(this.getPrvs().size());
        for(SubscribeFormPrv subscribeFormPrv:this.prvs){
            SubscribePrv subscribePrv = new SubscribePrv();
            subscribePrv.setDsId(new DataSource(subscribeFormPrv.getDsId()));
            subscribePrv.setDownload(subscribeFormPrv.getDownload());
            subscribePrv.setHistSnap(subscribeFormPrv.getHisSnap());
            if("-1".equals(subscribeFormPrv.getRealTime())){
                subscribePrv.setRealPlay(0);//无权限
            }else if("0".equals(subscribeFormPrv.getRealTime())){
                subscribePrv.setRealPlay(2);//任意时间
            }else {
                subscribePrv.setRealPlay(1);//时间段
            }
            if("-1".equals(subscribeFormPrv.getHistTime())){
                subscribePrv.setPlayBack(0);//无权限
            }else if("0".equals(subscribeFormPrv.getHistTime())){
                subscribePrv.setPlayBack(2);//任意时间
            }else {
                subscribePrv.setPlayBack(1);//时间段
            }
            subscribePrv.setRealControl(subscribeFormPrv.getRealControl());
            subscribePrv.setRealSnap(subscribeFormPrv.getRealSnap());
            subscribePrv.setRecord(subscribeFormPrv.getRecord());
            subscribePrv.setRealTime(subscribeFormPrv.getRealTime());
            subscribePrv.setHisTime(subscribeFormPrv.getHistTime());
            subscribePrv.setRealBeginTime(getBeginAndEnd(subscribeFormPrv.getRealTime(),0)[0]);
            subscribePrv.setRealEndTime(getBeginAndEnd(subscribeFormPrv.getRealTime(),0)[1]);
            subscribePrv.setHisBeginTime(getBeginAndEnd(subscribeFormPrv.getHistTime(),1)[0]);
            subscribePrv.setHisEndTime(getBeginAndEnd(subscribeFormPrv.getHistTime(),1)[1]);

            subscribePrvs.add(subscribePrv);
        }
        return subscribePrvs;

    }

    //截取时间

    /**
     *
     * @param time
     * @param type 0 for real, 1 for hist
     * @return
     */
    public static Timestamp[] getBeginAndEnd(String time,int type){

        Timestamp[] beginAndHist = new Timestamp[2];
        Calendar calendar = Calendar.getInstance();

        if(!"-1".equals(time)){
            switch (time){
                case "1":
                    if(type==0) {
                        beginAndHist[0] = new Timestamp(calendar.getTimeInMillis());
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        beginAndHist[1] = new Timestamp(calendar.getTimeInMillis());
                    }else {
                        beginAndHist[1] = new Timestamp(calendar.getTimeInMillis());
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        beginAndHist[0] = new Timestamp(calendar.getTimeInMillis());
                    }
                    break;
                case "2":
                    if(type==0) {
                        beginAndHist[0] = new Timestamp(calendar.getTimeInMillis());
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        beginAndHist[1] = new Timestamp(calendar.getTimeInMillis());
                    }else {
                        beginAndHist[1] = new Timestamp(calendar.getTimeInMillis());
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        beginAndHist[0] = new Timestamp(calendar.getTimeInMillis());
                    }
                    break;
                case "3":
                    if(type==0) {
                        beginAndHist[0] = new Timestamp(calendar.getTimeInMillis());
                        calendar.add(Calendar.MONTH, 1);
                        beginAndHist[1] = new Timestamp(calendar.getTimeInMillis());
                    }else {
                        beginAndHist[1] = new Timestamp(calendar.getTimeInMillis());
                        calendar.add(Calendar.MONTH, -1);
                        beginAndHist[0] = new Timestamp(calendar.getTimeInMillis());
                    }
                    break;
                default:
                    if(time.contains("~")){
                        String[] stringArray = time.split("~");
                        beginAndHist[0] = Timestamp.valueOf(stringArray[0]);
                        beginAndHist[1] = Timestamp.valueOf(stringArray[1]);
                    }
                    //其他情况都是null 1没有权限 2任意权限
            }

        }
        return beginAndHist;

    }
}




