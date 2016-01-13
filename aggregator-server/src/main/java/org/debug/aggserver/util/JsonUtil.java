package org.debug.aggserver.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Created by vishnuhr on 12/1/16.
 */

public class JsonUtil {
    public static final ObjectMapper yamlObjectmapper = new ObjectMapper(new YAMLFactory());
    public static final ObjectMapper Objectmapper = new ObjectMapper();
}
