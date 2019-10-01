package de.iaas.skywalker.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplicationProperties {
    private final List<String> genericPropertyTypes = new ArrayList<String>() {{
        add("EventSources");
        add("Function");
        add("InvokedServices");
    }};

    private Map<String, Object> eventSources;
    private Map<String, Object> functions;
    private Map<String, Object> invokedServices;

    public List<String> getGenericPropertyTypes() {
        return genericPropertyTypes;
    }

    public Map<String, Object> getEventSources() {
        return eventSources;
    }

    public void setEventSources(Map<String, Object> eventSources) {
        this.eventSources = eventSources;
    }

    public Map<String, Object> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, Object> functions) {
        this.functions = functions;
    }

    public Map<String, Object> getInvokedServices() {
        return invokedServices;
    }

    public void setInvokedServices(Map<String, Object> invokedServices) {
        this.invokedServices = invokedServices;
    }
}
