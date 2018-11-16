package utn.proy2k18.vantrack.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

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

    public String mapObjectForBackend(Object objectToMap) throws JsonProcessingException {
        return objectMapper.writeValueAsString(objectToMap);
    }

    public <T> T mapObjectFromBackend(Class<T> resultClass, String... params)
            throws BackendConnectionException {
        String strObject = getFromBackend(params);
        try {
            if (strObject.contains("error_msg")) {
                throw objectMapper.readValue(strObject, BackendException.class);
            } else {
                return objectMapper.readValue(strObject, resultClass);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> mapListFromBackend(Class<T> resultClass, String... params) {
        String strObject = getFromBackend(params);
        try {
            if (strObject.contains("error_msg")) {
                throw objectMapper.readValue(strObject, BackendException.class);
            } else {
                CollectionType returnType = objectMapper.getTypeFactory().constructCollectionType(
                        List.class, resultClass);
                return objectMapper.readValue(strObject, returnType);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newArrayList();
    }

    public String getFromBackend(String... params) {
        final HttpConnector httpConnector = new HttpConnector();
        try {
            return httpConnector.execute(params).get();
        } catch (ExecutionException | InterruptedException exc) {
            throw new BackendConnectionException("Error accediendo al servidor.");
        }
    }
}
