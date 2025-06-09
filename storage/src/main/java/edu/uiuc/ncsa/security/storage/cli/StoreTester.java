package edu.uiuc.ncsa.security.storage.cli;

import edu.uiuc.ncsa.security.core.*;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.MemoryStore;

import java.io.IOException;
import java.util.List;

/**
 * Command line tester for store commands. it uses an in memory store.
 */
public class StoreTester extends StoreCommands{
    public StoreTester(MyLoggingFacade logger, Store store) throws Throwable {
        super(logger, store);
    }

    @Override
    public void extraUpdates(Identifiable identifiable, int magicNumber) throws IOException {

    }

    @Override
    protected String format(Identifiable identifiable) {
        return "";
    }

    @Override
    protected int updateStorePermissions(Identifier newID, Identifier oldID, boolean copy) {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }

    public static class TestMemoryStore extends MemoryStore {
        public TestMemoryStore(IdentifiableProvider identifiableProvider) {
            super(identifiableProvider);
        }

        @Override
        public XMLConverter getXMLConverter() {
            return null;
        }

        @Override
        public List getMostRecent(int n, List attributes) {
            return List.of();
        }
    }
    public static void main(String[] args) {

    }
}
