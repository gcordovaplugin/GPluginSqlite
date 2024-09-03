package cn.yingzhichu.cordova.gsqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class GSqlite extends CordovaPlugin {
    private MySqlite mySqlite;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            if(mySqlite == null){
                mySqlite = new MySqlite(cordova.getContext(),args.getInt(0),args.getString(1),args.getString(2));
 }
            callbackContext.success();
        } else if (action.equals("insert")) {
            if(mySqlite == null){
                callbackContext.error("db not inint");
                return true;
            }
            callbackContext.success(mySqlite.insert(args.getString(0)));
        } else if (action.equals("delete")) {
            if(mySqlite == null){
                callbackContext.error("db not inint");
                return true;
            }
            callbackContext.success(mySqlite.delete(args.getString(0)));
        } else if (action.equals("update")) {
            if(mySqlite == null){
                callbackContext.error("db not inint");
                return true;
            }
            callbackContext.success(mySqlite.update(args.getString(0)));
        } else if (action.equals("fetch")) {
            if(mySqlite == null){
                callbackContext.error("db not inint");
                return true;
            }
            callbackContext.success(mySqlite.fetch(args.getString(0),getArgs(args.getJSONArray(1))));
        } else if (action.equals("fetchAll")) {
            if(mySqlite == null){
                callbackContext.error("db not inint");
                return true;
            }
            callbackContext.success(mySqlite.fetchAll(args.getString(0),getArgs(args.getJSONArray(1))));
        }
        return true;
    }

    private String[] getArgs(JSONArray args) {
        String[] ret = new String[args.length()];
        for (int i = 0; i < args.length(); i++) {
            try {
                ret[i] = args.getString(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    private class MySqlite extends SQLiteOpenHelper {
        private static final String DB_NAME = "app.db";
        private String initSql = "";
        private String upSql = "";

        public MySqlite(@Nullable Context context, int version,String initsql,String updatesql) {
            super(context, DB_NAME, null, version);
            initSql = initsql;
            upSql = updatesql;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if(!initSql.equals("")){
                db.execSQL(initSql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(!upSql.equals("") && newVersion > oldVersion){
                db.execSQL(upSql);
            }
        }

        //插入
        public int insert(String sql) {
            SQLiteDatabase db = getWritableDatabase();
            SQLiteStatement st = db.compileStatement(sql);
            int i = (int) st.executeInsert();
            st.close();
            db.close();
            return i;
        }

        public int update(String sql) {
            SQLiteDatabase db = getWritableDatabase();
            SQLiteStatement st = db.compileStatement(sql);
            int i = st.executeUpdateDelete();
            st.close();
            db.close();
            return i;
        }

        public int delete(String sql) {
            SQLiteDatabase db = getWritableDatabase();
            SQLiteStatement st = db.compileStatement(sql);
            int i = st.executeUpdateDelete();
            st.close();
            db.close();
            return i;
        }

        public JSONObject fetch(String sql, String[] args) {
            JSONObject ret = new JSONObject();
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery(sql, args);
            try {
                if (c.moveToFirst()) {
                    ret = getRow(c);
                }
            } catch (Exception e) {
            } finally {
                c.close();
                db.close();
            }
            return ret;
        }

        public JSONArray fetchAll(String sql, String[] args) {
            JSONArray ret = new JSONArray();
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery(sql, args);
            try {
                if (c.moveToFirst()) {
                    do{
                        ret.put(getRow(c));
                    }while (c.moveToNext());
                }
            } catch (Exception e) {
            } finally {
                c.close();
                db.close();
            }
            return ret;
        }

        private JSONObject getRow(Cursor c) throws JSONException {
            JSONObject ret = new JSONObject();
            String[] names = c.getColumnNames();
            for (String name : names) {
                int i = c.getColumnIndex(name);
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        ret.put(name, c.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        ret.put(name, c.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        ret.put(name, c.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        ret.put(name, c.getBlob(i));
                        break;
                }

            }
            return ret;
        }
    }
}
