package hello

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.hibernate.validator.constraints.NotEmpty

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class IDCard {
	var card_id: String =_
  @NotEmpty(message = "card_name cannot be empty!!!") private var card_name: String =_
  @NotEmpty(message = "card_number cannot be blank!!!") private var card_number: String =_
	private var expiration_date: String =_
	
	def getCard_id = this.card_id
	
	def setCard_id(newId: String) = this.card_id = newId

	def setCard_name(newCardName: String) = this.card_name = newCardName
	
	def getCard_name = this.card_name
	
	def setCard_number(newCardNumber: String) = this.card_number = newCardNumber
	
	def getCard_number = this.card_number
	
	def setExpiration_date(newEDate: String) = this.expiration_date = newEDate
	
	def getExpiration_date = this.expiration_date
}