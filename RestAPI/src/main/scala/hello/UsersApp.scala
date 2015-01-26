package hello

import org.springframework.boot.SpringApplication
import scala.collection.mutable.HashMap

object UsersApp {
	var userSchemaHashMap = new HashMap[String,Users]		//Hashmap for storing user objects
	var randomUserId: Int = 123450
	var randomCardId: Int = 154320
  var randomLoginId: Int = 167890
  var randomBaId: Int = 198760
		
  def main(args: Array[String]) {
		//Run Spring boot application
		SpringApplication.run(classOf[UsersController])
	}
}