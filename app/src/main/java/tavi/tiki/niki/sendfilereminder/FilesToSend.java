package tavi.tiki.niki.sendfilereminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FilesToSend extends Activity {

    static final int FILE_SELECT_CODE = 0;
    static final int PICK_CONTACT = 1;
    RecyclerView rv;
    Gson gson = new Gson();
    ArrayList<Plan> mplans = new ArrayList<Plan>();

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    protected void savePlans(String fileName, ArrayList<Plan> planstosave) {

        String string = gson.toJson(planstosave);
        FileOutputStream outputStream;

        try {

            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            //тут вставил чарсет
            /*
            OutputStreamWriter wr = new OutputStreamWriter(outputStream);
            wr.write(string);
            wr.close();
            */
            outputStream.write(string.getBytes(Charset.forName("UTF-8")));
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<Plan> loadPlans(String fileName) {
        try {
            FileInputStream is = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            /*
            int ch;
            while((ch = is.read()) != -1){
                builder.append((char)ch);
            }*/

            Type plansListType = new TypeToken<ArrayList<Plan>>() {
            }.getType();
            ArrayList<Plan> p = gson.fromJson(builder.toString(), plansListType);
            br.close();
            isr.close();
            is.close();
            if (p == null) {
                return new ArrayList<Plan>();
            } else {
                return p;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Plan>();
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mplans = loadPlans(getString(R.string.plans_path));
        setContentView(R.layout.main_layout_with_recycler);
        RecyclerViewInit();
        getOverflowMenu();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {


        super.onSaveInstanceState(outState);

        savePlans(getString(R.string.plans_path), mplans);

    }

    @Override
    protected void onDestroy() {
        savePlans(getString(R.string.plans_path), mplans);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        savePlans(getString(R.string.plans_path), mplans);
        super.onPause();
    }


    /**
     *
     */


    public void RecyclerViewInit() {
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(mplans);

        rv.setAdapter(adapter);

        //swipe to dissmiss
        ItemTouchHelper.Callback callback = new PlanTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);

    }

    public void AddElem(String contactName, String filename) {
        mplans.add(new Plan(filename, contactName));
        savePlans(getString(R.string.plans_path), mplans);
        rv.getAdapter().notifyItemInserted(mplans.size() - 1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_files_to_send, menu);

        return true;
    }


    // старый он активити резалт

    public void About(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FilesToSend.this);
        builder.setTitle(R.string.about_dialog_title);
        builder.setMessage(R.string.about_dialog_message);

        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void chooseFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    public void chooseContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        TextView v = (TextView) findViewById(R.id.contact_id);
                        v.setVisibility(View.VISIBLE);
                        v.setText(c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
                    }
                }
                break;
            case (FILE_SELECT_CODE):
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String filename;
                    if (uri.toString().contains("content://")) {
                        filename = RealPathUtil.getRealPathFromURI_API11to18(getApplicationContext(), uri);
                    } else filename = data.getData().getPath();

                    TextView v = (TextView) findViewById(R.id.filename_id);
                    v.setText(filename);
                    v.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void showAddPart(View view) {
        View addpart = findViewById(R.id.add_part_id);
        addpart.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    public void declineChoice(View view) {
        View addpart = findViewById(R.id.add_part_id);
        addpart.setVisibility(View.GONE);
        TextView text1 = (TextView) findViewById(R.id.filename_id);
        text1.setText("");
        text1.setVisibility(View.GONE);
        text1 = (TextView) findViewById(R.id.contact_id);
        text1.setText("");
        text1.setVisibility(View.GONE);
        ;
        findViewById(R.id.add_button).setVisibility(View.VISIBLE);
    }

    public void confirmChoice(View view) {
        String contactname = (String) ((TextView) findViewById(R.id.contact_id)).getText();
        String filename = (String) ((TextView) findViewById(R.id.filename_id)).getText();
        AddElem(contactname, filename);
        //hide and clear add parts
        View addpart = findViewById(R.id.add_part_id);

        addpart.setVisibility(View.GONE);
        TextView text1 = (TextView) findViewById(R.id.filename_id);
        text1.setText("");
        text1.setVisibility(View.GONE);
        text1 = (TextView) findViewById(R.id.contact_id);
        text1.setText("");
        text1.setVisibility(View.GONE);
        ;
        findViewById(R.id.add_button).setVisibility(View.VISIBLE);
    }
}
