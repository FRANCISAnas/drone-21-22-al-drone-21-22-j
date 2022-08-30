package entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialEvent {
    private String type;

    private String trackingNumber;

    private Delivery delivery;
}
