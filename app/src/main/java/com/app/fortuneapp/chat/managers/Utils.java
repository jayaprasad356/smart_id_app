package com.app.fortuneapp.chat.managers;

import static android.os.Build.VERSION.SDK_INT;
import static com.app.fortuneapp.chat.constants.IConstants.CHAT_SUPPORT;
import static com.app.fortuneapp.chat.constants.IConstants.CLOSED_TICKET;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_IS_ONLINE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_LASTSEEN;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_SEARCH;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_VERSION_CODE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_VERSION_NAME;
import static com.app.fortuneapp.chat.constants.IConstants.EXT_MP3;
import static com.app.fortuneapp.chat.constants.IConstants.EXT_VCF;
import static com.app.fortuneapp.chat.constants.IConstants.FALSE;
import static com.app.fortuneapp.chat.constants.IConstants.IMG_DEFAULTS;
import static com.app.fortuneapp.chat.constants.IConstants.IMG_FOLDER;
import static com.app.fortuneapp.chat.constants.IConstants.OPENED_TICKET;
import static com.app.fortuneapp.chat.constants.IConstants.PENDING_TICKET;
import static com.app.fortuneapp.chat.constants.IConstants.REF_CHATS;
import static com.app.fortuneapp.chat.constants.IConstants.REF_OTHERS;
import static com.app.fortuneapp.chat.constants.IConstants.REF_TOKENS;
import static com.app.fortuneapp.chat.constants.IConstants.REF_USERS;
import static com.app.fortuneapp.chat.constants.IConstants.SDPATH;
import static com.app.fortuneapp.chat.constants.IConstants.SENT_FILE;
import static com.app.fortuneapp.chat.constants.IConstants.SLASH;
import static com.app.fortuneapp.chat.constants.IConstants.STATUS_OFFLINE;
import static com.app.fortuneapp.chat.constants.IConstants.STATUS_ONLINE;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE_RECORDING;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.app.fortuneapp.BuildConfig;
import com.app.fortuneapp.R;
import com.app.fortuneapp.chat.fcmmodels.Token;
import com.app.fortuneapp.chat.files.FileUtils;
import com.app.fortuneapp.chat.models.AttachmentTypes;
import com.app.fortuneapp.chat.models.Chat;
import com.app.fortuneapp.chat.models.LocationAddress;
import com.app.fortuneapp.chat.models.Others;
import com.app.fortuneapp.helper.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author : Prashant Adesara
 * @url https://www.bytesbee.com
 * Util class set Default parameter and access in application
 */
public class Utils {

    public static final boolean IS_TRIAL = false;
    private static final int DEFAULT_VIBRATE = 500;
    public static boolean online = true, offline = true;
    public static boolean male = true, female = true, notset = true;
    public static boolean withPicture = true, withoutPicture = true;


    static final int ONE_MB = 1024;
    public static int MAX_SIZE_AUDIO = 10; // 10 MB Maximum
    public static int MAX_SIZE_VIDEO = 15; // 15 MB Maximum
    public static int MAX_SIZE_DOCUMENT = 5; // 5 MB Maximum

    final static String DEF_TEXT = "Please update your app to get attachment options and many new features.";
    public static String UPDATE_TEXT = "";


