import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
 
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
 
/**
 * Spring MongoDB configuration file
 * 
 */
@Configuration
class SpringMongoConfig{
 
	@Bean
	 def mongoTemplate() : MongoTemplate = {
 
	  val mongoClientURI = new MongoClientURI("mongodb://monil:12345@ds047930.mongolab.com:47930/monilshah-cmpe273");
		val mongoDbFactory = new SimpleMongoDbFactory(
				new MongoClient(mongoClientURI), "monilshah-cmpe273");
		val mongoTemplate = 
		    new MongoTemplate(mongoDbFactory);
		 mongoTemplate;
 
	}
 
}