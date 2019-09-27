package utn.proy2k18.vantrack.viewModels;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

import utn.proy2k18.vantrack.exceptions.BackendConnectionException;
import utn.proy2k18.vantrack.exceptions.BackendException;
import utn.proy2k18.vantrack.exceptions.FailedToCreateUserException;
import utn.proy2k18.vantrack.exceptions.FailedToDeleteUserException;
import utn.proy2k18.vantrack.exceptions.FailedToGetUserException;
import utn.proy2k18.vantrack.exceptions.FailedToModifyUserException;
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
    private static final String HTTP_PATCH = "PATCH";

    private static UsersViewModel viewModel;
    private User user;

    public static UsersViewModel getInstance() {
        if (viewModel == null) {
            viewModel = new UsersViewModel();
        }
        return viewModel;
    }

    public void registerUser(User userToRegister) {
        try {
            String body = backendMapper.mapObjectForBackend(userToRegister);
            String url = queryBuilder.getUrl(QueryBuilder.CREATE_USER);
            user = backendMapper.mapObjectFromBackend(User.class, url, HTTP_PUT, body);
        } catch (BackendConnectionException  e) {
            throw new FailedToCreateUserException();
        }  catch (BackendException be) {
            throw new FailedToCreateUserException(be.getMessage());
        }
    }

    public Integer getActualUserId(String userEmail) {
        return this.getUser(userEmail).getUserId();
    }

    public User getUser(String userEmail) {
        if (user == null || !user.getEmail().equalsIgnoreCase(userEmail)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", userEmail);
            String url = queryBuilder.getUrl(QueryBuilder.ACTUAL_USER, data);
            try {
                user = backendMapper.mapObjectFromBackend(User.class, url, HTTP_GET);
            } catch (BackendConnectionException  e) {
                throw new FailedToGetUserException();
            }  catch (BackendException be) {
                throw new FailedToGetUserException(be.getMessage());
            }
        }
        return user;
    }

    public String hashUserPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    public boolean isValidPassword(String email, String password) {
        return this.getUser(email).getPassword().equals(hashUserPassword(password));
    }

    public boolean userHasChargePenalty(String userEmail) {
        if (this.getUser(userEmail).getPenalties() == null) {
            return false;
        }
        for (UserPenalty userPenalty : this.getUser(userEmail).getPenalties()) {
            if (userPenalty.getPenaltyId().equals(UserPenalty.EXTRA_CHARGE_PENALTY_ID)) {
                return true;
            }
        }
        return false;
    }

    public void modifyUser(String userName, String userSurname, String userEmail) {
        if (!(userName.equalsIgnoreCase(this.getUser(userEmail).getName()) &&
                userSurname.equalsIgnoreCase(this.getUser(userEmail).getSurname()))) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", userEmail);
            data.put("name", formatString(userName));
            data.put("surname", formatString(userSurname));
            String url = queryBuilder.getUrl(QueryBuilder.ACTUAL_USER, data);
            String result;
            try {
                result = backendMapper.getFromBackend(url, HTTP_PATCH);
            } catch (BackendConnectionException  e) {
                throw new FailedToModifyUserException();
            }  catch (BackendException be) {
                throw new FailedToModifyUserException(be.getMessage());
            }
            if (result.equals("200")) {
                user.setName(capitalizeEach(userName));
                user.setSurname(capitalizeEach(userSurname));
            } else {
                throw new FailedToModifyUserException();
            }
        }
    }

    public void modifyUserPassword(String userEmail, String password) {
        if (!password.equals(this.getUser(userEmail).getPassword())) {
            HashMap<String, String> data = new HashMap<>();
            data.put("username", userEmail);
            data.put("password", password);
            try {
                String url = queryBuilder.getUrl(QueryBuilder.MODIFY_USER_PWD, data);
                backendMapper.mapObjectFromBackend(String.class, url, HTTP_PATCH);
                user.setPassword(password);
            } catch (BackendConnectionException e) {
                throw new FailedToModifyUserException();
            } catch (BackendException be) {
                throw new FailedToModifyUserException(be.getMessage());
            }
        }
    }

    private String formatString(String string) {
        return string.replaceAll(" ", "+");
    }

    private String capitalizeEach(String string) {
        String capString = "";
        String[] words = string.split(" ");
        for (String word : words) {
            capString += capitalizeWord(word) + " ";
        }
        return capString;
    }

    private String capitalizeWord(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
    }

    public void deleteUser(String userEmail) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", userEmail);
        String url = queryBuilder.getUrl(QueryBuilder.ACTUAL_USER, data);
        String result;
        try {
            result = backendMapper.getFromBackend(url, HTTP_DELETE);
        } catch (BackendConnectionException e) {
            throw new FailedToDeleteUserException();
        } catch (BackendException be) {
            throw new FailedToDeleteUserException(be.getMessage());
        }
        if (result.equals("200")) {
            user = null;
        } else {
            throw new FailedToDeleteUserException();
        }
    }
}
