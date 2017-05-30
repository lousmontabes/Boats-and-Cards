package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by JorgeTB on 30/05/2017.
 */

public class CollectionActivity extends Activity {

    private ImageView expandedImageView = null;
    private ImageView image_attack_up;
    private ImageView image_backwards;
    private ImageView image_discard_one;
    private ImageView image_multishot;
    private ImageView image_rand_warp;
    private ImageView image_reversed_hand;
    private ImageView image_speed_up;
    private ImageView image_stunned;
    private ImageView image_full_heal;
    private ImageView image_ko;
    private ImageView image_quick_revive;
    private ImageView image_dispel;

    private TextView cita;

    private View thumbView;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    private final Rect finalBounds = new Rect();
    private final Rect startBounds = new Rect();

    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        FrameLayout activityZ = (FrameLayout) findViewById(R.id.container);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;

        image_attack_up = (ImageView) findViewById(R.id.expanded_image_attack_up);
        image_backwards = (ImageView) findViewById(R.id.expanded_image_backwards);
        image_discard_one = (ImageView) findViewById(R.id.expanded_image_discard_one);
        image_multishot = (ImageView) findViewById(R.id.expanded_image_multishot);
        image_rand_warp = (ImageView) findViewById(R.id.expanded_image_rand_warp);
        image_reversed_hand = (ImageView) findViewById(R.id.expanded_image_reversed_hand);
        image_speed_up = (ImageView) findViewById(R.id.expanded_image_speed_up);
        image_stunned = (ImageView) findViewById(R.id.expanded_image_stunned);
        image_full_heal = (ImageView) findViewById(R.id.expanded_image_full_heal);
        image_ko = (ImageView) findViewById(R.id.expanded_image_ko);
        image_quick_revive = (ImageView) findViewById(R.id.expanded_image_quick_revive);
        image_dispel = (ImageView) findViewById(R.id.expanded_image_dispel);

        int height_pixels = (int) (40 * getResources().getDisplayMetrics().density);

        int left_cita = width/4 +20;
        int top_cita = (int) (height*0.65);
        int right_cita = 100;
        int bottom_cita = 0;

