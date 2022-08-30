package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Dimensions implements Serializable {

    private Double height;

    private Double width;

    private Double length;

}
