import org.hibernate.validator.constraints.{NotEmpty}
import com.fasterxml.jackson.databind.annotation.JsonSerialize
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class BankAccount {
	private var ba_id :Int =_
	private var account_name : String =_
	@NotEmpty(message = "routing_number is mandetory parameter")
	private var routing_number : String =_
	@NotEmpty(message = "account_number is mandetory parameter")
	private var account_number : String =_

	def setBa_id (ba_id : Int) {
		this.ba_id = ba_id
	}

	def getBa_id() : Int = ba_id

	def setAccount_name(account_name : String) {
		this.account_name = account_name
	}
	def getAccount_name () : String = account_name

	def setRouting_number(routing_number : String) {
		this.routing_number = routing_number
	}
	def getRouting_number () : String = routing_number

	def setAccount_number(account_number : String) {
		this.account_number = account_number
	}
	def getAccount_number() : String = account_number
}