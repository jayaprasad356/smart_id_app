package com.app.fortuneapp.chat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.AlarmClock.EXTRA_MESSAGE;


import static com.app.fortuneapp.chat.constants.IConstants.BROADCAST_DOWNLOAD_EVENT;
import static com.app.fortuneapp.chat.constants.IConstants.DELAY_ONE_SEC;
import static com.app.fortuneapp.chat.constants.IConstants.DOWNLOAD_DATA;
import static com.app.fortuneapp.chat.constants.IConstants.EMPTY;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_DATA;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_DURATION;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_FILE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_NAME;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_PATH;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_SIZE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_TYPE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_DATETIME;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_IMGPATH;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_RECEIVER;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_SEEN;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_SENDER;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_TYPE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_TYPING;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_TYPINGWITH;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_TYPING_DELAY;
import static com.app.fortuneapp.chat.constants.IConstants.EXT_MP3;
import static com.app.fortuneapp.chat.constants.IConstants.EXT_VCF;
import static com.app.fortuneapp.chat.constants.IConstants.FALSE;
import static com.app.fortuneapp.chat.constants.IConstants.FCM_URL;
import static com.app.fortuneapp.chat.constants.IConstants.ONE;
import static com.app.fortuneapp.chat.constants.IConstants.OPENED_TICKET;
import static com.app.fortuneapp.chat.constants.IConstants.PERMISSION_AUDIO;
import static com.app.fortuneapp.chat.constants.IConstants.PERMISSION_CONTACT;
import static com.app.fortuneapp.chat.constants.IConstants.PERMISSION_DOCUMENT;
import static com.app.fortuneapp.chat.constants.IConstants.PERMISSION_VIDEO;
import static com.app.fortuneapp.chat.constants.IConstants.REF_CHATS;
import static com.app.fortuneapp.chat.constants.IConstants.REF_CHAT_ATTACHMENT;
import static com.app.fortuneapp.chat.constants.IConstants.REF_CHAT_PHOTO_UPLOAD;
import static com.app.fortuneapp.chat.constants.IConstants.REF_OTHERS;
import static com.app.fortuneapp.chat.constants.IConstants.REF_USERS;
import static com.app.fortuneapp.chat.constants.IConstants.REF_VIDEO_THUMBS;
import static com.app.fortuneapp.chat.constants.IConstants.REPLY;
import static com.app.fortuneapp.chat.constants.IConstants.REQUEST_CODE_CONTACT;
import static com.app.fortuneapp.chat.constants.IConstants.REQUEST_CODE_PLAY_SERVICES;
import static com.app.fortuneapp.chat.constants.IConstants.REQUEST_PERMISSION_RECORD;
import static com.app.fortuneapp.chat.constants.IConstants.SLASH;
import static com.app.fortuneapp.chat.constants.IConstants.STATUS_ONLINE;
import static com.app.fortuneapp.chat.constants.IConstants.TICKET_ID;
import static com.app.fortuneapp.chat.constants.IConstants.TRUE;
import static com.app.fortuneapp.chat.constants.IConstants.TWO;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE_CONTACT;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE_IMAGE;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE_RECORDING;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE_TEXT;
import static com.app.fortuneapp.chat.constants.IConstants.VIBRATE_HUNDRED;
import static com.app.fortuneapp.chat.constants.IConstants.ZERO;
import static com.app.fortuneapp.helper.Constant.CATEGORY;
import static com.app.fortuneapp.helper.Constant.CLOSED_TICKET;
import static com.app.fortuneapp.helper.Constant.DESCRIPTION;
import static com.app.fortuneapp.helper.Constant.NAME;
import static com.app.fortuneapp.helper.Constant.PENDING_TICKET;
import static com.app.fortuneapp.helper.Constant.TYPE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.R;

