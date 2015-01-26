RestAPI
=======
This is class assignment for CMPE-273 (Sithu Aung).
Only "Content-Type: application/json" type supported.


-> Create user - POST - ../users

-> Get user details - GET - ../users/{userId}

-> Conditional GET user details - GET - ../users/{userId}  -- provide ETag value in RequestHeader

-> Update user details - PUT - ../users/{userId}

-> Create ID card for user - POST - ../users/{userId}/idcards

-> List IDCards - GET - ../users/{userId}/idcards

-> Delete IDCards - DELETE - ../users/{userId}/idcards/{cardId}

-> Create web login for user - POST - ../users/{userId}/weblogins

-> List all weblogin - GET - ../users/{userId}/weblogins

-> Delete web login - DELETE - ../users/{userId}/weblogins/{loginId}

-> Create bank account for user - POST - ../users/{userId}/bankaccounts

-> List all bank account details - GET - ../users/{userId}/bankaccounts

-> Delete bank accounts - DELETE - ../users/{userId}/bankaccounts/{baId}


Input JSON example for using the APIs:

User:
-----
{
"email":"monil@gmail.com",
"password":"newsecret",
"name":"monil"
}

IDCard:
-------
{
"card_name":"monil",
"card_number":"9876543210123456",
"expiration_date":"12-12-2017"
}

WebLogin:
---------
{
"url":"/users/userId/weblogin",
"login":"monil",
"password":"secret"
}

BankAccount:
------------
{
"account_name":"monil",
"routine_number":"us9876",
"account_number":"1234567890"
}
