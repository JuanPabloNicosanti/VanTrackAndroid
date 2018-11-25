package utn.proy2k18.vantrack.viewModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import utn.proy2k18.vantrack.exceptions.FailedToDeleteUsernameException;
import utn.proy2k18.vantrack.models.User;
import utn.proy2k18.vantrack.models.UserPenalty;
import utn.proy2k18.vantrack.utils.BackendMapper;
import utn.proy2k18.vantrack.utils.QueryBuilder;

public class UsersViewModel {
    private static final QueryBuilder queryBuilder = new QueryBuilder();
    private static final BackendMapper backendMapper = BackendMapper.getInstance();
    private static final String HTTP_PUT = "PUT";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_DELETE = "DELETE";

    private static UsersViewModel viewModel;
    private User user;

    public static UsersViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new UsersViewModel();
        }
        return viewModel;
    }

    public void registerUser(User userToRegister) throws JsonProcessingException {
        String body = backendMapper.mapObjectForBackend(userToRegister);
        String url = queryBuilder.getCreateUserUrl();
        String result = backendMapper.getFromBackend(url, HTTP_PUT, body);
        this.getUser();
    }

    public String getActualUserEmail() {
        return this.getUser().getEmail();
    }

    public Integer getActualUserId() {
        return this.getUser().getUserId();
    }

    public User getUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", currentUser.getEmail());
            String url = queryBuilder.getActualUser(data);
            user = backendMapper.mapObjectFromBackend(User.class, url, HTTP_GET);
        }
        return user;
    }

    public boolean userHasChargePenalty() {
        if (this.getUser().getPenalties() == null) {
            return false;
        }
        for (UserPenalty userPenalty : this.getUser().getPenalties()) {
            if (userPenalty.getPenaltyId().equals(UserPenalty.EXTRA_CHARGE_PENALTY_ID)) {
                return true;
            }
        }
        return false;
    }

    public void modifyUser(String userName, String userSurname) { }

    public void deleteUser() {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", this.getUser().getEmail());
        String url = queryBuilder.getActualUser(data);
        String result = backendMapper.getFromBackend(url, HTTP_DELETE);
        if (!result.equals("200")) {
            throw new FailedToDeleteUsernameException();
        }
    }
}
