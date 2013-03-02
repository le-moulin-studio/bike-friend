package com.lemoulinstudio.bikefriend.webapp.conf;

import com.lemoulinstudio.bikefriend.webapp.StationParser;
import com.lemoulinstudio.bikefriend.webapp.cbike.CBikeStationParser;
import com.lemoulinstudio.bikefriend.webapp.quartz.LoadStationDataJob;
import com.lemoulinstudio.bikefriend.webapp.ubike.YouBikeStationParser;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vincent Cantin
 */
@Component
public class StartupHook {
  
  @Autowired
  private MongoTemplate db;
  
  @Autowired
  private Scheduler scheduler;
  
  @Autowired
  private YouBikeStationParser youBikeStationParser;
  
  @Autowired
  private CBikeStationParser cBikeStationParser;
  
  @PostConstruct
  public void init() throws Exception {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("stationParserList", Arrays.<StationParser>asList(
            youBikeStationParser,
            cBikeStationParser));
    
    JobDetail loadStationDataJob = JobBuilder
            .newJob(LoadStationDataJob.class)
            .withIdentity("loadStationDataJob")
            .storeDurably(true)
            .usingJobData(jobDataMap)
            .build();
    
    scheduler.addJob(loadStationDataJob, true);
    
    // Once every 2 minutes.
    CronTrigger twoMinutesTrigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */2 * * * ?"))
            .forJob(loadStationDataJob)
            .build();

    scheduler.scheduleJob(twoMinutesTrigger);
  }
  
}
