import org.springframework.data.mongodb.repository.MongoRepository._
import com.mongodb._
import com.google.gson._
import collection.JavaConverters._
import com.mongodb.util.JSON
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

object DatabaseAdapter {
  def getCollection () : DBCollection = {
    println("In database adapter")
  val mongoClientUri = new MongoClientURI("mongodb://cmpeTest:cmpe@ds047930.mongolab.com:47930/ramwallet")
  val mongoClient = new MongoClient(mongoClientUri)
  val db = mongoClient.getDB(mongoClientUri.getDatabase())
  val collection = db.getCollection("CMPEAssignment2")
  collection
  }

  def connectToDB () : MongoOperations = {
  val ctx = 
                         new AnnotationConfigApplicationContext(classOf[SpringMongoConfig])
		val mongoOperation = 
                          ctx.getBean("mongoTemplate").asInstanceOf[MongoOperations];
		
  mongoOperation
  }
  
  def createUser(user : Map[String,String]) {
    val collection = connectToDB()
    val simpleDBObject = new BasicDBObject()
    println("USER MAP : "+user);
    for((key,value) <- user) {
      simpleDBObject.put(key,value)
    }
    
    val gson = new Gson()
    val idList = List[String]()
    //val webLoginList = user.getAllWebLogin().toList
    //val bankAccountList = user.getAllBankAccounts().toList
    simpleDBObject.put("IDCardData",Array())
    simpleDBObject.put("WebLoginData",Array())
    simpleDBObject.put("BankAccountData",Array())
    //println("JSON FROM OBJECT "+gson.toJson(user))
    // var userDataMap : Map[String, String] = Map();
     //userDataMap += "user"->gson.toJson(user)
    val jsonParser = new JsonParser()
    val jsonString = gson.toJson(user)
    //val jsonElement = gson.fromJson(jsonString.toString(), classOf[User])
   // val userData = new BasicDBObject(JSON.parse(jsonString) : DBObject)
    val dbObject  = JSON.parse(jsonString).asInstanceOf[DBObject]
    val coll = collection.getCollection("CMPEAssignment2")
    //val users = List(user)
    coll.insert(simpleDBObject)
   
  }
  

  def retrieveUser(id : String) : Map[String,String] = {
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val gson = new Gson()
    //val query = new Query()
		//query.addCriteria(Criteria.where("user_id").is(id));
    val query = new BasicDBObject("user_id",id)
    val cursor = collection.findOne(query)
    
    Map("user_id"->cursor.get("user_id").toString(), "email" -> cursor.get("email").toString(), 
            "password"->cursor.get("password").toString(), "created_at"->cursor.get("created_at").toString())
  }
  
  
  def retrieveIdCards(id : String) : String = {
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val gson = new Gson()
    //val query = new Query()
		//query.addCriteria(Criteria.where("user_id").is(id));
    val query = new BasicDBObject("user_id",id)
    val cursor = collection.findOne(query)
  
        val jsonString = gson.toJson(cursor.get("IDCardData"))
          jsonString.toString()
  }
  
  
   def retrieveWebLogins(id : String) : String = {
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val gson = new Gson()
    //val query = new Query()
		//query.addCriteria(Criteria.where("user_id").is(id));
    val query = new BasicDBObject("user_id",id)
    val cursor = collection.findOne(query)
    val jsonString = gson.toJson(cursor.get("WebLoginData"))
          jsonString.toString()
  }
  
  
   def retrieveBankAccounts(id : String) : String = {
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val gson = new Gson()
    //val query = new Query()
		//query.addCriteria(Criteria.where("user_id").is(id));
    val query = new BasicDBObject("user_id",id)
    val cursor = collection.findOne(query)
    val jsonString = gson.toJson(cursor.get("BankAccountData"))
          jsonString.toString()
  }
   
  def updateUser(id : String, newUser : User) {
    val coll = connectToDB()
    val collection = coll.getCollection("CMPEAssignment2")
     val query = new BasicDBObject("user_id", id);
    val simpleDBObject = new BasicDBObject()
    if(null!=newUser.getEmail()) { 
      simpleDBObject.put("email",newUser.getEmail()) 
    }
    if(null!=newUser.getPassword()) { 
      simpleDBObject.put("password",newUser.getPassword()) 
    }
    if(null!=newUser.getName()) { 
      simpleDBObject.put("name",newUser.getName()) 
    }
   // val cursor = collection.find(query)
     
    collection.update(query,new BasicDBObject("$set",simpleDBObject))
  }
  
