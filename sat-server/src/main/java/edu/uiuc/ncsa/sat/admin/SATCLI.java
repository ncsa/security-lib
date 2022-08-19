package edu.uiuc.ncsa.sat.admin;

import edu.uiuc.ncsa.sat.client.ClientProvider;
import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.MemoryStore;
import edu.uiuc.ncsa.security.storage.XMLMap;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.util.cli.CLIDriver;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import edu.uiuc.ncsa.security.util.cli.StoreCommands;

import java.io.*;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  2:48 PM
 */
public class SATCLI extends StoreCommands {
    protected SATCLI(MyLoggingFacade logger, String defaultIndent, Store store) {
        super(logger, defaultIndent, store);
    }

    @Override
    public String getPrompt() {
        return "sat>";
    }

    public SATCLI(MyLoggingFacade logger, Store store) {
        super(logger, store);
    }

    @Override
    public String getName() {
        return "sat";
    }

    public static final String FILE_FLAG = "-f";
    public static final String KEYS_FLAG = "-keys";
    protected void showSerializeHelp() {
        say("serialize  [" + FILE_FLAG + " path] index");
        sayi("Usage: XML serializes an object and either shows it on the ");
        sayi("   command line or put it in a file. Cf. deserialize.");
        sayi("See also: deserialize.");
    }
    @Override
    public void serialize(InputLine inputLine) {
        if(showHelp(inputLine)){
            showSerializeHelp();
            return;
        }
      Identifiable x = findItem(inputLine);
        OutputStream os = System.out;
        boolean hasFile = false;
        if (inputLine.hasArg(FILE_FLAG)) {
            try {
                os = new FileOutputStream(inputLine.getNextArgFor(FILE_FLAG));
                hasFile = true;
            } catch (FileNotFoundException e) {
                say("warning, could not find file in argument \"" + inputLine.getNextArgFor(FILE_FLAG));
            }
            inputLine.removeSwitchAndValue(FILE_FLAG);
        }

        XMLMap c = new XMLMap();
        getStore().getXMLConverter().toMap(x, c);

        if (inputLine.hasArg(KEYS_FLAG)) {
            List<String> keys = inputLine.getArgList(KEYS_FLAG);
            inputLine.removeSwitchAndValue(KEYS_FLAG);
            // c now contains all the fields. We remove anything
            XMLMap subset = new XMLMap();
            // put in the identifier
            MapConverter mc = (MapConverter) getStore().getXMLConverter();

            subset.put(mc.getKeys().identifier(), x.getIdentifierString());
            for (String key : keys) {
                if (c.containsKey(key)) {
                    subset.put(key, c.get(key));
                }
            }
            c = subset; // set it to the right variable to get serialized.
        }

        try {
            c.toXML(os);
            if (hasFile) {
                os.flush();
                os.close();
            }
            say("done!");
        } catch (IOException e) {
            say("Error serializing object.");
        }
    }

    public static final String UPDATE_FLAG = "-update";
    public static final String SHORT_UPDATE_FLAG = "-u";
    protected void showDeserializeHelp() {
        say("deserialize  [-new] -file path [" + SHORT_UPDATE_FLAG + "|" + UPDATE_FLAG + "]");
        sayi("Usage: Deserializes an object into the current store overwriting the contents. Cf. serialize.");
        sayi("This replaces the object with the given index in the store.\n");
        sayi("If the -new flag is used, it is assumed that the object should be new. This means that if there is an existing object");
        sayi("with that identifier the operation will fail. If there is no identifier, one will be created.");
        sayi("Omitting the -new flag means that any object will be overwritten and if needed, a new identifier will be created");
        sayi("If the  " + UPDATE_FLAG + " or " + SHORT_UPDATE_FLAG + " is used, the existing object is simply updated");
        sayi("Note that an object cannot be new and updated at the same time.");
        say("See also: serialize, create_hash");
    }

