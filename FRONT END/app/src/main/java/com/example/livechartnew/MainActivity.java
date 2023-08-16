package com.example.livechartnew;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    GraphView graphView;
    Random random = new Random();
    LineGraphSeries<DataPoint> series;
    private int lastX = 0 ;
    private double test = 0;
    private Socket socket ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            socket = IO.socket("http://10.0.2.2:3000");
            socket.connect();

        } catch (URISyntaxException e) {
            Log.e("SocketConnection", "Error connecting socket: " + e.getMessage());
        }

        // on below line we are initializing our graph view.
        graphView = this.findViewById(R.id.idGraph);

        series = new LineGraphSeries<DataPoint>();

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView.setTitle("My Graph View");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.black);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(30);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(20);
        viewport.setScrollable(true);

        socket.emit("chat","LiveChart");
        // get data from server
        socket.on("chat",onMessage);
    }

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(args[0].toString());
                    JSONObject data = (JSONObject) args[0];
                    try {
                        test = Double.parseDouble(data.getString("y_axis"));
                        lastX = Integer.parseInt(data.getString("x_axis"));
                        System.out.println(test + " " + lastX);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    addEntry(lastX);
                    graphView.addSeries(series);
                }
            });
        }
    };

    //  add random data to graph
    private void addEntry(int x) {
//        Double test = random.nextDouble() * 10d;
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(x, test), true, 10);
    }
}