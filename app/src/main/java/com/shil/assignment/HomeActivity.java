package com.shil.assignment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shil.assignment.audiostack.AudioCardListener;
import com.shil.assignment.audiostack.AudioSwipwAdapterView;
import com.shil.assignment.controller.AppController;
import com.shil.assignment.model.LessonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements AudioCardListener.ActionDownInterface {

    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private AudioSwipwAdapterView flingContainer;
    private List < LessonData > lessonData = new ArrayList < LessonData > ();
    private FrameLayout flingContainer1;
    private static String TAG = HomeActivity.class.getSimpleName();

    public String pronunciations;

    public static void removeBackground() {


        viewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();

    }


    // API
    String url = "http://www.akshaycrt2k.com/getLessonData.php";
    MediaPlayer mPlayer;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Get the Data from api by calling makejsonobject();
        makejsonobject();

        flingContainer = (AudioSwipwAdapterView) findViewById(R.id.frame);

    }


    //Load the data through api
    private void makejsonobject() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, (String) null, new Response.Listener < JSONObject > () {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    JSONArray jsonarray = response.getJSONArray("lesson_data");
                    System.out.println("jsonarray" + jsonarray);

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jobj = jsonarray.optJSONObject(i);

                        LessonData lessondata = new LessonData();

                        lessondata.setType(jobj.getString("type"));
                        lessondata.setConceptName(jobj.getString("conceptName"));
                        lessondata.setPronunciation(jobj.getString("pronunciation"));
                        lessondata.setTargetScript(jobj.getString("targetScript"));
                        lessondata.setAudio_url(jobj.getString("audio_url"));

                        lessonData.add(lessondata);
                    }


                    myAppAdapter = new MyAppAdapter(lessonData, HomeActivity.this);
                    flingContainer.setAdapter(myAppAdapter);
                    flingContainer.setFlingListener(new AudioSwipwAdapterView.onFlingListener() {
                        @Override
                        public void removeFirstObjectInAdapter() {

                        }

                        @Override
                        public void onLeftCardExit(Object dataObject) {
                            // lessonData.clone();
                            // lessonData.remove(0);
                            myAppAdapter.notifyDataSetChanged();
                            //Do something on the left!
                            //You also have access to the original object.
                            //If you want to use it just cast it (String) dataObject

                        }

                        @Override
                        public void onRightCardExit(Object dataObject) {

                            lessonData.remove(0);
                            myAppAdapter.notifyDataSetChanged();
                            if (lessonData.size() == 0) {
                                Toast.makeText(getApplicationContext(), "Reapeating the Assignment!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onAdapterAboutToEmpty(int itemsInAdapter) {

                        }

                        @Override
                        public void onScroll(float scrollProgressPercent) {

                            View view = flingContainer.getSelectedView();
                            view.findViewById(R.id.background).setAlpha(0);
                        }
                    });


                    // Optionally add an OnItemClickListener
                    flingContainer.setOnItemClickListener(new AudioSwipwAdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClicked(int itemPosition, Object dataObject) {

                            View view = flingContainer.getSelectedView();
                            view.findViewById(R.id.background).setAlpha(0);


                            myAppAdapter.notifyDataSetChanged();
                        }
                    });



                    myAppAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public TextView TextView_conceptName, TextView_targetScript, TextView_tips;
        public FloatingActionButton Button_play;
        public ImageView Button_speech, ImageView_blub;


    }

    public class MyAppAdapter extends BaseAdapter {


        public List < LessonData > lessonDataList;
        public Context context;

        private MyAppAdapter(List < LessonData > lessonDataList, Context context) {
            this.lessonDataList = lessonDataList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return lessonDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.card_item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();

                viewHolder.TextView_targetScript = (TextView) rowView.findViewById(R.id.TextView_targetScript);
                viewHolder.TextView_conceptName = (TextView) rowView.findViewById(R.id.TextView_conceptName);
                viewHolder.TextView_tips = (TextView) rowView.findViewById(R.id.TextView_tips);


                viewHolder.Button_play = (FloatingActionButton) rowView.findViewById(R.id.Button_play);
                viewHolder.Button_speech = (ImageView) rowView.findViewById(R.id.Button_speech);
                viewHolder.ImageView_blub = (ImageView) rowView.findViewById(R.id.ImageView_blub);


                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);

                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }
            viewHolder.TextView_targetScript.setText(lessonDataList.get(position).getTargetScript() + "");
            viewHolder.TextView_conceptName.setText(lessonDataList.get(position).getConceptName() + "");


            String type = lessonDataList.get(position).getType().toString();


            if (type.equals("learn")) {

                viewHolder.Button_speech.setVisibility(View.GONE);
                viewHolder.ImageView_blub.setVisibility(View.VISIBLE);
                viewHolder.TextView_tips.setVisibility(View.VISIBLE);


                viewHolder.Button_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Disable the play button
                        viewHolder.Button_play.setEnabled(false);
                        // The audio url to play
                        String audioUrl = lessonDataList.get(position).getAudio_url().toString();

                        // Initialize a new media player instance
                        mPlayer = new MediaPlayer();

                        // Set the media player audio stream type
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        //Try to play music/audio from url
                        try {

                            // Set the audio data source
                            mPlayer.setDataSource(audioUrl);
                            // Prepare the media player
                            mPlayer.prepare();

                            // Start playing audio from http url
                            mPlayer.start();

                            // Inform user for audio streaming
                            Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            // Catch the exception
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }

                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                Toast.makeText(getApplicationContext(), "End", Toast.LENGTH_SHORT).show();
                                viewHolder.Button_play.setEnabled(true);
                            }
                        });
                    }
                });


            } else {
                viewHolder.Button_speech.setVisibility(View.VISIBLE);
                viewHolder.ImageView_blub.setVisibility(View.GONE);
                viewHolder.TextView_tips.setVisibility(View.GONE);

                viewHolder.Button_speech.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Hege    SpeechInput();

                        pronunciations = lessonDataList.get(position).getPronunciation().toString();
                        SpeechInput();
                    }
                });

            }
            return rowView;
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private void SpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList < String > result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (pronunciations.equalsIgnoreCase(result.get(0))) {
                        Toast.makeText(getApplicationContext(), "Your Audio are Matched=" + lock_match(pronunciations, result.get(0)) + "%", Toast.LENGTH_LONG).show();

                        lock_match(pronunciations, result.get(0));
                    } else {
                        Toast.makeText(getApplicationContext(), "Oops! Audio matched" + lock_match(pronunciations, result.get(0)) + "%", Toast.LENGTH_LONG).show();
                        lock_match(pronunciations, result.get(0));

                    }
                }
                break;
            }

        }
    }



    public static int lock_match(String s, String t) {



        int totalw = word_count(s);
        int total = 100;
        int perw = total / totalw;
        int gotperw = 0;

        if (!s.equals(t)) {

            for (int i = 1; i <= totalw; i++) {
                if (simple_match(split_string(s, i), t) == 1) {
                    gotperw = ((perw * (total - 10)) / total) + gotperw;
                } else if (front_full_match(split_string(s, i), t) == 1) {
                    gotperw = ((perw * (total - 20)) / total) + gotperw;
                } else if (anywhere_match(split_string(s, i), t) == 1) {
                    gotperw = ((perw * (total - 30)) / total) + gotperw;
                } else {
                    gotperw = ((perw * smart_match(split_string(s, i), t)) / total) + gotperw;
                }
            }
        } else {
            gotperw = 100;
        }
        return gotperw;
    }

    public static int anywhere_match(String s, String t) {
        int x = 0;
        if (t.contains(s)) {
            x = 1;
        }
        return x;
    }

    public static int front_full_match(String s, String t) {
        int x = 0;
        String tempt;
        int len = s.length();

        //----------Work Body----------//
        for (int i = 1; i <= word_count(t); i++) {
            tempt = split_string(t, i);
            if (tempt.length() >= s.length()) {
                tempt = tempt.substring(0, len);
                if (s.contains(tempt)) {
                    x = 1;
                    break;
                }
            }
        }
        //---------END---------------//
        if (len == 0) {
            x = 0;
        }
        return x;
    }

    public static int simple_match(String s, String t) {
        int x = 0;
        String tempt;
        int len = s.length();


        //----------Work Body----------//
        for (int i = 1; i <= word_count(t); i++) {
            tempt = split_string(t, i);
            if (tempt.length() == s.length()) {
                if (s.contains(tempt)) {
                    x = 1;
                    break;
                }
            }
        }
        //---------END---------------//
        if (len == 0) {
            x = 0;
        }
        return x;
    }

    public static int smart_match(String ts, String tt) {

        char[] s = new char[ts.length()];
        s = ts.toCharArray();
        char[] t = new char[tt.length()];
        t = tt.toCharArray();


        int slen = s.length;
        //number of 3 combinations per word//
        int combs = (slen - 3) + 1;
        //percentage per combination of 3 characters//
        int ppc = 0;
        if (slen >= 3) {
            ppc = 100 / combs;
        }
        //initialising an integer to store the total % this class genrate//
        int x = 0;
        //declaring a temporary new source char array
        char[] ns = new char[3];
        //check if source char array has more then 3 characters//
        if (slen < 3) {} else {
            for (int i = 0; i < combs; i++) {
                for (int j = 0; j < 3; j++) {
                    ns[j] = s[j + i];
                }
                if (cross_full_match(ns, t) == 1) {
                    x = x + 1;
                }
            }
        }
        x = ppc * x;
        return x;
    }

    /**
     *
     * @param s
     * @param t
     * @return
     */
    public static int cross_full_match(char[] s, char[] t) {
        int z = t.length - s.length;
        int x = 0;
        if (s.length > t.length) {
            return x;
        } else {
            for (int i = 0; i <= z; i++) {
                for (int j = 0; j <= (s.length - 1); j++) {
                    if (s[j] == t[j + i]) {
                        // x=1 if any charecer matches
                        x = 1;
                    } else {
                        // if x=0 mean an character do not matches and loop break out
                        x = 0;
                        break;
                    }
                }
                if (x == 1) {
                    break;
                }
            }
        }
        return x;
    }

    public static String split_string(String s, int n) {

        int index;
        String temp;
        temp = s;
        String temp2 = null;

        int temp3 = 0;

        for (int i = 0; i < n; i++) {
            int strlen = temp.length();
            index = temp.indexOf(" ");
            if (index < 0) {
                index = strlen;
            }
            temp2 = temp.substring(temp3, index);
            temp = temp.substring(index, strlen);
            temp = temp.trim();

        }
        return temp2;
    }

    public static int word_count(String s) {
        int x = 1;
        int c;
        s = s.trim();
        if (s.isEmpty()) {
            x = 0;
        } else {
            if (s.contains(" ")) {
                for (;;) {
                    x++;
                    c = s.indexOf(" ");
                    s = s.substring(c);
                    s = s.trim();
                    if (s.contains(" ")) {} else {
                        break;
                    }
                }
            }
        }
        return x;
    }
}