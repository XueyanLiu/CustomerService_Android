package com.customerservice.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.customerservice.chat.jsonmodel.CsActionMsgEntity;
import com.customerservice.chat.jsonmodel.CsCardMsgEntity;
import com.customerservice.chat.jsonmodel.CsJsonParentEntity;
import com.customerservice.chat.jsonmodel.CsLinkMsgEntity;
import com.customerservice.chat.jsonmodel.CsNoticeMsgEntity;
import com.customerservice.chat.jsonmodel.CsTextMsgEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2016/12/8.
 */

public class CsAppUtils {

    /**
     * 游云：1-20525-4ab3a7c3ddb665945d0074f51e979ef0-andriod   6f3efde9fb49a76ff6bfb257f74f4d5b  584612
     * 库拍：    1-20119-f36892c6a26908e6e1b8e15f98d16ba7-andriod   08ff34703ff6ad82c9e8089ac272b4e9  607423
     */
    public static String CLIENT_ID = "1-20119-f36892c6a26908e6e1b8e15f98d16ba7-andriod";
    public static String SECRET = "08ff34703ff6ad82c9e8089ac272b4e9";
    /**
     * 游云测试：1-20142-2e563db99a8ca41df48973b0c43ea50a-andriod   ace518dab1fde58eacb126df6521d34c  549341
     * 库拍测试：    1-20119-f36892c6a26908e6e1b8e15f98d16ba7-andriod   08ff34703ff6ad82c9e8089ac272b4e9  652747
     */
    public static String CLIENT_ID_TEST = "1-20119-f36892c6a26908e6e1b8e15f98d16ba7-andriod";
    public static String SECRET_TEST = "08ff34703ff6ad82c9e8089ac272b4e9";

    public static boolean isOnlinePlatform;
    public static Context mAppContext;
    public static String uid; // 用户ID
    public static String nickName; // 用户昵称
    public static String headUrl = "http://avatar.csdn.net/6/A/5/1_y331271939.jpg"; // 用头像

    public static String CUSTOM_SERVICE_ID; // 客服ID
    public static final String CUSTOM_SERVICE_FIXED_ID = "607423"; // 正式客服id  // 584612
    public static final String CUSTOM_SERVICE_FIXED_ID_TEST = "652747"; // 测试客服id  // 549341

    public static final long MSG_TIME_SEPARATE = 300000L; // IM时间间隔5分钟

    public static final String MSG_TYPE_RECV_UNREAD_NUM = "msg_type_recv_unread_num"; // 收到未读消息数

    public static final String MSG_TYPE_RECEIVE = "msg_type_receive"; // 收到消息
    public static final String TYPE_MSG = "type_msg";
    public static final String MSG_TYPE_SEND_FILE_PRO = "msg_type_send_file_pro"; // 上传文件进度
    public static final String MSG_TYPE_DOWNLOAD_FILE_PRO = "msg_type_download_file_pro"; // 下载文件进度
    public static final String FILE_FILEID = "file_fileid";
    public static final String FILE_PROGRESS = "file_progress";

    public static final String MSG_TYPE_DOWNLOAD_IMAGE_FINISH = "msg_type_download_image_finish"; // 收到大图后更新聊天数据,避免重复下载
    public static final String MSG_TYPE_POSITION = "msg_type_position"; // 收到大图后更新聊天数据,避免重复下载

    public static int unReadNum = 0; // 用于暂时储存未读消息数

    public static int mScreenWidth;
    public static int mScreenHeigth;

    public static void init(Context context) {
        mAppContext = context;
        mScreenWidth = mAppContext.getResources().getDisplayMetrics().widthPixels;
        mScreenHeigth = mAppContext.getResources().getDisplayMetrics().heightPixels;
        if (mScreenWidth > mScreenHeigth) {
            mScreenWidth = mAppContext.getResources().getDisplayMetrics().heightPixels;
            mScreenHeigth = mAppContext.getResources().getDisplayMetrics().widthPixels;
        }
    }

