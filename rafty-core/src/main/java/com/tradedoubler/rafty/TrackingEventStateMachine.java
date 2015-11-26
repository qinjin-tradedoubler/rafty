package com.tradedoubler.rafty;

import com.google.common.collect.Lists;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.StateMachineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinwa
 */
public class TrackingEventStateMachine extends StateMachine {
    private static final Logger log = LoggerFactory.getLogger(TrackingEventStateMachine.class);
    private final Map<Object, Commit<SequentialCommand>> sequentialMap = new HashMap<>();
    private final Map<Object, Commit<ParallelEventCommand>> parallelMap = new HashMap<>();
    private final List<String> sequentialEventIds = Lists.newArrayList();
    private final List<String> parallelEventIds = Lists.newArrayList();

    @Override
    protected void configure(StateMachineExecutor executor) {
        executor.register(SequentialCommand.class, this::addSequentialEvent);
        executor.register(ParallelEventCommand.class, this::addParallelEvent);
        executor.register(SequentialEventQuery.class, this::getSequentialEvent);
        executor.register(ParallelEventQuery.class, this::getParallelEvent);

        executor.register(SequentialIdsQuery.class, this::getAllSequentialEventIds);
        executor.register(ParallelIdsQuery.class, this::getAllParallelEventIds);
    }

    private TrackingEvent addSequentialEvent(Commit<SequentialCommand> commit) {
        log.debug("Sequential Event added: commitIndex-"+commit.index()+" " + commit.operation().value());
        String key  = commit.operation().key();
        sequentialMap.put(key, commit);
        sequentialEventIds.add(key);
        return commit.operation().value();
    }

    private TrackingEvent addParallelEvent(Commit<ParallelEventCommand> commit) {
        log.debug("Parallel Event added: commitIndex-" + commit.index() + " " + commit.operation().value());
        String key  = commit.operation().key();
        parallelMap.put(key, commit);
        parallelEventIds.add(key);
        return commit.operation().value();
    }

    private List<String> getAllSequentialEventIds(Commit<SequentialIdsQuery> commit){
        return sequentialEventIds;
    }

    private List<String> getAllParallelEventIds(Commit<ParallelIdsQuery> commit){
        return parallelEventIds;
    }

    private TrackingEvent getSequentialEvent(Commit<SequentialEventQuery> commit) {
        try {
            Commit<SequentialCommand> value = sequentialMap.get(commit.operation().key());
            if(value == null){
                System.out.println("Query Sequential event "+commit.operation().key()+" not found.");
                return null;
            } else {
                TrackingEvent event = value.operation().value();
                System.out.println("Query Sequential event "+commit.operation().key()+" found: "+ event);
                return event;
            }
        } finally {
            commit.close();
        }
    }

    private TrackingEvent getParallelEvent(Commit<ParallelEventQuery> commit) {
        try {
            Commit<ParallelEventCommand> value = parallelMap.get(commit.operation().key());
            if(value == null){
                log.debug("Query Parallel event " + commit.operation().key() + " not found.");
                return null;
            } else {
                TrackingEvent event = value.operation().value();
                log.debug("Query Parallel event " + commit.operation().key() + " found: " + event);
                return event;
            }
        } finally {
            commit.close();
        }
    }
}