  def updateIdCard(id : String, userIDCard : Map[String,String]) {
    println("User id : "+userIDCard)
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val simpleDBObject = new BasicDBObject()
     for((key,value) <- userIDCard) {
      simpleDBObject.put(key,value)
    }
     val query = new BasicDBObject("user_id", id);
   
    val cursor = collection.find(query)
    // val idCardList = List(DBObject)
    try {
       while(cursor.hasNext()) {
         val gson = new Gson()
          val dbobj = cursor.next()
        //Converting BasicDBObject to a custom Class(Employee)
          println("CURSOR While updating ID CARD"+dbobj);
         // val emp = (new Gson()).fromJson((dbobj.toString() : String), classOf[User])
          val jsonString = gson.toJson(userIDCard)
          println("NEW JSON UPDATE IDCARDS")
          println(jsonString)
          println()
    //val jsonElement = gson.fromJson(jsonString.toString(), classOf[User])
   // val userData = new BasicDBObject(JSON.parse(jsonString) : DBObject)
    val dbObject  = JSON.parse(jsonString).asInstanceOf[DBObject]
          
        //  idCardList.add(dbObject)
           //val updateQuery = 
          collection.update(query,new BasicDBObject("$push", new BasicDBObject("IDCardData", simpleDBObject)))
         // println(emp.getName())
       }
    } finally {
       cursor.close()
    }
  }
  
  
  def updateWebLogins(id : String, userIDCard : Map[String,String]) {
    println("User id : "+userIDCard)
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val simpleDBObject = new BasicDBObject()
     for((key,value) <- userIDCard) {
      simpleDBObject.put(key,value)
    }
     val query = new BasicDBObject("user_id", id);
   
    val cursor = collection.find(query)
    // val idCardList = List(DBObject)
    try {
       while(cursor.hasNext()) {
         val gson = new Gson()
          val dbobj = cursor.next()
        //Converting BasicDBObject to a custom Class(Employee)
          println("CURSOR While updating ID CARD"+dbobj);
         // val emp = (new Gson()).fromJson((dbobj.toString() : String), classOf[User])
          val jsonString = gson.toJson(userIDCard)
          println("NEW JSON UPDATE IDCARDS")
          println(jsonString)
          println()
    //val jsonElement = gson.fromJson(jsonString.toString(), classOf[User])
   // val userData = new BasicDBObject(JSON.parse(jsonString) : DBObject)
    val dbObject  = JSON.parse(jsonString).asInstanceOf[DBObject]
          
        //  idCardList.add(dbObject)
           //val updateQuery = 
          collection.update(query,new BasicDBObject("$push", new BasicDBObject("WebLoginData", simpleDBObject)))
         // println(emp.getName())
       }
    } finally {
       cursor.close()
    }
  }
  
  def updateBankAccounts(id : String, userIDCard : Map[String,String]) : String = {
    println("User id : "+userIDCard)
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val simpleDBObject = new BasicDBObject()
    //val updateRoutingDBObject = new BasicDBObject()
            val rountingNumber = "account_name"
            var updatedUserName = ""
     for((key,value) <- userIDCard) {
       println("key :"+key+" value : "+value)
       if(key=="routing_number") {
         try {
         val restCallJsonParsing = new RestCallJsonParsing()
            val url = new String("https://www.routingnumbers.info/api/data.json?rn="+value)
		 val data = restCallJsonParsing.getJSON(url)
		 println("Http data : "+data)
		 val gson = new Gson()
		 val msg = gson.fromJson(data, classOf[Msg])
		 //println("msg : "+msg)
		 updatedUserName = msg.getCustomer_name()
		 
		 println(updatedUserName)
		 if(null == updatedUserName) {
		   println("THROWING Exception cought")
		   throw new Exception()
		   return "{error_message : Invalid routing number}"
		 }
         }catch {
           case e: Exception =>
             println("Exception cought")
          throw new Exception("Invalid routing number")
          return "{error_message : Invalid routing number}"
         }
       }
        
      simpleDBObject.put(key,value)
    }
   simpleDBObject.put(rountingNumber,updatedUserName)
     val query = new BasicDBObject("user_id", id);
   // val updateAccountName = new BasicDBObject(rountingNumber,updatedUserName)
    val cursor = collection.find(query)
    // val idCardList = List(DBObject)
    try {
       while(cursor.hasNext()) {
         val gson = new Gson()
          val dbobj = cursor.next()
        //Converting BasicDBObject to a custom Class(Employee)
          //println("CURSOR While updating ID CARD"+dbobj);
         // val emp = (new Gson()).fromJson((dbobj.toString() : String), classOf[User])
         // val jsonString = gson.toJson(userIDCard)
          //println("NEW JSON UPDATE IDCARDS")
          //println(jsonString)
          //println()
    //val jsonElement = gson.fromJson(jsonString.toString(), classOf[User])
   // val userData = new BasicDBObject(JSON.parse(jsonString) : DBObject)
    //val dbObject  = JSON.parse(jsonString).asInstanceOf[DBObject]
          
        //  idCardList.add(dbObject)
           //val updateQuery = 
          collection.update(query,new BasicDBObject("$push", new BasicDBObject("BankAccountData", simpleDBObject)))
          //collection.update(query,new BasicDBObject("$set", updateAccountName))
         // println(emp.getName())
          
       }
    } finally {
       cursor.close()
    }
    simpleDBObject.toString()
  }
  
  
  
  def deleteIdCard(id : String, ba_id : String) {
    println("User id : "+ba_id)
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val simpleDBObject = new BasicDBObject()
     //for((key,value) <- userIDCard) {
     // simpleDBObject.put(key,value)
    //}
     val query = new BasicDBObject("user_id", id);
  
    val updateQuery = new BasicDBObject("IDCardData", new BasicDBObject("card_id",ba_id))
          collection.update(query,new BasicDBObject("$pull", updateQuery))
  }
  
  
  def deleteWebLogin(id : String, ba_id : String) {
    println("User id : "+ba_id)
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val simpleDBObject = new BasicDBObject()
     //for((key,value) <- userIDCard) {
     // simpleDBObject.put(key,value)
    //}
     val query = new BasicDBObject("user_id", id);
   
    val updateQuery = new BasicDBObject("WebLoginData", new BasicDBObject("login_id",ba_id))
          collection.update(query,new BasicDBObject("$pull", updateQuery))
  }
  
  
  def deleteBankAccount(id : String, ba_id : String) {
    println("User id : "+ba_id)
    val coll = connectToDB()
     val collection = coll.getCollection("CMPEAssignment2")
     val simpleDBObject = new BasicDBObject()
     //for((key,value) <- userIDCard) {
     // simpleDBObject.put(key,value)
    //}
     val query = new BasicDBObject("user_id", id);
    val updateQuery = new BasicDBObject("BankAccountData", new BasicDBObject("ba_id",ba_id))
          collection.update(query,new BasicDBObject("$pull", updateQuery))
  }
}