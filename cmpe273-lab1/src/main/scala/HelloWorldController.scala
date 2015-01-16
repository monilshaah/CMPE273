
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.bind.annotation.{RequestMapping, RestController}
import org.springframework.boot._;
import org.springframework.boot.autoconfigure._;
import org.springframework.web.bind.annotation._;
@RestController
@EnableAutoConfiguration
@ComponentScan
class HelloWorldController {

   @RequestMapping(Array("/"))
   def mapResponse() = "Hello world..Finally, I am able to run Scala + Spring Boot"
}

object HelloWorldControllerApp {

    def main(args: Array[String]) {
 //    val runConfig : Array[Object] = Array(classOf[HelloWorldController])
    SpringApplication.run(classOf[HelloWorldController]);
	}
}
