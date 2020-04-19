package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import edu.rice.pcdp.PCDP;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        int primeCnt = 0;
        final SieveActorActor actor = new SieveActorActor(2);

        PCDP.finish(() -> {
            for (int i = 3; i <= limit; i+=2) {
                actor.send(i);
            }
        });
        primeCnt = SieveActorActor.thhreadNums;
        
        primeCnt = 0;
        SieveActorActor loopActor = actor;
        while (loopActor != null) {
            primeCnt++;
            loopActor = loopActor.nextActor;
        }

        return primeCnt;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private SieveActorActor nextActor = null;
        private int localPrime = 0;
        public static int thhreadNums = 0;

        public SieveActorActor (int localPrime) {
            thhreadNums++;
            this.localPrime = localPrime;
        }
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;
            if (candidate <= 0) {
                if (nextActor != null) {
                    nextActor.send(msg);
                }
            } else {
                if (candidate != localPrime) {
                    final boolean nonMultiple = ((candidate % localPrime) != 0);
                    if (nonMultiple) {
                        if (nextActor == null) {
                            nextActor = new SieveActorActor(candidate);
                        } else {
                            nextActor.send(msg);
                        }
                    }
                }
            }

        }
    }
}
