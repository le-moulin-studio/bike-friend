package com.lemoulinstudio.bikefriend.webapp.conf;

import javax.annotation.PreDestroy;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vincent Cantin
 */
@Component
public class ShutdownHook {
  
  @Autowired
  private MongoTemplate db;
  
  @Autowired
  private Scheduler scheduler;
  
  @PreDestroy
  public void destroy() throws Exception {
    scheduler.shutdown(true);
    db.getDb().getMongo().close();
  }
  
}
