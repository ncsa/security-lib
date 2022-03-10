package edu.uiuc.ncsa.qdl.xml;

import edu.uiuc.ncsa.qdl.module.MIWrapper;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.util.*;

/**
 * This is for things like {@link edu.uiuc.ncsa.qdl.state.State} and {@link edu.uiuc.ncsa.qdl.module.Module}
 * objects that are to be references by uuid when serializing/deserializing to prevent infinite recursion.
 * As state and modules (which include state) gets serialized, this holds running accounts of
 * unique elements which are in turn serialized individually for the workspace.
 * <p>Created by Jeff Gaynor<br>
 * on 2/10/22 at  4:52 PM
 */
public class XMLSerializationState {
    public boolean processedState(UUID uuid) {
        return stateMap.containsKey(uuid);
    }

    public boolean processedState(State state) {
        return processedState(state.getUuid());
    }

    public boolean processedTemplate(Module module) {
        return processedTemplate(module.getId());
    }

    public boolean processedTemplate(UUID uuid) {
        return templateMap.containsKey(uuid);
    }

    public boolean processedInstance(Module module) {
        return processedInstance(module.getId());
    }

    public boolean processedInstance(UUID uuid) {
        return processedInstances.containsKey(uuid);
    }

    public MIWrapper getInstance(UUID uuid){
        return processedInstances.get(uuid);
    }

    /**
     * The state, returning true if it was added and false otherwise.
     *
     * @param state
     * @return
     */
    public boolean addState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Null state");
        }
        if (state.getUuid() == null) {
            throw new IllegalStateException("State does not have a uuid:" + state);
        }
        if (!processedState(state.getUuid())) {
            stateMap.put(state.getUuid(), state);
            return true;
        }
        return false;
    }

    public State getState(UUID uuid) {
        return stateMap.get(uuid);
    }

    public Module getTemplate(UUID uuid) {
        return templateMap.get(uuid);
    }

    public boolean addTemplate(Module module) {
        if (module == null) {
            throw new IllegalArgumentException("null template");
        }
        if (!module.isTemplate()) {
            throw new IllegalArgumentException("Attempt to add non-template " + module);
        }
        if (module.getId() == null) {
            throw new IllegalStateException("Template does not have a uuid:" + module);
        }
        if (!processedTemplate(module.getId())) {
            templateMap.put(module.getId(), module);
            return true;
        }
        return false;
    }

    public boolean addInstance(MIWrapper miWrapper) {
        if (miWrapper == null) {
            throw new IllegalArgumentException("null instance");
        }
        if (miWrapper.getModule().getId() == null) {
            throw new IllegalStateException("Module does not have a uuid:" + miWrapper);
        }
        if (!processedInstance(miWrapper.getModule().getId())) {
            processedInstances.put(miWrapper.getModule().getId(), miWrapper);
            return true;
        }
        return false;
    }

    public Map<UUID, State> stateMap = new HashMap<>();
    public Map<UUID, Module> templateMap = new HashMap<>();
    /**
     * Note that the processed instances work in two ways. During serialization, this list
     * makes sure ther is no infinite recursion since instances can reference other modules.
     * During deserialization, this is populated as instances are created and then these are re-used
     * as needed, again avoiding potentially having the deserialization run away.
     * <br/><br/>
     * Templates are stored separately, but instances are recreated from local state as needed.
     */
    public Map<UUID, MIWrapper> processedInstances = new HashMap<>();
    /**
     * If a module is missing or otherwise fails to load, just skip it, don't exit loading.
     */
    public boolean skipBadModules = false;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isVersion2_0(){
        if(StringUtils.isTrivial(getVersion())){
            return false;
        }
        return getVersion().equals(XMLConstants.VERSION_2_0_TAG);
    }
    String version;

    public boolean isFailOnMissingModules() {
        return failOnMissingModules;
    }

    public void setFailOnMissingModules(boolean failOnMissingModules) {
        this.failOnMissingModules = failOnMissingModules;
    }

    boolean failOnMissingModules = false;
}
