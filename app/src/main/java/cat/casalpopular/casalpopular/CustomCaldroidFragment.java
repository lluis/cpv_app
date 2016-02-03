package cat.casalpopular.casalpopular;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

/**
 * Created by tictacbum on 01/02/16.
 */
public class CustomCaldroidFragment extends CaldroidFragment {
    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CaldroidCustomAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }
}
