package com.example.lexa.shellgame;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {
    private ArrayList<Shell> mShells = new ArrayList<Shell>(3);
    private Button buttonNewGame;
    private boolean mGameOver;
    private final String GAME_OVER = "GAME_OVER";

    private ImageView shellView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShells.add(new Shell(R.id.shell1));
        mShells.add(new Shell(R.id.shell2));
        mShells.add(new Shell(R.id.shell3));

        buttonNewGame = findViewById(R.id.buttonNewGame);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
            }
        });

        if (savedInstanceState != null) {
            for (Shell shell : mShells) {
                shell.loadState(savedInstanceState);
            }
            mGameOver = savedInstanceState.getBoolean(GAME_OVER);
            if (mGameOver) {
                buttonNewGame.setVisibility(View.VISIBLE);
            }
            else {
                buttonNewGame.setVisibility(View.INVISIBLE);
            }
        }
        else {
            newGame();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (Shell shell : mShells) {
            shell.saveState(outState);
        }
        outState.putBoolean(GAME_OVER, mGameOver);
    }

    private void newGame() {
        Random random = new Random();
        int luckyShellNumber = random.nextInt(2);

        for (int i = 0; i < mShells.size(); i++) {
            if (i == luckyShellNumber) {
                mShells.get(i).reset(true);
            }
            else {
                mShells.get(i).reset(false);
            }
        }

        mGameOver = false;
        buttonNewGame.setVisibility(View.INVISIBLE);
    }

    private class Shell implements View.OnClickListener {
        private final int CLOSED = 0, OPEN = 1, OPEN_AND_HAS_BALL = 2;
        private ImageView mView;
        private final String LEVEL = "STATE";
        private final String HAS_BALL = "HAS_BALL";

        private boolean mHasBall;

        public void reset(boolean hasBall) {
            this.mHasBall = hasBall;
            mView.getDrawable().setLevel(CLOSED);
        }

        public Shell (int id) {
            mView = MainActivity.this.findViewById(id);
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mGameOver) return;

            Drawable d = ((ImageView) v).getDrawable();

            if (d.getLevel() ==  CLOSED) {
                if (mHasBall) {
                    d.setLevel(OPEN_AND_HAS_BALL);
                    Toast.makeText(getApplicationContext(), R.string.you_have_won, Toast.LENGTH_LONG).show();
                }
                else {
                    d.setLevel(OPEN);
                    Toast.makeText(getApplicationContext(), R.string.you_loose, Toast.LENGTH_LONG).show();
                }
                mGameOver = true;
                buttonNewGame.setVisibility(View.VISIBLE);
            }
        }

        public void loadState( final Bundle state) {
            ((ImageView) mView).getDrawable().setLevel(state.getInt(LEVEL + mView.getId()));
            mHasBall = state.getBoolean(HAS_BALL + mView.getId());
        }

        public void saveState( final Bundle state) {
            state.putInt(LEVEL + mView.getId(), ((ImageView) mView).getDrawable().getLevel());
            state.putBoolean(HAS_BALL + mView.getId(), mHasBall);
        }
    }

}
