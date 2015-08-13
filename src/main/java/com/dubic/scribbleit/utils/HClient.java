package com.dubic.scribbleit.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

public class HClient {

    private String url;
    private HttpURLConnection req;

    public HClient(String url) throws IOException {
        this.url = url;
        URL u = new URL(url);
        this.req = ((HttpURLConnection) u.openConnection());
    }

    public HClient() {
        
    }

    public HClient accept(String contentType) {
        /* 33 */ this.req.setRequestProperty("Content-Type", contentType);
        /* 34 */ return this;
    }

    public String get() throws ProtocolException, IOException {
        /* 38 */ this.req.setRequestMethod("GET");
        /* 39 */ return connectAndRead();
    }

    public <T> T get(Class<T> t) throws ProtocolException, IOException {
        /* 43 */ this.req.setRequestMethod("GET");
        /* 44 */ return new Gson().fromJson(connectAndRead(), t);
    }

    public <T> List<T> getList(Class<T> t) throws ProtocolException, IOException {
        /* 48 */ this.req.setRequestMethod("GET");
        return Arrays.asList(new Gson().fromJson(connectAndRead(), t));
    }

    public String post(byte[] data,String urls) throws ProtocolException, IOException {
        URL durl = new URL(urls);
        HttpURLConnection conn = (HttpURLConnection) durl.openConnection();

        conn.setConnectTimeout(10000);
//            conn.setReadTimeout(timeout);

        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Content-Type", "*/*");

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);

//        FileInputStream fin = new FileInputStream("C:\\Users\\Dubic\\Pictures\\IMG_20150329_171004.jpg");
//        byte[] toByteArray = IOUtils.toByteArray(fin);
        BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
        os.write(data);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = read(br);
        close(os);
        close(br);

        return response;
    }

    public HClient path(String path) {
        /* 53 */ this.url += path;
        /* 54 */ return this;
    }

    private String connectAndRead() throws IOException {
//        this.req.connect();
        return IOUtils.toString(this.req.getInputStream());
    }

    @Override
    public String toString() {
        return "HClient{" + "url=" + url + ", req=" + req + '}';
    }

    public static void main(String[] args) {
        try{
            BufferedImage image = ImageIO.read(new File("C:\\Users\\Dubic\\Pictures\\IMG_20150411_181605.jpg"));
            BufferedImage subimage = image.getSubimage(500, 500, 1700, 1700);
            ImageIO.write(subimage, "jpg", new File("C:\\Users\\Dubic\\Pictures\\IMG_20150411_1816066.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String read(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        if (sb.length() == 0) {
            throw new IOException("Empty data string response");
        }
        return sb.toString();
    }

    private static void close(Closeable cl) {
        try {
            cl.close();
        } catch (Exception ex) {
        }
    }
}

/* Location:           E:\esb-admin\WEB-INF\classes\
 * Qualified Name:     net.convergenceondemand.esb.admin.service.HClient
 * JD-Core Version:    0.6.2
 */
