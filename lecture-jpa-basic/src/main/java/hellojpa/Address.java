package hellojpa;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String city;
    private String zipcode;
    private String street;
}
