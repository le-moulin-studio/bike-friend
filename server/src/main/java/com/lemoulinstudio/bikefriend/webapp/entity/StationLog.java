package com.lemoulinstudio.bikefriend.webapp.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Vincent Cantin
 */
@Document
@CompoundIndexes({
  @CompoundIndex(def = "{stationId: 1, date: 1}")
})
public class StationLog {
  
  public ObjectId id;
  
  @Indexed
  public ObjectId stationId;
  
  @Indexed
  public long date;
  
  public int nbBikes;
  
  public int nbEmptySlots;

  public StationLog() {
  }

  public StationLog(ObjectId stationId, long date, int nbBikes, int nbEmptySlots) {
    this.id = new ObjectId();
    this.stationId = stationId;
    this.date = date;
    this.nbBikes = nbBikes;
    this.nbEmptySlots = nbEmptySlots;
  }

}
