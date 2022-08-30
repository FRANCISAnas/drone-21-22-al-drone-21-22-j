package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Terminal implements Serializable {

    private Long id;
    private int idForStation;
    private TerminalStatus status;//TODO add an attribute to give this class an actual identity example a name or a number

    @Override
    public String toString() {
        return "Terminal{" +
                "id=" + id +
                ", idForStation=" + idForStation +
                ", status=" + status +
                '}';
    }
}
