package controller;

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
	private final double RADIUS_IN_KILOMETERS = 50;
	private Map<String, StormDto> allStorms = Collections.synchronizedMap(new HashMap<>());

	public List<StormDto> generate(Integer count) {
		List<StormDto> storms = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			StormDto storm = new StormDto();
			storm.setId(UUID.randomUUID().toString());
			storm.setMax(ThreadLocalRandom.current().nextInt(50, 1000 + 1) * 2);
			storm.setCurrent(storm.getMax() - ThreadLocalRandom.current().nextInt(3, 6 + 1));
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

	public Collection<StormDto> getAll() {
		return allStorms.values();
	}

	public void remove(String id) {
		allStorms.remove(id);
	}

	public void removeAll() {
		allStorms.clear();
	}

	public StormDto find(Double lat, Double lng) {
		List<StormDto> allFound = new ArrayList<>();
		allStorms.forEach((id, storm) -> {
			Geometry circle = createCircle(storm.getLat(), storm.getLng());
			Point point = createPoint(lat, lng);
			if (circle.contains(point)) {
				allFound.add(storm);
			}
		});

		if (allFound.isEmpty()) {
			return null;
		}

		return Collections.min(allFound, Comparator.comparing(stormDto -> stormDto.getMax() - stormDto.getCurrent()));
	}

	private Geometry createCircle(Double lat, Double lng) {
		GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
		shapeFactory.setNumPoints(64);
		shapeFactory.setCentre(new Coordinate(lat, lng));

		double diameterInMeters = RADIUS_IN_KILOMETERS * 1000 * 2;

//        https://stackoverflow.com/a/52857147
		// Length in meters of 1° of latitude = always 111.32 km
		shapeFactory.setWidth(diameterInMeters / 111320d);
		// Length in meters of 1° of longitude = 40075 km * cos( latitude ) / 360
		shapeFactory.setHeight(diameterInMeters / (40075000 * Math.cos(Math.toRadians(lat)) / 360));

		return shapeFactory.createCircle();
	}

	private Point createPoint(Double lat, Double lng) {
		GeometryFactory gf = new GeometryFactory();
		return gf.createPoint(new Coordinate(lat, lng));
	}
}
