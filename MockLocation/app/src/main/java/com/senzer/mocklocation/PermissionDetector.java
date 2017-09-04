package com.senzer.mocklocation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * ProjectName: PermissionDetector
 * Description: 权限检测器（6.0以后文件，拍照，相册等需要手动授权）
 * <p>
 * review by chenpan, wangkang, wangdong 2016/12/26
 * edit by JeyZheng 2016/12/26
 * author: JeyZheng
 * version: 4.0
 * created at: 2016/12/26 16:44
 */
public class PermissionDetector {
    // 6.0权限的基本知识
    // 以下是需要单独申请的权限，共分为9组，每组只要有一个权限申请成功了，就默认整组权限都可以使用了。
//    group:android.permission-group.CONTACTS
//    permission:android.permission.WRITE_CONTACTS
//    permission:android.permission.GET_ACCOUNTS
//    permission:android.permission.READ_CONTACTS
//
//    group:android.permission-group.PHONE
//    permission:android.permission.READ_CALL_LOG
//    permission:android.permission.READ_PHONE_STATE
//    permission:android.permission.CALL_PHONE
//    permission:android.permission.WRITE_CALL_LOG
//    permission:android.permission.USE_SIP
//    permission:android.permission.PROCESS_OUTGOING_CALLS
//    permission:com.android.voicemail.permission.ADD_VOICEMAIL
//
//    group:android.permission-group.CALENDAR
//    permission:android.permission.READ_CALENDAR
//    permission:android.permission.WRITE_CALENDAR
//
//    group:android.permission-group.CAMERA
//    permission:android.permission.CAMERA
//
//    group:android.permission-group.SENSORS
//    permission:android.permission.BODY_SENSORS
//
//    group:android.permission-group.LOCATION
//    permission:android.permission.ACCESS_FINE_LOCATION
//    permission:android.permission.ACCESS_COARSE_LOCATION
//
//    group:android.permission-group.STORAGE
//    permission:android.permission.READ_EXTERNAL_STORAGE
//    permission:android.permission.WRITE_EXTERNAL_STORAGE
//
//    group:android.permission-group.MICROPHONE
//    permission:android.permission.RECORD_AUDIO
//
//    group:android.permission-group.SMS
//    permission:android.permission.READ_SMS
//    permission:android.permission.RECEIVE_WAP_PUSH
//    permission:android.permission.RECEIVE_MMS
//    permission:android.permission.RECEIVE_SMS
//    permission:android.permission.SEND_SMS
//    permission:android.permission.READ_CELL_BROADCASTS

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 0x001;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // Camera Permissions
    public static final int REQUEST_TAKE_PHOTO = 0x002;
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // Phone Permissions
    public static final int REQUEST_PHONE_STATE = 0x003;
    private static String[] PERMISSIONS_PHONE = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE};

    // Contacts Permissions
    public static final int REQUEST_CONTACTS = 0x004;
    private static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS};

    // Calendar Permissions
    public static final int REQUEST_CALENDAR = 0x005;
    private static String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};

    // Sensors Permissions
    public static final int REQUEST_SENSORS = 0x006;
    private static String[] PERMISSIONS_SENSORS = {
            Manifest.permission.BODY_SENSORS};

    // SMS Permissions
    public static final int REQUEST_SMS = 0x007;
    private static String[] PERMISSIONS_SMS = {
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS};

    // Location Permissions
    public static final int REQUEST_LOCATION = 0x008;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    // Microphone Permissions
    public static final int REQUEST_MICROPHONE = 0x009;
    private static String[] PERMISSIONS_MICROPHONE = {
            Manifest.permission.RECORD_AUDIO};

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     * @return
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        boolean isPassed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);

                isPassed = false;
            }
        }

        return isPassed;
    }

    /**
     * Checks if the app has permission to use the camera
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     * @return
     */
    public static boolean verifyCameraPermissions(Activity activity) {
        boolean isPassed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int permission = activity.checkSelfPermission(Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                activity.requestPermissions(PERMISSIONS_CAMERA, REQUEST_TAKE_PHOTO);

                isPassed = false;
            }
        }

        return isPassed;
    }

    /**
     * Checks if the app has permission to read phone state
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     * @return true: granted; false: not granted
     */
    public static boolean verifyPhonePermissions(Activity activity) {
        boolean isPassed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_PHONE_STATE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_PHONE, REQUEST_PHONE_STATE);

                isPassed = false;
            }
        }

        return isPassed;
    }

    /**
     * Checks if the app has permission to location
     * <p>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     * @return true: granted; false: not granted
     */
    public static boolean verifyLocationPermissions(Activity activity) {
        boolean isPassed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_LOCATION);

                isPassed = false;
            }
        }

        return isPassed;
    }
}
