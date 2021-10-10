package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IpWhoIsError {
    public final String success;
    public final String message;



    public IpWhoIsError(@JsonProperty("success") String success,
                        @JsonProperty("message") String message){
        this.success = success;
        this.message = message;
    }
}