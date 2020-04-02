package com.example.mainactivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;


public class Overlay extends Service{
    public static final String TAG = "Overlay";
    private WindowManager mWindowManager;
    private View mFloatingView;
    private MyDatabase db;
    private MyDatabaseHelper helper;
    private String[] eventDetails;
    private ArrayList<String[]> events;
    private ArrayList<String> eventComparison;
    private TextView textView;
    private ImageView sonNguyenQuang;

    public Overlay() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new MyDatabase(this);
        helper = new MyDatabaseHelper(this);
        eventDetails = null;
        events = new ArrayList<>();
        eventComparison = new ArrayList<>();

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.activity_overlay, null);

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
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

        //Check if messages should be sent at the appropriate time
        checkTime();

        //Text Generator
        String type = typeOfQuang();
        textView.setText(messageGenerator(type));
        ViewGroup.LayoutParams lp = sonNguyenQuang.getLayoutParams();
        if(type.contentEquals("quang_hang")){
            lp.width = 361;
            lp.height = 586;
        }
        else if(type.contentEquals("quang_car")){
            lp.width = 1437;
            lp.height = 555;
        }
        sonNguyenQuang.setLayoutParams(lp);
        //Quang Car Disproportionately streched
        //sonNguyenQuang.setBackgroundResource(getResources().getIdentifier("quang_car", "drawable", this.getPackageName()));
        sonNguyenQuang.setBackgroundResource(getResources().getIdentifier(type, "drawable", this.getPackageName()));
        animate(mFloatingView);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null){
            mWindowManager.removeView(mFloatingView);
        }
    }

    private void animate(View v){
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(v, "y", 0f);
        animatorY.setDuration(1000);

        ObjectAnimator pause = ObjectAnimator.ofFloat(v, "y", 0);
        pause.setDuration(6000);

        ObjectAnimator animatorYDown = ObjectAnimator.ofFloat(v, "y", 500f);
        animatorYDown.setDuration(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorY, pause, animatorYDown);
        animatorSet.start();

    }
    private void checkTime(){
            Cursor cursor = db.getData();

            int index0 = cursor.getColumnIndex(Constants.DATECLICKED);
            int index1 = cursor.getColumnIndex(Constants.COLOR);
            int index2 = cursor.getColumnIndex(Constants.MESSAGE);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                eventDetails = new String[4];
                String date[];

                eventDetails[0] = cursor.getString(index0);
                date = eventDetails[0].split(" ");

                eventDetails[0] = date[1];
                eventDetails[1] = date[2];
                eventDetails[2] = cursor.getString(index1);
                //message
                eventDetails[3] = cursor.getString(index2);
                Log.d(TAG, "DATE [1]: " + date[1]); //MONTH
                Log.d(TAG, "DATE [2]: " + date[2]); //DAY OF MONTH
                Log.d(TAG, "FULL STRING: " + eventDetails[0]);
                events.add(eventDetails);
                cursor.moveToNext();
            }
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

        String commandVariantsPrefix[] = {"Make sure to ", "Don't forget, ", "Remember ", "Just reminding you that " };
        String commandVariantsSuffix[] = {" is coming up!", " is happening soon!", " is about to start!"};

        int a, b, c, d;

        a = diceRoll(sonNameVariants.length);
        b = diceRoll(introduction.size());
        c = diceRoll(commandVariantsPrefix.length);
        d = diceRoll(commandVariantsSuffix.length);

        if(eventDetails != null) {
            message = introduction.get(b) + sonNameVariants[a] + commandVariantsPrefix[c] + eventDetails[3] + commandVariantsSuffix[d];
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


    //Consult Ethan if we should make this productive
    private void searchForNextEvent(ArrayList<String[]> events, int index){
        if(checkMonth(events.get(index)[0]) == Calendar.MONTH){
            if(Integer.parseInt(events.get(index)[1]) == Calendar.DAY_OF_MONTH){
                //do something
            }
        }
    }

    private int checkMonth(String month){
        if(month.contentEquals("Jan")){
            return 0;
        }
        else if(month.contentEquals("Feb")){
            return 1;
        }
        else if(month.contentEquals("Mar")){
            return 2;
        }
        else if(month.contentEquals("Apr")){
            return 3;
        }
        else if(month.contentEquals("May")){
            return 4;
        }
        else if(month.contentEquals("Jun")){
            return 5;
        }
        else if(month.contentEquals("Jul")){
            return 6;
        }
        else if(month.contentEquals("Aug")){
            return 7;
        }
        else if(month.contentEquals("Sep")){
            return 8;
        }
        else if(month.contentEquals("Oct")){
            return 9;
        }
        else if(month.contentEquals("Nov")){
            return 10;
        }
        return 11;
    }

}