    public static String getDefaultMessage() {
        if (Utils.isEmpty(UPDATE_TEXT)) {
            return DEF_TEXT;
        } else {
            return UPDATE_TEXT;
        }
    }
    public static void uploadTypingStatus() {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_OTHERS);
                Others token = new Others(FALSE);
                reference.child(firebaseUser.getUid()).setValue(token);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }
    public static String showOnlineOffline(Context context, int status) {
        if (status == STATUS_ONLINE) {
            return context.getString(R.string.strOnline);
        }
        return context.getString(R.string.strOffline);
    }
    public static Typeface getRegularFont(Context context) {
        return ResourcesCompat.getFont(context, R.font.roboto_regular);
    }
    public static void closeKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void readStatus(int status) {
        try {
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                Utils.updateOnlineStatus(firebaseUser.getUid(), status);
            }
        } catch (Exception ignored) {
        }
    }

    public static void updateOnlineStatus(final String userId, final int status) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(EXTRA_IS_ONLINE, status);
            hashMap.put(EXTRA_VERSION_CODE, BuildConfig.VERSION_CODE);
            hashMap.put(EXTRA_VERSION_NAME, BuildConfig.VERSION_NAME);
            if (status == STATUS_OFFLINE)
                hashMap.put(EXTRA_LASTSEEN, Utils.getDateTime());
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }
    public static String getExtension(Context context, final Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private static long getVideoDurationValidation(Context context, File file) {
        try {
            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.fromFile(file));
            final String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final long durationMs = Long.parseLong(time);
            retriever.release();
            return durationMs;
        } catch (Exception ignored) {
        }
        return 0;
    }
    public static Cursor contactsCursor(final Context context, final String searchText) {
        try {
            final String search = Utils.isEmpty(searchText) ? null : Uri.encode(searchText);
            final Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, search);
            return context.getContentResolver().query(uri, null, null, null, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getVideoDuration(Context context, File file) {
        return convertSecondsToHMmSs(getVideoDurationValidation(context, file));
    }

    public static File getDownloadDirectory(Context context, String type) {
        String directoryName = type;
        if (type.equalsIgnoreCase(TYPE_RECORDING)) {
            directoryName = AttachmentTypes.getTypeName(AttachmentTypes.RECORDING);
        }
        Utils.sout("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Download Directory::: " + type + " == " + directoryName);
        return new File(AttachmentTypes.getDirectoryByType(type), SLASH + context.getString(R.string.app_name) + SLASH + directoryName);
    }

    public static File moveFileToFolder(final Context mContext, boolean isStoreFile, String newFileName, File sourceFile, int attachmentType) {
        try {
            final ContentResolver resolver = mContext.getContentResolver();
            final ContentValues contentValues = new ContentValues();
            final String type = AttachmentTypes.getTypeName(attachmentType);
            final File dest = isStoreFile ? Utils.getSentDirectory(mContext, type) : Utils.getDownloadDirectory(mContext, type);
            Uri target;

            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);
            try {
                final String mimeType = FileUtils.getMimeType(new File(newFileName));
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                //Utils.sout("OKNew File to Folder::: " + isStoreFile + " >> " + newFileName + " >>> " + type + " >dest> " + dest + " ::mimeType:: " + mimeType);
            } catch (Exception ignored) {

            }
            if (Utils.isAboveQ()) {
                String tempDest = dest.toString().replaceAll(SDPATH, "");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, tempDest);
                target = AttachmentTypes.getTargetUri(type); //MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            } else {
                contentValues.put(MediaStore.MediaColumns.DATA, dest.toString());
                target = MediaStore.Files.getContentUri("external");
            }
            Uri uri = resolver.insert(target, contentValues);
            if (isStoreFile) {
                try {
                    if (Utils.isAboveQ()) {
                        try {
                            InputStream is = new FileInputStream(sourceFile);
                            OutputStream os = resolver.openOutputStream(uri, "rwt");
                            byte[] buffer = new byte[1024];
                            for (int r; (r = is.read(buffer)) != -1; ) {
                                os.write(buffer, 0, r);
                            }
                            os.flush();
                            os.close();
                            is.close();
                        } catch (Exception e) {
                            Utils.getErrors(e);
                        }
                    } else {
                        final File newFile = new File(dest, newFileName);
                        FileUtils.copyFileToDest(sourceFile, newFile);
                    }
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
            }
            //Utils.sout("moved File successfully:: " + dest.toString());
            return dest;
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        return null;
    }
    public static String getUniqueFileName(final File fileToUpload, int attachmentType) {
        String pathSegment = Uri.fromFile(fileToUpload).getLastPathSegment();
        String fileExtension = FileUtils.getExtension(pathSegment);
        if (!Utils.isEmpty(fileExtension)) {
            pathSegment = pathSegment.replaceAll(fileExtension, AttachmentTypes.getExtension(fileExtension, attachmentType));
        }

//        String end = "_" + System.currentTimeMillis() + fileExtension;
        String end = "_" + Utils.getDateTimeStampName() + fileExtension;
        //        Utils.sout("----New File Name:: " + file + " >>> " + end);
        return pathSegment.replaceAll(fileExtension, end);
    }



    public static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory != null) {
                if (fileOrDirectory.isDirectory())
                    for (File child : fileOrDirectory.listFiles())
                        deleteRecursive(child);

                fileOrDirectory.delete();
            }
        } catch (Exception e) {
            //Utils.getErrors(e);
        }
    }
    public static String getChatUniqueId() {
        return FirebaseDatabase.getInstance().getReference().child(REF_CHATS).child("").push().getKey();
    }

    public static File getSentFile(final File directory, final String extension) {
//        final String ext = "_" + Utils.getDateTimeName() + extension;
        if (extension.equalsIgnoreCase(EXT_MP3)) {
            return new File(directory, "REC" + extension);
        } else if (extension.equalsIgnoreCase(EXT_VCF)) {
            return new File(directory, "CONT" + extension);
        }
        return new File(directory, Utils.getDateTimeStampName() + extension);
    }

    public static byte[] readAsByteArray(InputStream ios) throws IOException {
        ByteArrayOutputStream ous = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            int read;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException ignored) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException ignored) {
            }
        }
        return ous.toByteArray();
    }
    public static File getCacheFolder(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = Utils.getDateTimeStampName();
        String imageFileName = "PIC_" + timeStamp;
        File image, storageDir;

        if (Utils.isAboveQ()) {
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + File.separator + IMG_FOLDER);
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
//            String currentPhotoPath = image.getAbsolutePath();
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + IMG_FOLDER);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            image = new File(storageDir, imageFileName + ".jpg");
            image.createNewFile();
        }
        return image;
    }



    public static Typeface getBoldFont(Context context) {
        return ResourcesCompat.getFont(context, R.font.roboto_bold);
    }

    public static int getAudioSizeLimit() {
        return MAX_SIZE_AUDIO * ONE_MB;
    }

    public static int getVideoSizeLimit() {
        return MAX_SIZE_VIDEO * ONE_MB;
    }

    public static int getDocumentSizeLimit() {
        return MAX_SIZE_DOCUMENT * ONE_MB;
    }

    public static void sout(String msg) {
        if (IS_TRIAL) {
            System.out.println("Pra :: " + msg);
        }
    }
    public static String convertSecondsToHMmSs(final long mySec) {
        final long seconds = mySec / 1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }
    public static File getReceiveDirectory(Context context, String type) {
        final String directoryName = AttachmentTypes.getTypeName(type);
        final String mainPath = SLASH + context.getString(R.string.app_name) + SLASH + directoryName;
        File file;
        if (isAboveQ()) {
            file = new File(SDPATH + AttachmentTypes.getDirectoryByType(type), mainPath);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), SLASH + AttachmentTypes.getDirectoryByType(type) + mainPath);
        }
        return file;
    }
    public static String getMusicFolder() {
        return Environment.DIRECTORY_MUSIC;
    }

    public static String getMoviesFolder() {
        return Environment.DIRECTORY_MOVIES;
    }

    public static String getDownloadFolder() {
        return Environment.DIRECTORY_DOWNLOADS;
    }



    public static File getSentDirectory(Context context, String type) {
        final String directoryName = AttachmentTypes.getTypeName(type);
        final String systemFolder = AttachmentTypes.getDirectoryByType(directoryName);// Audio, Movie or Download(System Folders)

        File file;
        if (isAboveQ()) {
            file = new File(SDPATH + systemFolder, SLASH + context.getString(R.string.app_name) + SLASH + directoryName + SENT_FILE);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), SLASH + systemFolder + SLASH + context.getString(R.string.app_name) + SLASH + directoryName + SENT_FILE);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }
    public static String getFileExtensionFromPath(String string) {
        int index = string.lastIndexOf(".");
        return string.substring(index + 1);
    }
    public static Uri getUriForFileProvider(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getString(R.string.authority), file);
    }

    //Used to open the file by system
    public static Intent getOpenFileIntent(final Context context, final String path) {
        final String fileExtension = getFileExtensionFromPath(path);
        final File toInstall = new File(path);

        //if it's apk make the system open apk installer
        if (fileExtension.equalsIgnoreCase("apk")) {
            if (SDK_INT >= Build.VERSION_CODES.N) {
                final Uri apkUri = getUriForFileProvider(context, toInstall);
                final Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                return intent;
            } else {
                final Uri apkUri = Uri.fromFile(toInstall);
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
            }

        } else { //else make the system open an app that can handle given type
            if (SDK_INT >= Build.VERSION_CODES.N) {
                final Intent newIntent = new Intent(Intent.ACTION_VIEW);
                newIntent.setDataAndType(getUriForFileProvider(context, toInstall), Utils.getMimeType(context, Uri.fromFile(toInstall)));
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                return newIntent;
            } else {
                final Intent newIntent = new Intent(Intent.ACTION_VIEW);
                newIntent.setDataAndType(getUriForFileProvider(context, toInstall), Utils.getMimeType(context, Uri.fromFile(toInstall)));
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return newIntent;
            }
        }
    }
    public static void openPlayingVideo(final Context context, final File file) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(getUriForFileProvider(context, file), Utils.getMimeType(context, Uri.fromFile(file)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }
    public static void shareApp(final Activity mActivity, final String title) {
        try {
            final String app_name = Html.fromHtml(title).toString();
            final String share_text = Html.fromHtml(mActivity.getResources().getString(R.string.strShareContent)).toString();
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, app_name + "\n\n" + share_text + "\n\n" + "https://play.google.com/store/apps/details?id=" + mActivity.getPackageName());
            sendIntent.setType("text/plain");
            mActivity.startActivity(sendIntent);
        } catch (Resources.NotFoundException e) {
            Utils.getErrors(e);
        }
    }
    public static void openMapWithAddress(final Context mContext, final LocationAddress locationAddress) {
        try {
            final Uri gmmIntentUri = Uri.parse("geo:" + locationAddress.getLatitude() + "," + locationAddress.getLongitude() + "?q=" + Uri.encode(locationAddress.getAddress()));
            final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(mapIntent);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }
    private static final String staticMap = "https://maps.googleapis.com/maps/api/staticmap?key=%s&center=%s,%s&zoom=18&size=280x160&scale=2&format=jpg&markers=color:red|%s,%s|scale:4";

    public static void showStaticMap(final Context mContext, final LocationAddress locationAddress, int topLeft, int topRight, ImageView imgLocation) {
        Glide.with(mContext).load(String.format(staticMap, mContext.getString(R.string.key_maps), locationAddress.getLatitude(), locationAddress.getLongitude(), locationAddress.getLatitude(), locationAddress.getLongitude()))
                .transform(new CenterInside(), new GranularRoundedCorners(topLeft, topRight, 4, 4))
                .into(imgLocation);
    }
    public static void openCallIntent(final Context context, final String number) {
        try {
            final Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void shareApp(final Activity mActivity) {
        shareApp(mActivity, mActivity.getResources().getString(R.string.app_name));
    }


    public static String getMimeType(final Context context, final Uri uri) {
        String mimeType = null;
        try {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                final ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                final String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        return mimeType;
    }



    public static String getFileSize(final long size) {
        try {
            final int BYTES_IN_KILOBYTES = 1024;
            final DecimalFormat dec = new DecimalFormat("###.#");
            final String KILOBYTES = " KB";
            final String MEGABYTES = " MB";
            final String GIGABYTES = " GB";
            float fileSize = 0;
            String suffix = KILOBYTES;

            if (size > BYTES_IN_KILOBYTES) {
                fileSize = size / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    if (fileSize > BYTES_IN_KILOBYTES) {
                        fileSize = fileSize / BYTES_IN_KILOBYTES;
                        suffix = GIGABYTES;
                    } else {
                        suffix = MEGABYTES;
                    }
                }
            }
            return dec.format(fileSize) + suffix;
        } catch (Exception e) {
            return "";
        }
    }


    public static void setChatImage(Context mContext, String imgUrl, ImageView mImageView) {
        try {
            final int roundedCorner = 16;
            final GranularRoundedCorners gCorner = new GranularRoundedCorners(roundedCorner, roundedCorner, roundedCorner, roundedCorner);
            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {
//                Picasso.get().load(imgUrl).placeholder(R.drawable.image_load).fit().centerCrop().into(mImageView);
                Glide.with(mContext).load(imgUrl).placeholder(R.drawable.image_load)
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .transform(new CenterCrop(), gCorner)
                        .into(mImageView);
            } else {
//                Picasso.get().load(R.drawable.image_load).fit().centerCrop().into(mImageView);
                Glide.with(mContext).load(R.drawable.image_load).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CenterCrop(), gCorner)
                        .into(mImageView);
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean isAboveQ() {
        return SDK_INT >= Build.VERSION_CODES.Q;
    }
    public static Map<String, Chat> sortByChatDateTime(Map<String, Chat> unsortMap, final boolean order) {

        List<Entry<String, Chat>> list = new LinkedList<>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, Chat>>() {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            public int compare(Entry<String, Chat> o1, Entry<String, Chat> o2) {
                try {
                    if (order) {
                        return dateFormat.parse(o1.getValue().getDatetime()).compareTo(dateFormat.parse(o2.getValue().getDatetime()));
                    } else {
                        return dateFormat.parse(o2.getValue().getDatetime()).compareTo(dateFormat.parse(o1.getValue().getDatetime()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<String, Chat> sortedMap = new LinkedHashMap<>();
        for (Entry<String, Chat> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static Query getQuerySortBySearch() {
        return FirebaseDatabase.getInstance().getReference(REF_USERS).orderByChild(EXTRA_SEARCH).startAt("").endAt("" + "\uf8ff");
    }
    public static Query getQueryPendingTicket() {
        return FirebaseDatabase.getInstance().getReference(PENDING_TICKET);
    }
    public static Query getQuerySupportStatus() {
        return FirebaseDatabase.getInstance().getReference(CHAT_SUPPORT);
    }
    public static Query getQueryPendingTicketByMyId(String mobile) {
        return FirebaseDatabase.getInstance().getReference(PENDING_TICKET).orderByChild(Constant.MOBILE).equalTo(mobile);
    }
    public static Query getQueryOpenedTicketByMyId(String mobile) {
        return FirebaseDatabase.getInstance().getReference(OPENED_TICKET).orderByChild(Constant.MOBILE).equalTo(mobile);
    }
    public static Query checKPendingTicketUser(String TicketId) {
        return FirebaseDatabase.getInstance().getReference(PENDING_TICKET).child(TicketId);
    }
    public static Query getQueryOpenedTicket() {
        return FirebaseDatabase.getInstance().getReference(OPENED_TICKET);
    }
    public static Query getQueryClosedTicket() {
        return FirebaseDatabase.getInstance().getReference(CLOSED_TICKET);
    }
    public static Query getQueryClosedTicketByMyId(String mobile) {
        return FirebaseDatabase.getInstance().getReference(CLOSED_TICKET).orderByChild(Constant.MOBILE).equalTo(mobile);
    }
    public static void setProfileImage(Context context, String imgUrl, ImageView mImageView) {
        try {

            if (!imgUrl.equalsIgnoreCase(IMG_DEFAULTS)) {
//                Picasso.get().load(imgUrl).fit().placeholder(R.drawable.profile_avatar).into(mImageView);
                Glide.with(context).load(imgUrl).placeholder(R.drawable.profile_avatar)
                        .thumbnail(0.5f)
                        .into(mImageView);
            } else {
//                Picasso.get().load(R.drawable.profile_avatar).fit().into(mImageView);
                Glide.with(context).load(R.drawable.profile_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImageView);
            }
        } catch (Exception ignored) {
        }
    }
    public static void setVibrate(final Context mContext) {
        // Vibrate for 500 milliseconds
        setVibrate(mContext, DEFAULT_VIBRATE);
    }
    @SuppressLint("MissingPermission")
    public static void setVibrate(final Context mContext, long vibrate) {
        try {
            Vibrator vib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(vibrate, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vib.vibrate(vibrate); //deprecated in API 26
            }
        } catch (Exception ignored) {
        }
    }



    public static boolean isEmpty(final Object s) {
        if (s == null) {
            return true;
        }
        if ((s instanceof String) && (((String) s).trim().length() == 0)) {
            return true;
        }
        if (s instanceof Map) {
            return ((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return ((List<?>) s).isEmpty();
        }
        if (s instanceof Object[]) {
            return (((Object[]) s).length == 0);
        }
        return false;
    }

    public static void getErrors(final Exception e) {
        if (IS_TRIAL) {
            final String stackTrace = "Pra ::" + Log.getStackTraceString(e);
            System.out.println(stackTrace);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String getDateTime() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date date = new Date();

        return dateFormat.format(date);
    }

    public static String getDateTimeStampName() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCapsWord(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatDateTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatLocalTime(long timeInMillis) {
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormatUTC.parse(formatTime(timeInMillis));
        } catch (Exception ignored) {
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        if (date == null) {
            return dateFormat.format(timeInMillis);
        }
        return dateFormat.format(date);
    }

    public static String formatLocalFullTime(long timeInMillis) {
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormatUTC.parse(formatDateTime(timeInMillis));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        if (date == null) {
            return dateFormat.format(timeInMillis);
        }
        return dateFormat.format(date);
    }

    public static String formatDateTime(final Context context, final String timeInMillis) {
        long localTime = 0L;
        try {
            localTime = dateToMillis(formatLocalFullTime(dateToMillis(timeInMillis)));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        if (isToday(localTime)) {
            return formatTime(context, localTime);
        } else {
            return formatDateNew(localTime);
        }
    }

    public static long dateToMillis(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = sdf.parse(dateString);
        assert date != null;
        return date.getTime();
    }
    public static void uploadToken(String referenceToken,String userId) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(REF_TOKENS);
            Token token = new Token(referenceToken);
            reference.child(userId).setValue(token);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static String formatFullDate(String timeString) {
        long timeInMillis = 0;
        try {
            timeInMillis = dateToMillis(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timeInMillis).toUpperCase();
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDateNew(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yy HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(Calendar.getInstance().getTimeInMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     *
     * @param millisFirst  The time in milliseconds of the first date.
     * @param millisSecond The time in milliseconds of the second date.
     * @return Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }

    public static String formatLocalTime(Context context, long when) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        int flags = DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL;

        if (then.year != now.year) {
            flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        return DateUtils.formatDateTime(context, when, flags);
    }

    public static String formatTime(Context context, long when) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        int flags = DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL;

        if (then.year != now.year) {
            flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        return DateUtils.formatDateTime(context, when, flags);
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        // Create a new LinkedHashSet

        // Add the elements to set
        Set<T> set = new LinkedHashSet<>(list);

        // Clear the list
        list.clear();

        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);

        // return the list
        return list;
    }


    public static void RTLSupport(Window window) {
        try {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }



    public static Map<String, String> sortByString(Map<String, String> unsortMap, final boolean order) {

        List<Entry<String, String>> list = new LinkedList<>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, String>>() {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                try {
                    if (order) {
                        return dateFormat.parse(o1.getValue()).compareTo(dateFormat.parse(o2.getValue()));
                    } else {
                        return dateFormat.parse(o2.getValue()).compareTo(dateFormat.parse(o1.getValue()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    public static void chatSendSound(Context context) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd("chat_tone.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }


}
