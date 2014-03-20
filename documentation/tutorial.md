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

    
    public class RequestRegistration {
        public final String email;
        public final String name;
        public final String password;
    
        public RequestRegistration(String email, String name, String password) {
            this.email = email;
            this.name = name;
            this.password = password;
        }
    }

Some email with validation token should be send to specified email. After this user can confirm email.

    
    public class ConfirmEmail {
        public final String email;
        public final String token;
    
        public ConfirmEmail(String email, String token) {
            this.email = email;
            this.token = token;
        }
    }

After email gets confirmed person gets registered

    
    public class PersonRegistered {
        public String email;
        public String name;
        public String password;
    
        public PersonRegistered(String email, String name, String password) {
            this.email = email;
            this.name = name;
            this.password = password;
        }
    }

Now he can finally try to do good thinks.

    
    public class TryToDoGoodThink {
        public final String personEmail;
    
        public TryToDoGoodThink(String personEmail) {
            this.personEmail = personEmail;
        }
    }

Good thing could occur

    
    public class GoodThingDone {
        public final String personEmail;
        public final String message;
    
        public GoodThingDone(String personEmail, String message) {
            this.personEmail = personEmail;
            this.message = message;
        }
    }

but sometimes not so good things can happen instead

    
    public class NotSoGoodThingDone {
        public final String personEmail;
    
        public NotSoGoodThingDone(String personEmail) {
            this.personEmail = personEmail;
        }
    }

Domain
------
Now we can implement our domain. We will describe processes using *Sagas*.
Saga can handle messages (events and commands) and generate commands. Each Saga has own lifecycle.

    
    
    
    //Registration Saga represents Registration with email confirmation process.
    @Saga
    public class Registration {
        private SecurityService securityService;
        private String email;
        private String name;
        private String protectedPassword;
    
        public Registration(SecurityService securityService) {
            this.securityService = securityService;
        }
    
        //Start registration process
        @Start
        @CommandHandler
        public Timeout handle(@IdIn("email") RequestRegistration cmd) {
            email = cmd.email;
            name = cmd.name;
            protectedPassword = securityService.protectPassword(email, cmd.password);
            sendEmailWithToken(email, securityService.generateRegistrationToken(email));
            //hadntConfirmedForLongTime will be called in two weeks
            return new Timeout(14, TimeUnit.DAYS, new RegistrationTimeout());
        }
    
        //Try to confirm email
        @CommandHandler
        public Object handle(@IdIn("email") ConfirmEmail cmd) {
            if (securityService.checkRegistrationToken(cmd.email, cmd.token)) {
                return new Object[]{
                        //TODO: should generate command instead of event
                        new PersonRegistered(email, name, protectedPassword),    //Generate Domain Event
                        Finish.RESULT};                                          //Finish Registration Saga
            }
            return null;
        }
    
        //Email hadn't confirmed for long time
        @OnTimeout(RegistrationTimeout.class)
        public Object hadntConfirmedForLongTime() {
            return Finish.RESULT;   //Finish Registration Saga
        }
    
        private static class RegistrationTimeout {
        }
    
        //Fake email sender
        private void sendEmailWithToken(String email, String token) {
            System.out.println(String.format("Email with token [%s] send to [%s]", token, email));
        }
    }

We will describe Person with Aggregate.

    
    
    
    @Aggregate
    public class Person {
        @Id //Person identifier
        private String email;
        private String name;
        private String protectedPassword;
    
        private SecurityService securityService;
        private Random random = new Random();
    
        //securityService should be injected
        public Person(SecurityService securityService) {
            this.securityService = securityService;
        }
    
        @CommandHandler
        public Object handle(@IdIn("personEmail") ChangePassword cmd) {
            if (securityService.isPasswordCorrect(email, protectedPassword, cmd.oldPassword)) {
                String newProtectedPassword = securityService.protectPassword(email, cmd.newPassword);
                return new PasswordChanged(email, newProtectedPassword, new Date());
            }
            return null;
        }
    
        @CommandHandler
        public Object handle(@IdIn("personEmail") TryToDoGoodThink cmd) {
            switch (random.nextInt(10)) {
                case 0:
                    return new NotSoGoodThingDone(email);
                case 1:
                case 2:
                    return null;
                default:
                    return new GoodThingDone(email, "Good thing done by " + name);
            }
        }
    
        @Start          //When PersonRegistered occurs new Person get created
        @EventListener  //Listening events generated by other source.
        public void on(PersonRegistered e) {
            email = e.email;
            name = e.name;
            protectedPassword = e.password;
        }
    
        @EventListener  //Listening events generated by current aggregate
        public void on(PasswordChanged e) {
            protectedPassword = e.newProtectedPassword;
        }
    }

Each aggregate consumes commands and produces events. It can be started by event or command.
It should not call other aggregate during handling.

Explore [samples][2] for more information or send me email directly to st.kurilin@gmail.com.

  [1]: https://github.com/st-kurilin/eventor/blob/master/documentation/getting-started.md
  [2]: https://github.com/st-kurilin/eventor/blob/master/samples
