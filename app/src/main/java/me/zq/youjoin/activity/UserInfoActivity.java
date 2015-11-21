package me.zq.youjoin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.zq.youjoin.R;
import me.zq.youjoin.YouJoinApplication;
import me.zq.youjoin.model.UpdateUserInfoResult;
import me.zq.youjoin.model.UserInfo;
import me.zq.youjoin.network.NetworkManager;
import me.zq.youjoin.network.ResponseListener;
import me.zq.youjoin.utils.LogUtils;

public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.yj_personal_email)
    TextView yjPersonalEmail;
    @Bind(R.id.yj_personal_username)
    TextView yjPersonalUsername;
    @Bind(R.id.yj_personal_sex)
    EditText yjPersonalSex;
    @Bind(R.id.yj_personal_work)
    EditText yjPersonalWork;
    @Bind(R.id.yj_personal_location)
    EditText yjPersonalLocation;
    @Bind(R.id.yj_personal_birth)
    EditText yjPersonalBirth;
    @Bind(R.id.yj_personal_commit)
    Button yjPersonalCommit;
    @Bind(R.id.yj_personal_photo_container)
    LinearLayout yjPersonalPhotoContainer;
    @Bind(R.id.yj_personal_choose_photo)
    Button yjPersonalChoosePhoto;

    private UserInfo userInfo;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        userInfo = YouJoinApplication.getCurrUser();

        initViews();
    }

    private void initViews() {
        yjPersonalEmail.setText(userInfo.getEmail());
        yjPersonalUsername.setText(userInfo.getUsername());
        yjPersonalBirth.setText(userInfo.getBirth());
        yjPersonalSex.setText(userInfo.getSex());
        yjPersonalWork.setText(userInfo.getWork());
        yjPersonalLocation.setText(userInfo.getLocation());

        yjPersonalCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

        yjPersonalChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiImageSelectorActivity.startSelect(UserInfoActivity.this, 2, 1,
                        MultiImageSelectorActivity.MODE_SINGLE);
            }
        });
    }

    private void updateInfo() {
        userInfo.setSex(yjPersonalSex.getText().toString());
        userInfo.setWork(yjPersonalWork.getText().toString());
        userInfo.setLocation(yjPersonalLocation.getText().toString());
        userInfo.setBirth(yjPersonalBirth.getText().toString());

        NetworkManager.postUpdateUserInfo(userInfo, picPath, new ResponseListener<UpdateUserInfoResult>() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }

            @Override
            public void onResponse(UpdateUserInfoResult result) {
                LogUtils.d("hehe", "photo url is : " + result.getImg_url());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> mSelectPath =
                        data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                yjPersonalPhotoContainer.removeAllViews();
                for (String p : mSelectPath) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(p);

                    View itemView = View.inflate(UserInfoActivity.this, R.layout.yj_item_publish_photo, null);
                    ImageView img = (ImageView) itemView.findViewById(R.id.img);
                    itemView.setTag(p);

                    Picasso.with(UserInfoActivity.this)
                            .load(new File(p))
                            .resize(200, 200)
                            .centerCrop()
                            .into(img);
                    if (yjPersonalPhotoContainer != null) {
                        yjPersonalPhotoContainer.addView(itemView,
                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                    picPath = sb.toString();
                }
            }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }
}