import com.app.fortuneapp.chat.adapters.MessageAdapters;
import com.app.fortuneapp.chat.async.BaseTask;
import com.app.fortuneapp.chat.async.TaskRunner;
import com.app.fortuneapp.chat.constants.IConstants;
import com.app.fortuneapp.chat.fcm.APIService;
import com.app.fortuneapp.chat.fcm.RetroClient;
import com.app.fortuneapp.chat.files.FileUtils;
import com.app.fortuneapp.chat.files.MediaFile;
import com.app.fortuneapp.chat.files.PickerManager;
import com.app.fortuneapp.chat.files.PickerManagerCallbacks;
import com.app.fortuneapp.chat.managers.DownloadUtil;
import com.app.fortuneapp.chat.managers.FirebaseUploader;
import com.app.fortuneapp.chat.managers.Utils;
import com.app.fortuneapp.chat.models.Attachment;
import com.app.fortuneapp.chat.models.AttachmentTypes;
import com.app.fortuneapp.chat.models.Chat;
import com.app.fortuneapp.chat.models.DownloadFileEvent;
import com.app.fortuneapp.chat.models.User;
import com.app.fortuneapp.chat.views.SingleClickListener;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends BaseActivity implements View.OnClickListener, PickerManagerCallbacks {

    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private String currentId, userId,ticketId,category, userName = "Sender";
    private String strSender, strReceiver;
    private ArrayList<Chat> chats;
    private MessageAdapters messageAdapters;

    private ValueEventListener seenListenerSender;
    private Query seenReferenceSender;

    private APIService apiService;

    boolean notify = false;

    private String onlineStatus, strUsername, strCurrentImage;

    //    private ImageView imgAvatar;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    private FirebaseStorage storage;
    private StorageReference storageReference, storageAttachment;

    //New Component
    private LinearLayout btnGoToBottom;
    private EmojiPopup emojiIcon;
    private CardView mainAttachmentLayout;
    private View attachmentBGView;
    private EmojiEditText newMessage;
    private ImageView imgAddAttachment, imgAttachmentEmoji, imgCamera;
    private RelativeLayout rootView;

    //Picker
    private PickerManager pickerManager;

    //Recording
    private Handler recordWaitHandler, recordTimerHandler;
    private Runnable recordRunnable, recordTimerRunnable;
    private MediaRecorder mRecorder = null;
    private String recordFilePath;
    private RecordView recordView;
    private RecordButton recordButton;
    private boolean isStart = false;
    private int firstVisible = -1;

    private RelativeLayout rlChatView;

    private String vCardData, displayName, phoneNumber,Name,TicketType;
    private File fileUri = null;
    private Uri imgUri;
    private TextView txtUsername;
    RelativeLayout bottomChatLayout;
    String type,description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mActivity = this;


        apiService = RetroClient.getClient(FCM_URL).create(APIService.class);

        initUI();

        Intent intent = getIntent();
        userId = "admin_1";
        ticketId = intent.getStringExtra(TICKET_ID);
        category = intent.getStringExtra(CATEGORY);
        TicketType = intent.getStringExtra(TYPE);
        type = intent.getStringExtra(TYPE);
        description = intent.getStringExtra(DESCRIPTION);
        currentId = ticketId;
        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(currentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    strUsername = user.getUsername();
                    strCurrentImage = user.getImageURL();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Name = intent.getStringExtra(NAME);
        txtUsername.setText("Admin");


        strSender = currentId + SLASH + userId;
        strReceiver = userId + SLASH + currentId;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(REF_CHAT_PHOTO_UPLOAD + SLASH + strSender);
        storageAttachment = storage.getReference(REF_CHAT_ATTACHMENT + SLASH + strSender);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        btnGoToBottom.setVisibility(View.GONE);
        if (type.equals(CLOSED_TICKET)){
            bottomChatLayout.setVisibility(View.GONE);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    if (firstVisible == -1)
                        firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                    else
                        firstVisible = messageAdapters.getItemCount() >= TWO ? messageAdapters.getItemCount() - TWO : ZERO;
                } catch (Exception e) {
                    firstVisible = ZERO;
                }

                if (layoutManager.findLastVisibleItemPosition() < firstVisible) {
                    btnGoToBottom.setVisibility(View.VISIBLE);
                } else {
                    btnGoToBottom.setVisibility(View.GONE);
                }
            }
        });

        btnGoToBottom.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                try {
                    if (firstVisible != -1) {
                        mRecyclerView.smoothScrollToPosition(messageAdapters.getItemCount() - ONE);
                    }
                    btnGoToBottom.setVisibility(View.GONE);
                } catch (Exception ignored) {

                }
            }
        });

        rlChatView.setVisibility(View.VISIBLE);
        recordButton.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    userName = user.getUsername();
                    onlineStatus = Utils.showOnlineOffline(mActivity, user.getIsOnline());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readMessages();




        emojiIcon = EmojiPopup.Builder.fromRootView(rootView).setOnEmojiPopupShownListener(() -> {
            hideAttachmentView();
            imgAttachmentEmoji.setImageResource(R.drawable.ic_keyboard_24dp);
        }).setOnEmojiPopupDismissListener(() -> imgAttachmentEmoji.setImageResource(R.drawable.ic_insert_emoticon_gray)).build(newMessage);

        newMessage.setOnTouchListener((v, event) -> {
            hideAttachmentView();
            return false;
        });
        Utils.uploadTypingStatus();
        typingListening();


        final Handler handler = new Handler(Looper.getMainLooper());
        //This permission required because when you playing the recorded your voice, at that time audio wave effect shown.
        handler.postDelayed(this::permissionRecording, 800);
    }

    private void initUI() {

        mRecyclerView = findViewById(R.id.recyclerView);

        //New Component
        rootView = findViewById(R.id.rootView);
        rlChatView = findViewById(R.id.rlChatView);
        btnGoToBottom = findViewById(R.id.btnBottom);
        newMessage = findViewById(R.id.newMessage);
        imgAddAttachment = findViewById(R.id.imgAddAttachment);
        imgCamera = findViewById(R.id.imgCamera);
        mainAttachmentLayout = findViewById(R.id.mainAttachmentLayout);
        mainAttachmentLayout.setVisibility(View.GONE);
        attachmentBGView = findViewById(R.id.attachmentBGView);
        txtUsername = findViewById(R.id.txtUsername);
        bottomChatLayout = findViewById(R.id.bottomChatLayout);
        attachmentBGView.setVisibility(View.GONE);
        attachmentBGView.setOnClickListener(this);

        imgAttachmentEmoji = findViewById(R.id.imgAttachmentEmoji);
        imgAddAttachment.setOnClickListener(this);
        imgCamera.setOnClickListener(this);
        imgAttachmentEmoji.setOnClickListener(this);
        findViewById(R.id.btnAttachmentVideo).setOnClickListener(this);
        findViewById(R.id.btnAttachmentContact).setOnClickListener(this);
        findViewById(R.id.btnAttachmentGallery).setOnClickListener(this);
        findViewById(R.id.btnAttachmentAudio).setOnClickListener(this);
        findViewById(R.id.btnAttachmentLocation).setOnClickListener(this);
        findViewById(R.id.btnAttachmentDocument).setOnClickListener(this);

        recordView = findViewById(R.id.recordView);
        recordButton = findViewById(R.id.recordButton);
        recordButton.setRecordView(recordView);//IMPORTANT

        initListener();

        pickerManager = new PickerManager(this, this, this);
    }

    private void initListener() {
        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(v -> {
            clickToSend();
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        //final boolean isRTLOn = SessionManager.get().isRTLOn();
        recordView.setRTLDirection(false);
        recordView.setSlideMarginRight(recordView.getSlideMargin());
        recordView.setCancelBounds(8);
        recordView.setSlideFont(Utils.getRegularFont(mActivity));
        recordView.setCounterTimerFont(Utils.getBoldFont(mActivity));
        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);
        recordView.setSoundEnabled(true);
        recordView.setTimeLimit(60000);//1000 = 1 second
        recordView.setTrashIconColor(getResources().getColor(R.color.red_500));

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if (!blockUnblockCheckBeforeSend()) {
                    hideAttachmentView();
                    if (Objects.requireNonNull(newMessage.getText()).toString().trim().isEmpty()) {
                        if (recordWaitHandler == null)
                            recordWaitHandler = new Handler(Looper.getMainLooper());
                        recordRunnable = () -> recordingStart();
                        recordWaitHandler.postDelayed(recordRunnable, ONE);
                    }
                    hideEditTextLayout();
                }
            }

            @Override
            public void onCancel() {
                if (mRecorder != null && Utils.isEmpty(Objects.requireNonNull(newMessage.getText()).toString().trim())) {
                    recordingStop(FALSE);
                    screens.showToast(R.string.recording_cancelled);
                }
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                try {
                    if (recordWaitHandler != null && Objects.requireNonNull(newMessage.getText()).toString().trim().isEmpty())
                        recordWaitHandler.removeCallbacks(recordRunnable);
                    if (mRecorder != null && Objects.requireNonNull(newMessage.getText()).toString().trim().isEmpty()) {
                        recordingStop(TRUE);
                    }
                } catch (Exception ignored) {

                }
                showEditTextLayout();
            }

            @Override
            public void onLessThanSecond() {
                showEditTextLayout();
            }
        });

        recordView.setRecordPermissionHandler(() -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            if (recordPermissionsAvailable()) {
                return true;
            } else {
                permissionRecording();
            }
            return false;

        });

        recordView.setOnBasketAnimationEndListener(this::showEditTextLayout);



    }

    private void showalert() {
        new AlertDialog.Builder(mActivity)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to close this ticket?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(OPENED_TICKET).child(ticketId);
                        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference().child(CLOSED_TICKET).child(ticketId);
                        moveToOpenTicket(ref1,ref2, "closed");

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showEditTextLayout() {
        if (isStart) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                imgAttachmentEmoji.setVisibility(View.VISIBLE);
                newMessage.setVisibility(View.VISIBLE);
                imgAddAttachment.setVisibility(View.VISIBLE);
                //imgCamera.setVisibility(View.VISIBLE);
                imgCamera.setVisibility(View.GONE);
            }, 10);
        }
        isStart = false;
    }

    private void hideEditTextLayout() {
        isStart = true;
        imgAttachmentEmoji.setVisibility(View.GONE);
        newMessage.setVisibility(View.INVISIBLE);
        imgAddAttachment.setVisibility(View.GONE);
        imgCamera.setVisibility(View.GONE);
    }

    private void clickToSend() {
        if (TextUtils.isEmpty(Objects.requireNonNull(newMessage.getText()).toString().trim())) {
            screens.showToast(R.string.strEmptyMsg);
        } else {
            sendMessage(TYPE_TEXT, Objects.requireNonNull(newMessage.getText()).toString().trim(), null);
        }
        newMessage.setText(EMPTY);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.recordButton) {
            hideAttachmentView();
            clickToSend();
        } else if (id == R.id.imgAttachmentEmoji) {
            emojiIcon.toggle();
        } else if (id == R.id.imgAddAttachment) {
            if (!blockUnblockCheckBeforeSend()) {
                fileUri = null;
                imgUri = null;
                Utils.closeKeyboard(mActivity, view);
                if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
                    hideAttachmentView();
                } else {
                    showAttachmentView();
                }
            }
        } else if (id == R.id.imgCamera) {
            if (!blockUnblockCheckBeforeSend()) {
                fileUri = null;
                imgUri = null;
                hideAttachmentView();
                openCamera();
            }
        } else if (id == R.id.btnAttachmentGallery) {
            hideAttachmentView();
            openImage();
        } else if (id == R.id.btnAttachmentAudio) {
            hideAttachmentView();
            openAudioPicker();
        } else if (id == R.id.btnAttachmentLocation) {
            hideAttachmentView();
            openPlacePicker();
        } else if (id == R.id.btnAttachmentVideo) {
            hideAttachmentView();
            openVideoPicker();
        } else if (id == R.id.btnAttachmentDocument) {
            hideAttachmentView();
            openDocumentPicker();
        } else if (id == R.id.btnAttachmentContact) {
            hideAttachmentView();
            openContactPicker();
        } else if (id == R.id.attachmentBGView) {
            hideAttachmentView();
        }
    }

    private void openCamera() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            fileUri = Utils.createImageFile(mActivity);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getUriForFileProvider(mActivity, fileUri));
        } catch (Exception ignored) {

        }
        intentLauncher.launch(intent);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intentLauncher.launch(intent);
    }

    private void openAudioPicker() {
        if (permissionsAvailable(permissionsStorage)) {
            Intent target = FileUtils.getAudioIntent();
            Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
            try {
                pickerLauncher.launch(intent);
            } catch (Exception ignored) {
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, PERMISSION_AUDIO);
        }
    }

    private void openVideoPicker() {
        if (permissionsAvailable(permissionsStorage)) {
            Intent target = FileUtils.getVideoIntent();
            Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
            try {
                pickerLauncher.launch(intent);
            } catch (Exception ignored) {
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, PERMISSION_VIDEO);
        }
    }

    public void openDocumentPicker() {
        if (permissionsAvailable(permissionsStorage)) {
            final Intent target = FileUtils.getDocumentIntent();
            final Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
            try {
                pickerLauncher.launch(intent);
            } catch (ActivityNotFoundException ignored) {
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, PERMISSION_DOCUMENT);
        }
    }


    private void openContactPicker() {
        if (permissionsAvailable(permissionsContact)) {
            new MultiContactPicker.Builder(mActivity) //Activity/fragment context
                    .theme(R.style.MyCustomPickerTheme)
                    .setTitleText(getString(R.string.choose_contact))
                    .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                    .handleColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark)) //Optional - default: Azure Blue
                    .bubbleColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark)) //Optional - default: Azure Blue
                    .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                    .limitToColumn(LimitColumn.PHONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                    .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out) //Optional - default: No animation overrides
                    .showPickerForResult(REQUEST_CODE_CONTACT);
        } else {
            ActivityCompat.requestPermissions(this, permissionsContact, PERMISSION_CONTACT);
        }
    }

    private void openPlacePicker() {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CONTACT:
                if (permissionsAvailable(permissions))
                    openContactPicker();
                break;
            case PERMISSION_AUDIO:
                if (permissionsAvailable(permissions))
                    openAudioPicker();
                break;
            case PERMISSION_DOCUMENT:
                if (permissionsAvailable(permissions))
                    openDocumentPicker();
                break;
            case PERMISSION_VIDEO:
                if (permissionsAvailable(permissions))
                    openVideoPicker();
                break;
            case REQUEST_PERMISSION_RECORD:
                if (permissionsAvailable(permissions)) {
                    try {
                        if (messageAdapters != null)
                            messageAdapters.notifyDataSetChanged();
                    } catch (Exception ignored) {

                    }
                }
                break;
        }
    }

    /*
     * Intent launcher to get Image Uri from storage
     * */
    final ActivityResultLauncher<Intent> intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (fileUri != null) { // Image Capture
                    imgUri = Uri.fromFile(fileUri);
                } else { // Pick from Gallery
                    Intent data = result.getData();
                    assert data != null;
                    imgUri = data.getData();
                }

                try {
                    CropImage.activity(imgUri)
                            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                            .start(mActivity);
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
            }
        }
    });

    final ActivityResultLauncher<Intent> pickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                final Intent data = result.getData();
                assert data != null;
                final Uri uriData = data.getData();
                Utils.sout("PickerManager uri: " + uriData.toString());
                pickerManager.getPath(uriData, Build.VERSION.SDK_INT); /* {@link PickerManagerOnCompleteListener }*/
