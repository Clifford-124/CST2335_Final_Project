package com.project.cst2335;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.Fragment;



/* ** This class is extended from the Fragment class and is used to generate Fragment on another screen.

 * displays the details of the item clicked which is the date

 * Button for save, delete, Close of the details shown on the screen

 */
public class Covid_Details_Fragment extends Fragment {

    Covid_Dates_Fragment.CovidInformation selectedDate;

    int selectedPosition;
    SQLiteDatabase dbase;
    CovidDbOpener covidDbOpener;
    /**
     * Fragment class constructor
     * @param datetxt
     * @param position
     */

    public Covid_Details_Fragment(Covid_Dates_Fragment.CovidInformation datetxt , int position){
        selectedDate = datetxt;
        selectedPosition = position ;
    }
    /**-
     * The onCreateView method to inflate the layout with COVID details against selected result date
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        covidDbOpener= new CovidDbOpener(getContext());
        dbase = covidDbOpener.getWritableDatabase();
        View view = inflater.inflate(R.layout.covid_details_layout, container, false);
        Button closeBtn = view.findViewById(R.id.covidCloseBtn);
        Button deleteBtn = view.findViewById(R.id.covidDeleteBtn);
        Button saveBtn = view.findViewById(R.id.covidSaveBtn);

        //  finding the text view and buttons
        TextView covid_search_date = view.findViewById(R.id.date);
        TextView covid_col_cases = view.findViewById(R.id.total_cases);
        TextView covid_col_fatalite = view.findViewById(R.id.total_fatalities);
        TextView covid_col_hospital = view.findViewById(R.id.total_hospitalizations);
        TextView covid_col_vaccine = view.findViewById(R.id.total_vaccinations);
        TextView covid_col_recover = view.findViewById(R.id.total_recoveries);

        //  setting info in text view against the selected date
        covid_search_date.setText("The date is: " + selectedDate.getDate());
        covid_col_cases .setText("Total cases are: "+ selectedDate.getTotal_cases());
        covid_col_fatalite.setText("Total Fatalities are : "+ selectedDate.getTotal_fatalities());
        covid_col_hospital.setText("Total hospitalization are: " + selectedDate.getTotal_hospitalizations());
        covid_col_recover.setText("Total recoveries are: " + selectedDate.getTotal_recoveries());
        covid_col_vaccine.setText("Total vaccinations are : " +  selectedDate.getTotal_vaccinations());


        //couldnt get the deletion working yet
/**
 * using lambda expression to create on click listener to the delete button.
 * the function will allow button to delete the model information into database by using the CovidDataBaseOpener class.
 */
        /*deleteBtn.setOnClickListener( clickedDelete ->{
            CovidMainActivity parentActivity = (CovidMainActivity) getContext();

            parentActivity.notifyMessageDeleted(selectedDate, selectedPosition);
            getParentFragmentManager().beginTransaction().remove(this).commit();


        });*/

        /**
         * using lambda expression to create on click listener to the save button.
         * the function will allow button to save the model information into database by using the CovidDataBaseOpener class.
         */
        saveBtn.setOnClickListener(save -> {
            CovidMainActivity parentActivity = (CovidMainActivity) getContext();

            ContentValues newRow = new ContentValues();
            newRow.put(CovidDbOpener.Col_date,selectedDate.getDate());
            newRow.put(CovidDbOpener.Col_total_cases, selectedDate.getTotal_cases());
            newRow.put(CovidDbOpener.Col_total_fatalities, selectedDate.getTotal_fatalities());
            newRow.put(CovidDbOpener.Col_total_hospitalizations, selectedDate.getTotal_hospitalizations());
            newRow.put(CovidDbOpener.Col_total_vaccinations, selectedDate.getTotal_vaccinations());
            newRow.put(CovidDbOpener.Col_total_recoveries, selectedDate.getTotal_recoveries());
            dbase.insert(CovidDbOpener.Table_name, CovidDbOpener.Col_date, newRow);
            parentActivity.notifyMessageSaved(selectedDate, selectedPosition);
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mStrings = {selectedDate.getDate()};
                dbase.delete(CovidDbOpener.Table_name,"date =?",mStrings);
                getActivity().finish();
            }
        });
        /**
         * click listener for close button;
         * Add setOnClickListener on close button the function will allow button to close the fragment.
         */
        closeBtn.setOnClickListener(closeClicked ->{
            getParentFragmentManager().beginTransaction().remove(this).commit();

        });

        return  view;
    }


}