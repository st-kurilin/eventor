Eventor - Getting Started
=======

Write side - Domain
-------
We will start with defining *Commands* that should be handled by our application. We use regular Java classes to describe it.

    
    public class RequestCredit {
        private final String trackingId;
        private final int amount;
    
        public RequestCredit(String trackingId, int amount) {
            this.trackingId = trackingId;
            this.amount = amount;
        }
    
        public String getTrackingId() {
            return trackingId;
        }
    
        public int getAmount() {
            return amount;
        }
    }

So, Command is just simple POJO that will be handled by our App.

On next step we will define *Events* that can occur in our application.


    
    public class CreditRequestAccepted {
        private final String trackingId;
        private final int amount;
    
        public CreditRequestAccepted(String trackingId, int amount) {
            this.trackingId = trackingId;
            this.amount = amount;
        }
    
        public String getTrackingId() {
            return trackingId;
        }
    
        public int getAmount() {
            return amount;
        }
    }


    
    public class CreditRequestDeclined {
        private final String trackingId;
        private final String reasonMessage;
    
        public CreditRequestDeclined(String trackingId, String reasonMessage) {
            this.trackingId = trackingId;
            this.reasonMessage = reasonMessage;
        }
    
        public String getTrackingId() {
            return trackingId;
        }
    
        public String getReasonMessage() {
            return reasonMessage;
        }
    }


As you can see it's POJO's too.

Know let's define our domain. To do this we need to add Eventor framework as a dependency of application

    <dependencies>
        <dependency>
            <groupId>com.eventor</groupId>
            <artifactId>eventor</artifactId>
            <version>0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>eventor</id>
            <url>https://raw.github.com/st-kurilin/eventor/master/mvn-repo</url>
        </repository>
    </repositories>


We will describe our domain with simple *Command Handler*.

    
    
    @CommandHandler
    public class CreditRequestsCommandHandler {
        @CommandHandler
        public Object handle(RequestCredit cmd) {
            String trackingId = cmd.getTrackingId();
            if (cmd.getAmount() < 200) {
                return new CreditRequestDeclined(trackingId, "Our Bank doesn't provide credit's for amounts less than 200");
            }
            if (cmd.getAmount() > 1000) {
                return new CreditRequestDeclined(trackingId, "Our Bank doesn't provide credit's for amounts more than 1000");
            }
            return new CreditRequestAccepted(trackingId, cmd.getAmount());
        }
    }

Now, let's cover it with unit tests. We will need to add additional dependencies.

    <dependency>
        <groupId>com.eventor</groupId>
        <artifactId>test-framework</artifactId>
        <version>0.1-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
    </dependency>

Now we can write our test

    
    
    public class CreditRequestsCommandHandlerTest {
        @Test
        public void testDecline() throws Exception {
            new Fixture(CreditRequestsCommandHandler.class)
                    .givenEvents()
                    .whenCommands(new RequestCredit("U1", 10))
                    .then()
                    .eventsContainsAllOf(CreditRequestDeclined.class);
        }
    
        @Test
        public void testAccept() throws Exception {
            new Fixture(CreditRequestsCommandHandler.class)
                    .givenEvents()
                    .whenCommands(new RequestCredit("U1", 400))
                    .then()
                    .eventsContainsAllOf(new CreditRequestAccepted("U1", 400));
        }
    }

Read side
-------
So far so good, but how we can read information from our application? We define event listeners for this purpose.

    
    
    @EventListener
    public class CreditDecisionStat {
        private int total;
        private int accepted;
    
        public double getAcceptanceRate() {
            if (total == 0) {
                return 100;
            }
            return 100. * accepted / total;
        }
    
        @EventListener
        public void on(CreditRequestAccepted e) {
            total++;
            accepted++;
        }
    
        @EventListener
        public void on(CreditRequestDeclined e) {
            total++;
        }
    }

Let's cover listener with tests.

    
    
    
    public class CreditDecisionStatTest {
        @Test
        public void testAcceptedAllForZeroTotal() throws Exception {
            CreditDecisionStat stat = new CreditDecisionStat();
    
            assertEquals(100.0, stat.getAcceptanceRate(), 0.1);
        }
    
        @Test
        public void testAcceptedRate() throws Exception {
            CreditDecisionStat stat = new CreditDecisionStat();
    
            EventsApplier.apply(stat,
                    new CreditRequestAccepted("U1", 200),
                    new CreditRequestAccepted("M2", 200),
                    new CreditRequestDeclined("U3", "Declined"),
                    new CreditRequestAccepted("U6", 200)
            );
    
            assertEquals(75.0, stat.getAcceptanceRate(), 0.1);
        }
    }

Putting it all together
-------

Now let's finally run our application.

    
    
    public class Runner {
        public static void main(String[] args) throws InterruptedException {
            //You might use spring or guice instance creator instead of SimpleInstanceCreator in real world app
            InstanceCreator instanceCreator = new SimpleInstanceCreator();
            Eventor eventor = new EventorBuilder()
                    .withInstanceCreator(instanceCreator)
                    .addClasses(CreditRequestsCommandHandler.class)
                    .build();
            CreditDecisionStat stat = instanceCreator.getInstanceOf(CreditDecisionStat.class);
    
            for (int i = 1; i < 15; i++) {
                eventor.submit(new RequestCredit("U" + i, i * 100));
            }
            Thread.sleep(1000); //Let's wait for eventual consistency
    
            System.out.println(stat.getAcceptanceRate());
        }
    }


Summarize
-------

When you are going to build domain centric app with heavy read requests it's could be good point to separate Domain API, Domain and View.

![Abstract][1]


*API* consist from *Commands* and *Events*. Commands are something we want to happen, and Events are something that has happened.

*Domain* handles Commands and produce Events.

*View* consumes events generated by Domain. It should make system prepare for read requests.



[1]: http://goo.gl/lSei7P
