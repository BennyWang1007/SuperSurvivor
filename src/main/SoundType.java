package main;

public enum SoundType {

    BUTTON_CLICK("button_click.wav"),
    EXP_COLLECT("exp_collect.wav"),
    HIT_MONSTER("hit_monster.wav"),
    LEVEL_UP("level_up.wav"),
    PLAYER_HURT("player_hurt.wav"),
    HEALBAG_COLLECT("healbag_collect.wav"),
    ARROW_SHOOT("arrow_shoot.wav"),
    SELECT_BUFF("select_buff.wav")
    ;

    private final Sound sound;

    SoundType(String filePath) {
        this.sound = new Sound(filePath);
    }

    public void play() {
        sound.play();
    }

    public static void setVolume(int level) {
        for (SoundType type : SoundType.values()) {
            type.sound.setVolume(level);
        }
    }

}
