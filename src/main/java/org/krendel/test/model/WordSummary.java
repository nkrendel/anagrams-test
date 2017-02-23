package org.krendel.test.model;

/**
 * Contains summary information about words in the corpus:
 *     - count of words in the corpus and min/max/median/average word length
 */
public class WordSummary {
    private int count;
    private int minLength;
    private int maxLength;
    private float medLength;  // median length
    private float avgLength;  // average length

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public float getMedLength() {
        return medLength;
    }

    public void setMedLength(float medLength) {
        this.medLength = medLength;
    }

    public float getAvgLength() {
        return avgLength;
    }

    public void setAvgLength(float avgLength) {
        this.avgLength = avgLength;
    }
}
