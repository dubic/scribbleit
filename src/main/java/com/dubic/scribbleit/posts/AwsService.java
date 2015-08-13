/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.posts;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.dubic.scribbleit.utils.IdmUtils;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Dubic
 */
@Component
public class AwsService {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private Database db;
    @Value("${bucket.name}")
    private String bucket;
    @Value("${aws.key.id}")
    private String awsKeyId;
    @Value("${aws.key.secret}")
    private String awsKeySecret;
    private AWSCredentials credentials;

    @PostConstruct
    public void onCreate() {
        credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return AwsService.this.awsKeyId;
            }

            @Override
            public String getAWSSecretKey() {
                return AwsService.this.awsKeySecret;
            }
        };
    }

    public void putImage(MultipartFile file, String key,int w,int h) throws IOException {
        log.info("Connecting to S3 for PUT...");
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(file.getContentType());
        
        InputStream fis = file.getInputStream();
        RenderedImage resizeImage = IdmUtils.resizeImage(w, h, ImageIO.read(fis));
        IOUtils.closeQuietly(fis);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizeImage, "jpg", baos);
        byte[] data = baos.toByteArray();
        metaData.setContentLength(data.length);
//        log.info("length is : "+data.length);
        
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        PutObjectRequest request = new PutObjectRequest(bucket, key, bais, metaData);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(request);
        log.info("Posted image to S3 server");
    }

    public void remove(String key) {
        log.info("Connecting to S3 for DELETE...");
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        s3Client.deleteObject(bucket, key);
        log.info(String.format("Deleted [%s] from S3", key));
    }
}
