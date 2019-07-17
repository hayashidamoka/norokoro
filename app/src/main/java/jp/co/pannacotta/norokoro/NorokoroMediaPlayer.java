package jp.co.pannacotta.norokoro;

public class NorokoroMediaPlayer {
    NorokoroMediaPlayer mp;
    private static volatile NorokoroMediaPlayer instance = null;
    private NorokoroMediaPlayer(){ }

    public static NorokoroMediaPlayer getInstance(){
        if (instance == null){
            synchronized (NorokoroMediaPlayer.class){
                if (instance == null){
                    instance = new NorokoroMediaPlayer();
                }
            }
        }

        return instance;
    }
}
