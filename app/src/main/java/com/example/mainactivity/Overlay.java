package com.example.mainactivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;


public class Overlay extends Service{
    public static final String TAG = "Overlay";
    private WindowManager mWindowManager;
    private View mFloatingView;
    private TextView textView;
    private ImageView sonNguyenQuang;
    private String messageIntent = null;
    private String messageColor = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.activity_overlay, null);

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if(sharedPrefs.contains("message")){
            messageIntent = sharedPrefs.getString("message", null);
            if(sharedPrefs.contains("messageColor")){
                messageColor = sharedPrefs.getString("messageColor", null);
            }
        }



        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        mFloatingView.setTranslationY(500f);
        textView = mFloatingView.findViewById(R.id.message);
        sonNguyenQuang = mFloatingView.findViewById(R.id.SonNguyenQuang);

        String type = typeOfQuang();
        textView.setText(messageGenerator(type));
        if(messageColor != null) {
            changeColor(messageColor, textView);
        }
        ViewGroup.LayoutParams lp = sonNguyenQuang.getLayoutParams();

        if(!(type.contentEquals("quang_smile"))){
            lp.width = 512;
            lp.height = 531;
        }

        sonNguyenQuang.setLayoutParams(lp);
        sonNguyenQuang.setBackgroundResource(getResources().getIdentifier(type, "drawable", this.getPackageName()));
        animate(mFloatingView);
        vibrate();
        editor.putString("message", null);
        editor.commit();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                mWindowManager.removeView(mFloatingView);
                stopSelf();
            }
        }, 8000);



    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void animate(View v){
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(v, "y", 0f);
        animatorY.setDuration(1000);

        ObjectAnimator pause = ObjectAnimator.ofFloat(v, "y", 0);
        pause.setDuration(6000);

        ObjectAnimator animatorYDown = ObjectAnimator.ofFloat(v, "y", 600f);
        animatorYDown.setDuration(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorY, pause, animatorYDown);
        animatorSet.start();

    }

    private String messageGenerator(String version){
        String message;


        String sonNameVariants[] = {"Son ", "Son Boulders ", "Son Nguyen-Quang ", "Big Bad Boulders ", "xXBoulder_Master1994Xx ", "xXMaître_des_RochersXx ",
                                     "Fils Boulders ", "Gros Mauvais Rochers "
        };

        ArrayList<String> introduction = new ArrayList<>();

        String introductionVariantsGeneral[] = {"Hi I'm ", "What's up, I'm ", "Hey, I'm ", "How you doin, I'm ",
                  "It's a'me, ",
                 "The name's ",
                "People call me Son, ",
                "How's it goin' M'lady. It's me! ", "Bonjour, Je'mappelle ",
                "Oh Hi, didn't notice you there. "
        };

        addString(introductionVariantsGeneral, introduction);

        if(version.contentEquals("quang_car")) {
            String carIntroVariants[] = {"*Vroom* *Vroom*. What's up ", "*BEEP* *BEEP* GET OUT OF MY DAMN WAY. Oh Hi there, ", "*SKRRRRRRR*. Like the sound of that? I'm ",
                    "You probably already know me, But in case you live under a rock, I'm ",
                    "You know there's only one thing in this world more valuable than this car, (Besides me), and thats KNOWLEDGE. I'm ",
                    "A great man once said, that the journey of 1000 miles begins with a step. Well I don't need to take steps because I'm driving this baby. I'm ",
                    "Work hard in life and one day I'll consider letting you work for me. I'm "};

            addString(carIntroVariants, introduction);
        }
        else if(version.contentEquals("quang_hang")) {
            String boulderIntroVariants[] = {"Failure is not an excuse to stop trying, it's a reason to rest, start over, and aim higher this time. Hi I'm ",
                    "Joy and happiness are distinct and often disjointed feelings. I'm ", "Every now and then climbing turns into parkour. I'm ",
                    "I been working hard these last few years so I could bear the weight of the world on my shoulders. Hi I'm ",
                    "Reality is often disappointing, I'm "};
            addString(boulderIntroVariants, introduction);
        }
        else if(version.contentEquals("quang_smile")) {
            String psychoIntroVariants[] = {"You may have not noticed me, But I definitely noticed you. I'm ", "You smell great from here. Oh, I'm ",
                    "Couldn't help but notice you from under your phone. I'm ", "Hey stop ignoring me. I'm ", "Hey it's me uwu, ",
                    "I wish you'd pay all your attention to me. I'm ", "You didn't forget me did you? It's me! ", "It's me again! "};
            addString(psychoIntroVariants, introduction);
        }
        else {
            String partyIntroVariant[] = {"What's good in the hood, ", "Suh, ", "What's packin' ", "What's poppin' ", "PARTAAAAAAY!!... oh hey, I'm ", "EYYYYYYYYYY, "};
            addString(partyIntroVariant, introduction);
        }

        String commandVariantsPrefix[] = {"Make sure you remember that ", "Don't forget, ", "Remember ", "Just reminding you that " };
        String commandVariantsSuffix[] = {" is coming up!", " is happening soon!", " is about to start!"};

        int a, b, c, d;

        a = diceRoll(sonNameVariants.length);
        b = diceRoll(introduction.size());
        c = diceRoll(commandVariantsPrefix.length);
        d = diceRoll(commandVariantsSuffix.length);

        if(messageIntent != null) {
            message = introduction.get(b) + sonNameVariants[a] + commandVariantsPrefix[c] + messageIntent + commandVariantsSuffix[d];
        }
        else{
            message = "You have no upcoming events";
        }

        return message;
    }

    private int diceRoll(int arrayLength){
        int indexReturn = 0;
        indexReturn = (int) (Math.random()  * arrayLength);
        return indexReturn;
    }

    private String typeOfQuang(){
        int index = diceRoll(5);
        String type;
        if(index == 0){
            type = "quang_car";
        }
        else if(index == 1){
            type = "quang_hang";
        }
        else if(index == 2){
            type = "quang_smile";
        }
        else{
            type = "quang_tongue";
        }
        return type;
    }

    private void addString(String[] strings, ArrayList<String> arrayList){
        for(int i = 0; i < strings.length; i++){
            arrayList.add(strings[i]);
        }
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }
    }

    private void changeColor(String colorResource, TextView tv){
        Drawable background = tv.getBackground();
        if(colorResource.contentEquals("@colors/boxColor1")){
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.boxColor1));
        }
        else if(colorResource.contentEquals("@colors/boxColor2")){
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.boxColor2));
        }
        else if(colorResource.contentEquals("@colors/boxColor3")){
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.boxColor3));
        }
        else if(colorResource.contentEquals("@colors/boxColor4")){
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.boxColor4));
        }
        else if(colorResource.contentEquals("@colors/boxColor5")){
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.boxColor5));
        }
        else{
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.boxColor6));
        }
    }


}

