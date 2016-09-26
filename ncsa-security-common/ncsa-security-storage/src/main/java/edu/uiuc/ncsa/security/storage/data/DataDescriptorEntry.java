package edu.uiuc.ncsa.security.storage.data;

/**
 * Describes a column in an SQL database in a platform and vendor neutral way.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/11 at  2:34 PM
 */
public class DataDescriptorEntry implements Comparable {
    public DataDescriptorEntry(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public DataDescriptorEntry(String name, int type, boolean nullable) {
        this.name = name;
        this.nullable = nullable;
        this.type = type;
    }

    /**
     * If this column may be nulled.
     *
     * @return
     */
    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    boolean nullable = false;


    String name;
    int type;

    /**
     * The name for this column. This will be used in the resulting table.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * it is strongly suggested that you use {@link java.sql.Types since
     * these are stable and pretty easily map to most interesting data types.
     * These (within rather narrow limits)
     * are converted by the database abstraction layer to the correct underlying
     * data type. E.g. a BLOB would become a <code>LONG VARCHAR FOR BIT DATA</code>
     * in a DB2 implementation.
     *
     * @return
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int compareTo(Object o) {
        if (!(o instanceof DataDescriptorEntry)) {
            throw new ClassCastException("Error: The given object must be of type " + getClass().getSimpleName() + " to be compared here. It is of type " + o.getClass().getName());
        }
        DataDescriptorEntry cd = (DataDescriptorEntry) o;

        if (getName() == null) {
            if (cd.getName() == null) return 0;
            return cd.getName().compareTo(null); // return whatever Java thinks about comparing nulls.
        }
        return getName().compareTo(cd.getName());
    }
}
