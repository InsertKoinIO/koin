package org.koin.sample.android.compat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koin.sample.android.R;
import org.koin.sample.android.components.compat.CompatSimpleViewModel;

import kotlin.Lazy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.koin.android.scope.compat.ScopeCompat.lifecycleScope;
import static org.koin.android.viewmodel.compat.ScopeCompat.getViewModel;
import static org.koin.android.viewmodel.compat.SharedViewModelCompat.sharedViewModel;

public class JavaFragment extends Fragment {

    private Lazy<CompatSimpleViewModel> viewModel = sharedViewModel(this,
            CompatSimpleViewModel.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.java_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final JavaActivity activity = (JavaActivity) getActivity();
        if (activity == null) return;

        final CompatSimpleViewModel compatVM = getViewModel(
                lifecycleScope(activity), this, CompatSimpleViewModel.class
        );

        assertNotEquals(viewModel.getValue(), compatVM);
        assertEquals(viewModel.getValue(), (activity).viewModel.getValue());
    }
}
