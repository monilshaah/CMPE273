
import org.hibernate.validator.constraints.{NotEmpty}
import com.fasterxml.jackson.databind.annotation.JsonSerialize
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class WebLogin {
private var login_id :Int =_
@NotEmpty(message = "url is mandetory parameter")
private var url : String =_
@NotEmpty(message = "login is mandetory parameter")
private var login : String =_
@NotEmpty(message = "password is mandetory parameter")
private var password : String =_

def setLogin_id (login_id : Int) {
  this.login_id = login_id
}

def getLogin_id() : Int = login_id

def setUrl(url : String) {
  this.url = url
}
def getLogin () : String = login

def setLogin(login : String) {
  this.login = login
}
def getUrl () : String = url

def setPassword(password : String) {
  this.password = password
}
def getPassword() : String = password

}