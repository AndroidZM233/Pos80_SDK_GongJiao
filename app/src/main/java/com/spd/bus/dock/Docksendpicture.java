package com.spd.bus.dock;

import android.app.ListActivity;

public class Docksendpicture extends ListActivity {
//    private List<Map<String, Object>> mData;
//    private Button mBtnExit;
//    byte[] read_data = new byte[1];
//    int[] len = new int[1];
//    String path;
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//    private GoogleApiClient client;
//
//    private Dock mDock;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dock_send_pic);
//        mData = getData();
//        MyAdapter adapter = new MyAdapter(this);
//        setListAdapter(adapter);
//        mBtnExit = (Button) findViewById(R.id.btn_exit);
//        mBtnExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
//        new Thread(){
//            @Override
//            public void run() {
//                mDock = new Dock(getApplicationContext());
//            }
//        }.start();
//    }
//
//    private List<Map<String, Object>> getData() {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.image1);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("img", R.mipmap.image2);
//        list.add(map);
//
//        return list;
//    }
//
//    // ListView 中某项被选中后的逻辑
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        if(position == 0){
//            path = "/sdcard/Pictures/im1.jpg";
//        } else if(position == 1){
//            path = "/sdcard/Pictures/im2.jpg";
//        }
//        showInfo();
//    }
//
//    /**
//     * listview中点击按键弹出对话框
//     */
//    public void showInfo() {
//        if (DockgetStatus() == 0) {
//            if ((keyrandom.bytesToHexString(read_data)).equals("00")) {
//                new AlertDialog.Builder(this)
//                        .setTitle("")
//                        .setMessage("Upload success")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                byte[] image = image2byte(path);
//                                Log.v("----zhangjing log----",""+ keyrandom.bytesToHexString(image));
//                                int returnsend = -1;
//                                try {
//                                    returnsend = mDock.pictureSend(image , image.length);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                Log.v("zhangjing log","retruened"+returnsend);
//                            }
//                        })
//                        .setNegativeButton("Cancel" ,new DialogInterface.OnClickListener(){
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                })
//                        .show();
//            } else if ((keyrandom.bytesToHexString(read_data)).equals("01")) {
//                new AlertDialog.Builder(this)
//                        .setTitle("")
//                        .setMessage("Upload success")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                byte[] image = image2byte(path);
//                                Log.v("----zhangjing log----",""+ keyrandom.bytesToHexString(image));
//                                int returnsend = -1;
//                                try {
//                                    returnsend = mDock.pictureSend(image , image.length);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                Log.v("zhangjing log","retruened:"+returnsend);
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        })
//                        .show();
//            }
//        } else {
//            new AlertDialog.Builder(this)
//                    .setTitle("")
//                    .setMessage("Fail")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    })
//                    .show();
//        }
//    }
//
//    //图片到byte数组
//    public byte[] image2byte(String path) {
//        byte[] data = null;
//        FileInputStream input = null;
//        try {
//            input = new FileInputStream(new File(path));
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            byte[] buf = new byte[1024];
//            int numBytesRead = 0;
//            while ((numBytesRead = input.read(buf)) != -1) {
//                output.write(buf, 0, numBytesRead);
//            }
//            data = output.toByteArray();
//            output.close();
//            input.close();
//        } catch (FileNotFoundException ex1) {
//            ex1.printStackTrace();
//        } catch (IOException ex1) {
//            ex1.printStackTrace();
//        }
//        return data;
//    }
//
//    public int DockgetStatus() {
//        int result = -1;
//        try {
//            result = mDock.status(read_data, len);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("Docksendpicture Page")
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
//    }
//
//    public final class ViewHolder {
//        public ImageView img;
//    }
//
//    public class MyAdapter extends BaseAdapter {
//        private LayoutInflater mInflater;
//
//        public MyAdapter(Context context) {
//            this.mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return mData.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            if (convertView == null) {
//                holder = new ViewHolder();
//
//                convertView = mInflater.inflate(R.layout.docksendpicture, null);
//                holder.img = (ImageView) convertView.findViewById(R.id.dockimage);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
//            return convertView;
//        }
//    }
}
