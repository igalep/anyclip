import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class OKHttpClient {
    OkHttpClient client;
    Request request;
    ObjectMapper mapper;
    HttpUrl.Builder queryUrlBuilder;


    public OKHttpClient(){
        client = new OkHttpClient();
        mapper = new ObjectMapper();
    }

    /**
     *  build URL with query params
     * @param hostUrl
     * @param paramName
     * @param paramVal
     * @return full URL with query params
     */
    public String buildURL(String hostUrl, String paramName, String paramVal){
        queryUrlBuilder = HttpUrl.parse(hostUrl).newBuilder();
        queryUrlBuilder.addQueryParameter(paramName , paramVal);

        return queryUrlBuilder.build().toString();
    }

    /**
     * get Response as an InputStream
     * @param url
     * @return response as an InputStream
     */
    public InputStream getResponseAsStream(String url){
        try {
            Response response = getResponse(url);

            return response.body().byteStream();
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("IOException was caught ");
        }
        return null;
    }

    /**
     * get Response as a generic Model (provided by the consumer
     * @param model
     * @param url
     * @param <T> Generic Class type for building required model
     * @return Response as a model
     */
    public <T> T getModelResponse(Class<T> model, String url){
        try {
            Response response = getResponse(url);

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return (T) mapper.readValue(response.body().byteStream(), Class.forName(model.getName()));
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("IOException was caught ");
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            System.err.println("ClassNotFoundException was caught ");
        }
        return null;
    }

    private Response getResponse(String url) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
