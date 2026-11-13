package ke.co.mwalimuai.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class AssetManagerHelper {

    private static final String TAG = "AssetManagerHelper";

    /**
     * Reads an asset file into a String UTF-8.
     */
    public static String readAssetString(Context context, String assetPath) throws IOException {
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open(assetPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Loads a .tflite model file as a memory-mapped ByteBuffer from assets.
     */
    public static MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetManager assetManager = context.getAssets();
        try (AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    /**
     * Checks if an asset exists at the specified path.
     */
    public static boolean assetExists(Context context, String assetPath) {
        if (context == null || context.getAssets() == null) return false;
        try {
            InputStream is = context.getAssets().open(assetPath);
            is.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
