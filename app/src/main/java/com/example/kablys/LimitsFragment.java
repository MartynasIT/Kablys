package com.example.kablys;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LimitsFragment extends Fragment {
    TextView lim, more;
    Button check;
    Context ctx;
    DatabaseAPI db;
    SessionManager Session;
    Spinner fish, amount, lenght, card;
    RelativeLayout body;
    boolean notify = false;

    private ArrayList<String[]> limits = new ArrayList<String[]>();


    public LimitsFragment() {
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseAPI(ctx);
        Session = new SessionManager(ctx);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_limits, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        fish = (Spinner) view.findViewById(R.id.spinner_fish);
        amount = (Spinner) view.findViewById(R.id.spinner_amount);
        lenght = (Spinner) view.findViewById(R.id.spinner_lenght);
        check = view.findViewById(R.id.check_limits);
        card = view.findViewById(R.id.spinner_card);
        limits = db.getLimits();
        lim = view.findViewById(R.id.limit_text);
        more = view.findViewById(R.id.moreLimits);
        final ArrayList weights = new ArrayList();

        check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String textLim = "";

                if ((!fish.getSelectedItem().equals("Pasirinkite žuvį")) && (!amount.getSelectedItem().equals("Pasirinkite kiekį"))
                        && (!lenght.getSelectedItem().equals("Pasirinkite ilgį"))&& !card.getSelectedItem().equals("Žvejo mėgėjo kortelė?"))
                {
                    Iterator<String[]> itr = limits.iterator();
                    while (itr.hasNext()) {
                        String[] array = itr.next();

                       if ( fish.getSelectedItem().equals(array[0]))
                       {


                           String temp = (String) lenght.getSelectedItem();
                           temp = temp.replace("cm","");
                           int fishLen = Integer.parseInt(temp);

                           String am = (String) amount.getSelectedItem();
                           boolean kg;
                           Pattern pattr = Pattern.compile("[Kg]", Pattern.CASE_INSENSITIVE);
                           Matcher match = pattr.matcher(am);
                           boolean found = match.find();
                           if (found)
                               kg = true;
                           else kg = false;


                           if (kg)
                           {
                               am =  am.replace("kg","");
                               weights.add(am);

                               if (Integer.parseInt(am) > 5)
                               {

                                   textLim += "Negalima pagauti daugiau nei 5Kg (Kuršių marios 7kg) " +"\n" + "\n";
                               }

                           }

                           if  (!kg && Integer.valueOf((String) amount.getSelectedItem()) > Integer.valueOf(array[1]) && !array[1].equals("0") )
                           {

                               textLim += "Maksimalus kiekis šių žūvų yra: " + array[1]  + "\n" + "\n";

                           }

                           if (!card.getSelectedItem().equals("Taip") && array[3].equals("yes") )
                           {

                               textLim += "Šiai žuviai reikia mėgėjo kortelės"+ "\n" + "\n";

                           }

                           if  (fishLen < Integer.valueOf(array[2]))
                           {


                               textLim += "Ši žuvis negali būti trumpesnė negu "+ array[2] + " cm" + "\n" + "\n";

                           }

                           if (!kg && array[1].equals("0") && Integer.parseInt((String) amount.getSelectedItem()) > 1 )
                           {
                               textLim += "Žvejoti šias žuvis yra uždrausta" + "\n" + "\n";
                           }

                           if (kg && array[1].equals("0") )
                           {
                               textLim += "Žvejoti šias žuvis yra uždrausta" + "\n" + "\n";
                           }



                       }

                    }


                   lim.setText(textLim);
                    lim.setTextColor(Color.RED);
                    if (textLim.length() == 0 && textLim.equals(""))
                    {
                        lim.setText("Viskas gerai");
                        lim.setTextColor(Color.GREEN);
                    }
                }

                else
                {
                    Toast.makeText(ctx, "Pasirinkite visus atributus!",
                            Toast.LENGTH_SHORT).show();
                }

                Iterator iter = weights.iterator();
                int Kg = 0;
                while (iter.hasNext()) {
                    Kg += Integer.parseInt((String) iter.next());
                }

                if (Kg > 5 && !notify)
                {

                    lim.setText("Jūs šiandien pagavote daugiau nei 5Kg, kas yra draudžiama");
                    lim.setTextColor(Color.RED);
                    notify = true;
                }



            }
        });


        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DialogLimits Dialog = new DialogLimits();
                Dialog.show(getFragmentManager(), "More limits");
                FragmentManager fm = getFragmentManager();
                fm.executePendingTransactions();

            }
        });




}
}
