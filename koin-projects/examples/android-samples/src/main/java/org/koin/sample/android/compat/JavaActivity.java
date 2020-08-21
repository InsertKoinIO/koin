package org.koin.sample.android.compat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import kotlin.Lazy;
import org.koin.android.scope.ScopeActivity;
import org.koin.sample.android.R;
import org.koin.sample.android.components.compat.CompatSimpleViewModel;
import org.koin.sample.android.dynamic.DynamicActivity;

import static org.junit.Assert.assertEquals;
import static org.koin.android.viewmodel.compat.ScopeCompat.getViewModel;
import static org.koin.android.viewmodel.compat.ScopeCompat.viewModel;

public class JavaActivity extends ScopeActivity {

    Lazy<CompatSimpleViewModel> viewModel = viewModel(getScope(), this, CompatSimpleViewModel.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CompatSimpleViewModel compatVM = getViewModel(getScope(), this, CompatSimpleViewModel.class);

        assertEquals(viewModel.getValue(), compatVM);

        setTitle("Java Activity");
        setContentView(R.layout.java_activity);

        getSupportFragmentManager().beginTransaction().add(R.id.java_frame, new JavaFragment()).commit();

        Button javaButton = findViewById(R.id.java_button);
        javaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToNext();
            }
        });
    }

    private void navigateToNext() {
        Intent intent = new Intent(this, DynamicActivity.class);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }
}
