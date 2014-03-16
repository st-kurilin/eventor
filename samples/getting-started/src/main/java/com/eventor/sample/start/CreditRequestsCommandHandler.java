package com.eventor.sample.start;

import com.eventor.api.annotations.CommandHandler;

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
