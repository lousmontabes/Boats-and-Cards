package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.lluismontabes.gameofboatsandcards.Model.DisplayNextView;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by JorgeTB on 15/04/2017.
 */

public class AnimationsActivity extends Activity {


    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;


    //num clicks
    private int clicks = 0;

    private final Rect finalBounds = new Rect();
    private final Rect startBounds = new Rect();

    private ImageView expandedImageView;
    private ImageView reverseImage;
    private View thumbView;
    private boolean isFirstImage = true;

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        final ImageView reverse_of_multishot = (ImageView) findViewById(R.id.reverse_of_multishot);
        reverse_of_multishot.setVisibility(View.GONE);

        final ImageView reverse_of_reversed_hand = (ImageView) findViewById(R.id.reverse_of_reversed_hand);
        reverse_of_reversed_hand.setVisibility(View.GONE);

        final ImageView reverse_of_attack_up = (ImageView) findViewById(R.id.reverse_of_attack_up);
        reverse_of_attack_up.setVisibility(View.GONE);

        final ImageView reverse_of_rand_warp = (ImageView) findViewById(R.id.reverse_of_rand_warp);
        reverse_of_rand_warp.setVisibility(View.GONE);

        final ImageView reverse_of_backwards = (ImageView) findViewById(R.id.reverse_of_backwards);
        reverse_of_backwards.setVisibility(View.GONE);

        final ImageView reverse_of_discard_one = (ImageView) findViewById(R.id.reverse_of_discard_one);
        reverse_of_discard_one.setVisibility(View.GONE);

        final ImageView reverse_of_speed_up = (ImageView) findViewById(R.id.reverse_of_speed_up);
        reverse_of_speed_up.setVisibility(View.GONE);

        final ImageView reverse_of_stunned = (ImageView) findViewById(R.id.reverse_of_stunned);
        reverse_of_stunned.setVisibility(View.GONE);

        final ImageView reverse_of_full_restoration = (ImageView) findViewById(R.id.reverse_of_full_restoration);
        reverse_of_full_restoration.setVisibility(View.GONE);

        final ImageView reverse_of_dispel = (ImageView) findViewById(R.id.reverse_of_dispel);
        reverse_of_dispel.setVisibility(View.GONE);

        final ImageView reverse_of_ko = (ImageView) findViewById(R.id.reverse_of_ko);
        reverse_of_ko.setVisibility(View.GONE);

        final ImageView reverse_of_quick_revive = (ImageView) findViewById(R.id.reverse_of_quick_revive);
        reverse_of_quick_revive.setVisibility(View.GONE);

        //Descomentar para visualizar card-flip animation
        //getFragmentManager().beginTransaction().add(R.id.container,new CardFrontFragment()).commit();

        // Hook up clicks on the thumbnail views.

        final View thumb1View = findViewById(R.id.thumb_button_1);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedImageView.setVisibility(View.GONE);
                reverseImage = reverse_of_multishot;
                zoomImageFromThumb(thumb1View, R.drawable.multishot_collection2);
            }
        });

        final View thumb2View = findViewById(R.id.thumb_button_2);
        thumb2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_reversed_hand;
                zoomImageFromThumb(thumb2View, R.drawable.reversed_hand_collection);
            }
        });

        final View thumb3View = findViewById(R.id.thumb_button_3);
        thumb3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_attack_up;
                zoomImageFromThumb(thumb3View, R.drawable.attack_up_collection);
            }
        });

        final View thumb4View = findViewById(R.id.thumb_button_4);
        thumb4View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_rand_warp;
                zoomImageFromThumb(thumb4View, R.drawable.rand_warp_collection);
            }
        });

        final View thumb5View = findViewById(R.id.thumb_button_5);
        thumb5View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_backwards;
                zoomImageFromThumb(thumb5View, R.drawable.backwards_collection);
            }
        });

        final View thumb6View = findViewById(R.id.thumb_button_6);
        thumb6View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_discard_one;
                zoomImageFromThumb(thumb6View, R.drawable.discard_one_collection);
            }
        });

        final View thumb7View = findViewById(R.id.thumb_button_7);
        thumb7View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_speed_up;
                zoomImageFromThumb(thumb7View, R.drawable.speed_up_collection);
            }
        });

        final View thumb8View = findViewById(R.id.thumb_button_8);
        thumb8View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_stunned;
                zoomImageFromThumb(thumb8View, R.drawable.stunned_collection);
            }
        });

        final View thumb9View = findViewById(R.id.thumb_button_9);
        thumb9View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_full_restoration;
                zoomImageFromThumb(thumb9View, R.drawable.stunned_collection);
            }
        });

        final View thumb10View = findViewById(R.id.thumb_button_10);
        thumb10View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_dispel;
                zoomImageFromThumb(thumb10View, R.drawable.stunned_collection);
            }
        });

        final View thumb11View = findViewById(R.id.thumb_button_11);
        thumb11View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_ko;
                zoomImageFromThumb(thumb11View, R.drawable.stunned_collection);
            }
        });

        final View thumb12View = findViewById(R.id.thumb_button_12);
        thumb12View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseImage = reverse_of_quick_revive;
                zoomImageFromThumb(thumb12View, R.drawable.stunned_collection);
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void zoomImageFromThumb(final View thumbView, int imageResId){
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        this.thumbView = thumbView;

        // Load the high-resolution "zoomed-in" image.
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);
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
        //set.setDuration(mShortAnimationDuration);
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

                expandedImageView.clearAnimation();
                //image2.clearAnimation();

                clicks++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clicks == 1) {
                            //Descomentar para test card-flip
                            //flipCard();

                            //Implementation of flip animation between double ImageView instead of Fragments
                            System.out.println("1 CLICK!");
                            if (isFirstImage) {
                                applyRotation(0, 90);
                                isFirstImage = !isFirstImage;
                            } else {
                                applyRotation(0, -90);
                                isFirstImage = !isFirstImage;
                            }

                        } else if (clicks == 2) {
                            if (mCurrentAnimator != null) {
                                mCurrentAnimator.cancel();
                            }
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
                                    mCurrentAnimator = null;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    thumbView.setAlpha(1f);
                                    expandedImageView.setVisibility(View.GONE);
                                    mCurrentAnimator = null;
                                }
                            });
                            set.start();
                            mCurrentAnimator = set;

                        }
                        clicks = 0;
                    }
                }, 500);
            }
        });
    }


    // Animation Flip-Card with double ImageView
    private void applyRotation(float start, float end) {

// Find the center of image

        final float centerX = expandedImageView.getWidth() / 2.0f;
        final float centerY = expandedImageView.getHeight() / 2.0f;


// Create a new 3D rotation with the supplied parameter

// The animation listener is used to trigger the next animation

        final Flip3DAnimation rotation = new Flip3DAnimation(start, end, centerX, centerY);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(isFirstImage, expandedImageView, reverseImage));

        if (isFirstImage) {expandedImageView.startAnimation(rotation);}
        else {reverseImage.startAnimation(rotation);}



    }


    // Animation Flip-Card with double Fragment
    private void flipCard(){
        if (mShowingBack){
            getFragmentManager().popBackStack();
            mShowingBack = false;
            return;
        }
        //flip to the back

        mShowingBack = true;


        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.
        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                .replace(R.id.container, new CardBackFragment())

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();
    }

}
