Eventor
=======

Exploring idea of building CQRS + DDD + Event Sourcing framework with Akka using Java.


General View
-------

System should be build with clear separation of API, Domain and View.

*API* consist from *Commands* and *Events*. Commands are something we want to happen, and Events are something that has happened.

*Domain* handles Commands and produce Events. It should be described in terms of *Aggregates*, *Sagas*, *Command Handlers*, *Services*, *Repositories*.

*View* consumes events generated by Domain. It should make system prepare for read requests.


Sample
-------

Let's consider sample for registration with email confirmation.

###API
####Commands
@include ./sample/src/main/java/com/shop/api/registration/RegisterRequest.java
@include ./sample/src/main/java/com/shop/api/registration/ConfirmEmail.java

####Events
@include ./sample/src/main/java/com/shop/api/registration/PersonRegistered.java

###Domain
@include ./sample/src/main/java/com/shop/domain/Registration.java

###View
@include ./sample/src/main/java/com/shop/view/UsersList.java