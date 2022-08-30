package entities;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Warehouse {
    private Long id;

    private Coordinates location;//TODO add an attribute like a name to give this entity more identity

    public Warehouse(Coordinates location) {
        this.location = location;
    }
}
