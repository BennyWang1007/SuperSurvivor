package main;

public class GameSettings {
    private boolean musicOnOff = true;
    private boolean soundOnOff = true;

    public boolean isMusicOn() {
        return musicOnOff;
    }

    public void setMusicOnOff(boolean musicOnOff) {
        this.musicOnOff = musicOnOff;
    }

    public boolean isSoundOn() {
        return soundOnOff;
    }

    public void setSoundOnOff(boolean soundOnOff) {
        this.soundOnOff = soundOnOff;
    }
}
