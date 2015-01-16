
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import org.springframework.context.ApplicationContext;
import org.springframework.boot._;
import org.springframework.web.bind.annotation._;
import org.springframework.boot.autoconfigure._;
import collection.JavaConverters._
import org.springframework.http.MediaType._;
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import java.security.MessageDigest
import java.util.Date
import java.text.SimpleDateFormat

@EnableAutoConfiguration
@RestController
@RequestMapping(value=Array("/api/v1"))
class UserController {
  val userIdStream = Stream.iterate(0)(_ + 1).iterator
  val cardIdStream = Stream.iterate(0)(_ + 1).iterator
  val loginIdStream = Stream.iterate(0)(_ + 1).iterator
  val baIdStream = Stream.iterate(0)(_ + 1).iterator
  var conditionalGetUser : Map[String, String] = Map()

  /**
     * create user in wallet system and return result to end user.
     * accepts json data in post 
     * returns json data to users on success with 201 success code. 
     */  
    @RequestMapping(value = Array("/users"),
         method = Array(RequestMethod.POST), 
         consumes=Array("application/json"),
           produces=Array("application/json"))
           @ResponseBody
    def createNewUser(@RequestBody @Valid usr : User, result : BindingResult) : ResponseEntity[java.util.Map[String,String]] = {
        var httpHeader = new org.springframework.http.HttpHeaders()   
        if(result.hasErrors()) {
          var errors : String =""
              for (errorCodes <- result.getAllErrors().asScala) {
                errors += "{\"status\":\"failure\", \"error-text\":\""+errorCodes.getDefaultMessage()+"\"}"+"\n"  
              }
          var createdStatus = HttpStatus.BAD_REQUEST
         
          return new ResponseEntity(Map("errors"->errors).asJava, httpHeader, createdStatus)
        }
        println("IN CREATION OF DATABASE")
        val walletUtil = WalletUtil
        usr.setUser_id(getIncrementalId())
        walletUtil.setUser(usr.getUser_id(), usr)
        var addedUser = walletUtil.getUser(usr.getUser_id())
        var createdStatus = HttpStatus.CREATED
        println("AFTER SETTING HTTP STATUS")
        httpHeader.set("Cache-Control", "no-cache, no-store, must-revalidate, post-check=0, pre-check=0")
        httpHeader.set("Pragma", "no-cache")
        httpHeader.set("Expires", "0")
        println("BEFORE GETTING DATABASE CONNECTION")
        var collect = DatabaseAdapter
        collect.createUser(Map("user_id"->(addedUser.getUser_id).toString, "email" -> addedUser.getEmail,
            "password"->addedUser.getPassword(), "created_at"->new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(addedUser.getCreated_at()).toString()))
        new ResponseEntity(Map("user_id"->(addedUser.getUser_id).toString, "email" -> addedUser.getEmail, 
            "password"->addedUser.getPassword(), "created_at"->new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(addedUser.getCreated_at()).toString()).asJava, httpHeader, createdStatus)
    } 
  
  @RequestMapping(value=Array("/users/{userId}"),
      method = Array(RequestMethod.GET),
      produces = Array("application/json"))
      @ResponseBody
      def getUserDetails(@PathVariable userId : Int, @RequestHeader( value="Etag", required=false ) etag : String, @RequestHeader( value = "Last-Modified", required=false) last_modified : String) : ResponseEntity[java.util.Map[String,String]] = {
        var httpHeader = new org.springframework.http.HttpHeaders()
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var hashETagCode = ""
          var createdStatus = HttpStatus.NOT_MODIFIED;
        if(null != etag && !etag.isEmpty() && conditionalGetUser.contains(etag.trim()) && conditionalGetUser.get(etag.trim()).get.equalsIgnoreCase(last_modified.trim())) {
           createdStatus = HttpStatus.NOT_MODIFIED
           httpHeader.set("Etag",etag);
           httpHeader.set("Last-Modified",last_modified)
           httpHeader.set("Cache-Control", "no-cache, no-store, must-revalidate, post-check=0, pre-check=0")
           httpHeader.set("Pragma", "no-cache")
           httpHeader.set("Expires", "0")
           return new ResponseEntity(Map().asJava, httpHeader, createdStatus)
        }else {
          hashETagCode = generateHash(usr.getEmail())
          println("Hash ETag else part: "+hashETagCode)
          conditionalGetUser += (hashETagCode.trim()->new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(usr.getUpdated_at()).toString().trim())
          httpHeader.set("Etag",hashETagCode.trim());
          httpHeader.set("Last-Modified",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(usr.getUpdated_at()).toString().trim())
          httpHeader.set("Cache-Control", "no-cache, no-store, must-revalidate, post-check=0, pre-check=0")
          httpHeader.set("Pragma", "no-cache")
          httpHeader.set("Expires", "0")
          createdStatus = HttpStatus.OK
        }
        var collect = DatabaseAdapter
        new ResponseEntity(collect.retrieveUser(userId.toString()).asJava, httpHeader, createdStatus)
  }
  
