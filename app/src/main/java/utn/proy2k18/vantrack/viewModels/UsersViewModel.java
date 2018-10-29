package utn.proy2k18.vantrack.viewModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import utn.proy2k18.vantrack.connector.HttpConnector;
import utn.proy2k18.vantrack.models.User;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class UsersViewModel {
    private QueryBuilder queryBuilder = new QueryBuilder();
    private static final ObjectMapper objectMapper = JacksonSerializer.getObjectMapper();
    private static final String HTTP_PUT = "PUT";
    private static final String HTTP_GET = "GET";
    private static UsersViewModel viewModel;
    private static User user;

    public static UsersViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new UsersViewModel();
        }
        return viewModel;
    }

    public void registerUser(User user){
        final HttpConnector HTTP_CONNECTOR = new HttpConnector();
        try {
            String body = objectMapper.writeValueAsString(user);
            String url = queryBuilder.getCreateUserUrl();
            String result = HTTP_CONNECTOR.execute(url, HTTP_PUT, body).get();
            user.setUserId(result);
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public String getActualUserEmail(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && user.getEmail().equalsIgnoreCase(currentUser.getEmail()))
            return user.getEmail();
        else {
            user = getUserFromBack();
            return user.getEmail();
        }
    }

    public User getUserFromBack(){
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final HttpConnector HTTP_CONNECTOR = HttpConnector.getInstance();
            HashMap<String, String> data = new HashMap<>();
            data.put("username", currentUser.getEmail());
            String url = queryBuilder.getActualUser(data);
            try {
                String result = HTTP_CONNECTOR.execute(url, HTTP_GET).get();
                TypeReference listType = new TypeReference<User>() {
                };
                user = objectMapper.readValue(result, listType);
            } catch (ExecutionException ee) {
                ee.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        return user;
    }
}
