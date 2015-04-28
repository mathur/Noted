package com.rmathur.noted.ui.fragments.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.sendgrid.SendGrid;
import com.rmathur.noted.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainFragment extends Fragment {

    private static final int REQUEST_OK = 1;
    @InjectView(R.id.btnStartSpeech)
    ButtonRectangle btnStartSpeech;
    @InjectView(R.id.btnParseSpeech)
    ButtonRectangle btnParseSpeech;
    ArrayList<String> keywordsList = new ArrayList<String>();
    SharedPreferences sharedPreferences;
    private String recognizedText = "The Yemeni Civil War is an ongoing " +
            "conflict between two factions claiming to constitute the " +
            "Yemeni government, along with their supporters and allies. " +
            "Southern separatists and forces loyal to the government of " +
            "Abd Rabbuh Mansur Hadi, based in Aden, have clashed with " +
            "Houthi forces and forces loyal to the former president Ali " +
            "Abdullah Saleh. al-Qaeda in the Arabian Peninsula and the " +
            "Islamic State of Iraq and the Levant have also carried out" +
            " attacks, with AQAP controlling swaths of territory in the " +
            "hinterlands, and along stretches of the coast.";
    private String emailTitle = "Your terms list!";
    private String emailBody = "";
    private String email = "";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

        btnStartSpeech = (ButtonRectangle) view.findViewById(R.id.btnStartSpeech);
        btnParseSpeech = (ButtonRectangle) view.findViewById(R.id.btnParseSpeech);

        btnStartSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnParseSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseSpeech();
            }
        });

        return view;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQUEST_OK);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null) {
                recognizedText += result.get(0);
                Log.e("Recognized Text", recognizedText);
            }
        }
    }

    private void parseSpeech() {
        new AlchemyAsyncTask().execute();
    }

    String postAlchemyData() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://access.alchemyapi.com/calls/text/TextGetRankedKeywords");
        try {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("apikey", getString(R.string.apiKey)));
            pairs.add(new BasicNameValuePair("text", recognizedText));
            pairs.add(new BasicNameValuePair("outputMode", "json"));
            httppost.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = httpclient.execute(httppost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return null;
    }

    public void sendEmail(String body) {
        loadSettings();
        SendGrid sendgrid = new SendGrid("mathur", getString(R.string.sendgrid));
        sendgrid.addTo(email);
        sendgrid.setFrom("study@termed.com");
        sendgrid.setSubject(emailTitle);
        sendgrid.setHtml(body);
        sendgrid.send();
        Log.e("EMAILING", "WE HUR");
    }

//    private class DictionaryAsyncTask extends AsyncTask<String, String, ArrayList<String>> {
//
//        ArrayList<String> definitionList = new ArrayList<String>();
//
//        @Override
//        // params[0] is term, params[1] is term number
//        protected ArrayList<String> doInBackground(String... params) {
//            ArrayList<String> out = new ArrayList<String>();
//            String output = postDictionaryData(params[0]);
//            out.add(params[0]);
//            out.add(output);
//            out.add(params[1]);
//            return out;
//        }
//
//        protected void onPostExecute(ArrayList<String> out) {
//            String term = out.get(0);
//            String output = out.get(1);
//            String id = out.get(2);
//
//            try {
//                JSONObject data = new JSONObject(output);
//                JSONArray definitions = data.getJSONArray("definitions");
//                if (definitions.length() > 0) {
//                    String definitionText = definitions.getJSONObject(0).getString("text");
//                    definitionList.add(definitionText);
//                    Log.e("Definition", definitionText);
//                    emailBody += term + "\t \t \t \t" + definitionText + "\n";
//                } else {
//                    Log.e("Definition", "no defintion found");
//                }
//            } catch (JSONException e) {
//                Log.e("Error", e.getMessage());
//            }
//            if(id.equals("" + (keywordsList.size() - 1))) {
//                Log.e("EMAIL BODY FINAL", emailBody);
//                generatePdf(emailBody);
//            }
//        }
//    }
//
//    private String postDictionaryData(String word) {
//        HttpClient httpclient = new DefaultHttpClient();
//        StringTokenizer stk = new StringTokenizer(word);
//        String firstWord;
//        if (stk.hasMoreTokens()) {
//            firstWord = stk.nextToken();
//        } else {
//            firstWord = word;
//        }
//
//        HttpGet httppost = new HttpGet("https://montanaflynn-dictionary.p.mashape.com/define?word=" + firstWord);
//        try {
//            httppost.addHeader("X-Mashape-Authorization", getString(R.string.dictionaryApiKey));
//            HttpResponse response = httpclient.execute(httppost);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//            StringBuilder builder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line).append("\n");
//            }
//            return builder.toString();
//        } catch (Exception e) {
//            Log.e("error", e.getMessage());
//        }
//        return null;
//    }
//
//    public void generatePdf(String emailBody) {
//        sendEmail(emailBody);
//    }
//

    public void loadSettings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        email = sharedPreferences.getString("email", null);
        Log.e("OMG", email);
    }

    private class AlchemyAsyncTask extends AsyncTask<String, String, String> {

        public AlchemyAsyncTask() {
            keywordsList.clear();
            emailBody = "The following key words were identified from your speech:<br />";
        }

        @Override
        protected String doInBackground(String... params) {
            return postAlchemyData();
        }

        protected void onPostExecute(String output) {
            try {
                JSONObject data = new JSONObject(output);
                JSONArray keywords = data.getJSONArray("keywords");
                for (int i = 0; i < keywords.length(); i++) {
                    JSONObject keyword = keywords.getJSONObject(i);
                    String text = keyword.get("text").toString();
                    keywordsList.add(text);
                    Log.e("Keyword:", text);
                }
                for (int i = 0; i < keywordsList.size(); i++) {
                    String term = keywordsList.get(i);
                    // new DictionaryAsyncTask().execute(term, i + "");
                    emailBody += term + "<br />";
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
            }

            emailBody += "<br />Happy studying!<br />The Termed Team";
            sendEmail(emailBody);
        }
    }
}