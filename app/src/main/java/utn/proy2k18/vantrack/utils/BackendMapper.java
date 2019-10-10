package utn.proy2k18.vantrack.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;


public class BackendMapper {

    private static BackendMapper backendMapper;
    private final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();


    public BackendMapper () { }

    public static BackendMapper getInstance() {
        if (backendMapper == null) {
            backendMapper = new BackendMapper();
        }
        return backendMapper;
    }

    public String mapObjectForBackend(Object objectToMap) {
        try {
            return objectMapper.writeValueAsString(objectToMap);
        } catch (IOException ioe) {
            // TODO: this shouldn't be a BackendConnectionException
            throw new BackendConnectionException();
        }
    }

    private boolean isBackendException(String strObject) {
        return strObject.contains("message") && strObject.contains("http_msg") &&
                strObject.contains("http_code");
    }

    public <T> T mapObjectFromBackend(Class<T> resultClass, String... params) {
        String strObject = getFromBackend(params);
        try {
            if (isBackendException(strObject)) {
                throw objectMapper.readValue(strObject, BackendException.class);
            } else {
                return objectMapper.readValue(strObject, resultClass);
            }
        } catch (IOException e) {
            throw new BackendConnectionException();
        }
    }

    public <T> List<T> mapListFromBackend(Class<T> resultClass, String... params) {
        String strObject = getFromBackend(params);
        try {
            if (isBackendException(strObject)) {
                throw objectMapper.readValue(strObject, BackendException.class);
            } else {
                CollectionType returnType = objectMapper.getTypeFactory().constructCollectionType(
                        List.class, resultClass);
                return objectMapper.readValue(strObject, returnType);
            }
        } catch (IOException ioe) {
            throw new BackendConnectionException();
        }
    }

    public String getFromBackend(String... params) {
        final HttpConnector httpConnector = new HttpConnector();
        try {
            return httpConnector.execute(params).get();
        } catch (ExecutionException | InterruptedException exc) {
            throw new BackendConnectionException();
        }
    }
}
