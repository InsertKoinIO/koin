package org.koin.sample.view.java;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.koin.sample.view.simple.MySimpleActivity;
import org.koin.sample.view.simple.MySimplePresenter;

import fr.ekito.myweatherapp.R;
import kotlin.Lazy;

import static org.koin.java.KoinJavaComponent.inject;

public class JavaActivity extends AppCompatActivity {

    private final Lazy<MySimplePresenter> presenter = inject(MySimplePresenter.class);
    private final Lazy<MyJavaPresenter> javaPresenter = inject(MyJavaPresenter.class);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        setTitle("MyJavaActivity");

        TextView text = findViewById(R.id.text);
        ConstraintLayout background = findViewById(R.id.background);

        text.setText(presenter.getValue().sayHello() + "\n" + javaPresenter.getValue().sayHello());

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MySimpleActivity.class));
            }
        });
    }
}
