package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.MainActivity;
import com.swjtu.huxin.accountmanagement.activity.MoreExportActivity;
import com.swjtu.huxin.accountmanagement.activity.MoreOpenSourceActivity;
import com.swjtu.huxin.accountmanagement.activity.MoreOurInformationActivity;
import com.swjtu.huxin.accountmanagement.activity.MoreSkinActivity;
import com.swjtu.huxin.accountmanagement.activity.MoreSummaryActivity;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.utils.DataCacheUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MoreFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";
    private RelativeLayout btnSkin;
    private RelativeLayout btnSummary;

    private RelativeLayout btnExport;
    private RelativeLayout btnBackup;
    private RelativeLayout btnRestore;
    private RelativeLayout btnClear;
    private TextView sumCache;

    private RelativeLayout btnOpenSource;
    private RelativeLayout btnOurInformation;

    private ImageView loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null)
            mArgument = bundle.getString(ARGUMENT);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        btnSkin = (RelativeLayout) view.findViewById(R.id.btnSkin);
        btnSkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreSkinActivity.class);
                startActivity(intent);
            }
        });

        btnSummary = (RelativeLayout) view.findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreSummaryActivity.class);
                startActivity(intent);
            }
        });

        btnExport = (RelativeLayout) view.findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreExportActivity.class);
                startActivity(intent);
            }
        });

        btnBackup = (RelativeLayout) view.findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity()).title("确定进行数据备份？").content(
                        "——备份账目、账户数据与预算设置\n——将覆盖掉上一次的备份\n——此操作不可逆转！！！")
                        .positiveText("是").negativeText("否")
                        .backgroundColorAttr( R.attr.dialog_backgound)
                        .contentColorAttr(R.attr.textSecondaryColor).titleColorAttr(R.attr.textColor)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                new DataBackupAndRestoreTask(getActivity()).execute(DataBackupAndRestoreTask.COMMAND_BACKUP);
                            }})
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                }).show();
            }
        });

        btnRestore = (RelativeLayout) view.findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity()).title("确定进行数据还原？").content(
                        "——恢复以前备份的数据\n——若无备份则不进行还原\n——现有数据将会被清除\n——此操作不可逆转！！！")
                        .positiveText("是").negativeText("否")
                        .backgroundColorAttr( R.attr.dialog_backgound)
                        .contentColorAttr(R.attr.textSecondaryColor).titleColorAttr(R.attr.textColor)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                new DataBackupAndRestoreTask(getActivity()).execute(DataBackupAndRestoreTask.COMMAND_RESTORE);
                            }})
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnClear = (RelativeLayout) view.findViewById(R.id.btnClear);
        sumCache = (TextView) view.findViewById(R.id.sumCache);
        try {
            sumCache.setText(DataCacheUtils.getTotalCacheSize(getActivity()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity()).title("确定清除缓存？").content(
                        "——导出数据的副本、分享截图、皮肤缩略图均会被清除。\n——此操作不可逆转！！！")
                        .positiveText("是").negativeText("否")
                        .backgroundColorAttr( R.attr.dialog_backgound)
                        .contentColorAttr(R.attr.textSecondaryColor).titleColorAttr(R.attr.textColor)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                loading = ((MainActivity)getActivity()).getLoading();
                                if(loading != null) {
                                    loading.setVisibility(View.VISIBLE);
                                    loading.setImageResource(R.drawable.animation_list_loading_blue);
                                    AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
                                    animationDrawable.start();
                                    DataCacheUtils.clearAllCache(getActivity());
                                    loading.postDelayed(new Runnable() {
                                        public void run() {
                                            loading.setImageResource(R.drawable.animation_list_loading_blue_success);
                                            AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
                                            animationDrawable.start();
                                            loading.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loading.setVisibility(View.GONE);
                                                    try {
                                                        sumCache.setText(DataCacheUtils.getTotalCacheSize(getActivity()));
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, 1500);
                                        }
                                    }, 1000);
                                }
                            }})
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnOpenSource = (RelativeLayout) view.findViewById(R.id.btnOpenSource);
        btnOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity()).title("声明").content(
                        "——本程序已在github开源,无商业目的\n——界面仿照著名记账App\"口袋记账\"\n——感谢以下开源库的提供者：\n" +
                                "bottom-navigation-bar\n" +
                                "ptr-load-more\n" +
                                "WaveView\n" +
                                "materialdatetimepicker\n" +
                                "numberprogressbar\n" +
                                "hellocharts-library\n" +
                                "circleimageview\n" +
                                "roundedimageview\n" +
                                "dirchooser\n" +
                                "verticalviewpager\n" +
                                "android-gif-drawable\n" +
                                "calligraphy\n" +
                                "glide\n" +
                                "glide-transformations\n" +
                                "gpuimage-library\n"+
                                "material-dialogs")
                        .positiveText("前往\"github.com\"获取源代码").negativeText("不感兴趣，返回")
                        .backgroundColorAttr( R.attr.dialog_backgound)
                        .contentColorAttr(R.attr.textSecondaryColor).titleColorAttr(R.attr.textColor)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                Intent intent = new Intent(getActivity(), MoreOpenSourceActivity.class);
                                startActivity(intent);
                            }})
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnOurInformation = (RelativeLayout) view.findViewById(R.id.btnOurInformation);
        btnOurInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreOurInformationActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            sumCache.setText(DataCacheUtils.getTotalCacheSize(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static MoreFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        MoreFragment contentFragment = new MoreFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    class DataBackupAndRestoreTask extends AsyncTask<String, Integer, Integer> {
        private static final String COMMAND_BACKUP = "backupDatabase";
        public static final String COMMAND_RESTORE = "restroeDatabase";
        private static final int BACKUP_SUCCESS = 1;
        public static final int RESTORE_SUCCESS = 2;
        private static final int BACKUP_ERROR = 3;
        public static final int RESTORE_NOFLEERROR = 4;
        private Context mContext;

        public DataBackupAndRestoreTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            //获得数据库路径，默认是/data/data/(包名)/databases/
            File dbFile = mContext.getDatabasePath("accountmanagement.db");
            File spFile1 = new File("/data/data/com.swjtu.huxin.accountmanagement/shared_prefs/userData.xml");
            File spFile2 = new File("/data/data/com.swjtu.huxin.accountmanagement/shared_prefs/budgetData.xml");
            File exportDir = new File(Environment.getExternalStorageDirectory(), "dataBackup_accountmanagement");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File backup1 = new File(exportDir, dbFile.getName());
            File backup2 = new File(exportDir, spFile1.getName());
            File backup3 = new File(exportDir, spFile2.getName());

            String command = params[0];
            if (command.equals(COMMAND_BACKUP)) {
                try {
                    backup1.createNewFile();
                    backup2.createNewFile();
                    backup3.createNewFile();
                    fileCopy(dbFile, backup1);
                    fileCopy(spFile1, backup2);
                    fileCopy(spFile2, backup3);
                    return BACKUP_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    return BACKUP_ERROR;
                }
            }
            else if (command.equals(COMMAND_RESTORE)) {
                try {
                    fileCopy(backup1, dbFile);
                    fileCopy(backup2, spFile1);
                    fileCopy(backup3, spFile2);
                    return RESTORE_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    return RESTORE_NOFLEERROR;
                }
            }
            else {
                return BACKUP_ERROR;
            }
        }

        private void fileCopy(File dbFile, File backup) throws IOException {
            FileChannel inChannel = new FileInputStream(dbFile).getChannel();
            FileChannel outChannel = new FileOutputStream(backup).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case BACKUP_SUCCESS:
                    Log.i("backup", "ok");
                    if(loading != null) {
                        loading.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loading.setImageResource(R.drawable.animation_list_loading_blue_success);
                                AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
                                animationDrawable.start();
                                loading.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setVisibility(View.GONE);
                                        MyApplication.getApplication().getDataChangeObservable().dataChange();
                                    }
                                },1500);
                            }
                        },1000);
                    }
                    break;
                case BACKUP_ERROR:
                    Log.i("backup", "fail");
                    break;
                case RESTORE_SUCCESS:
                    Log.i("restore", "success");
                    if(loading != null) {
                        loading.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loading.setImageResource(R.drawable.animation_list_loading_blue_success);
                                AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
                                animationDrawable.start();
                                loading.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setVisibility(View.GONE);
                                    }
                                },1500);
                            }
                        },1000);
                    }
                    break;
                case RESTORE_NOFLEERROR:
                    Log.i("restore", "fail");
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ((MainActivity)getActivity()).getLoading();
            if(loading != null) {
                loading.setVisibility(View.VISIBLE);
                loading.setImageResource(R.drawable.animation_list_loading_blue);
                AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
                animationDrawable.start();
            }
        }
    }
}
