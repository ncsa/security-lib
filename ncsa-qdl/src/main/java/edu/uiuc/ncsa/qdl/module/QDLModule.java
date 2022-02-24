package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.exceptions.ModuleInstantiationException;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.ModuleStatement;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.MODULE_SOURCE_TAG;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/1/20 at  11:30 AM
 */
public class QDLModule extends Module {
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    String filePath;

    public ModuleStatement getModuleStatement() {
        return moduleStatement;
    }

    public void setModuleStatement(ModuleStatement moduleStatement) {
        this.moduleStatement = moduleStatement;
    }

    ModuleStatement moduleStatement;

    public List<String> getSource() {
        if (moduleStatement == null || moduleStatement.getSourceCode().isEmpty()) {
            return new ArrayList<>();
        }
        return moduleStatement.getSourceCode();
    }


    @Override
    public Module newInstance(State state) {
        if(state == null){
            // return a barebones module -- everything that does not depend on the state that
            // the template has
            QDLModule qdlModule = new QDLModule();
            qdlModule.setParentTemplateID(getId());// this object is a template, so set the parent uuid accordingly
            qdlModule.setAlias(getAlias());
            qdlModule.setNamespace(getNamespace());
            qdlModule.setModuleStatement(getModuleStatement());
            return qdlModule;
        }
        State localState = state.newLocalState();
        try {
            //p.execute(getModuleStatement().getSourceCode());
            localState.setImportMode(true);
            getModuleStatement().evaluate(localState);
            Module m  = getModuleStatement().getmInstance();
            getModuleStatement().clearInstance();
            localState.setImportMode(false);
            setupModule(m);
            return m;
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            throw new ModuleInstantiationException("Error: Could not create module:" + throwable.getMessage(), throwable);
        }
    }

    @Override
    public void writeExtraXMLElements(XMLStreamWriter xsw) throws XMLStreamException {
        super.writeExtraXMLElements(xsw);
        if (!getSource().isEmpty()) {
            xsw.writeStartElement(MODULE_SOURCE_TAG);
            xsw.writeCData(Base64.encodeBase64URLSafeString(StringUtils.listToString(getSource()).getBytes(StandardCharsets.UTF_8)));
            xsw.writeEndElement();
        }
    }

    @Override
    public void readExtraXMLElements(XMLEvent xe, XMLEventReader xer) throws XMLStreamException {
        super.readExtraXMLElements(xe, xer);
    }

    List<String> documentation = new ArrayList<>();

    /**
     * Documentation resides in the module definition, so it is loaded here at parse time.
     *
     * @return
     */
    @Override
    public List<String> getListByTag() {
        return documentation;
    }

    @Override
    public void setDocumentation(List<String> documentation) {
        this.documentation = documentation;
    }
}
