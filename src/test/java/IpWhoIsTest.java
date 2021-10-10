import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import model.IpWhoIsError;
import model.IpWhoIsIpResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpWhoIsTest
{
    static OKHttpClient client;
    static String hostUrl;
    @BeforeAll
    @Test
    static void setUp(){
        client = new OKHttpClient();
        hostUrl = "http://ipwhois.app/json/";
    }

    @Test
    @DisplayName("Get partial response")
    public void getPartialData() throws UnknownHostException {
        String paramName = "objects",
                paramVal = "country,city,currency";

        String url = client.buildURL(hostUrl, paramName, paramVal);

        try {
            JsonNode jsonResponse = new ObjectMapper().readTree(
                    client.getResponseAsStream(url));

            if (jsonResponse == null)
                Assertions.fail("Exception was thrown");
            else
                assertEquals(paramVal.split(",").length,
                             jsonResponse.size());
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("IOException was caught ");
            Assertions.fail("Exception was thrown");
        }
    }


    @Test
    @DisplayName("Get Country and city capital for your IP (Israel)")
    public void getDefaultData() throws UnknownHostException {
        IpWhoIsIpResponse response = client.getModelResponse(IpWhoIsIpResponse.class , hostUrl);

        if (response == null)
            Assertions.fail("Exception was thrown");
        else
            Assertions.assertAll(
                    () -> assertEquals("Israel" , response.country),
                    () -> assertEquals("Jerusalem", response.countryCapital));
    }


    @ParameterizedTest
    @MethodSource("errorResponses")
    @DisplayName("Validate Error Responses")
    void multiArguments(String ip, boolean flag, String message) {
        IpWhoIsError response = client.getModelResponse(IpWhoIsError.class, hostUrl + ip);
        if (response == null)
            Assertions.fail("Exception was thrown");
        else
            Assertions.assertAll(
                    () -> assertEquals(flag , Boolean.getBoolean(response.success)),
                    () -> assertEquals(message, response.message));
    }

    private static Stream<Arguments> errorResponses() {
        return Stream.of(
                Arguments.of("127.0.0.1", false , "reserved range" ),
                Arguments.of("256.2.1.0", false , "invalid IP address" )
        );
    }


    @Test
    @DisplayName("schema validation")
    public void validateSchema(){
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);

        InputStream schemaStream = inputStreamFromClasspath("schema.json");
        InputStream jsonStream = client.getResponseAsStream(hostUrl);

        if (jsonStream == null)
            Assertions.fail("Exception was thrown");
        else {
            try {
                JsonNode json = new ObjectMapper().readTree(jsonStream);
                JsonSchema schema = schemaFactory.getSchema(schemaStream);
                Set<ValidationMessage> validationResult = schema.validate(json);

                Assertions.assertEquals(0, validationResult.size());
            } catch (IOException e){
                System.err.println("IOException was caught ");
                Assertions.fail("Exception was thrown");
            }
        }
    }

    private static InputStream inputStreamFromClasspath(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}