package cat.paucasesnoves.sppmmactivitat4;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    private ListAdapter adapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Les columnes que volem obtenir per a cada element.

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        //Condició: volem obtenir totes les files (per això és null).
        String where = null;
        String[] whereArgs = null;
        //Ordre: que estiguin ordenats de forma ascendent.
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor c = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, projection,
                // Columnes per obtenir de cada fil
                where, // Criteri de selecció
                whereArgs, // Criteri de selecció
                sortOrder);  // Ordre


        //Si hi ha hagut un error
        if (c == null) {
            //Codi per tractar l’error, escriure logs, etc.
        }
        //Si el cursor està buit, el proveïdor no ha trobat resultats.
        else if (c.getCount() < 1) {
            //El cursor està buit, el content provider no té elements.
            Toast.makeText(this, "No hi ha dades", Toast.LENGTH_SHORT).show();
        } else {
            //Dades obtingudes
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        }
        //Mentre tenim un nou element al cursor
        while (c.moveToNext()) {
            //Obtenir ID
            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            //Obtenir nom
            String nomContacte = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //Saber si té telèfon
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            //Mostrar
            Toast.makeText(this, "id: " + contactId + "\n" + "Nom: " + nomContacte + "\n Tételèfon: " + hasPhone, Toast.LENGTH_SHORT).show();

            String telefon = null;
            String email = null;
            if (hasPhone.compareTo("1") == 0) {
                // Obtenim els telèfons
                Cursor telefons = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null,
                        null);
                //Recorrem els telèfons
                while (telefons.moveToNext()) {
                    telefon = telefons.getString(telefons.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                }     //Tanquem el cursor
                telefons.close();
            }

            //Obtenir cursor correus
            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
            //Recorrem els correus
            while (emails.moveToNext()) {
                email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
            //Tanquem el cursor
            emails.close();
            //Mostrar
            Toast.makeText(this, "id: " + contactId + "\n" + "Nom: " + nomContacte + "\nTelefon: " + telefon + "\n email: " + email, Toast.LENGTH_SHORT).show();
        }
        c.close();

        llistaContactes();
    }


    public void inserirEntradaDiccionari() {
        Uri UriNou;
        ContentValues Valors = new ContentValues();
        //Creem el valor de la nova entrada
        Valors.put(UserDictionary.Words.APP_ID, "com.example.activitatt2_4");
        Valors.put(UserDictionary.Words.LOCALE, "es_ES");
        Valors.put(UserDictionary.Words.WORD, "Inca");
        Valors.put(UserDictionary.Words.FREQUENCY, "100");
        //Inserim
        UriNou = getContentResolver().insert(UserDictionary.Words.CONTENT_URI, Valors);
    }


    public void llistaContactes() {
        String phoneNumber = null;
        String email = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Obtenir contactes
        Log.i(getClass().getSimpleName(), "Contactes");
        if (cursor.getCount() > 0) {
            // Hi ha contactes?
            ArrayList<HashMap<String, String>> llista = new ArrayList<HashMap<String, String>>();
            Log.i(getClass().getSimpleName(), "Hi ha");
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                String contacte_id = cursor.getString(cursor.getColumnIndex(_ID));
                String nom = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    map.put("nombre", nom);
                    // Cercar els telefons del contacte
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null,
                            Phone_CONTACT_ID + " = ?", new String[]{contacte_id}, null);

                    String telefons = "";
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        telefons = telefons + "\nTelèfon:" + phoneNumber;
                    }
                    phoneCursor.close();
                    map.put("telefono", telefons);
                    // Cercar els email per a cada contacte
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contacte_id},
                            null);
                    String emails = "";
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        emails = emails + "\nEmail:" + email;
                    }
                    emailCursor.close();
                    map.put("email", emails);
                    llista.add(map);
                }
            }

            //Assignar a la listview
            adapter = new SimpleAdapter(this, llista, R.layout.detalle_layout,
                    new String[]{"nombre", "telefono", "email"}, new int[]{R.id.nombre_input,
                    R.id.telefono_input, R.id.email_input});
            setListAdapter(adapter);
        }
    }

}

