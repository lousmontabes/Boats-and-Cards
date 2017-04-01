package com.example.lluismontabes.gameofboatsandcards;

import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by JorgeTB on 30/03/2017.
 */

public class CardZone {

    private LinearLayout layout_cards;
    private ImageButton containerCard1;
    private ImageButton containerCard2;
    private ImageButton containerCard3;

    public CardZone(LinearLayout layout_cards,ImageButton containerCard1,ImageButton containerCard2,ImageButton containerCard3){
        this.layout_cards = layout_cards;
        this.containerCard1 = containerCard1;
        this.containerCard2 = containerCard2;
        this.containerCard3 = containerCard3;
    }

    //SETTERS
    public void setLayout_cards(LinearLayout layout_cards){this.layout_cards = layout_cards;}
    public void setContainerCard1(ImageButton containerCard1){this.containerCard1 = containerCard1;}
    public void setContainerCard2(ImageButton containerCard2) {this.containerCard2 = containerCard2;}
    public void setContainerCard3 (ImageButton containerCard3) {this.containerCard3 = containerCard3;}

    //GETTERS
    public LinearLayout getLayout_cards(){return this.layout_cards;}
    public ImageButton getContainerCard1(){return this.containerCard1;}
    public ImageButton getContainerCard2(){return this.containerCard2;}
    public ImageButton getContainerCard3(){return this.containerCard3;}

    //FUNCTIONS

    //values of Alpha {0,1,2,...,255} , where 255 is No Transparent and 0 Totally Transparent
    //maxDistance define the distance from card zone starts to get transparent
    //minDistance define the distance at the transparency gets its minimum value and doesn't get smaller values
    public void improveVisibility(Player player,float maxDistance,float minDistance,int minAlpha){
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

        float distCard1X = positionPlayerX-card1X;
        float distCard1Y = positionPlayerY-card1Y;
        double distToCard1 = Math.sqrt(Math.pow((double)distCard1X,2) + Math.pow((double)distCard1Y,2));


        float distCard2X = positionPlayerX-card2X;
        float distCard2Y = positionPlayerY-card2Y;
        double distToCard2 = Math.sqrt(Math.pow((double)distCard2X,2) + Math.pow((double)distCard2Y,2));

        float distCard3X = positionPlayerX-card3X;
        float distCard3Y = positionPlayerY-card3Y;
        double distToCard3 = Math.sqrt(Math.pow((double)distCard3X,2) + Math.pow((double)distCard3Y,2));

            int maxTransparent = minAlpha;
            float reason = (255-maxTransparent)/(maxDistance - minDistance);
            double d;

            if (distToCard1 <= maxDistance && distToCard1 >= minDistance){
                d = (maxDistance-distToCard1) *  reason;
                if (d < 0){
                    d = 0;
                }
                containerCard1.setImageAlpha(255-(int)d);
            }
            if (distToCard2 <= maxDistance && distToCard2 >= minDistance){
                d = (maxDistance-distToCard2) *  reason;
                if (d < 0){
                    d = 0;
                }
                containerCard2.setImageAlpha(255-((int)d));
            }
            if (distToCard3 <= maxDistance && distToCard3 >= minDistance){
                d = (maxDistance-distToCard3) *  reason;
                if (d < 0){
                    d = 0;
                }
                containerCard3.setImageAlpha(255-((int)d));
            }
        }
    }
