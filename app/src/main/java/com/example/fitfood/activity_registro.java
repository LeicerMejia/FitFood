package com.example.fitfood;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class activity_registro extends AppCompatActivity {
    EditText nombre, apellido, usuario, correo, clave;
    AutoCompleteTextView rol;
    Button btn_registrar, btn_volver;
    Window window;
    BaseDeDatos bd;
    ConstraintLayout contextView;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = (EditText) findViewById(R.id.et_nombre_registro);
        apellido = (EditText) findViewById(R.id.et_apellido_registro);
        usuario = (EditText) findViewById(R.id.et_usuario_registro);
        correo = (EditText) findViewById(R.id.et_correo_registro);
        clave = (EditText) findViewById(R.id.et_clave_registro);
        rol = (AutoCompleteTextView) findViewById(R.id.et_rol_registro);
        btn_registrar = (Button) findViewById(R.id.btn_registrar_registro);
        btn_volver = (Button) findViewById(R.id.btn_volver);
        window = getWindow();
        bd = new BaseDeDatos(activity_registro.this);
        contextView = (ConstraintLayout) findViewById(R.id.context_view_registrar);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String[] items = new String[] {"Dietista", "Consumidor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity_registro.this, R.layout.list_item, items);
        rol.setAdapter(adapter);

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto_nombre = nombre.getText().toString().trim();
                String texto_apellido = apellido.getText().toString().trim();
                String texto_usuario = usuario.getText().toString().trim();
                String texto_correo = correo.getText().toString().trim();
                String texto_clave = clave.getText().toString().trim();
                String texto_rol = rol.getText().toString().trim();
                int vacio = 0;
                SQLiteDatabase sql = bd.getWritableDatabase();
                ContentValues cv = new ContentValues();

                if(texto_nombre.equals("")){
                    nombre.setError("Ingrese su nombre");
                    vacio++;
                }
                if(texto_apellido.equals("")){
                    apellido.setError("Ingrese un apellido");
                    vacio++;
                }
                if(texto_usuario.equals("")){
                    usuario.setError("Ingrese un usuario");
                    vacio++;
                }
                if(texto_clave.equals("")){
                    clave.setError("Ingrese una contraseña");
                    vacio++;
                }
                if(texto_correo.equals("")){
                    correo.setError("Ingrese su correo eletrónico");
                    vacio++;
                }
                if(texto_rol.equals("")){
                    rol.setError("Seleccione un rol");
                    vacio++;
                }

                if(vacio == 0){
                    Cursor user = sql.rawQuery("SELECT usuario FROM usuarios WHERE usuario = '" + texto_usuario + "'", null);
                    Cursor email = sql.rawQuery("SELECT correo FROM usuarios WHERE correo = '" + texto_correo + "'", null);

                    if(user != null && user.getCount() > 0){
                        usuario.setError("El usuario ya se encuentra registrado");
                        MaterialAlertDialogBuilder alerta = new MaterialAlertDialogBuilder(activity_registro.this);
                        alerta.setMessage("El usuario ya se encuentras registrado, si eres tú inicia sesión");
                        alerta.setPositiveButton("Iniciar sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent volver = new Intent(activity_registro.this, MainActivity.class);
                                startActivity(volver);
                                finish();
                            }
                        });
                        alerta.setNegativeButton("Seguir registro", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alerta.show();
                    } else if (email != null && email.getCount() > 0){
                        correo.setError("El correo ya se encuentra registrado");
                        MaterialAlertDialogBuilder alerta2 = new MaterialAlertDialogBuilder(activity_registro.this);
                        alerta2.setMessage("El correo electronico ya se encuentra registrado, si eres tú inicia sesión");
                        alerta2.setPositiveButton("Iniciar sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent volver = new Intent(activity_registro.this, MainActivity.class);
                                startActivity(volver);
                                finish();
                            }
                        });
                        alerta2.setNegativeButton("Seguir registro", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alerta2.show();
                    } else {
                        cv.put("nombre", texto_nombre);
                        cv.put("apellido", texto_apellido);
                        cv.put("usuario", texto_usuario);
                        cv.put("correo", texto_correo);
                        cv.put("clave", texto_clave);
                        cv.put("rol", texto_rol);

                        sql.insert("usuarios", null, cv);
                        sql.close();

                        switch (texto_rol){
                            case "Consumidor":
                                new enviarCorreo().execute(texto_correo, "<table style='text-align: center; margin: auto;'><tr><td style='font-size: 20px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; align-items: center; display: flex;'><img src='https://www.mediafire.com/convkey/dcd0/biptnt9cuhivmj99g.jpg' style='width: 46px;'> &nbsp; <strong>FitFood</strong></td></tr></table>" +
                                        "<table style='width: 370px; margin: auto;  border-top-left-radius: 30px; border-top-right-radius: 30px; border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; overflow: hidden; magin: 0; border: 1px solid #ccc;'>" +
                                        "    <tbody>" +
                                        "        <tr>" +
                                        "            <td><img src='https://image.freepik.com/free-vector/people-eating-healthy-exercising-regularly_53876-64671.jpg' style='magin: 0;  width: 370px;  border-top-left-radius: 30px;border-top-right-radius: 30px;'></td>" +
                                        "        </tr>" +
                                        "        <tr style='font-size: 29px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; line-height:40px;'>" +
                                        "            <td style='padding: 2px 10px;'><strong>¡Gracias por registrarte!</strong></td>" +
                                        "        </tr>" +
                                        "        <tr>" +
                                        "            <td style='font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; padding-top:16px; line-height: 23px; font-size: 17px; padding: 2px 10px;'>Hola " + texto_nombre + ", te damos la bienvenida a FitFood aquí podrás encontrar todo tipo de planes alimenticios, gracias por hacer parte de esta gran comunidad. <br><br> Estamos agradecidos de que estes con nosotros.</td>" +
                                        "        </tr>" +
                                        "    </tbody>" +
                                        "</table>" +
                                        "<p style='text-align: center; font-size: 12px; width: 450px; margin: auto; margin-top: 20px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Correo generado automáticamente, no responder. <br> Este correo fue generado por FitFood Inc. Si lo recibió por error haga caso omiso. <br> FitFood todo los derechos reservados ©</p>");
                                break;
                            case "Dietista":
                                new enviarCorreo().execute(texto_correo, "<table style='text-align: center; margin: auto;'><tr><td style='font-size: 20px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; align-items: center; display: flex;'><img src='https://www.mediafire.com/convkey/dcd0/biptnt9cuhivmj99g.jpg' style='width: 46px;'> &nbsp; <strong>FitFood</strong></td></tr></table>" +
                                        "<table style='width: 370px; margin: auto;  border-top-left-radius: 30px; border-top-right-radius: 30px; border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; overflow: hidden; magin: 0; border: 1px solid #ccc;'>" +
                                        "    <tbody>" +
                                        "        <tr>" +
                                        "            <td><img src='https://image.freepik.com/free-vector/diet-plan-schedule_3446-617.jpg' style='magin: 0;  width: 370px;  border-top-left-radius: 30px;border-top-right-radius: 30px;'></td>" +
                                        "        </tr>" +
                                        "        <tr style='font-size: 29px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; line-height:40px;'>" +
                                        "            <td style='padding: 2px 10px;'><strong>¡Gracias por registrarte!</strong></td>" +
                                        "        </tr>" +
                                        "        <tr>" +
                                        "            <td style='font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; padding-top:16px; line-height: 23px; font-size: 17px; padding: 2px 10px;'>Hola " + texto_nombre + ", te damos la bienvenida a FitFood desde aquí podrás subir tus planes alimenticios y ayudar a muchas personas a controlar su alimentación, y todo desde la comodidad de tu casa, gracias por hacer parte de esta gran comunidad. <br><br> Estamos agradecidos de que estes con nosotros.</td>" +
                                        "        </tr>" +
                                        "    </tbody>" +
                                        "</table>" +
                                        "<p style='text-align: center; font-size: 12px; width: 450px; margin: auto; margin-top: 20px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Correo generado automáticamente, no responder. <br> Este correo fue generado por FitFood Inc. Si lo recibió por error haga caso omiso. <br> FitFood todo los derechos reservados ©</p>");
                                break;
                        }

                    }
                } else {
                    Snackbar.make(contextView, "Se encontraron campos vacíos", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
                }


            }
        });

        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(activity_registro.this, MainActivity.class);
                startActivity(volver);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent volver = new Intent(activity_registro.this, MainActivity.class);
        startActivity(volver);
        finish();
    }

    class enviarCorreo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(activity_registro.this);
            pd.setMessage("Registrando usuario...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String to = strings[0];

            String from = "fitfood.no.reply@gmail.com";
            final String password = "fitfood1234";

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            try {
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

                message.setSubject("FitFood - Registro exitoso");
                message.setContent(strings[1], "text/html; charset=utf-8");

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_registro.this);
                alert.setTitle("Error");
                alert.setMessage(e.toString());
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent volver = new Intent(activity_registro.this, MainActivity.class);
                        startActivity(volver);
                        finish();
                    }
                });
                alert.setCancelable(false);
                alert.show();
                throw new RuntimeException(e);
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.hide();
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_registro.this);
            alert.setTitle("Registro exitoso");
            alert.setMessage("El usuario se ha registrado exitosamente, gracias por hacer parte de esta gran comunidad");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent volver = new Intent(activity_registro.this, MainActivity.class);
                    startActivity(volver);
                    finish();
                }
            });
            alert.setCancelable(false);
            alert.show();
        }
    }
}