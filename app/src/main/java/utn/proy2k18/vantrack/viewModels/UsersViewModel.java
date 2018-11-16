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
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.JacksonSerializer;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class UsersViewModel {
    private static final QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_PUT = "PUT";
    private static final String HTTP_GET = "GET";

    private static UsersViewModel viewModel;
    private User user;

    public static UsersViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new UsersViewModel();
        }
        return viewModel;
    }

    public void registerUser(User user) throws JsonProcessingException {
        String body = backendMapper.mapObjectForBackend(user);
        String url = queryBuilder.getCreateUserUrl();
        String result = backendMapper.getFromBackend(url, HTTP_PUT, body);
        user.setUserId(Integer.valueOf(result));
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

    public Integer getActualUserId() {
        if(user != null && user.getUserId() != null) {
            return user.getUserId();
        } else {
            user = getUserFromBack();
            return user.getUserId();
        }
    }

    public User getUserFromBack() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, String> data = new HashMap<>();
        data.put("username", currentUser.getEmail());
        String url = queryBuilder.getActualUser(data);
        return backendMapper.mapObjectFromBackend(User.class, url, HTTP_GET);
    }
}
