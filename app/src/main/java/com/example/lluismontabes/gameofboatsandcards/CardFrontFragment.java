package com.example.lluismontabes.gameofboatsandcards;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JorgeTB on 12/04/2017.
 */

public class CardFrontFragment extends Fragment {

    /**
     * A fragment representing the front of the card
     * @param inflater
     * @param container
     * @param saveIntanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveIntanceState){
        return inflater.inflate(R.layout.card_front,container, false);
    }
}
