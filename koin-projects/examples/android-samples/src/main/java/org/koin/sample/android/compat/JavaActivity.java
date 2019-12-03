package org.koin.sample.android.compat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.koin.sample.android.R;
import org.koin.sample.android.components.compat.CompatSimpleViewModel;
import org.koin.sample.android.dynamic.DynamicActivity;

import kotlin.Lazy;

import static org.junit.Assert.assertEquals;
import static org.koin.android.viewmodel.compat.ScopeCompat.currentScope;
import static org.koin.android.viewmodel.compat.ScopeCompat.getViewModel;
import static org.koin.android.viewmodel.compat.ScopeCompat.viewModel;

public class JavaActivity extends AppCompatActivity {

    Lazy<CompatSimpleViewModel> viewModel = viewModel(
            currentScope(this), this, CompatSimpleViewModel.class
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CompatSimpleViewModel compatVM = getViewModel(
                currentScope(this), this, CompatSimpleViewModel.class
        );

        assertEquals(viewModel.getValue(), compatVM);

        setTitle("Java Activity");
        setContentView(R.layout.java_activity);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.java_frame, new JavaFragment())
                .commit();

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
