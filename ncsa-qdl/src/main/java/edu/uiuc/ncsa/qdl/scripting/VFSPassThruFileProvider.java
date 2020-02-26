package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.util.FileUtil;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/20 at  1:05 PM
 */
public class VFSPassThruFileProvider implements VFSFileProvider {
    public VFSPassThruFileProvider(String rootDir, String scheme, String mountPoint) {

        this.rootDir = rootDir + (rootDir.endsWith(PATH_SEPARATOR)?"":PATH_SEPARATOR);
        this.scheme = scheme + (scheme.endsWith(SCHEME_DELIMITER)?"":SCHEME_DELIMITER);
        this.mountPoint = mountPoint + (mountPoint.endsWith(PATH_SEPARATOR)?"":PATH_SEPARATOR);
    }

    @Override
    public String getMountPoint() {
        return mountPoint;
    }

    String rootDir = null;
    String scheme = null;
    String mountPoint = null;
    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public boolean checkScheme(String name) {
        return name.startsWith(getScheme() + SCHEME_DELIMITER);
    }

    protected String makeFileName(String head, String rawPath) {
        return rootDir + rawPath.substring(head.length());

    }

    @Override
    public VFSEntry get(String name) throws Throwable {
        String head = getScheme() + getMountPoint() ;
        if (!name.startsWith(head)) {
            return null;
        }
        return FileEntries.fileToEntry(makeFileName(head, name));
    }

    @Override
    public void put(String name, VFSEntry entry) throws Throwable{
        String head = getScheme() + SCHEME_DELIMITER + getMountPoint() + PATH_SEPARATOR;
        if (!name.startsWith(head)) {
            throw new QDLException("Error: Could not write file");
        }
        if(entry.isBinaryType()){
            FileUtil.writeFileAsBinary(makeFileName(head, name), entry.getText());
        }else{
            FileUtil.writeStringToFile(makeFileName(head, name), entry.getText());
        }
    }

    @Override
    public boolean isScript(String name) {
        return false;
    }
}
