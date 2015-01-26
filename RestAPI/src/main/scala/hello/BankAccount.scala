package hello

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.hibernate.validator.constraints.NotEmpty

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
class BankAccount {
  private var ba_Id: String =_
  private var account_name: String =_
  @NotEmpty(message = "routine_number cannot be blank!!!") private var routine_number: String =_
  @NotEmpty(message = "account_number cannot be blank!!!") private var account_number: String =_

  def getBa_id = this.ba_Id

  def setBa_id(newBaId: String) = this.ba_Id = newBaId

  def setAccount_name(newAccountName: String) = this.account_name = newAccountName

  def getAccount_name = this.account_name

  def setRoutine_number(newRoutineNumber: String) = this.routine_number = newRoutineNumber

  def getRoutine_number = this.routine_number

  def setAccount_number(newAccountNumber: String) = this.account_number = newAccountNumber

  def getAccount_number = this.account_number
}