    /**
     * 获取Android Id
     *
     * @return
     */
    public static String generateOpenUDID(Activity activity) {
        // Try to get the ANDROID_ID
        String OpenUDID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (OpenUDID == null || OpenUDID.equals("9774d56d682e549c") | OpenUDID.length() < 15) {
            // if ANDROID_ID is null, or it's equals to the GalaxyTab generic
            // ANDROID_ID or bad, generates a new one
            final SecureRandom random = new SecureRandom();
            OpenUDID = new BigInteger(64, random).toString(16);
        }
        return OpenUDID;
    }

    /**
     * Toast
     *
     * @param msg
     */
    private static Toast toast;

    public static void toastMessage(String msg) {
        if (toast == null)
            toast = Toast.makeText(mAppContext, msg, Toast.LENGTH_SHORT);
        else
            toast.setText(msg);
        toast.show();
    }

    //////////////////////////////////////Android 6.0 运行时权限/////////////////////////////////////////

    public interface PermissionCallback{
        void onComplete(int requestCode);
    }

    public static void requestPermission(final Activity activity, final int requestCode, PermissionCallback callback, String... permissions) {
        List<String> deniedPermissions = findDeniedPermissions(activity, permissions);
        if (deniedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
        } else {
            if(callback != null)
                callback.onComplete(requestCode);
        }
    }

