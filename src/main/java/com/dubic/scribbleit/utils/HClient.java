package com.dubic.scribbleit.utils;

import com.google.gson.Gson;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
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
        String text = "     A ModelAndView object, with the model implicitly enriched with command objects and the results of @ModelAttribute annotated reference data accessor methods.\n"
                + "    A Model object, with the view name implicitly determined through a RequestToViewNameTranslator and the model implicitly enriched with command objects and the results of @ModelAttribute annotated reference data accessor methods.\n"
                + "    A Map object for exposing a model, with the view name implicitly determined through a RequestToViewNameTranslator and the model implicitly enriched with command objects and the results of @ModelAttribute annotated reference data accessor methods.\n"
                + "    A View object, with the model implicitly determined through command objects and @ModelAttribute annotated reference data accessor methods. The handler method may also programmatically enrich the model by declaring a Model argument (see above).\n"
                + "    A String value that is interpreted as the logical view name, with the model implicitly determined through command objects and @ModelAttribute annotated reference data accessor methods. The handler method may also programmatically enrich the model by declaring a Model argument (see above).\n"
                + "    void if the method handles the response itself (by writing the response content directly, declaring an argument of type ServletResponse / HttpServletResponse for that purpose) or if the view name is supposed to be implicitly determined through a RequestToViewNameTranslator (not declaring a response argument in the handler method signature).\n"
                + "    If the method is annotated with @ResponseBody, the return type is written to the response HTTP body. The return value will be converted to the declared method argument type using HttpMessageConverters. See the section called “Mapping the response body with the @ResponseBody annotation”.\n"
                + "    An HttpEntity<?> or ResponseEntity<?> object to provide access to the Servlet response HTTP headers and contents. The entity body will be converted to the response stream using HttpMessageConverters. See the section called “Using HttpEntity”.\n"
                + "    An HttpHeaders object to return a response with no body.\n"
                + "    A Callable<?> can be returned when the application wants to produce the return value asynchronously in a thread managed by Spring MVC.\n"
                + "    A DeferredResult<?> can be returned when the application wants to produce the return value from a thread of its own choosing.\n"
                + "    A ListenableFuture<?> can be returned when the application wants to produce the return value from a thread of its own choosing.\n"
                + "    Any other return type is considered to be a single model attribute to be exposed to the view, using the attribute name specified through @ModelAttribute at the method level (or the default attribute name based on the return type class name). The model is implicitly enriched with command objects and the results of @ModelAttribute annotated reference data accessor methods. ";

        InputStream in = null;
        OutputStreamWriter wout = null;
        BufferedReader br;

        try {
            URL url = new URL("http://localhost:7070/services/s3/add");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(10000);
//            conn.setReadTimeout(timeout);

            conn.setRequestProperty("Content-Length", String.valueOf(text.length()));
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            FileInputStream fin = new FileInputStream("C:\\Users\\Dubic\\Pictures\\IMG_20150329_171004.jpg");
            byte[] toByteArray = IOUtils.toByteArray(fin);
            BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(toByteArray);

//            wout = new OutputStreamWriter(conn.getOutputStream());
//            wout.write(text);
//            wout.close();
            // Read the response XML document
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = read(br);
            System.out.println(String.format("TELCO RESPONSE[%s]", response));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(in);
            close(wout);
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
