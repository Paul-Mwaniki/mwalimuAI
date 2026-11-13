package ke.co.mwalimuai.generator;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ke.co.mwalimuai.utils.AssetManagerHelper;

/**
 * Generates and adapts offline classroom mathematics games for Grade 6 CBC.
 */
public class GamificationGenerator {

    private static final String TAG = "GamificationGenerator";
    private static final String GAMIFICATION_FILE = "data/Gamification.json";

    public static class GameItem {
        @SerializedName("game_id")
        public String gameId;

        @SerializedName("curriculum_id")
        public String curriculumId;

        @SerializedName("activity_id")
        public String activityId;

        @SerializedName("name")
        public String name;

        @SerializedName("rules")
        public String rules;

        @SerializedName("materials")
        public String materials;

        @SerializedName("players")
        public String players;

        @SerializedName("duration_minutes")
        public int durationMinutes;

        @SerializedName("offline")
        public boolean offline;

        @SerializedName("inclusion")
        public String inclusion;

        @SerializedName("assessment")
        public String assessment;
    }

    public static List<GameItem> loadGames(Context context) {
        try {
            if (AssetManagerHelper.assetExists(context, GAMIFICATION_FILE)) {
                String json = AssetManagerHelper.readAssetString(context, GAMIFICATION_FILE);
                Gson gson = new Gson();
                Type type = new TypeToken<List<GameItem>>() {}.getType();
                List<GameItem> list = gson.fromJson(json, type);
                if (list != null) return list;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load games from JSON", e);
        }
        return new ArrayList<>();
    }

    public static List<GameItem> getGamesForCurriculum(Context context, String curriculumId) {
        List<GameItem> allGames = loadGames(context);
        List<GameItem> filtered = new ArrayList<>();
        for (GameItem game : allGames) {
            if (game.curriculumId != null && game.curriculumId.equalsIgnoreCase(curriculumId)) {
                filtered.add(game);
            }
        }
        return filtered.isEmpty() ? allGames : filtered;
    }
}
