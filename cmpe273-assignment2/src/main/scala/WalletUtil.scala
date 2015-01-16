object WalletUtil {
  
  private var userMap : Map[Int, User] = Map()
  
  def setUser( userId : Int, user : User ) {
    userMap += (userId -> user)
  }
  
  def getUser(userId : Int ) : User = userMap(userId)
  
  def deleteUser(userId : Int ) { 
   userMap -= userId
  }
  
}