package com.example.srirang.letsjims;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Query extends ActionBarActivity implements View.OnClickListener,

        MessageDataSource.MessagesCallbacks{



    public static final String USER_EXTRA = "USER";



    public static final String TAG = "ChatActivity";



    private ArrayList<Message> mMessages;

    private MessagesAdapter mAdapter;

    private String mRecipient;

    private ListView mListView;

    private Date mLastMessageDate = new Date();

    private String mConvoId;

    private MessageDataSource.MessagesListener mListener;

    private String facultyname;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.query_interface);



        mRecipient = "cse2";                //name from top horizontalScrollbar




        mListView = (ListView)findViewById(R.id.messages_list);

        mMessages = new ArrayList<>();

        mAdapter = new MessagesAdapter(mMessages);

        mListView.setAdapter(mAdapter);



        setTitle(mRecipient);             //name from list

        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }



        Button sendMessage = (Button)findViewById(R.id.send_message);

        sendMessage.setOnClickListener(this);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        facultyname=firebaseAuth.getCurrentUser().getEmail();


        int index = facultyname.indexOf('@');                 //Getting username
        facultyname = facultyname.substring(0,index);

        String[] ids = {facultyname,"aaa"};           //add loginname & receivername from msgList
                                                      //aaa is cse1 and aab is cse2 baa is ece1..
        Arrays.sort(ids);

        mConvoId = ids[0]+ids[1];



        Firebase.setAndroidContext(this);
        mListener = MessageDataSource.addMessagesListener(mConvoId,this); //error



    }



    public void onClick(View v) {

        EditText newMessageView = (EditText)findViewById(R.id.new_message);

        String newMessage = newMessageView.getText().toString();

        newMessageView.setText("");

        Message msg = new Message();

        msg.setmDate(new Date());

        msg.setmText(newMessage);

        msg.setmSender(facultyname);           //login name



        MessageDataSource.saveMessage(msg, mConvoId);

    }



    @Override

    public void onMessageAdded(Message message) {    //List is refreshed.

        mMessages.add(message);

        mAdapter.notifyDataSetChanged();

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

        MessageDataSource.stop(mListener);

    }





    private class MessagesAdapter extends ArrayAdapter<Message> {

        MessagesAdapter(ArrayList<Message> messages){

            super(Query.this, R.layout.message_item, R.id.message, messages);

        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = super.getView(position, convertView, parent);

            Message message = getItem(position);






            String boldText = message.getmSender();
            String normalText =message.getmText();
            String totaltext="\r\n"+normalText;                //Spliting lines
            SpannableString str = new SpannableString(boldText + totaltext);       //Making text bold
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);




            TextView nameView = (TextView)convertView.findViewById(R.id.message);

            nameView.setText(str);



            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();



            int sdk = Build.VERSION.SDK_INT;

            if (message.getmSender().equals(facultyname)){       //Login name

                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {

                    nameView.setBackground(getDrawable(R.drawable.bubbler));

                } else{

                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubbler));

                }

                layoutParams.gravity = Gravity.LEFT;

            }else{

                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {

                    nameView.setBackground(getDrawable(R.drawable.bubbler));

                } else{

                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubbler));

                }

                layoutParams.gravity = Gravity.RIGHT;

            }



            nameView.setLayoutParams(layoutParams);





            return convertView;

        }

    }

}