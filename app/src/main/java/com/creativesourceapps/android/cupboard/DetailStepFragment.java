package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class DetailStepFragment extends Fragment implements ExoPlayer.EventListener {
    int position;
    private static final String TAG = DetailStepFragment.class.getSimpleName();
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private IngredientListAdapter adapter;
    private ExpandableListView expandableListView;
    private TextView stepTextView, numberTextView;
    private ImageView expandImageView;
    long playerPosition;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    // Mandatory empty constructor
    public DetailStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null)
            playerPosition = savedInstanceState.getLong("playerPosition");

        final View rootView = inflater.inflate(R.layout.fragment_detail_step, container, false);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);
        final TextView textView = (TextView) rootView.findViewById(R.id.text_description);
        Button previousBtn = (Button) rootView.findViewById(R.id.buttonA);
        Button nextBtn = (Button) rootView.findViewById(R.id.buttonB);
        expandableListView = rootView.findViewById(R.id.step_list_view);
        stepTextView = rootView.findViewById(R.id.list_item_step);
        numberTextView = rootView.findViewById(R.id.tv_step_number);
        expandImageView = rootView.findViewById(R.id.iv_expand_less);
        final Recipe recipe = getActivity().getIntent().getParcelableExtra("parcel_data");
        adapter = new IngredientListAdapter(getContext(), "Ingredients", recipe.ingredients);

        expandableListView.setAdapter(adapter);

        position = 0;
        if(getArguments() != null)
            position = getArguments().getInt("position", 0);
        if(position == 0)
            numberTextView.setText("");
        else
            numberTextView.setText(String.valueOf(position));
        stepTextView.setText(recipe.steps.get(position));
        textView.setText(recipe.instructions.get(position));

        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.ic_launcher_foreground));

        initializeMediaSession();

        if(!recipe.media.get(position).equals(""))
            initializePlayer(Uri.parse(recipe.media.get(position)));
        else
            mPlayerView.setVisibility(View.GONE);

         previousBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(position != 0) {
                     position -= 1;
                     if(position == 0)
                         numberTextView.setText("");
                     else
                         numberTextView.setText(String.valueOf(position));
                     textView.setText(recipe.instructions.get(position));
                     stepTextView.setText(recipe.steps.get(position));

                     if(mExoPlayer == null) {
                         if (!recipe.media.get(position).equals("")) {
                             mPlayerView.setVisibility(View.VISIBLE);
                             initializePlayer(Uri.parse(recipe.media.get(position)));
                         } else
                             mPlayerView.setVisibility(View.GONE);
                     } else {
                         if (!recipe.media.get(position).equals("")) {
                             releasePlayer();
                             mPlayerView.setVisibility(View.VISIBLE);
                             initializePlayer(Uri.parse(recipe.media.get(position)));
                         } else {
                             mPlayerView.setVisibility(View.GONE);
                             releasePlayer();
                         }
                     }
                 } else {
                     position = recipe.instructions.size() - 1;
                     textView.setText(recipe.instructions.get(recipe.instructions.size() - 1));
                     numberTextView.setText(String.valueOf(recipe.instructions.size() - 1));
                     stepTextView.setText(recipe.steps.get(recipe.instructions.size() - 1));

                     if(mExoPlayer == null) {
                         if (!recipe.media.get(position).equals(""))
                             initializePlayer(Uri.parse(recipe.media.get(position)));
                         else
                             mPlayerView.setVisibility(View.GONE);
                     } else {
                         if (!recipe.media.get(position).equals("")) {
                             releasePlayer();
                             initializePlayer(Uri.parse(recipe.media.get(position)));
                         } else {
                             mPlayerView.setVisibility(View.GONE);
                             releasePlayer();
                         }
                     }
                 }
             }
         });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position != recipe.instructions.size() - 1) {
                    position += 1;
                    textView.setText(recipe.instructions.get(position));
                    if(position == 0)
                        numberTextView.setText("");
                    else
                        numberTextView.setText(String.valueOf(position));
                    stepTextView.setText(recipe.steps.get(position));

                    if(mExoPlayer == null) {
                        if (!recipe.media.get(position).equals("")) {
                            mPlayerView.setVisibility(View.VISIBLE);
                            initializePlayer(Uri.parse(recipe.media.get(position)));
                        } else
                            mPlayerView.setVisibility(View.GONE);
                    } else {
                        if (!recipe.media.get(position).equals("")) {
                            releasePlayer();
                            mPlayerView.setVisibility(View.VISIBLE);
                            initializePlayer(Uri.parse(recipe.media.get(position)));
                        } else {
                            mPlayerView.setVisibility(View.GONE);
                            releasePlayer();
                        }
                    }
                } else {
                    position = 0;
                    textView.setText(recipe.instructions.get(0));
                    stepTextView.setText(recipe.steps.get(0));
                    numberTextView.setText("");

                    if(mExoPlayer == null) {
                        if (!recipe.media.get(position).equals(""))
                            initializePlayer(Uri.parse(recipe.media.get(position)));
                        else
                            mPlayerView.setVisibility(View.GONE);
                    } else {
                        if (!recipe.media.get(position).equals("")) {
                            releasePlayer();
                            initializePlayer(Uri.parse(recipe.media.get(position)));
                        } else {
                            mPlayerView.setVisibility(View.GONE);
                            releasePlayer();
                        }
                    }
                }
            }
        });

        expandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Return the root view
        return rootView;
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "Cupboard");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
        mExoPlayer.seekTo(playerPosition);
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mExoPlayer != null)
            releasePlayer();
        mMediaSession.setActive(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            Bundle bundle = new Bundle();
            onSaveInstanceState(bundle);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("playerPosition", mExoPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
