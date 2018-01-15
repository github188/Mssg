package com.fable.mssg.service.slave;

/**
 * @Description Slave端和Master端之间的上传和下载
 * @Author wangmeng 2017/12/8
 */
public interface FileLoadService {
    byte[] download(String fileName);
}
