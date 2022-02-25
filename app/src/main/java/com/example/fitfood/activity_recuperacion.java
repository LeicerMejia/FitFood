package com.example.fitfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

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

public class activity_recuperacion extends AppCompatActivity {
    EditText correo;
    Button recuperar, volver;
    BaseDeDatos bd;
    Window window;
    ProgressDialog pd;
    ConstraintLayout contextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion);

        correo = (EditText) findViewById(R.id.et_correo_recuperar);
        recuperar = (Button) findViewById(R.id.btn_recuperar_);
        volver = (Button) findViewById(R.id.btn_cancelar);
        window = getWindow();
        bd = new BaseDeDatos(activity_recuperacion.this);
        contextView = (ConstraintLayout) findViewById(R.id.context_view_recuperar);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo_text = correo.getText().toString().trim();

                if(correo_text.equals("")){
                    correo.setError("Ingrese un correo electrónico");
                } else {
                    SQLiteDatabase sql = bd.getWritableDatabase();
                    Cursor cursor = sql.rawQuery("SELECT * FROM usuarios WHERE correo = '" + correo_text + "'", null);

                    if(cursor != null && cursor.moveToFirst()){

                        String usuario = cursor.getString(3);
                        String password = cursor.getString(5);
                        String texto_nombre = cursor.getString(1);

                        new enviarAsyntask().execute(correo_text, "<table style='text-align: center; margin: auto;'><tr><td style='font-size: 20px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; align-items: center; display: flex;'><img src='https://www.mediafire.com/convkey/dcd0/biptnt9cuhivmj99g.jpg' style='width: 36px;'> &nbsp; <strong>FitFood</strong></td></tr></table>" +
                                "<table style='width: 370px; margin: auto;  border-top-left-radius: 30px; border-top-right-radius: 30px; border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; overflow: hidden; magin: 0; border: 1px solid #ccc;'>" +
                                "   <tbody>" +
                                "         <tr>" +
                                "            <td><img src='https://img.freepik.com/vector-gratis/contrasena-acceso-seguro-aviso-acceso-o-codigo-verificacion-autenticacion-nota-mensaje-burbuja-icono-voz-plana-dibujos-animados_212005-104.jpg?size=626&amp;ext=jpg&amp;ga=GA1.2.1290984231.1628629898' style='magin: 0;  width: 370px;  border-top-left-radius: 30px;border-top-right-radius: 30px;'></td>" +
                                "         </tr>" +
                                "         <tr style='font-size: 29px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; line-height:40px;'>" +
                                "              <td style='padding: 2px 10px;'><strong>Recuperación de datos de acceso</strong></td>" +
                                "         </tr>" +
                                "         <tr>" +
                                "              <td style='font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; padding-top:16px; line-height: 23px; font-size: 17px; padding: 2px 10px;'>Hola " + texto_nombre + ": hemos recibido una solicitud para la recuperación de sus datos de acceso, a continuación se muestra toda su información:</td>" +
                                "         </tr>" +
                                "         <tr>" +
                                "            <td style=' padding-bottom: 10px;font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif; padding-top:16px; line-height: 25px; font-size: 17px; padding: 2px 10px;'><br><strong>Usuario:</strong> " + usuario + " <br> <strong>Contraseña:</strong> " + password + " </td>" +
                                "         </tr>" +
                                "  </tbody>" +
                                "</table>" +
                                "<p style='text-align: center; font-size: 12px; width: 450px; margin: auto; margin-top: 20px; font-family:adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Correo generado automáticamente, no responder. <br> Este correo fue generado por FitFood Inc. Si lo recibió por error haga caso omiso. <br> FitFood todo los derechos reservados ©</p>");
                    } else {
                        Snackbar.make(contextView, "El correo electrónico ingresado no se encuentra registrado", Snackbar.LENGTH_SHORT)
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                .show();
                    }
                }
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(activity_recuperacion.this, MainActivity.class);
                startActivity(volver);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent volver = new Intent(activity_recuperacion.this, MainActivity.class);
        startActivity(volver);
        finish();
    }

    class enviarAsyntask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(activity_recuperacion.this);
            pd.setMessage("Validando usuario...");
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

                message.setSubject("FitFood - Recuperación de datos de acceso");
                message.setContent(strings[1], "text/html; charset=utf-8");

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_recuperacion.this);
                alert.setTitle("Error");
                alert.setMessage(e.toString());
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent volver = new Intent(activity_recuperacion.this, MainActivity.class);
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
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_recuperacion.this);
            alert.setTitle("Recuperación de datos de acceso");
            alert.setMessage("Se ha enviado un correo eléctronico con los datos de acceso. \n\n" +
                    "Si no lo encuentra en la bandeja principal, recuerde verificar la carpeta de SPAM");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent volver = new Intent(activity_recuperacion.this, MainActivity.class);
                    startActivity(volver);
                    finish();
                }
            });
            alert.setCancelable(false);
            alert.show();
        }
    }

}