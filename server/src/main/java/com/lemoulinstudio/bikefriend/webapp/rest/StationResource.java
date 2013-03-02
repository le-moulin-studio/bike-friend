package com.lemoulinstudio.bikefriend.webapp.rest;

import com.lemoulinstudio.bikefriend.webapp.entity.Station;
import com.lemoulinstudio.bikefriend.webapp.vo.StationLogVo;
import com.lemoulinstudio.bikefriend.webapp.vo.StationVo;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.Circle;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vincent Cantin
 */
@Component
@Path("station")
public class StationResource {
  
  @Autowired
  private MongoTemplate db;
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<StationVo> getStation(
          @QueryParam("id") String id,
          @QueryParam("provider") Station.Provider provider,
          @QueryParam("lon") Double longitude,
          @QueryParam("lat") Double latitude,
          @QueryParam("dist") Double distance,
          @QueryParam("test") Boolean includeTestStations) {
    Criteria criteria = new Criteria();
    
    if (id != null) {
      criteria = criteria.and("_id").is(new ObjectId(id));
    }
    
    if (provider != null) {
      criteria = criteria.and("provider").is(provider);
    }
    
    if (longitude != null && latitude != null && distance != null) {
      criteria = criteria.and("location").withinSphere(
              new Circle(longitude, latitude, distance / 6371));
    }
    
    if (!Boolean.TRUE.equals(includeTestStations)) {
      criteria = criteria.and("isTestStation").is(false);
    }
    
    List<StationVo> stations = db.find(new Query(criteria), StationVo.class, "station");
    
    return stations;
  }
  
  @GET
  @Path("{stationId}/logs")
  @Produces(MediaType.APPLICATION_JSON)
  public List<StationLogVo> getStationLogs(
          @PathParam("stationId") String stationId,
          @QueryParam("from") long from,
          @QueryParam("to") long to) {
    List<StationLogVo> logs = db.find(new Query(Criteria
            .where("stationId").is(new ObjectId(stationId))
            .and("date").gte(from).lte(to))
            .with(new Sort(Sort.Direction.ASC, "date")),
            StationLogVo.class,
            "stationLog");
    
    return logs;
  }
  
}