    @Override
    public void deserialize(InputLine inputLine) {
        if (showHelp(inputLine)) {
                  showDeserializeHelp();
                  return;
              }
              InputStream is;
              boolean isNew = inputLine.hasArg("-new");
              boolean isUpdate = inputLine.hasArg(SHORT_UPDATE_FLAG) || inputLine.hasArg(UPDATE_FLAG);
              if (isNew && isUpdate) {
                  say("Sorry. You have asked me to make a new item and update an existing one.");
                  return;
              }
              if (!inputLine.hasArg(FILE_FLAG)) {
                  say("Missing file argument. Cannot deserialize.");
                  return;
              }
              try {
                  is = new FileInputStream(inputLine.getNextArgFor(FILE_FLAG));

                  XMLMap map = new XMLMap();
                  map.fromXML(is);
                  is.close();
                  // x contains the object that is now of the correct type.
                  Identifiable x = getStore().getXMLConverter().fromMap(map, null);
                  if (isNew) {
                      if (getStore().containsKey(x.getIdentifier())) {
                          say("Error! The object with identifier \"" + x.getIdentifierString() + "\" already exists and you specified the item was new. Aborting.");
                          return;
                      }
                      getStore().save(x);
                      return;
                  }
                  if (isUpdate) {
                      if (!getStore().containsKey(x.getIdentifier())) {
                          say("Error! The object with identifier \"" + x.getIdentifierString() + "\" does not exist and therefore cannot be updated.  Aborting.");
                          return;
                      }
                      // Get the current one
                      Identifiable oldVersion = (Identifiable) getStore().get(x.getIdentifier());
                      XMLMap oldValues = new XMLMap();
                      getStore().getXMLConverter().toMap(oldVersion, oldValues);
                      for (String key : map.keySet()) {
                          oldValues.put(key, map.get(key));

                      }
                      Identifiable updated = getStore().getXMLConverter().fromMap(oldValues, null);
                      getStore().save(updated);

                      return;
                  }
                  if (x.getIdentifier() == null) {
                      //handles the case where this is new and needs an identifier created. Only way to get
                      // a new unused identifier reliably is to have the store create a new entry then we update that.
                      Identifiable c = getStore().create();
                      x.setIdentifier(c.getIdentifier());
                      say("Created identifier \"" + c.getIdentifierString() + "\".");
                  }
                  // second case, overwrite whatever.
                  getStore().save(x);

                  say("done!");
              } catch (Throwable e) {
                  say("warning, load file at path \"" + inputLine.getNextArgFor(FILE_FLAG) + "\": " + e.getMessage());
              }
    }

    @Override
    public void search(InputLine inputLine) {

    }

    @Override
    public void edit(InputLine inputLine) {

    }

    @Override
    public boolean update(Identifiable identifiable) throws IOException {
        return false;
    }

    @Override
    public void extraUpdates(Identifiable identifiable) throws IOException {

    }

    @Override
    protected String format(Identifiable identifiable) {
        SATClient client = (SATClient)identifiable;
        String rc = "";
/*
         if (ca == null) {
             rc = "(?) " + client.getIdentifier() + " ";
         } else {
             boolean isApproved = ca != null && ca.isApproved();
             rc = "(" + (isApproved ? "Y" : "N") + ") " + client.getIdentifier() + " ";
         }
*/
        String name = (client.getName() == null ? "no name" : client.getName());
         if (20 < name.length()) {
             name = name.substring(0, 20) + "...";
         }
         rc = rc + "(" + name + ")";
         rc = rc + " created on " + Iso8601.date2String(client.getCreationTS());
         return rc;
    }

    @Override
    protected int longFormat(Identifiable identifiable) {
        return 0;
    }

    @Override
    protected int longFormat(Identifiable identifiable, boolean isVerbose) {
        return 0;
    }

    public static void main(String[] args) {
        CLIDriver driver = new CLIDriver(new SATCLI(null, new MemoryStore(new ClientProvider()) {
            @Override
            public XMLConverter getXMLConverter() {
                return null;
            }

            @Override
            public List getMostRecent(int n, List attributes) {
                return null;
            }
        }));

        driver.start();
    }
}
