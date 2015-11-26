package com.tradedoubler.rafty;

import io.atomix.copycat.client.Query;

import java.io.Serializable;
import java.util.List;

/**
 * @author qinwa
 */
public class SequentialIdsQuery implements Query<List<String>>, Serializable {
    public  ConsistencyLevel consistency() {
        return ConsistencyLevel.LINEARIZABLE;
    }
}
