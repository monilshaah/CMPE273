package hello

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import javax.validation.Valid
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.validation.{BindingResult}
import org.springframework.web.bind.annotation._

@RestController
@EnableAutoConfiguration
@RequestMapping(value=Array("/api/v1"))
class UsersController {
  val ctx = new AnnotationConfigApplicationContext(classOf[SpringMongoConfig])
  val mongoOperation = ctx.getBean("mongoTemplate").asInstanceOf[MongoOperations]
//Users Controller
   //Create user
   @RequestMapping(value = Array("/users"), method = Array(RequestMethod.POST), consumes = Array("application/json"), produces = Array("application/json"))
   @ResponseBody def createUser(@RequestBody @Valid userDetails: Users, bindingResult: BindingResult): ResponseEntity[Any] = {
       println("in users...")
       if (bindingResult.hasErrors) {
           val errorsList = bindingResult.getAllErrors
           var returnList = new util.ArrayList[String]
           var i = 0
           while(i<errorsList.size()) {
              returnList.add((i+1)+". "+errorsList.get(i).getDefaultMessage)
              i += 1
           }
           new ResponseEntity(returnList, HttpStatus.BAD_REQUEST)
       } else {
           println("In else")
           UsersApp.randomUserId += 1
           userDetails.setId("U"+UsersApp.randomUserId)
           userDetails.setIdCardHashMap
           userDetails.setWebLoginHashMap
           userDetails.setBankAccountHashMap
           UsersApp.userSchemaHashMap.update(userDetails.getId,userDetails)
           val eTag = "W/"+(userDetails.getId+userDetails.getEmail+userDetails.getPassword+userDetails.getName+userDetails.getCreatedAt+userDetails.getUpdatedAt).hashCode+"\""
           val responseHeaders: HttpHeaders = new HttpHeaders
           responseHeaders.setETag(eTag)
           val returnMap = new java.util.LinkedHashMap[String,String]
           returnMap.put("user_id",userDetails.getId)
           returnMap.put("email",userDetails.getEmail)
           returnMap.put("password",userDetails.getPassword)
           if (userDetails.getName != null) returnMap.put("name",userDetails.getName)
           returnMap.put("created_at",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userDetails.getCreatedAt))
           //returnMap.put("updated_at",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userDetails.getUpdatedAt))
           println("mongo start...!")
           mongoOperation.insert(userDetails)
            println("mongo finish...!")
           new ResponseEntity(returnMap, responseHeaders, HttpStatus.CREATED)
       }

  }

  //GET user
  @RequestMapping(value = Array("/users/{userId}"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  @ResponseBody def getUserById1(@RequestHeader(value = "If-None-Match", required = false) cachedETag: String, @PathVariable userId: String): ResponseEntity[java.util.LinkedHashMap[String,String]] = {
      //val userDetails = UsersApp.userSchemaHashMap.getOrElse(userId,null)
      val getUserQuery = new Query();
      getUserQuery.addCriteria(Criteria.where("_id").is(userId));
      val userDetails = mongoOperation.findOne(getUserQuery, classOf[Users])
      if (userDetails == null)
          new ResponseEntity(HttpStatus.NOT_FOUND)
      else {
          val currentETag = "W/"+(userDetails.getId+userDetails.getEmail+userDetails.getPassword+userDetails.getName+userDetails.getCreatedAt+userDetails.getUpdatedAt).hashCode+"\""
          println("res="+currentETag.equals(cachedETag))
          println("cachedETag: "+cachedETag+" "+currentETag)
          if (currentETag.equals(cachedETag)) {
              new ResponseEntity(HttpStatus.NOT_MODIFIED)
          } else {
              val responseHeader: HttpHeaders = new HttpHeaders
              responseHeader.setETag(currentETag)
              val returnMap = new java.util.LinkedHashMap[String, String]
              returnMap.put("user_id", userDetails.getId)
              returnMap.put("email", userDetails.getEmail)
              returnMap.put("password", userDetails.getPassword)
              if (userDetails.getName != null) returnMap.put("name", userDetails.getName)
              returnMap.put("created_at", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userDetails.getCreatedAt))
              //returnMap.put("updated_at", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userDetails.getUpdatedAt))
              new ResponseEntity(returnMap, responseHeader, HttpStatus.OK)

          }
      }
  }

   //Update user
   @RequestMapping(value = Array("/users/{userId}"), method = Array(RequestMethod.PUT), consumes = Array("application/json"), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.CREATED)
   //@ResponseBody def updateUserById(@RequestBody @Valid newUserInfo: java.util.HashMap[String,String], @PathVariable userId: String): ResponseEntity[Any] = {
   @ResponseBody def updateUserById(@RequestBody @Valid newUserInfo: Users, @PathVariable userId: String): ResponseEntity[Any] = {
/*            val userDetails = UsersApp.userSchemaHashMap.apply(userId)
            val newItr = newUserInfo.keySet.iterator
            var isUpdated = false
            while (newItr.hasNext) {
              newItr.next() match {
                case "email" =>
                  isUpdated = true
                  println("email" + newUserInfo.get("email"))
                  userDetails.setEmail(newUserInfo.get("email"))
                case "password" =>
                  isUpdated = true
                  println("password" + newUserInfo.get("password"))
                  userDetails.setPassword(newUserInfo.get("password"))
                case "name" =>
                  isUpdated = true
                  println("name" + newUserInfo.get("name"))
                  userDetails.setName(newUserInfo.get("name"))
              }
            }
            if (isUpdated) {
              userDetails.setUpdatedAt(new java.util.Date)
              val currentETag = "W/" + (userDetails.getId + userDetails.getEmail + userDetails.getPassword + userDetails.getName + userDetails.getCreatedAt + userDetails.getUpdatedAt).hashCode + "\""
              val responseHeader: HttpHeaders = new HttpHeaders
              responseHeader.setETag(currentETag)
              responseHeader.setLastModified(userDetails.getUpdatedAt.getTime)
              val returnMap = new java.util.LinkedHashMap[String, String]
              returnMap.put("user_id", userDetails.getId)
              returnMap.put("email", userDetails.getEmail)
              returnMap.put("password", userDetails.getPassword)
              if (userDetails.getName != null) returnMap.put("name", userDetails.getName)
              returnMap.put("created_at", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userDetails.getCreatedAt))
              //returnMap.put("updatedAt", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(userDetails.getUpdatedAt))
              new ResponseEntity(returnMap, responseHeader, HttpStatus.CREATED)
            } else {
              new ResponseEntity(HttpStatus.NO_CONTENT)
            }*/
     var userDetails = new Users
     newUserInfo.setId(userId) // to locate the perticular user by id = userId
     newUserInfo.setUpdatedAt(new Date()) // setting the timeStamp
     mongoOperation.save(newUserInfo) // updating the users

     //********* Fetching the user from  database************//
     val newUserQuery = new Query();
     newUserQuery.addCriteria(Criteria.where("_id").is(userId));
     userDetails = mongoOperation.findOne(newUserQuery, classOf[Users])
     val currentETag = "W/" + (userDetails.getId + userDetails.getEmail + userDetails.getPassword + userDetails.getName + userDetails.getCreatedAt + userDetails.getUpdatedAt).hashCode + "\""
     val responseHeader: HttpHeaders = new HttpHeaders
     responseHeader.setETag(currentETag)
     //println(userDetails)
     return new ResponseEntity(userDetails, responseHeader, HttpStatus.CREATED)
  }

  //IDCard controller
  //Create IDCard record
   @RequestMapping(value= Array("/users/{userId}/idcards"), method = Array(RequestMethod.POST), consumes = Array("application/json"), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.CREATED)
   @ResponseBody def createIDCard(@RequestBody @Valid newCard: IDCard, bindingResult: BindingResult, @PathVariable userId: String): ResponseEntity[Any] = {
      if (bindingResult.hasErrors) {
        val errorsList = bindingResult.getAllErrors
        var returnList = new util.ArrayList[String]
        var i = 0
        while (i < errorsList.size()) {
          returnList.add((i + 1) + ". " + errorsList.get(i).getDefaultMessage)
          i += 1
        }
        new ResponseEntity(returnList, HttpStatus.BAD_REQUEST)
      } else {
          UsersApp.randomCardId += 1
          newCard.setCard_id("C" + UsersApp.randomCardId)
//          val userDetails = UsersApp.userSchemaHashMap.apply(userId)
//          userDetails.getIdCardHashMap.update(newCard.getCard_id, newCard)
//          new ResponseEntity(newCard, HttpStatus.CREATED)
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        //userTest2.makecardmap(usercard)
        userDetails.getIdCardHashMap.update(newCard.getCard_id, newCard)
        mongoOperation.save(userDetails)
        new ResponseEntity(newCard, HttpStatus.CREATED)
      }
   }

  //List all IDCards
   @RequestMapping(value = Array("/users/{userId}/idcards"), method = Array(RequestMethod.GET), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.OK)
   @ResponseBody def getCardsById(@PathVariable userId: String): Array[IDCard] = {
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        val idCards = userDetails.getIdCardHashMap.values.toArray
        idCards
   }

   //Delete IDCard with <card_id> of <userId>
   @RequestMapping(value = Array("users/{userId}/idcards/{cardId}"), method = Array(RequestMethod.DELETE), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @ResponseBody def deleteCardById(@PathVariable userId: String, @PathVariable cardId: String): Unit = {
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        userDetails.getIdCardHashMap -= cardId
        mongoOperation.save(userDetails)
   }

//WebLogin Controller
  //Create Web Login
   @RequestMapping(value= Array("/users/{userId}/weblogins"), method = Array(RequestMethod.POST), consumes = Array("application/json"), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.CREATED)
   @ResponseBody def createWebLogin(@RequestBody @Valid newWebLogin: WebLogin, bindingResult: BindingResult, @PathVariable userId: String): ResponseEntity[Any] = {
      if (bindingResult.hasErrors) {
        val errorsList = bindingResult.getAllErrors
        var returnList = new util.ArrayList[String]
        var i = 0
        while (i < errorsList.size()) {
          returnList.add((i + 1) + ". " + errorsList.get(i).getDefaultMessage)
          i += 1
        }
        new ResponseEntity(returnList, HttpStatus.BAD_REQUEST)
      } else {
          UsersApp.randomLoginId += 1
          newWebLogin.setLogin_id("W" + UsersApp.randomLoginId)
          val userQuery = new Query();
          userQuery.addCriteria(Criteria.where("_id").is(userId));
          val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
          userDetails.getWebLoginHashMap.update(newWebLogin.getLogin_id, newWebLogin)
          mongoOperation.save(userDetails)
          new ResponseEntity(newWebLogin, HttpStatus.CREATED)
      }
   }

  //List all web logins
   @RequestMapping(value = Array("/users/{userId}/weblogins"), method = Array(RequestMethod.GET), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.OK)
   @ResponseBody def getWebLoginById(@PathVariable userId: String): Array[WebLogin] = {
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        val webLogins = userDetails.getWebLoginHashMap.values.toArray
        webLogins
   }

  //Delete web login with <loginId> of <userId>
   @RequestMapping(value = Array("users/{userId}/weblogins/{loginId}"), method = Array(RequestMethod.DELETE), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @ResponseBody def deleteWebLoginById(@PathVariable userId: String, @PathVariable loginId: String): Unit = {
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        userDetails.getWebLoginHashMap -= loginId
        mongoOperation.save(userDetails)
   }

//BankAccount Controller
  //Create Bank Account record
   @RequestMapping(value= Array("/users/{userId}/bankaccounts"), method = Array(RequestMethod.POST), consumes = Array("application/json"), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.CREATED)
   @ResponseBody def createBankAccount(@RequestBody @Valid newBankAccount: BankAccount, bindingResult: BindingResult, @PathVariable userId: String): ResponseEntity[Any] = {
      if (bindingResult.hasErrors) {
        val errorsList = bindingResult.getAllErrors
        var returnList = new util.ArrayList[String]
        var i = 0
        while (i < errorsList.size()) {
          returnList.add((i + 1) + ". " + errorsList.get(i).getDefaultMessage)
          i += 1
        }
        new ResponseEntity(returnList, HttpStatus.BAD_REQUEST)
      } else {
        UsersApp.randomBaId += 1
        newBankAccount.setBa_id("BA" + UsersApp.randomBaId)
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        userDetails.getBankAccountHashMap.update(newBankAccount.getBa_id, newBankAccount)
        mongoOperation.save(userDetails)
        new ResponseEntity(newBankAccount, HttpStatus.CREATED)
      }
   }

  //List all bank accounts
   @RequestMapping(value = Array("/users/{userId}/bankaccounts"), method = Array(RequestMethod.GET), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.OK)
   @ResponseBody def getBankAccountsById(@PathVariable userId: String): Array[BankAccount] = {
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        val bankAccounts = userDetails.getBankAccountHashMap.values.toArray
        bankAccounts
   }

  //Delete bank account with <baId> of <userId>
   @RequestMapping(value = Array("users/{userId}/bankaccounts/{baId}"), method = Array(RequestMethod.DELETE), produces = Array("application/json"))
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @ResponseBody def deleteBankAccountById(@PathVariable userId: String, @PathVariable baId: String): Unit = {
        val userQuery = new Query();
        userQuery.addCriteria(Criteria.where("_id").is(userId));
        val userDetails = mongoOperation.findOne(userQuery, classOf[Users])
        userDetails.getBankAccountHashMap -= baId
        mongoOperation.save(userDetails)
   }
}