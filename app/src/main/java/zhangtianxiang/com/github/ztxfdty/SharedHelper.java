package zhangtianxiang.com.github.ztxfdty;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ztx on 2018/5/30.
 */

class SharedHelper {

    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void save(String username, String password) {
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public Map<String, String> read() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        data.put("username", sp.getString("username", ""));
        data.put("password", sp.getString("password", ""));
        return data;
    }

    public void save_nickname(String nickname) {
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", nickname);
        editor.commit();
    }

    public String read_nickname() {
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        return sp.getString("nickname", "");
    }

    //保存info
    public void save_info(ArrayList<String> info) {
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int sz = sp.getInt("infosz",0);
        for (int i = 0; i < sz; i ++ ) {
            editor.remove("info"+i);
        }
        sz = info.size();
        editor.putInt("infosz",info.size());
        for (int i = 0; i < sz; i ++ ) {
            editor.putString("info"+i,info.get(i));
        }
        editor.commit();
    }
    // 读取info
    public ArrayList<String> read_info() {
        ArrayList<String> info = new ArrayList<String>();
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        int sz = sp.getInt("infosz",0);
        for (int i = 0; i < sz; i ++ ) {
            info.add(sp.getString("info"+i,"no info"));
        }
        return info;
    }


    //保存rec
    public void save_rec(ArrayList<String> rec) {
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int sz = sp.getInt("recsz",0);
        for (int i = 0; i < sz; i ++ ) {
            editor.remove("rec"+i);
        }
        sz = rec.size();
        editor.putInt("recsz",rec.size());
        for (int i = 0; i < sz; i ++ ) {
            editor.putString("rec"+i,rec.get(i));
        }
        editor.commit();
    }
    // 读取rec
    public ArrayList<String> read_rec() {
        ArrayList<String> rec = new ArrayList<String>();
        SharedPreferences sp = mContext.getSharedPreferences("ztxfdtysp", Context.MODE_PRIVATE);
        int sz = sp.getInt("recsz",0);
        for (int i = 0; i < sz; i ++ ) {
            rec.add(sp.getString("rec"+i,"no rec"));
        }
        return rec;
    }
}
