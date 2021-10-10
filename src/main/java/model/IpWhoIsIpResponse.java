package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IpWhoIsIpResponse {
    public final String country;
    public final String countryCapital;


    public IpWhoIsIpResponse(@JsonProperty("country") String country,
                             @JsonProperty("country_capital") String countryCapital){
        this.country = country;
        this.countryCapital = countryCapital;
    }
}
