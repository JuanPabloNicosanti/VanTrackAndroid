package mainFunctionality.viewsModels;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import utn.proy2k18.vantrack.utils.BackendMapper;

public class MapUserViewModel {
	private static final BackendMapper backendMapper = BackendMapper.getInstance();
	private static final String HTTP_GET = "GET";
	private static MapUserViewModel viewModel;
	
	public static MapUserViewModel getInstance() {
		if (viewModel == null) {
			viewModel = new MapUserViewModel();
		}
		return viewModel;
	}
	
	public JSONObject fetchDirections(String url) {
		String result = backendMapper.getFromBackend(url, HTTP_GET);
		
		try {
			return new JSONObject(result);
		} catch (JSONException err) {
			Log.d("Error", err.toString());
			return new JSONObject();
		}
	}
}