//                    newFileUploadTask(data.getDataString(), AttachmentTypes.DOCUMENT, null);
            }
        }
    });


    private boolean blockUnblockCheckBeforeSend() {
        boolean isBlock = false;
        return isBlock;
    }

    private void sendMessage(String type, String message, Attachment attachment) {
        if (blockUnblockCheckBeforeSend()) {
            return;
        }


        notify = true;
        String defaultMsg;
        final String sender = currentId;
        final String receiver = userId;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(EXTRA_SENDER, sender);
        hashMap.put(EXTRA_RECEIVER, receiver);
        hashMap.put(EXTRA_MESSAGE, message);
        hashMap.put(EXTRA_ATTACH_TYPE, type);
        hashMap.put(EXTRA_TYPE, TYPE_TEXT);//This is for older version users(Default TEXT, all other set as IMAGE)

        try {
            if (!type.equalsIgnoreCase(TYPE_TEXT) && !type.equalsIgnoreCase(TYPE_IMAGE)) {
                defaultMsg = Utils.getDefaultMessage();
                hashMap.put(EXTRA_MESSAGE, defaultMsg);
            }
        } catch (Exception ignored) {
        }

        try {
            if (type.equalsIgnoreCase(TYPE_TEXT)) {
                //No need to do anything here.
            } else if (type.equalsIgnoreCase(TYPE_IMAGE)) {
                hashMap.put(EXTRA_TYPE, TYPE_IMAGE);
                hashMap.put(EXTRA_IMGPATH, message);
            } else {
                hashMap.put(EXTRA_ATTACH_PATH, message);
                try {
                    if (attachment != null) {
                        hashMap.put(EXTRA_ATTACH_NAME, attachment.getName());
                        hashMap.put(EXTRA_ATTACH_FILE, attachment.getFileName());
                        hashMap.put(EXTRA_ATTACH_SIZE, attachment.getBytesCount());
                        if (attachment.getData() != null) {
                            hashMap.put(EXTRA_ATTACH_DATA, attachment.getData());
                        }
                        if (attachment.getDuration() != null) {
                            hashMap.put(EXTRA_ATTACH_DURATION, attachment.getDuration());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        hashMap.put(EXTRA_SEEN, FALSE);
        hashMap.put(EXTRA_DATETIME, Utils.getDateTime());

        final String key = Utils.getChatUniqueId();
        hashMap.put(IConstants.ID, key);
        reference.child(REF_CHATS).child(strSender).child(key).setValue(hashMap);
        reference.child(REF_CHATS).child(strReceiver).child(key).setValue(hashMap);



        if (TicketType.equals(OPENED_TICKET)){
            HashMap<String, Object> hashMap2 = new HashMap<>();
            hashMap2.put(REPLY, "true");
            reference.child(OPENED_TICKET).child(ticketId).updateChildren(hashMap2);

        }

        Utils.chatSendSound(getApplicationContext());

        try {
            String msg = message;
            if (!type.equalsIgnoreCase(TYPE_TEXT) && !type.equalsIgnoreCase(TYPE_IMAGE)) {
                try {
                    String firstCapital = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                    if (attachment != null) {
                        msg = "New " + firstCapital + "(" + attachment.getName() + ")";
                    } else {
                        msg = firstCapital;
                    }
                } catch (Exception e) {
                    msg = message;
                }
            }

            if (notify) {
                //sendNotification(msg);
            }
            notify = false;
        } catch (Exception ignored) {
        }
    }
    public void moveToOpenTicket(DatabaseReference fromPath, final DatabaseReference toPath, String type)
    {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null)
                        {
                            System.out.println("Copy failed");
                        }
                        else {
                            if (type.equals("pending")) {
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(PENDING_TICKET).child(ticketId);
                                ref1.removeValue();


                            }
                            else {
                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(CLOSED_TICKET).child(ticketId);
                                ref1.removeValue();
                            }

                        }


                    }


                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    private void sendNotification(String msg)
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.TITLE,Name);
        params.put(Constant.DESCRIPTION,msg);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {


                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }



            }

            //pass url
        }, MessageActivity.this, Constant.SEND_ADMIN_NOTIFY_URL, params,false);



    }

    private void readMessages() {
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strReceiver);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Chat chat = snapshot.getValue(Chat.class);
                            assert chat != null;
                            if (!Utils.isEmpty(chat.getMessage())) {
                                chat.setId(snapshot.getKey());
                                chats.add(chat);
                            }

                        } catch (Exception ignored) {
                        }
                    }
                }
                try {
                    messageAdapters = new MessageAdapters(mActivity, chats, userName, strCurrentImage, "",currentId);
                    mRecyclerView.setAdapter(messageAdapters);
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage() {
        seenReferenceSender = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strSender).orderByChild(EXTRA_SEEN).equalTo(false);
        seenListenerSender = seenReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Chat chat = snapshot.getValue(Chat.class);
                            assert chat != null;
                            if (!Utils.isEmpty(chat.getMessage())) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(EXTRA_SEEN, TRUE);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    MenuItem itemBlockUnblock;


    private void typingListening() {
        newMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() == 0) {
                        stopTyping();
                        recordButton.setListenForRecord(true);
                        recordButton.setImageResource(R.drawable.recv_ic_mic_white);
                    } else if (s.length() > 0) {
                        startTyping();
                        idleTyping(s.length());
                        recordButton.setListenForRecord(false);
                        recordButton.setImageResource(R.drawable.ic_send);
                    }
                } catch (Exception e) {
                    stopTyping();
                    recordButton.setListenForRecord(true);
                    recordButton.setImageResource(R.drawable.recv_ic_mic_white);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void idleTyping(final int currentLen) {
        try {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                int newLen = Objects.requireNonNull(newMessage.getText()).length();
                if (currentLen == newLen) {
                    stopTyping();
                }

            }, EXTRA_TYPING_DELAY);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void startTyping() {
        typingStatus(TRUE);
    }

    private void stopTyping() {
        typingStatus(FALSE);
    }

    /**
     * typingStatus - Update typing and userId with db
     * isTyping = True means 'startTyping' method called
     * isTyping = False means 'stopTyping' method called
     */
    private void typingStatus(boolean isTyping) {
        try {
            reference = FirebaseDatabase.getInstance().getReference(REF_OTHERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_TYPINGWITH, currentId);
            hashMap.put(EXTRA_TYPING, isTyping);
            reference.updateChildren(hashMap);
        } catch (Exception ignored) {
        }
    }
    public static String getExtension(Context context, final Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

//    private File myFile = null;

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + Utils.getExtension(mActivity, imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        return fileReference.getDownloadUrl();
                    })
                    .addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        if (task.isSuccessful()) {
                            final Uri downloadUri = task.getResult();
                            final String mUrl = downloadUri.toString();
                            sendMessage(TYPE_IMAGE, mUrl, null);
//                                Utils.deleteRecursive(myFile);
                        } else {
                            screens.showToast(R.string.msgFailedToUpload);
                        }
                        pd.dismiss();
                    }).addOnFailureListener(e -> {
                        Utils.getErrors(e);
                        screens.showToast(e.getMessage());
                        pd.dismiss();
                    });
        } else {
            screens.showToast(R.string.msgNoImageSelected);
        }
    }

    private void uploadThumbnail(final String filePath) {
        if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
            mainAttachmentLayout.setVisibility(View.GONE);
            attachmentBGView.setVisibility(View.GONE);
            imgAddAttachment.animate().setDuration(400).rotationBy(-45).start();
        }

        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.show();

        File file = new File(filePath);
        final StorageReference storageReference = storageAttachment.child(AttachmentTypes.getTypeName(AttachmentTypes.VIDEO) + SLASH + REF_VIDEO_THUMBS).child(currentId + "_" + file.getName() + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            //If thumbnail exists
            pd.dismiss();
            final Attachment attachment = new Attachment();
            attachment.setData(uri.toString());
            myFileUploadTask(filePath, AttachmentTypes.VIDEO, attachment);
            Utils.deleteRecursive(Utils.getCacheFolder(mActivity));
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                final BaseTask baseTask = new BaseTask() {
                    @Override
                    public void setUiForLoading() {
                        super.setUiForLoading();
                    }

                    @Override
                    public Object call() {
                        return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    }

                    @Override
                    public void setDataAfterLoading(Object result) {
                        final Bitmap bitmap = (Bitmap) result;
                        if (bitmap != null) {
                            //Upload thumbnail and then upload video
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = storageReference.putBytes(data);
                            uploadTask.continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
                            }).addOnCompleteListener(task -> {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    final Uri downloadUri = task.getResult();
                                    final Attachment attachment = new Attachment();
                                    attachment.setData(downloadUri.toString());
                                    myFileUploadTask(filePath, AttachmentTypes.VIDEO, attachment);
                                } else {
                                    myFileUploadTask(filePath, AttachmentTypes.VIDEO, null);
                                }
                                Utils.deleteRecursive(Utils.getCacheFolder(mActivity));
                            }).addOnFailureListener(e1 -> {
                                pd.dismiss();
                                myFileUploadTask(filePath, AttachmentTypes.VIDEO, null);
                                Utils.deleteRecursive(Utils.getCacheFolder(mActivity));
                            });
                        } else {
                            pd.dismiss();
                            myFileUploadTask(filePath, AttachmentTypes.VIDEO, null);
                            Utils.deleteRecursive(Utils.getCacheFolder(mActivity));
                        }
                    }

                };

                final TaskRunner thumbnailTask = new TaskRunner();
                thumbnailTask.executeAsync(baseTask);
            }
        });
    }

    private void myFileUploadTask(String filePath, @AttachmentTypes.AttachmentType final int attachmentType, final Attachment attachment) {
        hideAttachmentView();

        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.setCancelable(false);
        pd.show();

        final File mFileUpload = new File(filePath);
        final String fileName = Utils.getUniqueFileName(mFileUpload, attachmentType);
        final File fileToUpload = new File(Objects.requireNonNull(Utils.moveFileToFolder(mActivity, true, fileName, mFileUpload, attachmentType)).toString(), fileName);

        Utils.sout("newFileUploadTask::: " + fileName + " :Exist: " + fileToUpload.exists() + " >>> " + fileToUpload);
        final StorageReference storageReference = storageAttachment.child(AttachmentTypes.getTypeName(attachmentType)).child(currentId + "_" + fileName);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            //If file is already uploaded
            Attachment myAttachment = null;
            try {
                myAttachment = attachment;
                if (myAttachment == null) myAttachment = new Attachment();
                if (attachmentType == AttachmentTypes.CONTACT) {
                } else {
                    myAttachment.setName(fileToUpload.getName());
                    myAttachment.setFileName(fileName);
                    myAttachment.setDuration(Utils.getVideoDuration(mActivity, fileToUpload));
                }
                myAttachment.setUrl(uri.toString());
                myAttachment.setBytesCount(fileToUpload.length());
            } catch (Exception ignored) {
            }
            sendMessage(AttachmentTypes.getTypeName(attachmentType), uri.toString(), myAttachment);
            pd.dismiss();
            //Utils.deleteRecursive(new File(dir));
            Utils.deleteRecursive(Utils.getCacheFolder(mActivity));
        }).addOnFailureListener(exception -> {
            //Else upload and then send message
            FirebaseUploader firebaseUploader = new FirebaseUploader(storageReference, new FirebaseUploader.UploadListener() {
                @Override
                public void onUploadFail(String message) {
                    Utils.sout("onUploadFail::: " + message);
                    pd.dismiss();
                }

                @Override
                public void onUploadSuccess(String downloadUrl) {
                    Attachment myAttachment = null;
                    try {
                        myAttachment = attachment;
                        if (myAttachment == null) myAttachment = new Attachment();
                        if (attachmentType == AttachmentTypes.CONTACT) {
                        } else {
                            myAttachment.setName(mFileUpload.getName());
                            myAttachment.setFileName(fileName); // fileToUpload.getName()
                            try {
                                myAttachment.setDuration(Utils.getVideoDuration(mActivity, fileToUpload));
                            } catch (Exception e) {
                                Utils.getErrors(e);
                            }
                        }
                        myAttachment.setUrl(downloadUrl);
                        myAttachment.setBytesCount(fileToUpload.length());
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                    sendMessage(AttachmentTypes.getTypeName(attachmentType), downloadUrl, myAttachment);
                    pd.dismiss();
                    try {
                        Utils.deleteRecursive(Utils.getCacheFolder(mActivity));
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                }

                @Override
                public void onUploadProgress(int progress) {
                    try {
                        pd.setMessage("Uploading " + progress + "%...");
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onUploadCancelled() {
                    pd.dismiss();
                }
            });
            firebaseUploader.uploadFile(fileToUpload);
        });
    }

    private void getSendVCard(final List<ContactResult> results) {
        try {
            displayName = results.get(0).getDisplayName();
            phoneNumber = results.get(0).getPhoneNumbers().get(0).getNumber();
        } catch (Exception e) {
            Utils.getErrors(e);
        }

        final BaseTask baseTask = new BaseTask() {
            @Override
            public void setUiForLoading() {
                super.setUiForLoading();
            }

            @Override
            public Object call() {
                Cursor cursor = Utils.contactsCursor(mActivity, phoneNumber);
                File toSend = Utils.getSentDirectory(mActivity, TYPE_CONTACT);//Looks like this : AppName/Contact/.sent/
                if (cursor != null && !cursor.isClosed()) {
                    cursor.getCount();
                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range") String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                        try {
                            AssetFileDescriptor assetFileDescriptor = getContentResolver().openAssetFileDescriptor(uri, "r");
                            if (assetFileDescriptor != null) {
                                FileInputStream inputStream = assetFileDescriptor.createInputStream();
                                boolean dirExists = toSend.exists();
                                if (!dirExists)
                                    dirExists = toSend.mkdirs();
                                if (dirExists) {
                                    try {
                                        toSend = Utils.getSentFile(toSend, EXT_VCF);
                                        boolean fileExists = toSend.exists();
                                        if (!fileExists)
                                            fileExists = toSend.createNewFile();
                                        if (fileExists) {
                                            OutputStream stream = new BufferedOutputStream(new FileOutputStream(toSend, false));
                                            byte[] buffer = Utils.readAsByteArray(inputStream);
                                            vCardData = new String(buffer);
                                            stream.write(buffer);
                                            stream.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Utils.getErrors(e);
                        } finally {
                            cursor.close();
                        }
                    }
                }
                return toSend;
            }

            @Override
            public void setDataAfterLoading(Object result) {
                final File f = (File) result;
                if (f != null && !TextUtils.isEmpty(vCardData)) {
                    Attachment attachment = new Attachment();
                    attachment.setData(vCardData);
                    try {
                        attachment.setName(displayName);
                        attachment.setFileName(displayName);
                        attachment.setDuration(phoneNumber);
                    } catch (Exception ignored) {
                    }
                    myFileUploadTask(f.getAbsolutePath(), AttachmentTypes.CONTACT, attachment);
                }
            }
        };

        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(baseTask);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                if (uploadTask != null && uploadTask.isInProgress()) {
                    screens.showToast(R.string.msgUploadInProgress);
                } else {
                    uploadImage();
                }
            }
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CONTACT:
                    try {
                        assert data != null;
                        List<ContactResult> results = MultiContactPicker.obtainResult(data);
                        getSendVCard(results);
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                    break;

                case REQUEST_CODE_PLAY_SERVICES:
                    openPlacePicker();
            }
        }
    }

    private void hideAttachmentView() {
        if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
            mainAttachmentLayout.setVisibility(View.GONE);
            attachmentBGView.setVisibility(View.GONE);
            imgAddAttachment.animate().setDuration(400).rotationBy(-45).start();
        }
    }

    private void showAttachmentView() {
        mainAttachmentLayout.setVisibility(View.VISIBLE);
        attachmentBGView.setVisibility(View.VISIBLE);
        imgAddAttachment.animate().setDuration(400).rotationBy(45).start();
        emojiIcon.dismiss();
    }

    private void recordingStop(boolean send) {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception ex) {
            mRecorder = null;
        }
        recordTimerStop();
        if (send) {
            myFileUploadTask(recordFilePath, AttachmentTypes.RECORDING, null);
        } else {
            try {
                new File(recordFilePath).delete();
            } catch (Exception ignored) {
            }
        }
    }

    private void permissionRecording() {
        if (!recordPermissionsAvailable()) {
            ActivityCompat.requestPermissions(mActivity, permissionsRecord, REQUEST_PERMISSION_RECORD);
        }
    }

    private void recordingStart() {
        if (blockUnblockCheckBeforeSend()) {
            return;
        }
        if (recordPermissionsAvailable()) {
            File recordFile = Utils.getSentDirectory(mActivity, TYPE_RECORDING);//Looks like this : AppName/RECORDING/Sent/
            boolean dirExists = recordFile.exists();
            if (!dirExists)
                dirExists = recordFile.mkdirs();

            if (dirExists) {
                try {
                    recordFile = Utils.getSentFile(getCacheDir(), EXT_MP3);
                    if (!recordFile.exists())
                        recordFile.createNewFile();
                    recordFilePath = recordFile.getAbsolutePath();
                    Utils.sout("RecordingStart Path: " + recordFilePath);
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mRecorder.setOutputFile(recordFilePath);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mRecorder.prepare();
                    mRecorder.start();
                    recordTimerStart();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mRecorder = null;
                }
            }
        } else {
            permissionRecording();
        }
    }

    private void recordTimerStart() {
        screens.showToast(R.string.recording);
        try {
            recordTimerRunnable = new Runnable() {
                public void run() {
                    recordTimerHandler.postDelayed(this, DELAY_ONE_SEC);
                }
            };
            if (recordTimerHandler == null)
                recordTimerHandler = new Handler(Looper.getMainLooper());

            recordTimerHandler.post(recordTimerRunnable);
        } catch (Exception ignored) {
        }
        Utils.setVibrate(mActivity, VIBRATE_HUNDRED);
    }

    private void recordTimerStop() {
        try {
            recordTimerHandler.removeCallbacks(recordTimerRunnable);
            Utils.setVibrate(mActivity, VIBRATE_HUNDRED);
        } catch (Exception ignored) {
        }
    }

    private boolean recordPermissionsAvailable() {
        boolean available = true;
        for (String permission : permissionsRecord) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                available = false;
                break;
            }
        }
        return available;
    }

    private final ArrayList<Integer> positionList = new ArrayList<>();

    //Download complete listener
    private final BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null)
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    if (positionList.size() > ZERO && messageAdapters != null) {
                        for (int pos : positionList) {
                            if (pos != -1) {
//                                Uncomment to play recording directly once download completed
//                                But before that please stop the current playing audio if playing
//                                try {
//                                    chats.get(pos).setDownloadProgress(COMPLETED);
//                                } catch (Exception ignored) {
//                                }
                                messageAdapters.notifyItemChanged(pos);
                            }
                        }
                    }
                    positionList.clear();
                }
        }
    };

    public void downloadFile(DownloadFileEvent downloadFileEvent) {
        if (permissionsAvailable(permissionsStorage)) {
            new DownloadUtil().loading(this, downloadFileEvent);
            positionList.add(downloadFileEvent.getPosition());
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, 47);
        }
    }

    //Download event listener
    private final BroadcastReceiver downloadEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadFileEvent downloadFileEvent = (DownloadFileEvent) intent.getSerializableExtra(DOWNLOAD_DATA);
            try {
                if (downloadFileEvent != null) {
                    downloadFile(downloadFileEvent);
                }
            } catch (Exception ignored) {
            }
        }
    };

    //
    //  PickerManager Listeners
    //
    //  The listeners can be used to display a Dialog when a file is selected from Dropbox/Google Drive or OnDrive.
    //  The listeners are callbacks from an AsyncTask that creates a new File of the original in /storage/emulated/0/Android/data/your.package.name/files/Temp/
    //
    //  PickerManagerOnUriReturned()
    //  When selecting a file from Google Drive, for example, the Uri will be returned before the file is available(if it has not yet been cached/downloaded).
    //  Google Drive will first have to download the file before we have access to it.
    //  This can be used to let the user know that we(the application), are waiting for the file to be returned.
    //
    //  PickerManagerOnStartListener()
    //  This will be call once the file creations starts and will only be called if the selected file is not local
    //
    //  PickerManagerOnProgressUpdate(int progress)
    //  This will return the progress of the file creation (in percentage) and will only be called if the selected file is not local
    //
    //  PickerManagerOnCompleteListener(String path, boolean wasDriveFile)
    //  If the selected file was from Dropbox/Google Drive or OnDrive, then this will be called after the file was created.
    //  If the selected file was a local file then this will be called directly, returning the path as a String
    //  Additionally, a boolean will be returned letting you know if the file selected was from Dropbox/Google Drive or OnDrive.

    private TextView percentText;
    private ProgressBar mProgressBar;
    private AlertDialog mdialog;
    private ProgressDialog progressBar;

    @Override
    public void PickerManagerOnUriReturned() {
        progressBar = new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.msgWaitingForFile));
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    public void PickerManagerOnStartListener() {
        final Handler mPickHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread. Your worker tells you in the message what to do.
                if (progressBar.isShowing()) {
                    progressBar.cancel();
                }
                final AlertDialog.Builder mPro = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, R.style.myDialog));
                @SuppressLint("InflateParams") final View mPView = LayoutInflater.from(mActivity).inflate(R.layout.dailog_layout, null);
                percentText = mPView.findViewById(R.id.percentText);

                percentText.setOnClickListener(new SingleClickListener() {
                    @Override
                    public void onClickView(View view) {
                        pickerManager.cancelTask();
                        if (mdialog != null && mdialog.isShowing()) {
                            mdialog.cancel();
                        }
                    }
                });

                mProgressBar = mPView.findViewById(R.id.mProgressBar);
                mProgressBar.setMax(100);
                mPro.setView(mPView);
                mdialog = mPro.create();
                mdialog.show();
            }
        };
        mPickHandler.sendEmptyMessage(ZERO);
    }

    @Override
    public void PickerManagerOnProgressUpdate(int progress) {
        try {
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    final String progressPlusPercent = progress + "%";
                    percentText.setText(progressPlusPercent);
                    mProgressBar.setProgress(progress);
                }
            };
            mHandler.sendEmptyMessage(ZERO);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    //REQUEST_PICK_AUDIO, REQUEST_PICK_VIDEO, REQUEST_PICK_DOCUMENT
    @Override
    public void PickerManagerOnCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String reason) {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.cancel();
        }
        Utils.sout("Picker Path :: " + new File(path).exists() + " >> " + path + " :drive: " + wasDriveFile + " :<Success>: " + wasSuccessful);

        int fileType = 0;
        try {
            fileType = Objects.requireNonNull(MediaFile.getFileType(path)).fileType;
        } catch (Exception e) {
            //Utils.getErrors(e);
        }

        if (wasSuccessful) {
            //Utils.sout("Was Successfully::: " + wasSuccessful);
            final int file_size = Integer.parseInt(String.valueOf(new File(path).length() / 1024));

            if (MediaFile.isAudioFileType(fileType)) {
                if (file_size > Utils.getAudioSizeLimit()) {
                    screens.showToast(String.format(getString(R.string.msgFileTooBig), Utils.MAX_SIZE_AUDIO));
                } else {
                    myFileUploadTask(path, AttachmentTypes.AUDIO, null);
                }
            } else if (MediaFile.isVideoFileType(fileType)) {
                if (file_size > Utils.getVideoSizeLimit()) {
                    screens.showToast(String.format(getString(R.string.msgFileTooBig), Utils.MAX_SIZE_VIDEO));
                } else {
                    uploadThumbnail(Uri.parse(path).getPath());
                }
            } else {
                if (file_size > Utils.getDocumentSizeLimit()) {
                    screens.showToast(String.format(getString(R.string.msgFileTooBig), Utils.MAX_SIZE_DOCUMENT));
                } else {
                    myFileUploadTask(path, AttachmentTypes.DOCUMENT, null);
                }
            }

        } else {
            screens.showToast(R.string.msgChooseFileFromOtherLocation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            LocalBroadcastManager.getInstance(this).registerReceiver(downloadEventReceiver, new IntentFilter(BROADCAST_DOWNLOAD_EVENT));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.readStatus(STATUS_ONLINE);
        if (type.equals(PENDING_TICKET)){
            reference = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strSender);
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        String msg = "";
                        if (category.equals("I want to refer friend")){
                            msg = "Hi, Thanks for your message. Please share your friends whatsapp number who is going to join, We will guide him.";

                        }
                        else if (category.equals("App issue")){
                            msg = "Hi, Thanks for your message, Please share the screen shot of the issue you are facing. Share the details and wait for our reply, We will chat with you shortly.";

                        }
                        else if (category.equals("Withdrawal not received")){
                            msg = "Hi, Thanks for your message. Please share the below details. Date Of Withdrawal, Amount Withdrawn, Bank Account Details. Share the details and wait for our reply, We will chat with you shortly.";

                        }
                        else if (category.equals("Refer bonus not received")){
                            msg = "Hi, Thanks for your message. Please share the below details. Date of referral, Joined person Name & Contact number. Share the details and wait for our reply, We will chat with you shortly.";
                        }
                        else {
                            msg = "Hi, Thanks for your message. Please share the query and wait for our reply, We will chat with you shortly.";
                        }
//                        sendMessage(TYPE_TEXT,description, null);
//                        sendAutoMessage(TYPE_TEXT, msg, null);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(downloadCompleteReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadEventReceiver);
        } catch (Exception ignored) {
        }
        try {
            if (messageAdapters != null) {
                messageAdapters.stopAudioFile();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        try {
            pickerManager.deleteTemporaryFile(this);
        } catch (Exception ignored) {
        }
        if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
            hideAttachmentView();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            seenReferenceSender.removeEventListener(seenListenerSender);
            stopTyping();
        } catch (Exception ignored) {
        }
        try {
            if (!isChangingConfigurations()) {
                pickerManager.deleteTemporaryFile(this);
            }
        } catch (Exception ignored) {
        }
    }
}
