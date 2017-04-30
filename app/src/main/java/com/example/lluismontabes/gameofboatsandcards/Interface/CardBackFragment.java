package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by JorgeTB on 12/04/2017.
 */

public class CardBackFragment extends Fragment {

    /**
     * A fragmnet representing the back of the card
     * @param inflater
     * @param container
     * @param saveIntanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveIntanceState){
        return inflater.inflate(R.layout.card_back, container, false);
    }

}



