/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.application;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

/**
 *
 * @author dubem
 */
//@Component
public class GsonMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Gson gson = new Gson();
    private String jsonPrefix;

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     */
    public GsonMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET),
                new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    /**
     * Set the {@code Gson} instance to use. If not set, a default
     * {@link Gson#Gson() Gson} instance is used.
     * <p>
     * Setting a custom-configured {@code Gson} is one way to take further
     * control of the JSON serialization process.
     *
     * @param gson
     */
    public void setGson(Gson gson) {
        Assert.notNull(gson, "'gson' is required");
        this.gson = gson;
    }

    /**
     * Return the configured {@code Gson} instance for this converter.
     *
     * @return
     */
    public Gson getGson() {
        return this.gson;
    }

    /**
     * Specify a custom prefix to use for JSON output. Default is none.
     *
     * @param jsonPrefix
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with "{}
     * &&". Default is {@code false}.
     * <p>
     * Prefixing the JSON string in this manner is used to help prevent JSON
     * Hijacking. The prefix renders the string syntactically invalid as a
     * script so that it cannot be hijacked. This prefix does not affect the
     * evaluation of JSON, but if JSON validation is performed on the string,
     * the prefix would need to be ignored.
     *
     * @param prefixJson
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? "{} && " : null);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return canRead(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
// should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        TypeToken<?> token = getTypeToken(clazz);
        return readTypeToken(token, inputMessage);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        TypeToken<?> token = getTypeToken(type);
        return readTypeToken(token, inputMessage);
    }

    /**
     * Return the Gson {@link TypeToken} for the specified type.
     * <p>
     * The default implementation returns {@code TypeToken.get(type)}, but this
     * can be overridden in subclasses to allow for custom generic collection
     * handling. For instance:
     * <pre class="code">
     * protected TypeToken<?> getTypeToken(Type type) { if (type instanceof
     * Class && List.class.isAssignableFrom((Class<?>) type)) { return new
     * TypeToken<ArrayList<MyBean>>() {}; } else { return
     * super.getTypeToken(type); } }
     * </pre>
     *
     * @param type the type for which to return the TypeToken
     * @return the type token
     */
    protected TypeToken<?> getTypeToken(Type type) {
        return TypeToken.get(type);
    }

    private Object readTypeToken(TypeToken<?> token, HttpInputMessage inputMessage) throws IOException {
        Reader json = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
        try {
            return this.gson.fromJson(json, token.getType());
        } catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    private Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharSet() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharSet();
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Charset charset = getCharset(outputMessage.getHeaders());
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
        try {
            if (this.jsonPrefix != null) {
                writer.append(this.jsonPrefix);
            }
            if (o instanceof JsonObject) {
                JsonObject json = new JsonObject();
                for (Map.Entry<String, JsonElement> en : ((JsonObject)o).entrySet()) {
                    json.add(en.getKey(), en.getValue());
                }
                this.gson.toJson(json, writer);
            } else {
                this.gson.toJson(o, writer);
            }
            writer.close();
        } catch (JsonIOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    public static void main(String[] dkjija) {
//        List<DT> dtlist = new ArrayList<DT>();
//        DT dt = new DT();
//        dt.setiTotalDisplayRecords(200L);
//        dt.setiTotalRecords(100L);
//        dtlist.add(dt);
//        DT dt2 = new DT();
//        dt2.setiTotalDisplayRecords(780L);
//        dt2.setiTotalRecords(300L);
//        dtlist.add(dt2);
//        System.out.println(new Gson().toJson(dtlist));
//        System.out.println(dtlist.getClass());
//        String str = "[{\"iTotalRecords\":100,\"iTotalDisplayRecords\":200,\"aaData\":[]},{\"iTotalRecords\":300,\"iTotalDisplayRecords\":780,\"aaData\":[]}]";
//        List fromJson = new Gson().fromJson(str, dtlist.getClass());
//        for (Object o : fromJson) {
//            System.out.println("total = " + ((DT) o).getiTotalRecords());
//        }
    }
}
