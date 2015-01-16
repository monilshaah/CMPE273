import collection.immutable.HashMap
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import scala.collection.mutable.MutableList
import org.hibernate.validator.constraints.{NotEmpty, Email}
import com.mongodb._
import org.springframework.data.mongodb.core.mapping.Document;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@Document(collection = "users")
class User {
  
	var IdCardData : Map[Int, IDCard] = Map() 
	var webLoginData : Map[Int, WebLogin] = Map()
	var bankAccount : Map[Int, BankAccount] = Map()
	private var user_id : Int =_
	@NotEmpty(message = "Email field must be provided")
	@Email(message = "Not a valid Email Address!")
	private var email : String =_
	@NotEmpty(message = "password field must be provided")
	private var password : String =_
	private var name : String =_
	private var created_at : Date = new Date()
	  //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()).toString()
	private var updated_at : Date = new Date()
  
	def getUser_id():Int  = user_id
	
	def setUser_id(uId : Int ) {
	  user_id = uId
	}
	
	def getEmail():String = email
	def setEmail( emailId : String) {
	  email = emailId
	}
	def getPassword():String = password
	
	def setPassword( pass : String) {
	  password = pass
	}
	
	def getName():String = name
	
	def setName( userName : String) {
	  name = userName
	}
	
	def getCreated_at():Date = created_at
	
	def setCreated_at( created : Date) {
	  created_at = created
	}
	
	def getUpdated_at():Date = updated_at
	
	def setUpdated_at( updated : Date)
	{
	  updated_at = updated
	}
  
  def setIdCard( userId : Int, idCard : IDCard ) {
    IdCardData += (userId -> idCard)
  }
  
  def getIdCard(iCardId : Int ) : IDCard = IdCardData(iCardId)
  
  def getAllICard() : MutableList[IDCard] = {
    val allIcards : MutableList[IDCard] = MutableList()
    
    for( (key,value) <- IdCardData ) {
      allIcards += value 
    }
    allIcards
  }
  
    def getAllWebLogin() : MutableList[WebLogin] = {
    val allIcards : MutableList[WebLogin] = MutableList()
    
    for( (key,value) <- webLoginData ) {
      allIcards += value 
    }
    allIcards
  }
    
      def getAllBankAccounts() : MutableList[BankAccount] = {
    val allIcards : MutableList[BankAccount] = MutableList()
    
    for( (key,value) <- bankAccount ) {
      allIcards += value 
    }
    allIcards
  }
  def deleteIdCard(userId : Int ) { 
   IdCardData -= userId
  }
  
   def setWebLoginData( userId : Int, webData : WebLogin ) {
    webLoginData += (userId -> webData)
  }
  
  def getWebLoginData(userId : Int ) : WebLogin = webLoginData(userId)
  
  def deleteWebLogin(userId : Int ) { 
   webLoginData -= userId
  }
  
   def setBankAccount( userId : Int, bankAccountData : BankAccount ) {
    bankAccount += (userId -> bankAccountData)
  }
  
  def getBankAccount(userId : Int ) : BankAccount = bankAccount(userId)
  
  def deleteBankAccount(userId : Int ) { 
   bankAccount -= userId
  }
  
	
}