package com.tradedoubler.rafty;

import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.StateMachineExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinwa
 */
public class TrackingEventStateMachine extends StateMachine {
    private final Map<Object, Commit<TrackingEventCommand>> map = new HashMap<>();

    @Override
    protected void configure(StateMachineExecutor executor) {
        executor.register(TrackingEventCommand.class, this::addEvent);
        executor.register(TrackingEventQuery.class, this::getEvent);
    }

    private TrackingEvent addEvent(Commit<TrackingEventCommand> commit) {
        // Store the full commit object in the map to ensure we can properly clean it from the commit log once we're done.
        System.out.println("Event added: "+commit.operation().value());
        map.put(commit.operation().key(), commit);
        return commit.operation().value();
    }

    private TrackingEvent getEvent(Commit<TrackingEventQuery> commit) {
        try {
            // Get the commit value and return the operation value if available.
            Commit<TrackingEventCommand> value = map.get(commit.operation().key());
            if(value == null){
                System.out.println("Query event "+commit.operation().key()+" not found.");
                return null;
            } else {
                TrackingEvent event = value.operation().value();
                System.out.println("Query event "+commit.operation().key()+" found: "+ event);
                return event;
            }
        } finally {
            // Close the query commit once complete to release it back to the internal commit pool.
            // Failing to do so will result in warning messages.
            commit.close();
        }
    }
}
