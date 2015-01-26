package hello


import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.hibernate.validator.constraints.NotEmpty

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class WebLogin {
  private var login_id: String =_
  @NotEmpty(message = "url cannot be blank!!!") private var url: String =_
  @NotEmpty(message = "login cannot be blank!!!")  private var login: String =_
  @NotEmpty(message = "password cannot be blank!!!") private var password: String =_

  def getLogin_id = this.login_id

  def setLogin_id(newId: String) = this.login_id = newId

  def setUrl(newLoginUrl: String) = this.url = newLoginUrl

  def getUrl = this.url

  def setLogin(newLogin: String) = this.login = newLogin

  def getLogin = this.login

  def setPassword(newPassword: String) = this.password = newPassword

  def getPassword = this.password
}