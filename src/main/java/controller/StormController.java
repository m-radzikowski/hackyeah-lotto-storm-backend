package controller;

import com.vividsolutions.jts.awt.PointShapeFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import model.dto.StormDto;

import javax.ejb.Singleton;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class StormController {
    private final double RADIUS = 50;
    private Map<String, StormDto> allStorms = Collections.synchronizedMap(new HashMap<>());

    public List<StormDto> generate(Integer count) {
        List<StormDto> storms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            StormDto storm = new StormDto();
            storm.setId(UUID.randomUUID().toString());
//            storm.setMax(ThreadLocalRandom.current().nextInt(20, 1000 + 1));
            storm.setMax(3);
            allStorms.put(storm.getId(), storm);
            storms.add(storm);
        }
        return storms;
    }

    public void updatePosition(StormDto stormDto) {
        if (allStorms.containsKey(stormDto.getId()) && stormDto.getLng() != null && stormDto.getLat() != null) {
            allStorms.get(stormDto.getId()).setLng(stormDto.getLng());
            allStorms.get(stormDto.getId()).setLat(stormDto.getLat());
        }
    }

    public void updateCurrent(StormDto stormDto) {
        if (allStorms.containsKey(stormDto.getId())) {
            allStorms.get(stormDto.getId()).setCurrent(stormDto.getCurrent());
        }
    }

    public void remove(String id) {
        allStorms.remove(id);
    }

    public StormDto find(Double lng, Double lat) {
        List<StormDto> allFound = new ArrayList<>();
        allStorms.forEach((id, storm) -> {
            Geometry circle = createCircle(storm.getLng(), storm.getLat());
            Point point = createPoint(lng, lat);
            if (circle.contains(point)) {
                allFound.add(storm);
            }
        });
        return Collections.min(allFound, Comparator.comparing(stormDto -> stormDto.getMax() - stormDto.getCurrent()));
    }

    private Geometry createCircle(Double lng, Double lat) {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(32);
        shapeFactory.setCentre(new Coordinate(lng, lat));
        shapeFactory.setSize(RADIUS * 2);
        return shapeFactory.createCircle();
    }

    private Point createPoint(Double lng, Double lat) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(lng, lat));
    }
}
