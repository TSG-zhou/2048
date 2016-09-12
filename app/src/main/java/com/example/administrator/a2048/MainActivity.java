package com.example.administrator.a2048;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean flag = false;
    private int df = 0;
    List<Integer> list =  new ArrayList<Integer>();
    int[] number = {2,4};
    private static int x;
    private static int y;
    private static int yx;
    private static int yy;

    private static final String TAG = "MainActivity";
    private Fangkuai fk_11;
    private Fangkuai fk_12;
    private Fangkuai fk_13;
    private Fangkuai fk_14;
    private Fangkuai fk_21;
    private Fangkuai fk_22;
    private Fangkuai fk_23;
    private Fangkuai fk_24;
    private Fangkuai fk_31;
    private Fangkuai fk_32;
    private Fangkuai fk_33;
    private Fangkuai fk_34;
    private Fangkuai fk_41;
    private Fangkuai fk_42;
    private Fangkuai fk_43;
    private Fangkuai fk_44;
    private LinearLayout ll;
    private Fangkuai[][] fk = {{fk_11,fk_12,fk_13,fk_14},{fk_21,fk_22,fk_23,fk_24},{fk_31,fk_32,fk_33,fk_34},{fk_41,fk_42,fk_43,fk_44}};

    //记住分数的数组
    private int jz[][] = new int[4][4];
    private int jzgf = 0;

    private TextView tv_df;
    private TextView tv_info;
    private SQLiteDatabase db;
    private int fs;
    //刷新实时得分
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                tv_df.setText(df+"");
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySqlite ms = new MySqlite(this,"2048.db",null,1);
        db = ms.getWritableDatabase();

        tv_df = (TextView) findViewById(R.id.tv_df);
        tv_info = (TextView) findViewById(R.id.tv_info);

        fk[0][0] = (Fangkuai) findViewById(R.id.fk_11);
        fk[0][1] = (Fangkuai) findViewById(R.id.fk_12);
        fk[0][2] = (Fangkuai) findViewById(R.id.fk_13);
        fk[0][3] = (Fangkuai) findViewById(R.id.fk_14);
        fk[1][0] = (Fangkuai) findViewById(R.id.fk_21);
        fk[1][1] = (Fangkuai) findViewById(R.id.fk_22);
        fk[1][2] = (Fangkuai) findViewById(R.id.fk_23);
        fk[1][3] = (Fangkuai) findViewById(R.id.fk_24);
        fk[2][0] = (Fangkuai) findViewById(R.id.fk_31);
        fk[2][1] = (Fangkuai) findViewById(R.id.fk_32);
        fk[2][2] = (Fangkuai) findViewById(R.id.fk_33);
        fk[2][3] = (Fangkuai) findViewById(R.id.fk_34);
        fk[3][0] = (Fangkuai) findViewById(R.id.fk_41);
        fk[3][1] = (Fangkuai) findViewById(R.id.fk_42);
        fk[3][2] = (Fangkuai) findViewById(R.id.fk_43);
        fk[3][3] = (Fangkuai) findViewById(R.id.fk_44);

        //从数据库中查找到最高分，并将最高分显示到界面上
        TextView tv_gf = (TextView) findViewById(R.id.tv_gf);
        Cursor cursor = db.query("fenshu",null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            fs = cursor.getInt(cursor.getColumnIndex("fs"));
            tv_gf.setText(fs +"");
        }

        //初始化设置两个元素在界面中
        for (int i=0;i<2;i++) {
            int fkx = (int) ((Math.random()) * 4);
            int fky = (int) ((Math.random()) * 4);
            fk[fkx][fky].setNum(number[(int) (Math.random()*2)]);
        }

        //设置手指在屏幕内滑动的逻辑
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        yx = (int) event.getX();
                        yy = (int) event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        x = (int) event.getX();
                        y = (int) event.getY();
                        if (x==0&&y==0){
                            break;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((x - yx) > Math.abs(y - yy) && (x - yx) > 50) {
                            right();
                        } else if ((yx - x) > Math.abs(y - yy) && (yx - x) > 50) {
                            left();
                        } else if ((y - yy) > Math.abs(x - yx) && (y - yy) > 50) {
                            down();
                        } else if ((yy - y) > Math.abs(x - yx) && (yy - y) > 50) {
                            up();
                        }
                        break;
                }
                return true;
            }
        });
    }
    //滑动逻辑判断，第一个for循环将空白的格子用所在列（或所在行）的有数值的填充
    // 第二个for循环，将所在列（或所在行）相等的数值相加，并将最上（最下，最左，最右）的元素设置为相加数值，另一元素设置为0
    //第三个for循环将空白的格子用所在列（或所在行）的有数值的填充
    //第四个for循环，搜索边界为0的元素，并添加到数组中。
    //最后的if判断，将从数组中随机得到的数值添加到随机的元素中
    public void up(){
        jizhu();
        for (int i = 0;i<4;i++){
            for (int j = 0; j< 4 ;j++){
                if (fk[i][j].getNum()==0) {
                    for (int k = i+1; k < 4; k++){
                        if (fk[k][j].getNum()!=0){
                            fk[i][j].setNum(fk[k][j].getNum());
                            fk[k][j].setNum(0);
                            break;
                        }
                    }
                }
            }
        }

        for (int i = 0;i<4;i++){
            for (int j = 0; j< 4 ;j++){
                if (fk[i][j].getNum()>0){
                    if (i != 4) {
                        for (int k = i+1; k < 4; k++) {
                            if (Math.abs(k-i)>1 && fk[k][j].getNum()>0){
                                break;
                            }
                            if (fk[k][j].getNum() == fk[i][j].getNum()) {
                                fk[i][j].setNum(fk[i][j].getNum() + fk[k][j].getNum());
                                jzgf = df;
                                df = df+fk[i][j].getNum();
                                handler.sendEmptyMessage(1);
                                fk[k][j].setNum(0);
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0;i<4;i++){
            for (int j = 0; j< 4 ;j++){
                if (fk[i][j].getNum()==0) {
                    for (int k = i+1; k < 4; k++){
                        if (fk[k][j].getNum()!=0){
                            fk[i][j].setNum(fk[k][j].getNum());
                            fk[k][j].setNum(0);
                            break;
                        }
                    }
                }
            }
        }
        isfull();
        for (int j = 0; j< 4 ;j++){
            if (fk[3][j].getNum()==0){
                list.add(j);
            }
        }
        if (!list.isEmpty()){
            int num = (int) (Math.random()*(list.size()));
            fk[3][list.get(num)].setNum(number[(int) (Math.random()*2)]);
            list.clear();
        }
    }
    public void down(){
        jizhu();
        for (int i = 3;i>= 0;i--){
            for (int j = 3; j >= 0 ;j--) {
                if (fk[i][j].getNum()==0){
                    for (int k = i-1;k >= 0;k--){
                        if (fk[k][j].getNum()>0){
                            fk[i][j].setNum(fk[k][j].getNum());
                            fk[k][j].setNum(0);
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 3;i>= 0;i--){
            for (int j = 3; j >= 0 ;j--){
                if (fk[i][j].getNum()>0){
                    if (i != 0) {
                        for (int k = i - 1; k >= 0; k--) {
                            if (Math.abs(k-i)>1 && fk[k][j].getNum()>0){
                                break;
                            }
                            if (fk[i][j].getNum() == fk[k][j].getNum()) {
                                fk[i][j].setNum(fk[i][j].getNum() + fk[k][j].getNum());
                                jzgf = df;
                                df = df+fk[i][j].getNum();
                                handler.sendEmptyMessage(1);
                                fk[k][j].setNum(0);
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 3;i>= 0;i--){
            for (int j = 3; j >= 0 ;j--) {
                if (fk[i][j].getNum()==0){
                    for (int k = i-1;k >= 0;k--){
                        if (fk[k][j].getNum()>0){
                            fk[i][j].setNum(fk[k][j].getNum());
                            fk[k][j].setNum(0);
                            break;
                        }
                    }
                }
            }
        }
        isfull();
        for (int j = 0; j< 4 ;j++){
            if (fk[0][j].getNum()==0){
                list.add(j);
            }
        }
        if (!list.isEmpty()){
            int num = (int) (Math.random()*(list.size()));
            fk[0][list.get(num)].setNum(number[(int) (Math.random()*2)]);
            list.clear();
        }
    }
    public void left(){
        jizhu();
        for (int j=0;j<4;j++) {
            for (int i = 0; i < 4; i++) {
                if (fk[i][j].getNum()==0){
                    for (int k = j+1;k<4;k++){
                            if (fk[i][k].getNum()>0){
                                fk[i][j].setNum(fk[i][k].getNum());
                                fk[i][k].setNum(0);
                                break;
                            }
//                        }
                    }
                }
            }
        }
        for (int j=0;j<4;j++) {
            for (int i = 0; i < 4; i++) {
                if (fk[i][j].getNum() > 0) {
                    for (int k = j+1;k<4;k++){
                        if (Math.abs(k-j)>1 && fk[i][j].getNum()>0){
                            break;
                        }
                        if (j<3){
                            if (fk[i][j].getNum()==fk[i][k].getNum()){
                                fk[i][j].setNum(fk[i][j].getNum()+fk[i][k].getNum());
                                jzgf = df;
                                df = df+fk[i][j].getNum();
                                handler.sendEmptyMessage(1);
                                fk[i][k].setNum(0);
                            }
                        }
                    }
                }
            }
        }
        for (int j=0;j<4;j++) {
            for (int i = 0; i < 4; i++) {
                if (fk[i][j].getNum()==0){
                    for (int k = j+1;k<4;k++){
                            if (fk[i][k].getNum()>0){
                                fk[i][j].setNum(fk[i][k].getNum());
                                fk[i][k].setNum(0);
                                break;
                            }
//                        }
                    }
                }
            }
        }
        isfull();
        for (int i = 0; i< 4 ;i++){
            if (fk[i][3].getNum()==0){
                list.add(i);
            }
        }
        if (!list.isEmpty()){
            int num = (int) (Math.random()*(list.size()));
            fk[list.get(num)][3].setNum(number[(int) (Math.random()*2)]);
            list.clear();
        }
    }
    public void right(){
        jizhu();
        for (int j = 3;j>=0;j--){
            for (int i= 3;i>=0;i--){
                if (fk[i][j].getNum()==0){
                    for (int k = j-1;k>=0;k--){
                        if (fk[i][k].getNum()>0){
                            fk[i][j].setNum(fk[i][k].getNum());
                            fk[i][k].setNum(0);
                            break;
                        }
                    }
                }
            }
        }
        for (int j = 3;j>=0;j--){
            for (int i= 3;i>=0;i--){
                if (fk[i][j].getNum()>0){
                    for (int k = j-1;k>=0;k--){
                        if (j>=0) {
                            if (fk[i][j].getNum() == fk[i][k].getNum()) {
                                if (Math.abs(k-j)>1 && fk[i][j].getNum()>0){
                                    break;
                                }
                                fk[i][j].setNum(fk[i][j].getNum()+fk[i][k].getNum());
                                jzgf = df;
                                df = df+fk[i][j].getNum();
                                handler.sendEmptyMessage(1);
                                fk[i][k].setNum(0);
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int j = 3;j>=0;j--){
            for (int i= 3;i>=0;i--){
                if (fk[i][j].getNum()==0){
                    for (int k = j-1;k>=0;k--){
                        if (fk[i][k].getNum()>0){
                            fk[i][j].setNum(fk[i][k].getNum());
                            fk[i][k].setNum(0);
                            break;
                        }
                    }
                }
            }
        }
        isfull();
        for (int i = 0; i< 4 ;i++){
            if (fk[i][0].getNum()==0){
                list.add(i);
            }
        }
        if (!list.isEmpty()){
            int num = (int) (Math.random()*(list.size()));
            fk[list.get(num)][0].setNum(number[(int) (Math.random()*2)]);
            list.clear();
        }
    }
    //检测是否无法移动,每个元素周围无相等的数值则游戏失败
    public void isfull(){
        //检测最3x3元素周围的情况
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (fk[i][j].getNum() ==0 | fk[i][j].getNum()==fk[i][j+1].getNum() | fk[i][j].getNum()==fk[i+1][j].getNum()){
                    return;
                }
            }
        }
        //检测第四列周围的情况
        for (int i = 0;i< 3;i++){
            if (fk[i][3].getNum() ==0 |fk[i][3].getNum()==fk[i+1][3].getNum()){
                return;
            }
        }
        //检测第四行周围的情况
        for (int j = 0;j< 3;j++){
            if (fk[3][j].getNum() ==0 |fk[3][j].getNum()==fk[3][j+1].getNum()){
                return;
            }
        }
        flag = true;
        //逻辑走到这代表游戏结束，将最高分添加到数据库中
        if (df > fs) {
            db.execSQL("update fenshu set fs = ? where _id = ?", new Object[]{df, 1});
            Toast.makeText(this,"创造了新的历史哦！斯国一",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"失败了也...",Toast.LENGTH_SHORT).show();
        }
        tv_info.setText("Failed");
    }
    //重构返回键判断规则
    @Override
    public void onBackPressed() {
        final boolean[] flag = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("真的要退出吗？退出了就要重新玩了哦！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    public void click(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart");
        if (flag){
            builder.setMessage("重新开始？");
        }else {
            builder.setMessage("这一局还没有玩完哦");
        }
        builder.setPositiveButton("重玩", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(MainActivity.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }

    public void jizhu(){
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                jz[i][j] = fk[i][j].getNum();
            }
        }
    }

    //后退一步
    public void houtui(View v){
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                fk[i][j].setNum(jz[i][j]);
            }
        }
        df = jzgf;
        tv_df.setText(jzgf+"");
    }
}