package mainFunctionality.moreOptions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

import utn.proy2k18.vantrack.initAndAccManagement.AccountFragment;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.initAndAccManagement.InitActivity;
import utn.proy2k18.vantrack.models.Option;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoreOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoreOptionsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */

public class MoreOptionsFragment extends Fragment {

    private ListView moListView;
    private MoreOptionsAdapter moAdapter;
    private OnFragmentInteractionListener mListener;
    private Option logOutOption;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static HashMap<String, View.OnClickListener> listenerActions = new HashMap<>();

    public MoreOptionsFragment() {
        // Required empty public constructor
    }

    public static MoreOptionsFragment newInstance() {
        return new MoreOptionsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initListenerActions();
        this.googleSignInOnCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more_options, container, false);
        moListView = (ListView) view.findViewById(R.id.more_options_list_view);

        ArrayList<Option> MoreOptionsArray = new ArrayList<Option>();
        MoreOptionsArray.add(new Option("Notificaciones", R.drawable.ic_add_notif, listenerActions.get("NOTIFICACIONES")));
        MoreOptionsArray.add(new Option("Mi Cuenta", R.drawable.ic_add_user, listenerActions.get("MI_CUENTA")));
        MoreOptionsArray.add(new Option("Ayuda", R.drawable.ic_add_help, listenerActions.get("AYUDA")));
        MoreOptionsArray.add(new Option("Cerrar sesión", R.drawable.ic_add_close_session, listenerActions.get("CERRAR_SESION")));

        // specify an adapter (see also next example)
        moAdapter = new MoreOptionsAdapter(this.getContext(), MoreOptionsArray);
        moListView.setAdapter(moAdapter);

        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = null;
        if (activity != null) {
            actionBar = activity.getSupportActionBar();
        }
        if (actionBar != null) {
            actionBar.setTitle(R.string.more_options);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void initListenerActions() {
        listenerActions.put("NOTIFICACIONES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "NOTIFICACIONES DRIVER", Toast.LENGTH_LONG).show();
            }
        });
        listenerActions.put("MI_CUENTA", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new AccountFragment());
            }
        });
        listenerActions.put("AYUDA", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "AYUDA", Toast.LENGTH_LONG).show();
            }
        });
        listenerActions.put("CERRAR_SESION", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "CERRAR_SESION", Toast.LENGTH_LONG).show();
                showLogoutAlertToUser();
            }
        });
    }

    private void showLogoutAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder
                .setTitle(R.string.close_session_alert_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(final DialogInterface dialog, final int id) {
                                mAuth.signOut();
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    private void googleSignInOnCreate() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getContext(), InitActivity.class));
                }
            }
        };

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}