        final TextView cita_attack_up = new TextView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.width,height_pixels);
        layoutParams.setMargins(left_cita,top_cita,right_cita,bottom_cita);
        cita_attack_up.setVisibility(View.INVISIBLE);
        cita_attack_up.setLayoutParams(layoutParams);
        cita_attack_up.setText("  'More damage, more power, but \n for a pirate there is never enough'");
        cita_attack_up.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_attack_up.setTextColor(Color.WHITE);
        activityZ.addView(cita_attack_up);

        final TextView cita_backwards = new TextView(this);
        //RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(this.width, height_pixels);
        //layoutParams.setMargins(left_cita,top_cita,right_cita,bottom_cita);
        cita_backwards.setVisibility(View.INVISIBLE);
        cita_backwards.setLayoutParams(layoutParams);
        cita_backwards.setText("'Who has set the rudder upside down?'");
        cita_backwards.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_backwards.setTextColor(Color.WHITE);
        activityZ.addView(cita_backwards);

        final TextView cita_discard_one = new TextView(this);
        //RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(this.width, height_pixels);
        //layoutParams2.setMargins(left_cita,top_cita,right_cita,bottom_cita);
        cita_discard_one.setVisibility(View.INVISIBLE);
        cita_discard_one.setLayoutParams(layoutParams);
        cita_discard_one.setText("  'Freshwater sailor, \n a good one escaped from you!'");
        cita_discard_one.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_discard_one.setTextColor(Color.WHITE);
        activityZ.addView(cita_discard_one);

        final TextView cita_multishot = new TextView(this);
        //RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(this.width, height_pixels);
        //layoutParams3.setMargins(left_cita,top_cita,right_cita,bottom_cita);
        cita_multishot.setVisibility(View.INVISIBLE);
        cita_multishot.setLayoutParams(layoutParams);
        cita_multishot.setText("     'Too much rum? \nMaybe you start seeing triple?'");
        cita_multishot.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_multishot.setTextColor(Color.WHITE);
        activityZ.addView(cita_multishot);

        final TextView cita_rand_warp = new TextView(this);
        //RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(this.width, height_pixels);
        //layoutParams4.setMargins(left_cita,top_cita,right_cita,bottom_cita);
        cita_rand_warp.setVisibility(View.INVISIBLE);
        cita_rand_warp.setLayoutParams(layoutParams);
        cita_rand_warp.setText("  'The sea is very unpredictable,\n maybe too much ARRRRG'");
        cita_rand_warp.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_rand_warp.setTextColor(Color.WHITE);
        activityZ.addView(cita_rand_warp);

        final TextView cita_reversed_hand = new TextView(this);
        cita_reversed_hand.setVisibility(View.INVISIBLE);
        cita_reversed_hand.setLayoutParams(layoutParams);
        cita_reversed_hand.setText("  'You can not cheat if \n you do not see the cards'");
        cita_reversed_hand.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_reversed_hand.setTextColor(Color.WHITE);
        activityZ.addView(cita_reversed_hand);

        final TextView cita_speed_up = new TextView(this);
        cita_speed_up.setVisibility(View.INVISIBLE);
        cita_speed_up.setLayoutParams(layoutParams);
        cita_speed_up.setText("             'Stern wind!'");
        cita_speed_up.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_speed_up.setTextColor(Color.WHITE);
        activityZ.addView(cita_speed_up);

        final TextView cita_stunned = new TextView(this);
        cita_stunned.setVisibility(View.INVISIBLE);
        cita_stunned.setLayoutParams(layoutParams);
        cita_stunned.setText("  'This looks like a whirlpool \nof water ... good luck, you'll need it'");
        cita_stunned.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_stunned.setTextColor(Color.WHITE);
        activityZ.addView(cita_stunned);

        final TextView cita_full_heal = new TextView(this);
        cita_full_heal.setVisibility(View.INVISIBLE);
        cita_full_heal.setLayoutParams(layoutParams);
        cita_full_heal.setText("      'Take an orange! there is\n nothing better against scurvy'");
        cita_full_heal.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_full_heal.setTextColor(Color.WHITE);
        activityZ.addView(cita_full_heal);

        final TextView cita_ko = new TextView(this);
        cita_ko.setVisibility(View.INVISIBLE);
        cita_ko.setLayoutParams(layoutParams);
        cita_ko.setText("'You will come down with me!'");
        cita_ko.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_ko.setTextColor(Color.WHITE);
        activityZ.addView(cita_ko);

        final TextView cita_quick_revive = new TextView(this);
        cita_quick_revive.setVisibility(View.INVISIBLE);
        cita_quick_revive.setLayoutParams(layoutParams);
        cita_quick_revive.setText("  'Son of the sea,\n you have another chance'");
        cita_quick_revive.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_quick_revive.setTextColor(Color.WHITE);
        activityZ.addView(cita_quick_revive);

        final TextView cita_dispel = new TextView(this);
        cita_dispel.setVisibility(View.INVISIBLE);
        cita_dispel.setLayoutParams(layoutParams);
        cita_dispel.setText("       'Have a drink of rum!'");
        cita_dispel.setTypeface(null, Typeface.BOLD_ITALIC);
        cita_dispel.setTextColor(Color.WHITE);
        activityZ.addView(cita_dispel);



        //getLayoutInflater().inflate(R.layout.expanded_image,(ViewGroup)findViewById(R.id.container),true);

        //getFragmentManager().beginTransaction().add(R.id.container,new CardFrontFragment()).commit();

        // Hook up clicks on the thumbnail views.


        final View thumb1View = findViewById(R.id.thumb_button_1);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandedImageView != image_attack_up && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_attack_up;
                cita = cita_attack_up;
                zoomImageFromThumb(thumb1View, R.drawable.attack_up);
                cita_attack_up.setVisibility(View.VISIBLE);

            }
        });

        final View thumb2View = findViewById(R.id.thumb_button_2);
        thumb2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_backwards && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_backwards;
                cita = cita_backwards;
                zoomImageFromThumb(thumb2View, R.drawable.backwards);
                cita_backwards.setVisibility(View.VISIBLE);
            }
        });

        final View thumb3View = findViewById(R.id.thumb_button_3);
        thumb3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_discard_one && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_discard_one;
                cita = cita_discard_one;
                zoomImageFromThumb(thumb3View, R.drawable.discard_one);
                cita_discard_one.setVisibility(View.VISIBLE);
            }
        });

        final View thumb4View = findViewById(R.id.thumb_button_4);
        thumb4View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_multishot && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_multishot;
                cita = cita_multishot;
                zoomImageFromThumb(thumb4View, R.drawable.multishot);
                cita_multishot.setVisibility(View.VISIBLE);
            }
        });

        final View thumb5View = findViewById(R.id.thumb_button_5);
        thumb5View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_rand_warp && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_rand_warp;
                cita = cita_rand_warp;
                zoomImageFromThumb(thumb5View, R.drawable.rand_warp);
                cita_rand_warp.setVisibility(View.VISIBLE);
            }
        });

        final View thumb6View = findViewById(R.id.thumb_button_6);
        thumb6View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_reversed_hand && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_reversed_hand;
                cita = cita_reversed_hand;
                zoomImageFromThumb(thumb6View, R.drawable.reversed_hand);
                cita_reversed_hand.setVisibility(View.VISIBLE);
            }
        });

        final View thumb7View = findViewById(R.id.thumb_button_7);
        thumb7View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_speed_up && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_speed_up;
                cita = cita_speed_up;
                zoomImageFromThumb(thumb7View, R.drawable.speed_up);
                cita_speed_up.setVisibility(View.VISIBLE);
            }
        });

        final View thumb8View = findViewById(R.id.thumb_button_8);
        thumb8View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_stunned && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_stunned;
                cita = cita_stunned;
                zoomImageFromThumb(thumb8View, R.drawable.stunned);
                cita_stunned.setVisibility(View.VISIBLE);
            }
        });

        final View thumb9View = findViewById(R.id.thumb_button_9);
        thumb9View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_full_heal && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_full_heal;
                cita = cita_full_heal;
                zoomImageFromThumb(thumb9View, R.drawable.full_heal);
                cita_full_heal.setVisibility(View.VISIBLE);
            }
        });

        final View thumb10View = findViewById(R.id.thumb_button_10);
        thumb10View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_ko && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_ko;
                cita = cita_ko;
                zoomImageFromThumb(thumb10View, R.drawable.ko);
                cita_ko.setVisibility(View.VISIBLE);
            }
        });

        final View thumb11View = findViewById(R.id.thumb_button_11);
        thumb11View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_quick_revive && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_quick_revive;
                cita = cita_quick_revive;
                zoomImageFromThumb(thumb11View, R.drawable.quick_revive);
                cita_quick_revive.setVisibility(View.VISIBLE);
            }
        });

        final View thumb12View = findViewById(R.id.thumb_button_12);
        thumb12View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedImageView != image_dispel && expandedImageView != null){
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    cita.setVisibility(View.INVISIBLE);
                    mCurrentAnimator = null;
                }
                expandedImageView = image_dispel;
                cita = cita_dispel;
                zoomImageFromThumb(thumb12View, R.drawable.dispel);
                cita_dispel.setVisibility(View.VISIBLE);
            }
        });


        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }


    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        this.thumbView = thumbView;
        // Load the high-resolution "zoomed-in" image.
        //expandedImageView = (ImageView) findViewById(
        //      R.id.expanded_image);

        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.

        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds); // startBounds ahora contiene coordenadas de la miniatura en el layout root
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset); // la imagen expandida ocupará el 'contenedor' y finalbounds se settea con sus coordenadas y el punto globalOffset (coordenadas X,Y origen)

        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        finalBounds.top = this.height / 2 - 711 / 2;
        finalBounds.left = this.width / 2 - 525 / 2;





        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        System.out.println(startBounds.width() + " " + startBounds.height());

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);


        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);


        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;



        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                expandedImageView.clearAnimation();

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        cita.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        cita.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }


}
