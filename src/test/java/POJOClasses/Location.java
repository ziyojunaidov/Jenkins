package POJOClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Location {
    private String postcode;
    private String country;
    private String countryAbbreviation;
    private List<Place> places;

    public String getPostcode() {
        return postcode;
    }
    @JsonProperty("post code")
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryAbbreviation() {
        return countryAbbreviation;
    }
    @JsonProperty("country abbreviation")
    public void setCountryAbbreviation(String countryAbbreviation) {
        this.countryAbbreviation = countryAbbreviation;
    }

    public List<Place> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "Location{" +
                "postcode='" + postcode + '\'' +
                ", country='" + country + '\'' +
                ", countryAbbreviation='" + countryAbbreviation + '\'' +
                ", places=" + places +
                '}';
    }

    public void setPlaces(List<Place> places) {
        this.places = places;


    }
}
