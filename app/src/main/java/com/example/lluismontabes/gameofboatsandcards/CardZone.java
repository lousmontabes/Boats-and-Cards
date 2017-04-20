package com.example.lluismontabes.gameofboatsandcards;

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
    private boolean reversed;

    public CardZone(LinearLayout layout_cards, ImageView containerCard1, ImageView containerCard2, ImageView containerCard3) {
        this.layout_cards = layout_cards;
        this.containerCard1 = containerCard1;
        this.containerCard2 = containerCard2;
        this.containerCard3 = containerCard3;
        cardList = new ArrayList<Card>();
        reversed = false;
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

    public void setCardList(ArrayList<Card> cardList) {
        this.cardList = cardList;
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

    public ArrayList<Card> getCardList() {
        return cardList;
    }

    //FUNCTIONS

    //values of Alpha {0,1,2,...,255} , where 255 is No Transparent and 0 Totally Transparent
    //maxDistance define the distance from card zone starts to get transparent
    //minDistance define the distance at the transparency gets its minimum value and doesn't get smaller values
    public void improveVisibility(Player player, float maxDistance, float minDistance, int minAlpha) {
        float positionPlayerX = player.getCenter().x;
        float positionPlayerY = player.getCenter().y;

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
        double distToCard1 = Math.hypot(distCard1X, distCard1Y);

        float distCard2X = positionPlayerX - card2X;
        float distCard2Y = positionPlayerY - card2Y;
        double distToCard2 = Math.hypot(distCard2X, distCard2Y);

        float distCard3X = positionPlayerX - card3X;
        float distCard3Y = positionPlayerY - card3Y;
        double distToCard3 = Math.hypot(distCard3X, distCard3Y);

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
        card.setReversed(reversed);
        cardList.add(card);
        updateContainers();
    }

    public Card popCard(int idx) {
        Card c = cardList.remove(idx - 1);
        updateContainers();
        return c;
    }

    public void reverseCards(boolean reverse) {
        if (reversed != reverse) {
            reversed = reverse;
            for (Card c : cardList) {
                c.setReversed(reversed);
            }
            updateContainers();
        }
    }

    public void updateContainers() {
        switch (cardList.size()) {
            case 0:
                containerCard1.setVisibility(GONE);
                containerCard2.setVisibility(GONE);
                containerCard3.setVisibility(GONE);
                break;
            case 1:
                containerCard1.setImageResource(cardList.get(0).getResourceID());
                containerCard1.setVisibility(VISIBLE);
                containerCard2.setVisibility(GONE);
                containerCard3.setVisibility(GONE);
                break;
            case 2:
                containerCard1.setImageResource(cardList.get(0).getResourceID());
                containerCard2.setImageResource(cardList.get(1).getResourceID());
                containerCard1.setVisibility(VISIBLE);
                containerCard2.setVisibility(VISIBLE);
                containerCard3.setVisibility(GONE);
                break;
            case 3:
                containerCard1.setImageResource(cardList.get(0).getResourceID());
                containerCard2.setImageResource(cardList.get(1).getResourceID());
                containerCard3.setImageResource(cardList.get(2).getResourceID());
                containerCard1.setVisibility(VISIBLE);
                containerCard2.setVisibility(VISIBLE);
                containerCard3.setVisibility(VISIBLE);
                break;

        }
    }

}


