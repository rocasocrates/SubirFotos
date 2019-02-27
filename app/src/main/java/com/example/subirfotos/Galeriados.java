package com.example.subirfotos;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Galeriados extends AppCompatActivity implements View.OnClickListener{

    private Button btnBuscar;
    private Button btnSubir;
    private Button recorrer;
    private Button salir;

    private ImageView imageView;

    // private EditText editTextName;

    private Bitmap bitmap;

    private int GALLERY_CODE = 1;
    private int PICK_IMAGE_MULTIPLE = 1;
    private String imagePath;
    private List<String> imagePathList;
    private String UPLOAD_URL ="";

    private String KEY_IMAGEN = "foto";
    private String KEY_NOMBRE = "nombre";
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeriados);

      //  Intent intent = getIntent();
      //  Bundle extras = intent.getExtras();
      //  if (extras != null) {
       //     data = extras.getString("datos");
            UPLOAD_URL = "";//http://www.segymant.com/bd/android/subirdegaleria.php";
       // }

        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnSubir = (Button) findViewById(R.id.btnSubir);
        salir = (Button) findViewById(R.id.salir);
        recorrer = (Button) findViewById(R.id.recorrer);

        //editTextName = (EditText) findViewById(R.id.editText);

        imageView  = (ImageView) findViewById(R.id.imageView);

        btnBuscar.setOnClickListener(this);
        btnSubir.setOnClickListener(this);
        salir.setOnClickListener(this);
        recorrer.setOnClickListener(this);
    }

    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    //PULSO SUBIR IMAGEN
    private void uploadImage(){
        //Mostrar el diálogo de progreso
        final ProgressDialog loading = ProgressDialog.show(this,"Subiendo...","Espere por favor...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();
                        //Mostrando el mensaje de la respuesta

                        Toast toast = new Toast(getApplicationContext());

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.realizado,
                                (ViewGroup) findViewById(R.id.lytLayout));

                        TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
                        txtMsg.setText(s);

                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {

                                        System.exit(0);

                                    }
                                },
                                2000);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();

                        //Showing toast

                        //Toast.makeText(Galeriados.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Toast toasterror = new Toast(getApplicationContext());

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.error,
                                (ViewGroup) findViewById(R.id.lytLayout));

                        TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
                        txtMsg.setText("ERROR DE CONEXION !!!!");

                        toasterror.setDuration(Toast.LENGTH_LONG);
                        toasterror.setView(layout);
                        toasterror.show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Convertir bits a cadena
                String imagen = getStringImagen(bitmap.createScaledBitmap(bitmap, 500, 600, true));

                //Obtener el nombre de la imagen
                String nombre = data;

                //Creación de parámetros
                Map<String,String> params = new Hashtable<String, String>();

                //Agregando de parámetros
                params.put(KEY_IMAGEN, imagen);
                params.put(KEY_NOMBRE, nombre);

                //Parámetros de retorno
                return params;
            }
        };

        //Creación de una cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Agregar solicitud a la cola
        requestQueue.add(stringRequest);
    }
    // SELECCIONAMOS IMAGEN
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Imagen"), PICK_IMAGE_MULTIPLE);
    }
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null){

            imagePathList = new ArrayList<>();

            if(data.getClipData() != null){

                int count = data.getClipData().getItemCount();
                for (int i=0; i<count; i++){

                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    getImageFilePath(imageUri);
                }
            }
            else if(data.getData() != null){

                Uri imgUri = data.getData();
                getImageFilePath(imgUri);
            }
        }
    }
    public void getImageFilePath(Uri uri) {

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePathList.add(imagePath);
            cursor.close();
        }
    }


    @Override
    public void onClick(View v) {

        if(v == btnBuscar){
            showFileChooser();
        }

        if(v == btnSubir){
            uploadImage();
        }
        if (v == salir) {
            finish();
        }
        if (v == recorrer)
        {
            recorrer();
        }
    }

    private void recorrer() {

        for(int i = 0; i < imagePathList.size(); i++)
        {
            String valor = imagePathList.get(i);

            String elvalor = valor;
        }
    }
}