    private static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (ActivityCompat.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    //////////////////////////////////////客服JSON消息处理/////////////////////////////////////////

    private static final String TYPE = "type";
    private static final String TEXT = "text";
    private static final String LINK = "link";
    private static final String ACTION = "action";
    private static final String EVENT = "event";
    private static final String CARD = "card";
    private static final String CONTENT = "content";
    private static final String PARAMS = "params";
    private static final String URL = "url";
    private static final String DATA_ID = "data_id";
    private static final String SORT = "sort";
    private static final String ENTER_KEY = "entercs";
    private static final String LEAVE_KEY = "leavecs";
    private static final String FROM = "from";
    public static final String NICK_NAME = "name";
    public static final String HEAD_URL = "pic";
    public static final String USERID = "uid";
    public static final String SYSTEM_MSG_CODE = "50001";

    /**
     * 封装text消息
     *
     * @param text
     * @return
     */
    public static String encapsulateTextMsg(String text) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, TEXT);
            object.put(CONTENT, text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 封装头像额外消息
     *
     * @return
     */
    public static String encapsulateExt() {
        JSONObject object = new JSONObject();
        try {
            object.put(USERID, uid);
            object.put(NICK_NAME, nickName);
            object.put(HEAD_URL, headUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 封装进入和离开客服界面发送给Gateway的命令
     *
     * @param sort 1:enter 2:leave
     * @return
     */
    public static String encapsulateEnterOrLeaveMsg(int sort) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, EVENT);
            JSONObject paramObj = new JSONObject();
            if (1 == sort)
                paramObj.put(SORT, ENTER_KEY);
            else if (2 == sort)
                paramObj.put(SORT, LEAVE_KEY);
            paramObj.put(FROM, "room");
            object.put(PARAMS, paramObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 封装Action消息
     *
     * @param csActionMsgEntity
     * @return
     */
    public static String encapsulateClickMsg(CsActionMsgEntity csActionMsgEntity) {
        return csActionMsgEntity.actionJson;
    }

    /**
     * 解析机器人消息
     *
     * @param json
     * @return
     */
    public static CsJsonParentEntity parseRobotMsg(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has(TYPE)) {
                String type = object.getString(TYPE);
                if (TEXT.equals(type)) {
                    String content = object.getString(CONTENT);
                    if (object.has(DATA_ID) && SYSTEM_MSG_CODE.equals(object.getString(DATA_ID))) {
                        CsNoticeMsgEntity noticeMsgEntity = new CsNoticeMsgEntity();
                        noticeMsgEntity.content = content;
                        return noticeMsgEntity;
                    } else {
                        CsTextMsgEntity csTextMsgEntity = new CsTextMsgEntity();
                        csTextMsgEntity.content = content;
                        return csTextMsgEntity;
                    }
                } else if (LINK.equals(type)) {
                    String content = object.getString(CONTENT);
                    String url = object.getString(URL);
                    CsLinkMsgEntity linkMsgEntity = new CsLinkMsgEntity();
                    linkMsgEntity.content = content;
                    linkMsgEntity.url = url;
                    return linkMsgEntity;
                } else if (ACTION.equals(type)) {
                    String content = object.getString(CONTENT);
                    CsActionMsgEntity csActionMsgEntity = new CsActionMsgEntity();
                    csActionMsgEntity.content = content;
                    csActionMsgEntity.actionJson = object.toString();
                    return csActionMsgEntity;
                } else if (CARD.equals(type)) {
                    CsCardMsgEntity cardMsgEntity = new CsCardMsgEntity();
                    List<CsJsonParentEntity> list = new ArrayList<>();
                    JSONArray array = object.getJSONArray(CONTENT);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject arrayObj = array.getJSONObject(i);
                        list.add(parseRobotMsg(arrayObj.toString()));
                    }
                    cardMsgEntity.content = list;
                    return cardMsgEntity;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isJSONObject(String json) {
        try {
            JSONTokener tokener = new JSONTokener(json);
            if (tokener != null) {
                Object object = tokener.nextValue();
                if (object instanceof JSONObject) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isJSONArray(String json) {
        try {
            JSONTokener tokener = new JSONTokener(json);
            if (tokener != null) {
                Object object = tokener.nextValue();
                if (object instanceof JSONArray) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 自己封装发送客服消息类型，测试使用
     *
     * @return
     */
    @Deprecated
    public static String encapsulateTest() {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, CARD);
            JSONArray array = new JSONArray();


            JSONObject textObj = new JSONObject();
            textObj.put(TYPE, TEXT);
            textObj.put(CONTENT, "您好，请选择一下问题，点击即可获取答案哦！");
            array.put(textObj);


            JSONObject cardObj2 = new JSONObject();
            cardObj2.put(TYPE, CARD);
            JSONArray cardArray2 = new JSONArray();

            JSONObject actionObj = new JSONObject();
            actionObj.put(TYPE, ACTION);
            actionObj.put(CONTENT, "付款后什么时候可以发货呢？");
            JSONObject paramObj = new JSONObject();
            paramObj.put("id", 1);
            paramObj.put("time", System.currentTimeMillis());
            actionObj.put(PARAMS, paramObj);
            cardArray2.put(actionObj);

            JSONObject actionObj2 = new JSONObject();
            actionObj2.put(TYPE, ACTION);
            actionObj2.put(CONTENT, "请问发什么快递呢？");
            JSONObject paramObj2 = new JSONObject();
            paramObj2.put("id", 1);
            paramObj2.put("time", System.currentTimeMillis());
            actionObj2.put(PARAMS, paramObj2);
            cardArray2.put(actionObj2);

            JSONObject actionObj3 = new JSONObject();
            actionObj3.put(TYPE, ACTION);
            actionObj3.put(CONTENT, "可以指定某个快递吗？");
            JSONObject paramObj3 = new JSONObject();
            paramObj3.put("id", 1);
            paramObj3.put("time", System.currentTimeMillis());
            actionObj3.put(PARAMS, paramObj3);
            cardArray2.put(actionObj3);

            JSONObject actionObj4 = new JSONObject();
            actionObj4.put(TYPE, ACTION);
            actionObj4.put(CONTENT, "发货地址写错了，可以更改吗？");
            JSONObject paramObj4 = new JSONObject();
            paramObj4.put("id", 1);
            paramObj4.put("time", System.currentTimeMillis());
            actionObj4.put(PARAMS, paramObj4);
            cardArray2.put(actionObj4);

            cardObj2.put(CONTENT, cardArray2);
            array.put(cardObj2);


            JSONObject cardObj3 = new JSONObject();
            cardObj3.put(TYPE, CARD);
            JSONArray cardArray3 = new JSONArray();

            JSONObject textObj3 = new JSONObject();
            textObj3.put(TYPE, TEXT);
            textObj3.put(CONTENT, "没有我要的答案，请选择");
            cardArray3.put(textObj3);

            JSONObject actionObj5 = new JSONObject();
            actionObj5.put(TYPE, ACTION);
            actionObj5.put(CONTENT, "接人工客服");
            JSONObject paramObj5 = new JSONObject();
            paramObj5.put("id", 1);
            paramObj5.put("time", System.currentTimeMillis());
            actionObj5.put(PARAMS, paramObj5);
            cardArray3.put(actionObj5);

            cardObj3.put(CONTENT, cardArray3);
            array.put(cardObj3);


            JSONObject cardObj4 = new JSONObject();
            cardObj4.put(TYPE, CARD);
            JSONArray cardArray4 = new JSONArray();

            JSONObject textObj4 = new JSONObject();
            textObj4.put(TYPE, TEXT);
            textObj4.put(CONTENT, "商品详情页：");
            cardArray4.put(textObj4);

            JSONObject actionObj6 = new JSONObject();
            actionObj6.put(TYPE, LINK);
            actionObj6.put(CONTENT, "点击我！！！");
            actionObj6.put(URL, "http://17youyun.com/");
            cardArray4.put(actionObj6);

            cardObj4.put(CONTENT, cardArray4);
            array.put(cardObj4);

            object.put(CONTENT, array);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    ////////////////////////////图片压缩处理///////////////////////

    public static String compressImage(String path, String destPath) {
        try {
            // 获取源图片的大小
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 当opts不为null时，但decodeFile返回空，不为图片分配内存，只获取图片的大小，并保存在opts的outWidth和outHeight
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;

            // 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
            /*if (srcWidth > srcHeight) {
                ratio = (double) srcWidth / (double) 1600;
				destWidth = 1600;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / (double) 1200;
				destHeight = 1200;
				destWidth = (int) (srcWidth / ratio);
			}*/

            if ((srcHeight > 4000 && srcWidth < 1000)
                    || (srcWidth > 4000 && srcHeight < 1000))
                return path;
            int min = srcWidth > srcHeight ? srcHeight : srcWidth;
            if (min <= 720) {
                destHeight = srcHeight;
                destWidth = srcWidth;
            } else {
                if (min == srcHeight) {
                    ratio = (double) srcHeight / (double) 720;
                    destHeight = 720;
                    destWidth = (int) (srcWidth / ratio);
                } else {
                    ratio = (double) srcWidth / (double) 720;
                    destWidth = 720;
                    destHeight = (int) (srcHeight / ratio);
                }
            }
            // 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 添加尺寸信息，
            // 获取缩放后图片
            Bitmap destBm = BitmapFactory.decodeFile(path, newOpts);

			/*if (srcWidth < srcHeight) {
                Matrix matrix = new Matrix(); // 将图像顺时针旋转90度
				matrix.setRotate(270); // 生成旋转后的图像
				destBm = Bitmap.createBitmap(destBm, 0, 0, destBm.getWidth(),destBm.getHeight(), matrix, false);
			}*/

            if (destBm == null) {
                return path;
            } else {
                try {
                    ExifInterface exif = new ExifInterface(path);
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, 1);
                    android.util.Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    destBm = Bitmap
                            .createBitmap(destBm, 0, 0, destBm.getWidth(),
                                    destBm.getHeight(), matrix, true); // rotating
                    // bitmap
                } catch (Exception e) {
                    android.util.Log.e("FileUtil", "" + e.getMessage());
                }
                File destFile = new File(destPath);
                // 创建文件输出流
                OutputStream os = new FileOutputStream(destFile);
                // 存储
                destBm.compress(Bitmap.CompressFormat.JPEG, 90, os);
                // 关闭流
                os.close();
                destBm.recycle();
                return destPath;
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return null;
    }

    // 发送图片的缩略图大小
    public final static int THUMBNAIL_WIDTH = 150;
    public final static int THUMBNAIL_HEIGHT = 150;
    public final static long THUMBNAIL_MAX_LEN = 50 * 1024;

    public static byte[] genSendImgThumbnail(String filePath) {
        byte[] b = null;
        if (filePath != null && !filePath.equals("")) {
            File f = new File(filePath);
            if (f.exists()) {
                Bitmap bmp = compressImage(filePath, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, true);
                if (bmp != null) {
                    b = Bitmap2Bytes(bmp, THUMBNAIL_MAX_LEN);
                }
            }
        }
        return b;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm, long maxLength) {
        ByteArrayOutputStream baos;
        int quality = 100;
        int p = 5;
        do {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= p;
        } while (baos.toByteArray().length >= maxLength && quality > 0);
        return baos.toByteArray();
    }

    /**
     * 取出来的缩放图片 宽最大不大于max_w 高最大不大于max_h (正比缩放) 原图宽高都小于最大值 则按原图 (注:缩放长宽结果不精确)
     *
     * @param path
     * @param max_w
     * @param max_h
     * @param cut   是否裁剪 if true:缩放时长宽谁先达到要求就停止 其他还有大于max的地方截取中间 if
     *              false:缩放时长宽都要达到要求
     * @return
     */
    public static Bitmap compressImage(String path, int max_w, int max_h, boolean cut) {
        try {
            // 获取源图片的大小
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 当opts不为null时，但decodeFile返回空，不为图片分配内存，只获取图片的大小，并保存在opts的outWidth和outHeight
            BitmapFactory.decodeFile(path, opts);
            // 原图的宽高
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;

            // 目标图片的宽高
            int destWidth = 0;
            int destHeight = 0;

            float scaleSize = 1;

            if (srcWidth <= max_w && srcHeight <= max_h) {// 原图宽高都小于最大值 则按原图
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else {// 需要缩放
                float scaleWidth = ((float) max_w / srcWidth);
                float scaleHeight = ((float) max_h / srcHeight);
                // 缩放比例
                if (cut) {
                    scaleSize = Math.max(scaleWidth, scaleHeight);
                } else {
                    scaleSize = Math.min(scaleWidth, scaleHeight);
                }
                destWidth = (int) (srcWidth * scaleSize);
                destHeight = (int) (srcHeight * scaleSize);
            }

            // 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            int scaleSizeInt = (int) (1.0 / scaleSize);
            newOpts.inSampleSize = scaleSizeInt;// 可能导致压缩长宽结果不精确
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 添加尺寸信息，
            // 获取缩放后图片
            Bitmap destBm = BitmapFactory.decodeFile(path, newOpts);
            if (cut) {
                if (destBm.getWidth() > max_w || destBm.getHeight() > max_h) {
                    int x, y, w, h;
                    if (destBm.getWidth() > max_w) {
                        x = (destBm.getWidth() - max_w) / 2;
                        w = max_w;
                    } else {
                        x = 0;
                        w = destBm.getWidth();
                    }
                    if (destBm.getHeight() > max_h) {
                        y = (destBm.getHeight() - max_h) / 2;
                        h = max_h;
                    } else {
                        y = 0;
                        h = destBm.getHeight();
                    }
                    destBm = Bitmap.createBitmap(destBm, x, y, w, h);
                }
            }
            return destBm;
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return null;
    }

    // 保存byte[]图片
    public static void saveImg(byte[] byteImg, String filePath) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
            saveImg(bmp, filePath);

        } catch (Exception e) {
            Log.e("FileUtil", e.getMessage());
        } finally {
            if (bmp != null) {
                bmp.recycle();
                bmp = null;
            }
        }

    }

    public static void saveImg(Bitmap bmp, String filePath) {
        saveImg(bmp, filePath, 100);
    }

    public static void saveImg(Bitmap bmp, String filePath, int quality) {
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (fos != null) {
                bmp.compress(Bitmap.CompressFormat.PNG, quality, fos);
                fos.flush();
            }

        } catch (Exception e) {
            Log.e("FileUtil", e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("FileUtil", e.getMessage());
                }
                fos = null;
            }
        }
    }

    /////////////////////////////////////图片收发路径管理/////////////////////////////////////

    public static String getAppRootPath() {
        String filePath = "/kefu";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory() + filePath;
        } else {
            filePath = mAppContext.getApplicationContext().getCacheDir() + filePath;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;
        return filePath;
    }

    public static String getImageRootPath() {
        String filePath = getAppRootPath() + "/image/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;
        return filePath;
    }

    public static String getCameraPath() {
        String filePath = getImageRootPath() + "/camera/";
        File file = new File(filePath);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = null;
        return filePath;
    }

    public static String getThumbnailImgRootPath(String uid) {
        String filePath = getImageRootPath() + "/thumbnail/" + uid + "/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;

        return filePath;
    }

    public static String getThumbnailPath(String uid, String filename) {
        String path = getThumbnailImgRootPath(uid) + filename + ".png";
        return path;
    }

    public static String getChatImageRootPath() {
        String path = getImageRootPath() + "chat_img/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getChatImagePath(String fileName) {
        String path = getChatImageRootPath() + fileName;
        return path;
    }

}
