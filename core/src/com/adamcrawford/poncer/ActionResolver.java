package com.adamcrawford.poncer;

/**
 * Author:  Adam Crawford
 * Project: MGD1505
 * Package: com.adamcrawford.poncer.android
 * File:    ActionResolver
 * Purpose: TODO Minimum 2 sentence description
 */
public interface ActionResolver {
    boolean getSignedInGPGS();
    void loginGPGS();
    void submitScoreGPGS(int score);
    void unlockAchievementGPGS(String achievementId);
    void getLeaderboardGPGS();
    void getAchievementsGPGS();
    void shareFB(int score);
}
