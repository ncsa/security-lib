package edu.uiuc.ncsa.security.util.pkcs;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.QueuePopulationThread;
import edu.uiuc.ncsa.security.core.util.QueueWithSpare;

import java.security.KeyPair;

/**
 * Worker thread that creates {@link KeyPair}s and puts them into a queue.
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/12 at  2:35 PM
 */
public class KeyPairPopulationThread extends QueuePopulationThread<KeyPair> {
    public KeyPairPopulationThread(int maxQueueSize, long sleepInterval, QueueWithSpare<KeyPair> q) {
        super(maxQueueSize, sleepInterval, q);
    }

    public KeyPairPopulationThread(QueueWithSpare<KeyPair> q) {
        super(q);
    }

    @Override
    protected KeyPair createNew() {
        try {
            return KeyUtil.generateKeyPair();
        } catch (Exception e) {
            throw new GeneralException("Error generating keypair", e);
        }
    }
}
