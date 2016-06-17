package com.allen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper{
	private SQLiteDatabase db;
	private static DBhelper dbhelper;	
	private static final String DB_NAME="db01";
	private static final String TB_NAME="dbTbl";
	private  static final String CREATE_TBL="create table "
		+"dbtbl(_id integer primary key autoincrement,bname text,url text,mark int)";

	public DBhelper(Context context) {
		super(context,DB_NAME,null,1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db=db;
		db.execSQL(CREATE_TBL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		this.db=db;
		db.execSQL("drop table dbtbl");
	}

	public void save(ContentValues v){
		SQLiteDatabase db=getWritableDatabase();
		db.insert(TB_NAME, null, v);
		db.close();		
	}
	
	public void del(int id){
		if(db==null){
			db=getWritableDatabase();			
			db.delete(TB_NAME, "_id=?", new String[]{String.valueOf(id)});
		}
	}
	
	public Cursor query(){
		SQLiteDatabase db=getWritableDatabase();
		Cursor c=db.query(TB_NAME, null, null, null, null, null, null);
		return c;
			}
	public void close(){
		if (db!=null){db.close();}
	} 


    public void updatemark(long id,int lastreadpoint){
    	ContentValues values = new ContentValues();
    	values.put("mark", lastreadpoint);
    	db.update(TB_NAME, values, "_id="+id, null);
    }
    
    public static void initDB(Context context){
		DBhelper.getInstance(context);
		
	}
    public static DBhelper getInstance(Context context){
		if(null == dbhelper){
			dbhelper = new DBhelper(context);
		}
		return dbhelper;
	}

}
