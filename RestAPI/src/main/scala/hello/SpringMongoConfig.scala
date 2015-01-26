package hello

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.mongodb._
import com.mongodb.MongoClientURI
import org.springframework.data.mongodb.core.{SimpleMongoDbFactory, MongoTemplate}

/**
 * Spring MongoDB configuration file
 *
 */
@Configuration
class SpringMongoConfig{

  @Bean
  def mongoTemplate() : MongoTemplate = {
    val mongoClientURI = new MongoClientURI("mongodb://monil:12345@ds049130.mongolab.com:49130/monilshah-cmpe273")
    val mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(mongoClientURI), "monilshah-cmpe-273")
    val mongoTemplate = new MongoTemplate(mongoDbFactory)
    mongoTemplate

  }
}