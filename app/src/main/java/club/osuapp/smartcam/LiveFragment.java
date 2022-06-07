package club.osuapp.smartcam;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amazonaws.services.kinesisvideoarchivedmedia.AWSKinesisVideoArchivedMediaClient;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AWSKinesisVideoClient;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointResult;
import com.amazonaws.services.kinesisvideoarchivedmedia.AWSKinesisVideoArchivedMediaClient;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.GetHLSStreamingSessionURLRequest;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.GetHLSStreamingSessionURLResult;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.HLSDiscontinuityMode;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.HLSFragmentSelector;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.HLSFragmentSelectorType;
import com.amazonaws.services.kinesisvideoarchivedmedia.model.HLSTimestampRange;
import com.google.android.exoplayer2.util.MimeTypes;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import club.osuapp.smartcam.databinding.FragmentLiveBinding;

public class LiveFragment extends Fragment {
    private static final String TAG  = MainActivity.class.getSimpleName();
    private FragmentLiveBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLiveBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExoPlayer player = new ExoPlayer.Builder(getContext()).build();

        StyledPlayerView playerView = view.findViewById(R.id.live_player);
        playerView.setPlayer(player);

        /*MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();*/
        CompletableFuture.runAsync(() -> {

            BasicAWSCredentials cred = new BasicAWSCredentials("AKIA6HO33SN7ILNQMWGX", "o2hFVg6z8xjVbbC9rnLhOpsqIY9MzEBqfUwFA3QU");

            AWSKinesisVideoClient kinesisVideo = new AWSKinesisVideoClient(cred);
            kinesisVideo.setRegion(Region.getRegion(Regions.US_WEST_2));

            GetDataEndpointResult var10000 = kinesisVideo.getDataEndpoint((new GetDataEndpointRequest()).withStreamName("Test").withAPIName("GET_HLS_STREAMING_SESSION_URL"));
            String endpoint = var10000.getDataEndpoint();
            Log.i(TAG, "endpoint: " + endpoint);

            AWSKinesisVideoArchivedMediaClient kinesisVideoArchivedContent = new AWSKinesisVideoArchivedMediaClient(cred);
            kinesisVideoArchivedContent.setRegion(Region.getRegion(Regions.US_WEST_2));
            kinesisVideoArchivedContent.setEndpoint(endpoint);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            //change later
            String text = "2022-06-06T18:43";
            Date date = new Date();

            GetHLSStreamingSessionURLRequest request = (new GetHLSStreamingSessionURLRequest())
                    .withStreamName("Test")
                    .withPlaybackMode("LIVE")
                    .withDiscontinuityMode(HLSDiscontinuityMode.ALWAYS)
                    .withHLSFragmentSelector((new HLSFragmentSelector())
                            .withFragmentSelectorType(HLSFragmentSelectorType.SERVER_TIMESTAMP));

            try {
                GetHLSStreamingSessionURLResult var12 = kinesisVideoArchivedContent.getHLSStreamingSessionURL(request);
                String streamUrl = var12.getHLSStreamingSessionURL();
                Log.i(TAG, "stream url: " + streamUrl);
                runOnUiThread(() -> {
//                        MediaSource mediaSource =
//                                new RtspMediaSource.Factory()
//                                        .createMediaSource(MediaItem.fromUri(streamUrl));
                    MediaItem mediaItem = new MediaItem.Builder().setUri(streamUrl).setMimeType(MimeTypes.APPLICATION_M3U8).build();
                    //MediaItem mediaItem = MediaItem.fromUri(streamUrl);
//                        player.setMediaSource(mediaSource);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                });
            } catch (Exception exception) {
                Log.i(TAG, "Error: " + exception.getLocalizedMessage());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}