/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.application;

import com.dubic.scribbleit.utils.HClient;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author dubem
 */
@Component
public class CronFactory {
    @Value("${bucket.url.remove}")
    private String s3url;
    private final Logger log = Logger.getLogger(getClass());
    
    @Async
    public void deleteImage(String image){
        try {
            Boolean delete = new HClient(String.format(s3url, image)).accept("application/json").get(Boolean.class);
            log.info(String.format("deleted image [%s]? "+delete, image));
        } catch (IOException ex) {
            log.error(ex.getMessage()+"; title="+image,ex);
        }
    }
}
