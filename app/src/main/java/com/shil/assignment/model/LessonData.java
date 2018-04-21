package com.shil.assignment.model;

/**
 * Created by snehasisshil on 22/04/18.
 */

public class LessonData {

    private String type;
    private String conceptName;
    private String pronunciation;
    private String targetScript;
    private String audio_url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getTargetScript() {
        return targetScript;
    }

    public void setTargetScript(String targetScript) {
        this.targetScript = targetScript;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public LessonData() {
    }


    public LessonData(String type, String conceptName, String pronunciation, String targetScript, String audio_url) {
        this.type = type;
        this.conceptName = conceptName;
        this.pronunciation = pronunciation;
        this.targetScript = targetScript;
        this.audio_url = audio_url;
    }

}
