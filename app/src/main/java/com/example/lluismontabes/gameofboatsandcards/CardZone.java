package com.example.lluismontabes.gameofboatsandcards;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by JorgeTB on 30/03/2017.
 */

public class CardZone {

    private LinearLayout layout_cards;
    private ImageView containerCard1;
    private ImageView containerCard2;
    private ImageView containerCard3;
    private ArrayList<Card> cardList;

    public CardZone(LinearLayout layout_cards, ImageView containerCard1, ImageView containerCard2, ImageView containerCard3) {
        this.layout_cards = layout_cards;
        this.containerCard1 = containerCard1;
        this.containerCard2 = containerCard2;
        this.containerCard3 = containerCard3;
        cardList = new ArrayList<Card>();
        updateContainers();
    }

    //SETTERS
    public void setLayout_cards(LinearLayout layout_cards) {
        this.layout_cards = layout_cards;
    }

    public void setContainerCard1(ImageView containerCard1) {
        this.containerCard1 = containerCard1;
    }

    public void setContainerCard2(ImageView containerCard2) {
        this.containerCard2 = containerCard2;
    }

    public void setContainerCard3(ImageView containerCard3) {
        this.containerCard3 = containerCard3;
    }

    //GETTERS
    public LinearLayout getLayout_cards() {
        return this.layout_cards;
    }

    public ImageView getContainerCard1() {
        return this.containerCard1;
    }

    public ImageView getContainerCard2() {
        return this.containerCard2;
    }

    public ImageView getContainerCard3() {
        return this.containerCard3;
    }

    //FUNCTIONS

    //values of Alpha {0,1,2,...,255} , where 255 is No Transparent and 0 Totally Transparent
    //maxDistance define the distance from card zone starts to get transparent
    //minDistance define the distance at the transparency gets its minimum value and doesn't get smaller values
    public void improveVisibility(Player player, float maxDistance, float minDistance, int minAlpha) {
        float positionPlayerX = player.getCenterX();
        float positionPlayerY = player.getCenterY();

        int[] location1 = new int[2];
        containerCard1.getLocationOnScreen(location1);
        float card1X = location1[0];
        float card1Y = location1[1];

        int[] location2 = new int[2];
        containerCard2.getLocationOnScreen(location2);
        float card2X = location2[0];
        float card2Y = location2[1];

        int[] location3 = new int[2];
        containerCard3.getLocationOnScreen(location3);
        float card3X = location3[0];
        float card3Y = location3[1];

        float distCard1X = positionPlayerX - card1X;
        float distCard1Y = positionPlayerY - card1Y;
        double distToCard1 = Math.sqrt(Math.pow((double) distCard1X, 2) + Math.pow((double) distCard1Y, 2));


        float distCard2X = positionPlayerX - card2X;
        float distCard2Y = positionPlayerY - card2Y;
        double distToCard2 = Math.sqrt(Math.pow((double) distCard2X, 2) + Math.pow((double) distCard2Y, 2));

        float distCard3X = positionPlayerX - card3X;
        float distCard3Y = positionPlayerY - card3Y;
        double distToCard3 = Math.sqrt(Math.pow((double) distCard3X, 2) + Math.pow((double) distCard3Y, 2));

        int maxTransparent = minAlpha;
        float ratio = (255 - maxTransparent) / (maxDistance - minDistance);
        double d;

        if (distToCard1 <= maxDistance && distToCard1 >= minDistance) {
            d = (maxDistance - distToCard1) * ratio;
            if (d < 0) {
                d = 0;
            }
            containerCard1.setImageAlpha(255 - (int) d);
        }
        if (distToCard2 <= maxDistance && distToCard2 >= minDistance) {
            d = (maxDistance - distToCard2) * ratio;
            if (d < 0) {
                d = 0;
            }
            containerCard2.setImageAlpha(255 - ((int) d));
        }
        if (distToCard3 <= maxDistance && distToCard3 >= minDistance) {
            d = (maxDistance - distToCard3) * ratio;
            if (d < 0) {
                d = 0;
            }
            containerCard3.setImageAlpha(255 - ((int) d));
        }
    }

    public void addCard(Card card) {
        cardList.add(card);
        updateContainers();
    }

    public Card popCard(int idx) {
        Card c = cardList.remove(idx);
        updateContainers();
        return c;
    }

    public void updateContainers() {
        switch(cardList.size()) {
            case 0:
                containerCard1.setVisibility(GONE);
                containerCard2.setVisibility(GONE);
                containerCard3.setVisibility(GONE);
                break;
            case 1:
                containerCard1.setVisibility(VISIBLE);
                containerCard2.setVisibility(GONE);
                containerCard3.setVisibility(GONE);
                containerCard1.setImageResource(((Card) cardList.get(0)).getResourceID());
                break;
            case 2:
                containerCard1.setVisibility(VISIBLE);
                containerCard2.setVisibility(VISIBLE);
                containerCard3.setVisibility(GONE);
                containerCard1.setImageResource(((Card) cardList.get(0)).getResourceID());
                containerCard1.setImageResource(((Card) cardList.get(1)).getResourceID());
                break;
            case 3:
                containerCard1.setVisibility(VISIBLE);
                containerCard2.setVisibility(VISIBLE);
                containerCard3.setVisibility(VISIBLE);
                containerCard1.setImageResource(((Card) cardList.get(0)).getResourceID());
                containerCard2.setImageResource(((Card) cardList.get(1)).getResourceID());
                containerCard3.setImageResource(((Card) cardList.get(2)).getResourceID());
                break;

        }
    }

}


