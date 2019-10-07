package de.iaas.skywalker.MappingModules.util;

import de.iaas.skywalker.MappingModules.model.DeploymentModel;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class DeploymentModelMapper {

    private Map<String, Object> deploymentModel;
    private String ruleFilePath;

    private static final String SELECT = "select";
    private static final String ROOT = "root";
    private static final String PATH = "path";
    private static final String VALUE = "value";
    private static final String WHERE = "where";
    private static final String ARRAY_LIST = "ArrayList";

    public DeploymentModelMapper(DeploymentModel deploymentModel, String ruleFilePath) throws IOException {
        this.deploymentModel = this.parseYAMLInHashMap(deploymentModel.getName());
        this.ruleFilePath = ruleFilePath;
    }

    private Map<String, Object> parseYAMLInHashMap(String templateName) throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("templates/" + templateName);
        Map<String, Object> templateMap = yaml.load(inputStream);
        inputStream.close();
        return templateMap;
    }

    public Map<String, Map<String, Object>> translateIntoGenericModel(List<String> genericPropertyTypes) {
        Map<String, Map<String, Object>> genericModel = new HashMap<>();
        for(String genericPropType : genericPropertyTypes) {
            genericModel.put(genericPropType, this.extractApplicationProperties(genericPropType));
        }
        return genericModel;
    }

    public Map<String, Object> extractApplicationProperties(String appProperty) {
        // Read mapping template from config file
        Map<String, Object> mapping_template = getMappingTemplate();

        // Limit to mapping rule set for this appProperty of the generic application model
        mapping_template = (Map<String, Object>) mapping_template.get(appProperty);

        // get SELECT config for mapping the resources
        Map<String, Object> mapping_config = (Map<String, Object>) mapping_template.get(SELECT);
        List<String> mapping_config_root = (mapping_config.get(ROOT) != null) ? (List<String>) mapping_config.get(ROOT) : new ArrayList<>();
        List<String> mapping_config_path = (mapping_config.get(PATH) != null) ? (List<String>) mapping_config.get(PATH) : new ArrayList<>();

        // get WHERE config
        Map<String, Object> statement_config = (mapping_template.get(WHERE) != null) ? (Map<String, Object>) mapping_template.get(WHERE) : new HashMap<>();
        List<String> statement_path = (statement_config.get(PATH) != null) ? (List<String>) statement_config.get(PATH) : new ArrayList<>();
        String statement_value = ((String) statement_config.get(VALUE) != null) ? (String) statement_config.get(VALUE) : "";

        // first get all resources from the SELECT ROOT
        Map<String, Object> root = this.deploymentModel;
        for (String node : mapping_config_root) {
            try {
                root = (Map<String, Object>) root.get(node);
            } catch (ClassCastException e) {
                if (root.get(node).getClass().getSimpleName().equals(ARRAY_LIST))
                    root = this.handleArrayListCastExceptions(root, node);
            }
        }

        // check WHERE statement inside of the ROOT
        Map<String, Object> results = new HashMap<>();
        Iterator it = root.entrySet().iterator();
        while (it.hasNext()) {
            String template_value = "";
            Map.Entry nextRoot = (Map.Entry) it.next();
            Map<String, Object> rootCopy;
            try {
                rootCopy = (Map<String, Object>) nextRoot.getValue();
            } catch (ClassCastException e) {
                continue;
            } catch (NullPointerException e) {
                continue;
            }
            for (String node : statement_path) {
                try {
                    rootCopy = (Map<String, Object>) rootCopy.get(node);
                } catch (ClassCastException e) {
                    template_value = (String) rootCopy.get(node);
                }
            }
            if (template_value.equals(statement_value)) {
//                if (mapping_config_path.isEmpty()) results.put(nextRoot.getKey().toString(), this.stringifyObject(nextRoot.getValue()));
                if (mapping_config_path.isEmpty()) results.put(nextRoot.getKey().toString(), nextRoot.getValue());
                else {
                    Map<String, Object> thisRootTree = (Map<String, Object>) nextRoot.getValue();
                    for (String node : mapping_config_path) {
                        try {
                            thisRootTree = (Map<String, Object>) thisRootTree.get(node);
                        } catch (ClassCastException e) {
                            if (thisRootTree.get(node).getClass().getSimpleName().equals(ARRAY_LIST))
                                thisRootTree = this.handleArrayListCastExceptions(thisRootTree, node);
                        }
                    }
                    results.putAll(thisRootTree);
                }
            }
        }
        return results;
    }

    private String stringifyObject(Object object) {
        String oString = "";
        try {
            Map<String, Object> oMap = (Map<String, Object>) object;
            oString += oMap.keySet().iterator().next() + " { " + this.stringifyObject(oMap.entrySet().iterator().next().getValue());
        } catch (ClassCastException c) {
            try {
                List<Object> oList = (List<Object>) object;
                for (Object o : oList) {
                    oString += this.stringifyObject(o);
                }
            } catch (ClassCastException cList) {
                try {
                    oString += (String) object;
                } catch (ClassCastException cString) {
                    cString.printStackTrace();
                }
            }
        }
        return oString;
    }

    private Map<String, Object> getMappingTemplate() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(this.ruleFilePath);
        return yaml.load(inputStream);
    }

    private Map<String, Object> handleArrayListCastExceptions(Map<String, Object> root, String node) {
        Map<String, Object> tempTree = new HashMap<>();
        for(Map<String, Object> property : (List<Map<String, Object>> ) root.get(node)) {
            tempTree.putAll(property);
        }
        return tempTree;
    }
}

