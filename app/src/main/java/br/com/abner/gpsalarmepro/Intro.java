package br.com.abner.gpsalarmepro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by AbnerAdmin on 20/08/2016.
 */
public class Intro extends AppIntro2 {

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(getApplicationContext(), MapsActivity.class);

        addSlide(AppIntroFragment.newInstance("Relaxe", getResources().getString(R.string.description_1),
                R.drawable.img_slide_01, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Pressione", getResources().getString(R.string.description_2),
                R.drawable.img_slide_02, getResources().getColor(R.color.textColorPrimary)));
        addSlide(AppIntroFragment.newInstance("Relaxe denovo", getResources().getString(R.string.description_3),
                R.drawable.img_slide_03, getResources().getColor(R.color.red)));

        setProgressButtonEnabled(true);

        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
