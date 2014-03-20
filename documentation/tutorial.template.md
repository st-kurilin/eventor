Eventor - Tutorial
=======

Exploring idea of building CQRS + DDD + Event Sourcing framework with Akka using Java.

We are working hard to implement this framework.

Start explore it from [Getting Started][1].

As we find out in Getting started section we can clearly separate Domain API, Domain and View.
We discovered that we can express Domain API using commands and events. Also we implement View as event listeners for domain events.
The hard part is Domain. We expressed it with Command Handlers but we need more things to be able for effective Domain modeling.

Let's consider next domain.
Person's can try to do good things. With high probability good things gets done.  Sometimes not so good thing gets done instead.
To perform actions Person should be registered. To register person should confirm his email.
If case of person hadn't confirmed his registration in two weeks registration become canceled.


Commands and Events
------
We will start model our domain with actions that could be handled by our domain and events that can be fired during handling.

User can request registration.

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/RequestRegistration.java

Some email with validation token should be send to specified email. After this user can confirm email.

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/ConfirmEmail.java

After email gets confirmed person gets registered

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/PersonRegistered.java

Now he can finally try to do good thinks.

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/TryToDoGoodThink.java

Good thing could occur

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/GoodThingDone.java

but sometimes not so good things can happen instead

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/NotSoGoodThingDone.java

Domain
------
Now we can implement our domain. We will describe processes using *Sagas*.
Saga can handle messages (events and commands) and generate commands. Each Saga has own lifecycle.

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/Registration.java

We will describe Person with Aggregate.

@include ./samples/tutorial/src/main/java/com/eventor/sample/tutorial/Person.java

Each aggregate consumes commands and produces events. It can be started by event or command.
It should not call other aggregate during handling.

Explore [samples][2] for more information or send me email directly to st.kurilin@gmail.com.

  [1]: https://github.com/st-kurilin/eventor/blob/master/documentation/getting-started.md
  [2]: https://github.com/st-kurilin/eventor/blob/master/samples
