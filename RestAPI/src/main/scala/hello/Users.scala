package hello

import java.util.Date
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.hibernate.validator.constraints.{NotEmpty, Email}
import scala.collection.mutable.HashMap
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class Users {
  private var id: String = _
  @NotEmpty(message = "email cannot be empty!!!") @Email(message = "Invalid Email address!!!") private var email: String = _
  @NotEmpty(message = "password cannot be blank!!!") private var password: String = _
  private var name: String = _
  private var createdAt: Date = new Date()
  private var updatedAt: Date = new Date()
  private var idCardHashMap: HashMap[String, IDCard] = _
  private var webLoginHashMap: HashMap[String, WebLogin] = _
  private var bankAccountHashMap: HashMap[String, BankAccount] = _

  //Define getter-setter method for @RequestBody to use
  def getId = this.id

  def setId(newId: String) = this.id = newId

  def getEmail = this.email

  def setEmail(newEmail: String) = this.email = newEmail

  def getPassword = this.password

  def setPassword(newPassword: String) = this.password = newPassword

  def getName = this.name

  def setName(newName: String) = this.name = newName

  def getCreatedAt = this.createdAt	

  def setCreatedAt(cTime: Date) = this.createdAt = cTime

  def getUpdatedAt = this.updatedAt

  def setUpdatedAt(uTime: Date) = this.updatedAt = uTime

  def getIdCardHashMap = this.idCardHashMap

  def setIdCardHashMap = this.idCardHashMap = new HashMap

  def getWebLoginHashMap = this.webLoginHashMap

  def setWebLoginHashMap = this.webLoginHashMap = new HashMap

  def getBankAccountHashMap = this.bankAccountHashMap

  def setBankAccountHashMap = this.bankAccountHashMap = new HashMap

}