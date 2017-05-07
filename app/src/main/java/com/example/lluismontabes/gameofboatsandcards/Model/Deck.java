package com.example.lluismontabes.gameofboatsandcards.Model;

import com.example.lluismontabes.gameofboatsandcards.Views.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Delta on 03/05/2017.
 */



public class Deck {

    private ArrayList<Card> cardArrayList;
    private ArrayList<Card> discardPile;
    private Iterator<Card> cardIterator;

    public Deck(ArrayList<Card> list) {
        cardArrayList = list;
        discardPile = new ArrayList<Card>();
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(cardArrayList);
        cardIterator = cardArrayList.iterator();
    }

    public Card next() {
        Card toDraw = cardArrayList.remove(0);
        if (cardArrayList.isEmpty()) {
            if (discardPile.isEmpty()) {
                throw new UnknownError();
            }
            Collections.copy(cardArrayList, discardPile);
            discardPile.clear();
        }
        return toDraw;
    }

    public void discard(Card c) {
        discardPile.add(c);
    }

}
