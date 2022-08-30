package graphicalItems;

import entities.IsInside;
import entities.MapCoordinate;

import java.awt.*;

public interface GraphicalItem {
    void draw(Graphics g, MapCoordinate mapOnX, MapCoordinate mapOnY, IsInside isInside);
}
