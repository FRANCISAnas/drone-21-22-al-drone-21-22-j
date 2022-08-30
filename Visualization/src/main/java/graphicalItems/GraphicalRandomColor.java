package graphicalItems;

import java.awt.*;
import java.util.Random;

public interface GraphicalRandomColor {
    Color color = new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
}
