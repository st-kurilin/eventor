package com.eventor.sample.start;

import com.eventor.Eventor;
import com.eventor.EventorBuilder;
import com.eventor.api.InstanceCreator;
import com.eventor.impl.SimpleInstanceCreator;

public class Runner {
    public static void main(String[] args) throws InterruptedException {
        //You might use spring or guice instance creator instead of SimpleInstanceCreator in real world app
        InstanceCreator instanceCreator = new SimpleInstanceCreator();
        Eventor eventor = new EventorBuilder()
                .withInstanceCreator(instanceCreator)
                .addClasses(CreditRequestsCommandHandler.class, CreditDecisionStat.class)
                .build();

        for (int i = 1; i < 11; i++) {
            eventor.submit(new RequestCredit("U" + i, i * 100));
        }
        Thread.sleep(1000); //Let's wait for eventual consistency
        CreditDecisionStat stat = instanceCreator.findInstanceOf(CreditDecisionStat.class);
        System.out.println(stat.getAcceptanceRate());
    }
}
