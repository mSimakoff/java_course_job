package com.example.onechat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {



    Server server;
    MessageController controller;
    String myName;

    @Override
    protected void onStart() {
        super.onStart();

        server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // this call message
                        controller.addMessage(
                                new MessageController.Message(
                                        pair.first,
                                        pair.second,
                                        false
                                )
                        );
                    }
                });
            }
        });
        server.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // TODO disconnecting on server
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { // create window (Activity)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // interface in activity_main (!)

        final EditText chatMessage = findViewById(R.id.chatMessage);
        Button sendButton = findViewById(R.id.sendButton);
        RecyclerView chatWindow = findViewById(R.id.chatWindow);

        controller = new MessageController();

        controller.setIncomingLayout(R.layout.message) // incoming_message
                .setOutgoingLayout(R.layout.outgoing_message)    //outgoing_message
                .setMessageTextId(R.id.messageText) // message_text
                .setUserNameId(R.id.username)   // username_container
                .setMessageTimeId(R.id.messageTime)     // msgTime_container
                .appendTo(chatWindow, this);

        // test

        /*
        controller.addMessage(
                new MessageController.Message("Good morning! How are you? helloworld!!!",
                        "Vasya",
                        true
                )
        );

        controller.addMessage(
                new MessageController.Message("Hello! I'm very cool!",
                        "Misha",
                        false
                )
        );
        */

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code callable on CLICK
                String userMessage = chatMessage.getText().toString();
                controller.addMessage(
                        new MessageController.Message(userMessage,
                                myName,
                                true
                        )
                );
                server.sendMessage(userMessage);
                chatMessage.setText("");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name:");

        final EditText nameInput = new EditText(this);
        builder.setView(nameInput);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = nameInput.getText().toString();
                server.sendName(myName);
            }
        });

        builder.show();
    }
}