   @RequestMapping(value=Array("/users/{userId}"),
      method = Array(RequestMethod.PUT),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseStatus(HttpStatus.CREATED)
      @ResponseBody
      def updateUserDetails(@PathVariable userId : Int, @RequestBody @Valid updatedUserData : User, result: BindingResult, @RequestHeader( value = "Etag", required=false) etag : String, @RequestHeader(value="Last-Modified", required= false )last_modified : String) : 
      ResponseEntity[java.util.Map[String,String]] = {
        var httpHeader = new org.springframework.http.HttpHeaders() 
        if(result.hasErrors()) {
          var errors : String =""
              for (errorCodes <- result.getAllErrors().asScala) {
                errors += "{\"status\":\"failure\", \"error-text\":\""+errorCodes.getDefaultMessage()+"\"}"+"\n"  
              }
          var createdStatus = HttpStatus.BAD_REQUEST
         
          return new ResponseEntity(Map("errors"->errors).asJava, httpHeader, createdStatus)
        }
        val walletUtil = WalletUtil
        var hashETagCode = ""
        var usr = walletUtil.getUser(userId)
        println(updatedUserData)
        usr = updateUser(usr, updatedUserData)
        if(null != etag && !etag.isEmpty() && conditionalGetUser.contains(etag.trim()) && conditionalGetUser.get(etag.trim()).get.equalsIgnoreCase(last_modified.trim())) { 
          conditionalGetUser += (etag -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(usr.getUpdated_at()).toString())
          httpHeader.set("Etag",etag);
        }else {
          hashETagCode = generateHash(usr.getEmail())
          println("Hash ETag : "+hashETagCode)
          conditionalGetUser += (hashETagCode->new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(usr.getUpdated_at()).toString())
          httpHeader.set("Etag",hashETagCode);
        }
        var collect = DatabaseAdapter
        collect.updateUser(userId.toString(), updatedUserData)
        httpHeader.set("Last-Modified",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(usr.getUpdated_at()).toString())
        //httpHeader.set("Cache-Control", "max-age=21600")
        var createdStatus = HttpStatus.CREATED
        new ResponseEntity(Map("userId"->(usr.getUser_id).toString, "email" -> usr.getEmail, 
            "password"->usr.getPassword(), "created_at"->new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(usr.getCreated_at()).toString()).asJava, httpHeader, createdStatus)
  }
   
   @RequestMapping(value=Array("/users/{userId}/idcards"),
      method = Array(RequestMethod.POST),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseBody
      def createIcard(@PathVariable userId : Int, @RequestBody @Valid iCard : IDCard, result: BindingResult) : ResponseEntity[java.util.Map[String,String]] = {
          var httpHeader = new org.springframework.http.HttpHeaders() 
          if(result.hasErrors()) {
            var errors : String =""
                for (errorCodes <- result.getAllErrors().asScala) {
                  errors += "{\"status\":\"failure\", \"error-text\":\""+errorCodes.getDefaultMessage()+"\"}"+"\n"  
                }
          var createdStatus = HttpStatus.BAD_REQUEST
          return new ResponseEntity(Map("errors"->errors).asJava, httpHeader, createdStatus)
        }
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        val iCardId = getIncrementalICardId()
        iCard.setCard_id(iCardId)
        usr.setIdCard(iCardId, iCard)
        var usrIcard = usr.getIdCard(iCardId)
        var createdStatus = HttpStatus.CREATED
         var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.updateIdCard(userId.toString(), Map("card_id"->(usrIcard.getCard_id()).toString, "card_name" -> usrIcard.getCard_name(), 
            "card_number"->usrIcard.getCard_number(), "expiration_date"->usrIcard.getExpiration_date()))
        new ResponseEntity(Map("card_id"->(usrIcard.getCard_id()).toString, "card_name" -> usrIcard.getCard_name(), 
            "card_number"->usrIcard.getCard_number(), "expiration_date"->usrIcard.getExpiration_date()).asJava,httpHeader, createdStatus)
  }
   
   @RequestMapping(value=Array("/users/{userId}/idcards"),
      method = Array(RequestMethod.GET),
      produces = Array("application/json"))
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      def getIcard(@PathVariable userId : Int) : String = {
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var usrIcard = usr.getAllICard().toList
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.retrieveIdCards(userId.toString())
        //usrIcard.asJava
   }
   @RequestMapping(value=Array("/users/{userId}/idcards/{cardId}"),
      method = Array(RequestMethod.DELETE),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseStatus(HttpStatus.NO_CONTENT)
      @ResponseBody
      def deleteIcard(@PathVariable userId : Int, @PathVariable cardId : Int)  = {
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.deleteIdCard(userId.toString(), cardId.toString())
        usr.deleteIdCard(cardId)
   }
  
   //////////////////////weblogin///////////////////////////
   
   @RequestMapping(value=Array("/users/{userId}/weblogins"),
      method = Array(RequestMethod.POST),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseStatus(HttpStatus.CREATED)
      @ResponseBody def createWebLogin(@PathVariable userId : Int, @RequestBody @Valid webLogin : WebLogin, result: BindingResult) : ResponseEntity[java.util.Map[String,String]] = {
          var httpHeader = new org.springframework.http.HttpHeaders()
          if(result.hasErrors()) {
          var errors : String =""
         for (errorCodes <- result.getAllErrors().asScala) {
          errors += "{\"status\":\"failure\", \"error-text\":\""+errorCodes.getDefaultMessage()+"\"}"+"\n"  
          }
          var createdStatus = HttpStatus.BAD_REQUEST
          return new ResponseEntity(Map("errors"->errors).asJava, httpHeader, createdStatus)
        }   
     val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        val webLoginId = getIncrementalWebLoginId()
        webLogin.setLogin_id(webLoginId)
        usr.setWebLoginData(webLoginId, webLogin)
       // var collect = DatabaseAdapter
       // collect.updateUser(userId, usr)
        var usrWebLogin = usr.getWebLoginData(webLoginId)
        var createdStatus = HttpStatus.CREATED
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.updateWebLogins(userId.toString(),Map("login_id"->(usrWebLogin.getLogin_id()).toString, "url" -> usrWebLogin.getUrl(), 
            "login"->usrWebLogin.getLogin(), "password"->usrWebLogin.getPassword()))
         new ResponseEntity(Map("login_id"->(usrWebLogin.getLogin_id()).toString, "url" -> usrWebLogin.getUrl(), 
            "login"->usrWebLogin.getLogin(), "password"->usrWebLogin.getPassword()).asJava, httpHeader, createdStatus)
        
  }
   
   @RequestMapping(value=Array("/users/{userId}/weblogins"),
      method = Array(RequestMethod.GET),
      produces = Array("application/json"))
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody def getWebLogin(@PathVariable userId : Int) : String = {
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var usrWebLogin = usr.getAllWebLogin().toList
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.retrieveWebLogins(userId.toString())
       // usrWebLogin.asJava
   }
   @RequestMapping(value=Array("/users/{userId}/weblogins/{webLoginId}"),
      method = Array(RequestMethod.DELETE),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseStatus(HttpStatus.NO_CONTENT)
      @ResponseBody def deleteWebLogin(@PathVariable userId : Int, @PathVariable webLoginId : Int) = {
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.deleteWebLogin(userId.toString(), webLoginId.toString())
        usr.deleteWebLogin(webLoginId)
   }
  
   ////////////////////////Bank Accounts////////////////////////////////////
    @RequestMapping(value=Array("/users/{userId}/bankaccounts"),
      method = Array(RequestMethod.POST),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseStatus(HttpStatus.CREATED)
      @ResponseBody
      def createBankAccount(@PathVariable userId : Int, @RequestBody @Valid bankAccount : BankAccount, result: BindingResult) : ResponseEntity[String] = {
      var httpHeader = new org.springframework.http.HttpHeaders() 
      if(result.hasErrors()) {
          var errors : String =""
         for (errorCodes <- result.getAllErrors().asScala) {
          errors += "{\"status\":\"failure\", \"error-text\":\""+errorCodes.getDefaultMessage()+"\"}"+"\n"  
          }
          var createdStatus = HttpStatus.BAD_REQUEST
          return new ResponseEntity(("{\"errors\":\""+errors+"\"}"), httpHeader, createdStatus)
        }
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        val bankAccountId = getIncrementalBankAccountId()
        bankAccount.setBa_id(bankAccountId)
        usr.setBankAccount(bankAccountId, bankAccount)
       // var collect = DatabaseAdapter
        //collect.updateUser(userId, usr)
        var usrBankAccount = usr.getBankAccount(bankAccountId)
        var createdStatus = HttpStatus.CREATED
         var collectionOfIdCards = DatabaseAdapter
         try {
        return new ResponseEntity(collectionOfIdCards.updateBankAccounts(userId.toString(),Map("ba_id"->(usrBankAccount.getBa_id()).toString, "account_name" -> usrBankAccount.getAccount_name(), 
            "routing_number"->usrBankAccount.getRouting_number(), "account_number"->usrBankAccount.getAccount_number())), httpHeader, createdStatus)
         }catch {
           case e: Exception =>
           createdStatus = HttpStatus.BAD_REQUEST
           return new ResponseEntity("{ \"Invalid Rounting Number\":\""+usrBankAccount.getRouting_number()+"\"}", httpHeader, createdStatus)
         }
            /*new ResponseEntity(Map("ba_id"->(usrBankAccount.getBa_id()).toString, "account_name" -> usrBankAccount.getAccount_name(), 
            "routing_number"->usrBankAccount.getRouting_number(), "account_number"->usrBankAccount.getAccount_number()).asJava, httpHeader, createdStatus)
             */  
         }
   
   @RequestMapping(value=Array("/users/{userId}/bankaccounts"),
      method = Array(RequestMethod.GET),
      produces = Array("application/json"))
      @ResponseStatus(HttpStatus.OK)
      @ResponseBody
      def getBankAccount(@PathVariable userId : Int) : String = {
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var usrBankAccount = usr.getAllBankAccounts().toList
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.retrieveBankAccounts(userId.toString())
       // usrBankAccount.asJava
   }
   @RequestMapping(value=Array("/users/{userId}/bankaccounts/{ba_id}"),
      method = Array(RequestMethod.DELETE),
      produces = Array("application/json"),
      consumes = Array("application/json"))
      @ResponseStatus(HttpStatus.NO_CONTENT)
      @ResponseBody
      def deleteBankAccount(@PathVariable userId : Int, @PathVariable ba_id : Int)  = {
        val walletUtil = WalletUtil
        var usr = walletUtil.getUser(userId)
        var collectionOfIdCards = DatabaseAdapter
        collectionOfIdCards.deleteBankAccount(userId.toString(), ba_id.toString())
        usr.deleteBankAccount(ba_id)
   }
  
   
   
   def updateUser (existingUser : User, newUser : User) : User = {
       
         if(null!=newUser.getEmail()) { existingUser.setEmail(newUser.getEmail()) }
         if(null!=newUser.getPassword()) { existingUser.setPassword(newUser.getPassword()) }
         if(null!=newUser.getName()) { existingUser.setName(newUser.getName()) }
         
       //}
     //}
     existingUser.setUpdated_at( new Date())
     existingUser
   }
   
   def generateHash(email : String) :String = {
   val hashETag = MessageDigest.getInstance("MD5").digest(email.getBytes());
   ""+hashETag
   }
  def getIncrementalId() : Int = {
    val number: Int = userIdStream.next
    number
  }
  def getIncrementalICardId() : Int = {
    val number: Int = cardIdStream.next
    number
  }
  def getIncrementalWebLoginId() : Int = {
    val number : Int = loginIdStream.next
    number;
  }
  
  def getIncrementalBankAccountId() : Int = {
    val number : Int = baIdStream.next
    number;
  }
}

object UserController {
  def main( arg : Array[String] ) {
    var config : Array[Object] = Array ( classOf[UserController] )
    SpringApplication.run(config, arg)
  }
}