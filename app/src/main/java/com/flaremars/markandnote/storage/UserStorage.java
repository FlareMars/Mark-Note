package com.flaremars.markandnote.storage;

import com.flaremars.markandnote.bean.Result;
import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.common.callback.SimpleCallback;
import com.flaremars.markandnote.entity.User;
import com.flaremars.markandnote.util.SharedPrefHelper;
import com.flaremars.markandnote.util.StringUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by FlareMars on 2016/12/14
 */
public class UserStorage {

    private static UserStorage instance;
    private SharedPrefHelper sharedPrefHelper;
    private User currentUser;

    private UserStorage() {
        sharedPrefHelper = ComponentHolder.getAppComponent().getSharedPrefHelper();
        currentUser = getUserData();
    }

    public static UserStorage getInstance() {
        if (instance == null) {
            synchronized(UserStorage.class) {
                if (instance == null) {
                    instance = new UserStorage();
                }
            }
        }
        return instance;
    }

    public interface LoginListener {
        void onLoginSuccess(User user);
        void onLoginFail(String message);
    }

    public void login(String phone, String password, final LoginListener listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo(Constants.USERKEY_PHONE, phone)
                .addWhereEqualTo(Constants.USERKEY_PASSWORD, password);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        currentUser = list.get(0);
                        saveToLocal(currentUser);
                        listener.onLoginSuccess(currentUser);
                    } else {
                        listener.onLoginFail("账号或密码错误");
                    }
                } else {
                    listener.onLoginFail(e.getErrorCode() + " " + e.getMessage());
                }
            }
        });
    }

    public void logout() {
        sharedPrefHelper.remove(Constants.USERKEY_ID);
        sharedPrefHelper.remove(Constants.USERKEY_PHONE);
        sharedPrefHelper.remove(Constants.USERKEY_PASSWORD);
        sharedPrefHelper.remove(Constants.USERKEY_USERNAME);
        sharedPrefHelper.remove(Constants.USERKEY_AVATAR);
        currentUser = null;
    }

    public boolean saveToLocal(User user) {
        if (StringUtils.INSTANCE.isEmpty(user.getPhone()) || StringUtils.INSTANCE.isEmpty(user.getObjectId())) {
            return false;
        }

        updateBaseInfo(user);
        return true;
    }

    public void updateBaseInfo(User user) {
        sharedPrefHelper.saveString(Constants.USERKEY_ID, user.getObjectId());
        sharedPrefHelper.saveString(Constants.USERKEY_PHONE, user.getPhone());
        sharedPrefHelper.saveString(Constants.USERKEY_PASSWORD, user.getPassword());
        sharedPrefHelper.saveString(Constants.USERKEY_USERNAME, user.getUsername());
        sharedPrefHelper.saveString(Constants.USERKEY_AVATAR, user.getAvatarUrl());
    }

    public User getUserData() {
        if (sharedPrefHelper.getString(Constants.USERKEY_ID) == null) {
            return null;
        }

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setObjectId(sharedPrefHelper.getString(Constants.USERKEY_ID));
            currentUser.setPhone(sharedPrefHelper.getString(Constants.USERKEY_PHONE));
            currentUser.setPassword(sharedPrefHelper.getString(Constants.USERKEY_PASSWORD));
            currentUser.setUsername(sharedPrefHelper.getString(Constants.USERKEY_USERNAME));
            currentUser.setAvatarUrl(sharedPrefHelper.getString(Constants.USERKEY_AVATAR));
        }

        return currentUser;
    }

    public void register(String phone, String verificationCode, String username, String password, final SimpleCallback callback) {
        currentUser = new User();
        currentUser.setAvatarUrl("");
        currentUser.setPhone(phone);
        currentUser.setPassword(password);
        currentUser.setUsername(username);

        currentUser.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    currentUser.setObjectId(objectId);
                    saveToLocal(currentUser);
                    callback.onCallback(new Result());
                } else {
                    currentUser = null;
                    callback.onCallback(new Result(Result.CODE_FAIL, "注册失败: " + e.getErrorCode() + " " + e.getMessage()));
                }
            }
        });
    }

    public void updateAvatar(String userId, String photoPath) {

    }

}
