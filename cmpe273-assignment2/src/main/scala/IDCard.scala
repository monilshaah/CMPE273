import org.hibernate.validator.constraints.{NotEmpty}
import com.fasterxml.jackson.databind.annotation.JsonSerialize
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class IDCard() {
private var card_id :Int =_
@NotEmpty(message  = "card_name is mandetory")
private var card_name : String =_
@NotEmpty(message = "card_number is mandetory" )
private var card_number : String =_
private var expiration_date : String =_

def setCard_id (iCardId : Int) {
  card_id = iCardId
}

def getCard_id() : Int = card_id

def setCard_name(iCardName : String) {
  card_name = iCardName
}
def getCard_name () : String = card_name

def setCard_number(iCardNumber : String) {
  card_number = iCardNumber
}
def getCard_number () : String = card_number

def setExpiration_date(iCardExpirationDate : String) {
  expiration_date = iCardExpirationDate
}
def getExpiration_date() : String = expiration_date

}